package org.civilian.tool.csp;


import java.util.List;

import org.civilian.template.ComponentBuilder;
import org.civilian.tool.csp.CspTLineParser.LiteralPart;
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

		printSrcCommentln(cbExpr);
	}


	public void printComponentEnd(int level, boolean multiLine, String comment)
	{
		String var = getCbVar(level);
		out.print(var);
		out.print(".endComponent(");
		out.print(multiLine);
		out.print(");"); // ends the wrapper block
		printSrcCommentln(comment);
	}


	public void printCodeLine(String line, String src)
	{
		out.print(line);
		printSrcCommentln(src);
	}
	
	
	public void printLiteralLine(List<LiteralPart> parts, int start, boolean usePrintln)
	{
		boolean lastPartWasCode = false;
		boolean textPrintln = false;
		int size = parts.size();
		for (int i=start; i<size; i++)
		{
			lastPartWasCode = false;
			LiteralPart part = parts.get(i);
			switch (part.type)
			{
				case COMPONENT:
				case COMPONENT_END:
				case COMPONENT_START:
					throw new IllegalStateException();
				case JAVA_EXPRESSION:
				case JAVA_STATEMENT:
				case JAVA_CONDITION_START:
				case JAVA_CONDITION_END:
					lastPartWasCode = true;
					printLiteralCode(part);
					break;
				case SKIPLN:
					return;
				case TEXT:
					textPrintln = usePrintln && (i == size - 1);
					printLiteralText(part.value, textPrintln);
					break;
			}
		}
		if (usePrintln && !textPrintln)
		{
			if (lastPartWasCode)
				out.println("out.printlnIfNotEmpty();");
			else
				out.println("out.println();");
		}
	}

	
	/**
	 * Prints a template code snippet.
	 * @param raw the snippet included the start and end symbol.
	 * @param code the code content with the symbols and trimmed.
	 */
	private void printLiteralCode(LiteralPart part) throws CspException
	{
		switch(part.type)
		{
			case JAVA_EXPRESSION:
				out.print("out.print(");
				out.print(part.value);
				out.print(");");
				printSrcCommentln(part.rawValue);
				break;
			case JAVA_STATEMENT:
				out.print(part.value);
				printSrcCommentln(part.rawValue);
				break;
			case JAVA_CONDITION_START:
				out.print("if (");
				out.print(part.value);
				out.print(")");
				printSrcCommentln(part.rawValue);
				out.beginBlock();
				break;
			case JAVA_CONDITION_END:
				out.endBlock();
				break;
			default:
				throw new IllegalArgumentException("invalid " + part.type);
		}
	}

	
	private void printLiteralText(String text, boolean usePrintln)
	{
		out.print(usePrintln ? "out.println(\"" : "out.print(\"");
		int length = text.length();
		for (int i=0; i<length; i++)
		{
			char c = text.charAt(i);
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
		printSrcCommentln(text);
	}

	
	private void printSrcCommentln(String s)
	{
		out.printSrcCommentln(s, scanner_.input.getLineIndex());
	}

	
	private static String getCbVar(int level)
	{
		return "cspCb" + level;
	}


	public final SourceWriter out;
	private final Scanner scanner_;
}
