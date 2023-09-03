package org.civilian.tool.csp;


import org.civilian.template.ComponentBuilder;
import org.civilian.util.Scanner;


class CspTlinePrinter
{
	public CspTlinePrinter(SourceWriter out, Scanner scanner)
	{
		this.out 	= out;
		scanner_   	= scanner;
	}
	
	
	public void printComponentStart(int level, String cbExpr, boolean multiLine, boolean declare)
	{
		String var = getCbVar(level);
		if (declare)
		{
			out.print(ComponentBuilder.class.getName());
			out.print(' ');
		}
		out.print(var);
		out.print(" = ");
		out.print(cbExpr);
		out.println(";");
		out.print(var);
		out.print(".startComponent(");
		out.print(multiLine);
		out.print(");");

		printSrcln(cbExpr);
	}


	public void printComponentEnd(int level, boolean multiLine, String comment)
	{
		String var = getCbVar(level);
		out.print(var);
		out.print(".endComponent(");
		out.print(multiLine);
		out.print(");"); // ends the wrapper block
		printSrcln(comment);
	}


	public void printCodeLine(String line, String src)
	{
		out.print(line);
		printSrcln(src);
	}
	
	
	public void printLiteralLine(String line, boolean usePrintln)
	{
		int length = line.length();
		int start = 0;
		int p = 0;
		boolean lastPartWasCode = false;

		while((start < length) && ((p = line.indexOf("<%", start)) != -1))
		{
			lastPartWasCode = false;
			if (line.regionMatches(p, "<%%", 0, 3))
			{
				if ((p + 3 < length) && (line.charAt(p + 3) == '>'))
				{
					// <%%> detected
					if (start < p)
						printTemplateText(line, start, p, false);
					start = p + 4;
				}
				else
				{
					// <%% detected: print literal
					printTemplateText(line, start, p+2, false);
					start = p + 3;
				}
			}
			else
			{
				if (start < p)
					printTemplateText(line, start, p, false);

				int q = line.indexOf("%>", p);

				// code end signal not found
				if (q == -1)
					throw new CspException("closing '%>' not found", scanner_);

				// ignore empty code segments <%%>
				if (q > p + 2)
				{
					// line end signal <%/%> found
					if ((q == p + 3) && (line.charAt(p + 2) == '/'))
						return;

					String snippetRaw  = line.substring(p, q + 2);
					String snippetCode = line.substring(p +2, q).trim();

					printTemplateSnippet(snippetRaw, snippetCode);
					lastPartWasCode = true;
				}
				start = q + 2;
			}
		}
		if (start < length)
			printTemplateText(line, start, length, usePrintln);
		else if (usePrintln)
		{
			if (lastPartWasCode)
				out.println("out.printlnIfNotEmpty();");
			else
				out.println("out.println();");
		}
	}


	/**
	 * Prints a template code segment embedded in a literal line between "&lt;%" and "%&gt;".
	 * @param raw the snippet including the boundaries "&lt;%" and "%&gt;".
	 * @param code the code content, trimmed.
	 */
	private void printTemplateSnippet(String raw, String code) throws CspException
	{
		if (code.charAt(0) == '?')
		{
			if (code.length() != 1)
			{
				out.print("if (");
				out.print(code.substring(1).trim());
				out.print(")");
				printSrcln(raw);
				out.beginBlock();
			}
			else
				out.endBlock();
		}
		else if (code.endsWith(";"))
		{
			out.print(code);
			printSrcln(raw);
		}
		else
		{
			out.print("out.print(");
			out.print(code);
			out.print(");");
			printSrcln(raw);
		}
	}

	
	private void printTemplateText(String content, int start, int end, boolean usePrintln)
	{
		out.print(usePrintln ? "out.println(\"" : "out.print(\"");
		for (int i=start; i<end; i++)
		{
			char c = content.charAt(i);
			switch(c)
			{
				case '\t':
					out.print("\\t");
					break;
				case '\\':
					out.print("\\\\");
					break;
				case '"':
					out.print("\\\"");
					break;
				default:
					out.print(c);
					break;
			}
		}
		out.print("\");");
		printSrcln(content.substring(start, end));
	}


	private void printSrcln(String s)
	{
		out.printSrcln(s, scanner_.getLineIndex());
	}

	
	private static String getCbVar(int level)
	{
		return "cspCb" + level;
	}


	public final SourceWriter out;
	private final Scanner scanner_;
}
