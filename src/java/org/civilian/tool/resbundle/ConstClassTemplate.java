/**
 * Generated from ConstClassTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resbundle;


import java.util.HashSet;
import java.util.List;
import org.civilian.template.TemplateWriter;
import org.civilian.util.ClassUtil;
import org.civilian.util.DateTime;
import org.civilian.util.StringUtil;


class ConstClassTemplate
{
	public ConstClassTemplate(ResBundleCompiler.Config config, String packageName, DateTime generationTime, List<Translation> translations)
	{
		this.config = config;
		this.packageName = packageName;
		this.generationTime = generationTime;
		this.translations = translations;
	}


	public synchronized void print(TemplateWriter out)
	{
		if (out == null)
			throw new IllegalArgumentException("out is null");
		this.out = out;
		print();
	}


	protected void print()
	{
		boolean hasIdClass   = config.idClass != null;                  // line 13: @boolean hasIdClass   = config.idClass != null;
		String idClassSimple = hasIdClass ? ClassUtil.cutPackageName(config.idClass) : "String"; // line 14: @String idClassSimple = hasIdClass ? ClassUtil.cutPackageName(config.idClass) : "String";
		HashSet<String> usedConstants = new HashSet<>();                // line 15: @HashSet<String> usedConstants = new HashSet<>();
		StringBuilder s = new StringBuilder();                          // line 16: @StringBuilder s = new StringBuilder();
		//                                                              // line 17: @//
		out.print("package ");                                          // line 18: package
		out.print(packageName);                                         // line 18: <%packageName%>
		out.println(";");                                               // line 18: ;
		out.println();
		out.println();
		if (hasIdClass && !config.inlineIdClass)                        // line 21: @if (hasIdClass && !config.inlineIdClass)
		{
			out.print("import ");                                       // line 22: import
			out.print(config.idClass);                                  // line 22: <%config.idClass%>
			out.println(";");                                           // line 22: ;
			out.println();
			out.println();
		}
		out.println("/**");                                             // line 25: /**
		out.print(" * Contains constants for resource bundle ids in "); // line 26: * Contains constants for resource bundle ids in
		out.print(config.excelFile.getName());                          // line 26: <%config.excelFile.getName()%>
		out.println(".");                                               // line 26: .
		out.print(" * Generated at ");                                  // line 27: * Generated at
		out.print(generationTime);                                      // line 27: <%generationTime%>
		out.println(". Do not edit directly.");                         // line 27: . Do not edit directly.
		out.println(" */");                                             // line 28: */
		out.print("public interface ");                                 // line 29: public interface
		out.print(config.constClass);                                   // line 29: <%config.constClass%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 30: {
		out.increaseTab();
		for (Translation t : translations)                              // line 31: @for (Translation t : translations)
		{
			// javafy key: must be a valid java identifier              // line 32: @// javafy key: must be a valid java identifier
			// escape key when passed as argument to the MsgKey ctor    // line 33: @// escape key when passed as argument to the MsgKey ctor
			// keep a list of javafied keys and add _x suffixes if not unique // line 34: @// keep a list of javafied keys and add _x suffixes if not unique
			String id = t.id;                                           // line 35: @String id = t.id;
			String constantName = getConstantName(id, s, usedConstants); // line 36: @String constantName = getConstantName(id, s, usedConstants);
			if (constantName != null)                                   // line 37: @if (constantName != null)
			{
				if (config.javadoc)                                     // line 38: @if (config.javadoc)
				{
					out.print("/**");                                   // line 39: /**
					for (String lang : t.lang)                          // line 40: @for (String lang : t.lang)
					{
						out.print(" \"");                               // line 41: "
						out.print(lang);                                // line 41: <%lang%>
						out.print("\"");                                // line 41: "
					}
					out.println("*/");                                  // line 42: */
				}
				out.print("public static final ");                      // line 43: public static final
				out.print(idClassSimple);                               // line 43: <%idClassSimple%>
				out.print(" ");                                         // line 43: 
				out.print(constantName);                                // line 43: <%constantName%>
				for (int i=id.length(); i<=20; i++)                     // line 44: @for (int i=id.length(); i<=20; i++)
				{
					out.print(" ");                                     // line 45: 
				}
				out.print(" = ");                                       // line 46: =
				if (hasIdClass)                                         // line 46: <%?hasIdClass%>
				{
					out.print("new ");                                  // line 46: new
					out.print(idClassSimple);                           // line 46: <%idClassSimple%>
					out.print("(");                                     // line 46: (
				}
				out.print("\"");                                        // line 46: "
				out.print(escapeId(id));                                // line 46: <%escapeId(id)%>
				out.print("\"");                                        // line 46: "
				if (hasIdClass)                                         // line 46: <%?hasIdClass%>
				{
					out.print(")");                                     // line 46: )
				}
				out.println(";");                                       // line 46: ;
			}
		}
		if (config.inlineIdClass)                                       // line 47: @if (config.inlineIdClass)
		{
			out.println();
			out.println();
			out.print("public static class ");                          // line 50: public static class
			out.print(idClassSimple);                                   // line 50: <%idClassSimple%>
			out.println(" implements CharSequence");                    // line 50: implements CharSequence
			out.println("{");                                           // line 51: {
			out.increaseTab();
			out.print("public ");                                       // line 52: public
			out.print(idClassSimple);                                   // line 52: <%idClassSimple%>
			out.println("(String value)");                              // line 52: (String value)
			out.println("{");                                           // line 53: {
			out.increaseTab();
			out.println("value_ = value;");                             // line 54: value_ = value;
			out.decreaseTab();
			out.println("}");                                           // line 55: }
			out.println();
			out.println("@Override public int length()");               // line 57: @Override public int length()
			out.println("{");                                           // line 58: {
			out.increaseTab();
			out.println("return value_.length();\");");                 // line 59: return value_.length();");
			out.decreaseTab();
			out.println("}");                                           // line 60: }
			out.println();
			out.println("@Override public char charAt(int index)");     // line 62: @Override public char charAt(int index)
			out.println("{");                                           // line 63: {
			out.increaseTab();
			out.println("return value_.charAt(index);\");");            // line 64: return value_.charAt(index);");
			out.decreaseTab();
			out.println("}");                                           // line 65: }
			out.println();
			out.println("@Override public CharSequence subSequence(int start, int end)"); // line 67: @Override public CharSequence subSequence(int start, int end)
			out.println("{");                                           // line 68: {
			out.increaseTab();
			out.println("return value_.subSequence(start, end);\");");  // line 69: return value_.subSequence(start, end);");
			out.decreaseTab();
			out.println("}");                                           // line 70: }
			out.println();
			out.println("@Override public String toString()");          // line 72: @Override public String toString()
			out.println("{");                                           // line 73: {
			out.increaseTab();
			out.println("return value_;");                              // line 74: return value_;
			out.decreaseTab();
			out.println("}");                                           // line 75: }
			out.println();
			out.println("private String value_;");                      // line 77: private String value_;
			out.decreaseTab();
			out.println("}");                                           // line 78: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 79: }
	}
	
	
	private String getConstantName(String id, StringBuilder s, HashSet<String> usedConstants)
	{
		String constantName = StringUtil.makeJavaName(id, s);
		if (constantName != null)
		{
			if (usedConstants.contains(constantName))
				constantName = null;
			else
				usedConstants.add(constantName);
		}
		return constantName;
	}
	
		
	private String escapeId(String id)
	{
		return id.replace("\\",  "\\\\").replace("\"", "\\\"");
	}


	private ResBundleCompiler.Config config;
	private String packageName;
	private DateTime generationTime;
	private List<Translation> translations;
	protected TemplateWriter out;
}
