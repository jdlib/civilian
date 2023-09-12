/**
 * Generated from ConstClassTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resbundle;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import org.civilian.template.CspWriter;
import org.civilian.util.ClassUtil;
import org.civilian.util.JavaName;


class ConstClassTemplate
{
	public ConstClassTemplate(ResBundleCompiler.Config config, String packageName, LocalDateTime generationTime, List<Translation> translations)
	{
		this.config = config;
		this.packageName = packageName;
		this.generationTime = generationTime;
		this.translations = translations;
	}


	public synchronized void print(CspWriter out)
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
		//                                                              // line 16: @//
		out.print("package ");                                          // line 17: package
		out.print(packageName);                                         // line 17: ^packageName
		out.println(";");                                               // line 17: ;
		out.println();
		out.println();
		if (hasIdClass && !config.inlineIdClass)                        // line 20: @if (hasIdClass && !config.inlineIdClass)
		{
			out.print("import ");                                       // line 21: import
			out.print(config.idClass);                                  // line 21: ^{config.idClass}
			out.println(";");                                           // line 21: ;
			out.println();
			out.println();
		}
		out.println("/**");                                             // line 24: /**
		out.print(" * Contains constants for resource bundle ids in "); // line 25: * Contains constants for resource bundle ids in
		out.print(config.excelFile.getName());                          // line 25: ^{config.excelFile.getName()}
		out.println(".");                                               // line 25: .
		out.print(" * Generated at ");                                  // line 26: * Generated at
		out.print(generationTime);                                      // line 26: ^generationTime
		out.println(". Do not edit directly.");                         // line 26: . Do not edit directly.
		out.println(" */");                                             // line 27: */
		out.print("public interface ");                                 // line 28: public interface
		out.print(config.constClass);                                   // line 28: ^{config.constClass}
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 29: {
		out.increaseTab();
		for (Translation t : translations)                              // line 30: @for (Translation t : translations)
		{
			// javafy key: must be a valid java identifier              // line 31: @// javafy key: must be a valid java identifier
			// escape key when passed as argument to the MsgKey ctor    // line 32: @// escape key when passed as argument to the MsgKey ctor
			// keep a list of javafied keys and add _x suffixes if not unique // line 33: @// keep a list of javafied keys and add _x suffixes if not unique
			String id = t.id;                                           // line 34: @String id = t.id;
			String constantName = getConstantName(id, usedConstants);   // line 35: @String constantName = getConstantName(id, usedConstants);
			if (constantName != null)                                   // line 36: @if (constantName != null)
			{
				if (config.javadoc)                                     // line 37: @if (config.javadoc)
				{
					out.print("/**");                                   // line 38: /**
					for (String lang : t.lang)                          // line 39: @for (String lang : t.lang)
					{
						out.print(" \"");                               // line 40: "
						out.print(lang);                                // line 40: ^lang
						out.print("\"");                                // line 40: "
					}
					out.println("*/");                                  // line 41: */
				}
				out.print("public static final ");                      // line 42: public static final
				out.print(idClassSimple);                               // line 42: ^idClassSimple
				out.print(" ");                                         // line 42: 
				out.print(constantName);                                // line 42: ^constantName
				for (int i=id.length(); i<=20; i++)                     // line 43: @for (int i=id.length(); i<=20; i++)
				{
					out.print(" ");                                     // line 44: 
				}
				out.print(" = ");                                       // line 45: =
				if (hasIdClass)                                         // line 45: ^?hasIdClass
				{
					out.print("new ");                                  // line 45: new
					out.print(idClassSimple);                           // line 45: ^idClassSimple
					out.print("(");                                     // line 45: (
				}
				out.print("\"");                                        // line 45: "
				out.print(escapeId(id));                                // line 45: ^{escapeId(id)}
				out.print("\"");                                        // line 45: "
				if (hasIdClass)                                         // line 45: ^?hasIdClass
				{
					out.print(")");                                     // line 45: )
				}
				out.println(";");                                       // line 45: ;
			}
		}
		if (config.inlineIdClass)                                       // line 46: @if (config.inlineIdClass)
		{
			out.println();
			out.println();
			out.print("public static class ");                          // line 49: public static class
			out.print(idClassSimple);                                   // line 49: ^idClassSimple
			out.println(" implements CharSequence");                    // line 49: implements CharSequence
			out.println("{");                                           // line 50: {
			out.increaseTab();
			out.print("public ");                                       // line 51: public
			out.print(idClassSimple);                                   // line 51: ^idClassSimple
			out.println("(String value)");                              // line 51: (String value)
			out.println("{");                                           // line 52: {
			out.increaseTab();
			out.println("value_ = value;");                             // line 53: value_ = value;
			out.decreaseTab();
			out.println("}");                                           // line 54: }
			out.println();
			out.println("@Override public int length()");               // line 56: @Override public int length()
			out.println("{");                                           // line 57: {
			out.increaseTab();
			out.println("return value_.length();\");");                 // line 58: return value_.length();");
			out.decreaseTab();
			out.println("}");                                           // line 59: }
			out.println();
			out.println("@Override public char charAt(int index)");     // line 61: @Override public char charAt(int index)
			out.println("{");                                           // line 62: {
			out.increaseTab();
			out.println("return value_.charAt(index);\");");            // line 63: return value_.charAt(index);");
			out.decreaseTab();
			out.println("}");                                           // line 64: }
			out.println();
			out.println("@Override public CharSequence subSequence(int start, int end)"); // line 66: @Override public CharSequence subSequence(int start, int end)
			out.println("{");                                           // line 67: {
			out.increaseTab();
			out.println("return value_.subSequence(start, end);\");");  // line 68: return value_.subSequence(start, end);");
			out.decreaseTab();
			out.println("}");                                           // line 69: }
			out.println();
			out.println("@Override public String toString()");          // line 71: @Override public String toString()
			out.println("{");                                           // line 72: {
			out.increaseTab();
			out.println("return value_;");                              // line 73: return value_;
			out.decreaseTab();
			out.println("}");                                           // line 74: }
			out.println();
			out.println("private String value_;");                      // line 76: private String value_;
			out.decreaseTab();
			out.println("}");                                           // line 77: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 78: }
	}
	
	
	private String getConstantName(String id, HashSet<String> usedConstants)
	{
		String constantName = javaName_.make(id);
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
	
	
	private final JavaName javaName_ = new JavaName();


	protected ResBundleCompiler.Config config;
	protected String packageName;
	protected LocalDateTime generationTime;
	protected List<Translation> translations;
	protected CspWriter out;
}
