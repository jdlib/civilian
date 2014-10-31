/**
 * Generated from ConstClassTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resbundle;


import java.util.ArrayList;
import java.util.HashSet;
import org.civilian.response.ResponseWriter;
import org.civilian.util.ClassUtil;
import org.civilian.util.DateTime;


class ConstClassTemplate
{
	public ConstClassTemplate(ResBundleCompiler.Config config, String packageName, DateTime generationTime, ArrayList<String> ids)
	{
		this.config = config;
		this.packageName = packageName;
		this.generationTime = generationTime;
		this.ids = ids;
	}


	public synchronized void print(ResponseWriter out)
	{
		try
		{
			this.out = out;
			print();
		}
		finally
		{
			this.out = null;
		}
	}


	protected void print()
	{
		boolean hasIdClass   = config.idClass != null;                  // line 12: @boolean hasIdClass   = config.idClass != null;
		String idClassSimple = hasIdClass ? ClassUtil.cutPackageName(config.idClass) : "String"; // line 13: @String idClassSimple = hasIdClass ? ClassUtil.cutPackageName(config.idClass) : "String";
		HashSet<String> usedConstants = new HashSet<>();                // line 14: @HashSet<String> usedConstants = new HashSet<>();
		StringBuilder s = new StringBuilder();                          // line 15: @StringBuilder s = new StringBuilder();
		//                                                              // line 16: @//
		out.print("package ");                                          // line 17: package
		out.print(packageName);                                         // line 17: <%packageName%>
		out.println(";");                                               // line 17: ;
		out.println();
		out.println();
		if (hasIdClass && !config.inlineIdClass)                        // line 20: @if (hasIdClass && !config.inlineIdClass)
		{
			out.print("import ");                                       // line 21: import
			out.print(config.idClass);                                  // line 21: <%config.idClass%>
			out.println(";");                                           // line 21: ;
			out.println();
			out.println();
		}
		out.println("/**");                                             // line 24: /**
		out.print(" * Contains constants for resource bundle ids in "); // line 25: * Contains constants for resource bundle ids in
		out.print(config.excelFile.getName());                          // line 25: <%config.excelFile.getName()%>
		out.println(".");                                               // line 25: .
		out.print(" * Generated at ");                                  // line 26: * Generated at
		out.print(generationTime);                                      // line 26: <%generationTime%>
		out.println(". Do not edit directly.");                         // line 26: . Do not edit directly.
		out.println(" */");                                             // line 27: */
		out.print("public interface ");                                 // line 28: public interface
		out.print(config.constClass);                                   // line 28: <%config.constClass%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 29: {
		out.increaseTab();
		for (String id : ids)                                           // line 30: @for (String id : ids)
		{
			// javafy key: must be a valid java identifier              // line 31: @// javafy key: must be a valid java identifier
			// escape key when passed as argument to the MsgKey ctor    // line 32: @// escape key when passed as argument to the MsgKey ctor
			// keep a list of javafied keys and add _x suffixes if not unique // line 33: @// keep a list of javafied keys and add _x suffixes if not unique
			String constantName = getConstantName(id, usedConstants, s); // line 34: @String constantName = getConstantName(id, usedConstants, s);
			if (constantName != null)                                   // line 35: @if (constantName != null)
			{
				out.print("public static final ");                      // line 36: public static final
				out.print(idClassSimple);                               // line 36: <%idClassSimple%>
				out.print(" ");                                         // line 36: 
				out.print(constantName);                                // line 36: <%constantName%>
				for (int i=id.length(); i<=20; i++)                     // line 37: @for (int i=id.length(); i<=20; i++)
				{
					out.print(" ");                                     // line 38: 
				}
				out.print(" = ");                                       // line 39: =
				if (hasIdClass)                                         // line 39: <%?hasIdClass%>
				{
					out.print("new ");                                  // line 39: new
					out.print(idClassSimple);                           // line 39: <%idClassSimple%>
					out.print("(");                                     // line 39: (
				}
				out.print("\"");                                        // line 39: "
				out.print(escapeId(id));                                // line 39: <%escapeId(id)%>
				out.print("\"");                                        // line 39: "
				if (hasIdClass)                                         // line 39: <%?hasIdClass%>
				{
					out.print(")");                                     // line 39: )
				}
				out.println(";");                                       // line 39: ;
			}
		}
		if (config.inlineIdClass)                                       // line 40: @if (config.inlineIdClass)
		{
			out.println();
			out.println();
			out.print("public static class ");                          // line 43: public static class
			out.print(idClassSimple);                                   // line 43: <%idClassSimple%>
			out.println(" implements CharSequence");                    // line 43: implements CharSequence
			out.println("{");                                           // line 44: {
			out.increaseTab();
			out.print("public ");                                       // line 45: public
			out.print(idClassSimple);                                   // line 45: <%idClassSimple%>
			out.println("(String value)");                              // line 45: (String value)
			out.println("{");                                           // line 46: {
			out.increaseTab();
			out.println("value_ = value;");                             // line 47: value_ = value;
			out.decreaseTab();
			out.println("}");                                           // line 48: }
			out.println();
			out.println("@Override public int length()");               // line 50: @Override public int length()
			out.println("{");                                           // line 51: {
			out.increaseTab();
			out.println("return value_.length();\");");                 // line 52: return value_.length();");
			out.decreaseTab();
			out.println("}");                                           // line 53: }
			out.println();
			out.println("@Override public char charAt(int index)");     // line 55: @Override public char charAt(int index)
			out.println("{");                                           // line 56: {
			out.increaseTab();
			out.println("return value_.charAt(index);\");");            // line 57: return value_.charAt(index);");
			out.decreaseTab();
			out.println("}");                                           // line 58: }
			out.println();
			out.println("@Override public CharSequence subSequence(int start, int end)"); // line 60: @Override public CharSequence subSequence(int start, int end)
			out.println("{");                                           // line 61: {
			out.increaseTab();
			out.println("return value_.subSequence(start, end);\");");  // line 62: return value_.subSequence(start, end);");
			out.decreaseTab();
			out.println("}");                                           // line 63: }
			out.println();
			out.println("@Override public String toString()");          // line 65: @Override public String toString()
			out.println("{");                                           // line 66: {
			out.increaseTab();
			out.println("return value_;");                              // line 67: return value_;
			out.decreaseTab();
			out.println("}");                                           // line 68: }
			out.println();
			out.println("private String value_;");                      // line 70: private String value_;
			out.decreaseTab();
			out.println("}");                                           // line 71: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 72: }
	}
	
	
	private String getConstantName(String id, HashSet<String> usedConstants, StringBuilder s)
	{
		int length = id.length();
		if (length == 0)
			return null;
		
		if (!Character.isJavaIdentifierStart(id.charAt(0)))
			return null;
		
		s.setLength(0);
		for (int i=0; i<length; i++)
		{
			char c = id.charAt(i);
			if (!Character.isJavaIdentifierPart(c))
				c = '_';
			s.append(c);
		}
		String constantName = s.toString();
		if (usedConstants.contains(constantName))
			return null;
		
		usedConstants.add(constantName);
		return constantName;
	}
	
		
	private String escapeId(String id)
	{
		return id.replace("\\",  "\\\\").replace("\"", "\\\"");
	}


	private ResBundleCompiler.Config config;
	private String packageName;
	private DateTime generationTime;
	private ArrayList<String> ids;
	protected ResponseWriter out;
}
