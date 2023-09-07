package org.civilian.tool.csp;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import org.civilian.util.Check;


/**
 * Outputs the transpiled Java class from a parsed CSP template. 
 */
class CspClassPrinter
{
	public CspClassPrinter(SourceWriter out, ClassData classData)
	{
		this.out 	= Check.notNull(out, "out");
		classData_ 	= Check.notNull(classData, "classData");
	}
	
	
	public void print(File templFile, boolean timestamp,  String body) throws IOException
	{
		printClassStart(templFile, timestamp);
		printBody(body);
		printFields();
		out.endBlock(); // class block, started in printClassStart
	}
	
	
	private void printClassStart(File templFile, boolean timestamp) throws IOException
	{
		out.println("/**");
		out.print(" * Generated from " + templFile.getName());
		if (timestamp)
		{
			out.print(" at ");
			out.print(LocalDateTime.now());
		}
		out.println();
		out.println(" * Do not edit.");
		out.println(" */");
		out.print("package ");
		out.print(classData_.packageName);
		out.println(";");
		out.println();
		out.println();
		if (classData_.imports.write(out, null /* app package*/))
		{
			out.println();
			out.println();
		}
		for (String prolog : classData_.prolog)
			out.println(prolog);

		if (classData_.isPublic)
			out.print("public ");
		if (classData_.isAbstract)
			out.print("abstract ");
		out.print("class ");
		out.print(classData_.className);
		if (classData_.extendsClass != null)
		{
			out.print(" extends ");
			out.print(classData_.extendsClass);
		}
		if (classData_.implementsList != null)
		{
			out.print(" implements ");
			out.print(classData_.implementsList);
		}
		out.println();
		out.beginBlock();

		if (classData_.needsCtor())
			printClassCtor(out);

		if (classData_.standalone)
			printClassPublicPrintMethod(out);
		if (!classData_.mixins.isEmpty())
		{
			printClassInitMethod(out);
			printClassExitMethod(out);
		}

		if (classData_.hasMainTemplate)
		{
			if (classData_.extendsClass != null)
				out.print("@Override ");
			out.print("protected void print()");
			if (classData_.exception != null)
			{
				out.print(" throws ");
				out.print(classData_.exception);
			}
			out.println();
		}
	}

	
	private void printClassCtor(SourceWriter out) throws IOException
	{
		out.print("public ");
		out.print(classData_.className);
		out.print("(");
		if (classData_.args != null)
			out.print(classData_.args);
		out.println(")");
		out.beginBlock();
		if (classData_.superArgs != null)
		{
			out.print("super(");
			out.print(classData_.superArgs);
			out.println(");");
		}
		for (Argument arg : classData_.arguments)
		{
			arg.fieldAssign(out);
			out.println();
		}
		out.endBlock();
		out.println();
		out.println();
	}

	
	private void printClassPublicPrintMethod(SourceWriter out)
	{
		out.print("public synchronized void print(");
		out.print(classData_.writerClassSimple);
		out.print(" out)");
		if (classData_.exception != null)
		{
			out.print(" throws ");
			out.print(classData_.exception);
		}
		out.println();
		out.beginBlock();
			out.println("if (out == null)");
			out.increaseTab();
				out.println("throw new IllegalArgumentException(\"out is null\");");
			out.decreaseTab();
			out.println("this.out = out;");
			if (!classData_.mixins.isEmpty())
				out.println("init();");
			out.println("print();");
		out.endBlock();
		out.println();
		out.println();
	}


	private void printClassInitMethod(SourceWriter out)
	{
		if (!classData_.standalone)
			out.print("@Override ");
		out.println("protected void init()");
		out.beginBlock();
			out.println("super.init();");
			for (MixinField mixin : classData_.mixins)
			{
				out.print(mixin.fieldName);
				out.print(" = new ");
				out.print(mixin.simpleName);
				out.println("(out);");
			}
		out.endBlock();
		out.println();
		out.println();
	}


	private void printClassExitMethod(SourceWriter out)
	{
		if (!classData_.standalone)
			out.print("@Override ");
		out.println("protected void exit()");
		out.beginBlock();
			for (MixinField mixin : classData_.mixins)
			{
				out.print(mixin.fieldName);
				out.println(" = null;");
			}
			out.println("super.exit();");
		out.endBlock();
		out.println();
		out.println();
	}
	

	private void printBody(String body)
	{
		int tab = out.getTabCount();
		out.setTabCount(0);
		out.print(body);
		out.setTabCount(tab);
	}
	

	private void printFields() throws IOException
	{
		if (classData_.hasFields())
		{
			out.println();
			out.println();
			for (Argument arg : classData_.arguments)
			{
				arg.fieldDecl(out);
				out.println();
			}
			for (MixinField mixin : classData_.mixins)
			{
				out.print("protected ");
				out.print(mixin.simpleName);
				out.print(" ");
				out.print(mixin.fieldName);
				out.println(";");
			}
			if (classData_.standalone)
			{
				out.print("protected ");
				out.print(classData_.writerClassSimple);
				out.println(" out;");
			}
		}
	}
	
	
	private final SourceWriter out;
	private final ClassData classData_;
}
