/**
 * Generated from ServerTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resource;


import org.civilian.Application;
import org.civilian.Resource;
import org.civilian.controller.ControllerSignature;
import org.civilian.resource.scan.ResourceInfo;
import org.civilian.template.TemplateWriter;
import org.civilian.util.ClassUtil;
import org.civilian.util.DateTime;
import org.civilian.util.JavaName;


class ServerTemplate
{
	public ServerTemplate(ResourceInfo root, String outputPackage, String outputName, Application app, boolean timestamp)
	{
		this.root = root;
		this.outputPackage = outputPackage;
		this.outputName = outputName;
		this.app = app;
		this.timestamp = timestamp;
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
		ctrlRootPackage_ = app.getControllerConfig().getRootPackage();  // line 15: @ctrlRootPackage_ = app.getControllerConfig().getRootPackage();
		out.println("/**");                                             // line 16: /**
		out.print(" * Generated ");                                     // line 17: * Generated
		if (timestamp)                                                  // line 17: <%?timestamp%>
		{
			out.print("at ");                                           // line 17: at
			out.print(new DateTime());                                  // line 17: <%new DateTime()%>
			out.print(" ");                                             // line 17: 
		}
		out.print("by ");                                               // line 17: by
		out.print(ServerConstGenerator.class.getName());                // line 17: <%ServerConstGenerator.class.getName()%>
		out.println(".");                                               // line 17: .
		out.println(" * Do not edit.");                                 // line 18: * Do not edit.
		out.println(" */");                                             // line 19: */
		out.print("package ");                                          // line 20: package
		out.print(outputPackage);                                       // line 20: <%outputPackage%>
		out.println(";");                                               // line 20: ;
		out.println();
		out.println();
		out.println("/**");                                             // line 23: /**
		out.print(" * Defines the resources of application ");          // line 24: * Defines the resources of application
		out.print(app.getClass().getName());                            // line 24: <%app.getClass().getName()%>
		out.println(".");                                               // line 24: .
		out.println(" */");                                             // line 25: */
		out.print("public interface ");                                 // line 26: public interface
		out.print(outputName);                                          // line 26: <%outputName%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 27: {
		out.increaseTab();
		printResourceComment(root);                                     // line 28: @printResourceComment(root);
		out.println("public static final Root root = new Root();");     // line 29: public static final Root root = new Root();
		printResourceClass(root);                                       // line 30: @printResourceClass(root);
		out.decreaseTab();
		out.println("}");                                               // line 31: }
	}
	
	
	private void printResourceClass(ResourceInfo info)
	{
		// two line spacer                                              // line 37: @// two line spacer
		out.println();
		out.println();
		int childCount = info.getChildCount();                          // line 40: @int childCount = info.getChildCount();
		printResourceComment(info);                                     // line 41: @printResourceComment(info);
		String className = getJavaClass(info);                          // line 42: @String className = getJavaClass(info);
		out.print("public static class ");                              // line 43: public static class
		out.print(className);                                           // line 43: <%className%>
		out.print(" extends ");                                         // line 43: extends
		out.print(Resource.class.getName());                            // line 43: <%Resource.class.getName()%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 44: {
		out.increaseTab();
		out.print("public ");                                           // line 45: public
		out.print(className);                                           // line 45: <%className%>
		out.print("(");                                                 // line 45: (
		if (!info.isRoot())                                             // line 45: <%?!info.isRoot()%>
		{
			out.print(Resource.class.getName());                        // line 45: <%Resource.class.getName()%>
			out.print(" parent");                                       // line 45: parent
		}
		out.println(")");                                               // line 45: )
		out.println("{");                                               // line 46: {
		out.increaseTab();
		if (!info.isRoot())                                             // line 47: @if (!info.isRoot())
		{
			out.print("super(");                                        // line 48: super(
			printCtorArgs(info, false);                                 // line 48: <%printCtorArgs(info, false);%>
			out.println(");");                                          // line 48: );
		}
		if (info.getControllerSignature() != null)                      // line 49: @if (info.getControllerSignature() != null)
		{
			printSetCtrlSeg(info);                                      // line 50: @printSetCtrlSeg(info);
			out.println();
		}
		// print field definitions                                      // line 52: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 53: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 54: @ResourceInfo child = info.getChild(i);
			String field = getJavaField(child);                         // line 55: @String field = getJavaField(child);
			out.print("this.");                                         // line 56: this.
			out.print(field);                                           // line 56: <%field%>
			out.print(" = new ");                                       // line 56: = new
			if (child.getChildCount() == 0)                             // line 57: @if (child.getChildCount() == 0)
			{
				out.print(Resource.class.getName());                    // line 58: <%Resource.class.getName()%>
				out.print("(");                                         // line 58: (
				printCtorArgs(child, true);                             // line 58: <%printCtorArgs(child, true);%>
				out.println(");");                                      // line 58: );
				if (child.getControllerSignature() != null)             // line 59: @if (child.getControllerSignature() != null)
				{
					out.print("this.");                                 // line 60: this.
					out.print(field);                                   // line 60: <%field%>
					out.print(".");                                     // line 60: .
					printSetCtrlSeg(child);                             // line 60: <%printSetCtrlSeg(child);%>
					out.printlnIfNotEmpty();
				}
			}
			else                                                        // line 61: @else
			{
				out.print(getJavaClass(child));                         // line 62: <%getJavaClass(child)%>
				out.println("(this);");                                 // line 62: (this);
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 63: }
		// print field declarations                                     // line 64: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 65: @for (int i=0; i<childCount; i++)
		{
			out.println();
			ResourceInfo child = info.getChild(i);                      // line 67: @ResourceInfo child = info.getChild(i);
			printResourceComment(child);                                // line 68: @printResourceComment(child);
			out.print("public final ");                                 // line 69: public final
			out.print(child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()); // line 69: <%child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()%>
			out.print(" ");                                             // line 69: 
			out.print(getJavaField(child));                             // line 69: <%getJavaField(child)%>
			out.println(";");                                           // line 69: ;
		}
		for (int i=0; i<childCount; i++)                                // line 70: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 71: @ResourceInfo child = info.getChild(i);
			if (child.getChildCount() > 0)                              // line 72: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 73: @printResourceClass(child);
			}
		}
		if (info.isRoot())                                              // line 74: @if (info.isRoot())
		{
			out.println();
			out.println();
			out.println("private static String cls(String subPackage, String className)"); // line 77: private static String cls(String subPackage, String className)
			out.println("{");                                           // line 78: {
			out.increaseTab();
			out.print("return \"");                                     // line 79: return "
			out.print(ctrlRootPackage_);                                // line 79: <%ctrlRootPackage_%>
			out.println("\" + subPackage + '.' + className;");          // line 79: " + subPackage + '.' + className;
			out.decreaseTab();
			out.println("}");                                           // line 80: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 81: }
	}
	
	
	
	private void printResourceComment(ResourceInfo resInfo)
	{
		ControllerSignature ctrlSig = resInfo.getControllerSignature(); // line 88: @ControllerSignature ctrlSig = resInfo.getControllerSignature();
		out.println("/**");                                             // line 89: /**
		out.print(" * \"");                                             // line 90: * "
		out.print(resInfo);                                             // line 90: <%resInfo%>
		out.print("\"");                                                // line 90: "
		if (ctrlSig != null)                                            // line 90: <%?ctrlSig != null%>
		{
			out.print(" -> ");                                          // line 90: ->
			out.print(ctrlSig);                                         // line 90: <%ctrlSig%>
		}
		out.printlnIfNotEmpty();
		out.println(" */");                                             // line 91: */
	}
	
	
	private void printCtorArgs(ResourceInfo info, boolean isChild)
	{
		out.print(isChild ? "this" : "parent");                         // line 97: <%isChild ? "this" : "parent"%>
		out.print(", ");                                                // line 97: ,
		if (info.getSegment() != null)                                  // line 98: @if (info.getSegment() != null)
		{
			out.print("\"");                                            // line 99: "
			out.print(info.getSegment());                               // line 99: <%info.getSegment()%>
			out.print("\"");                                            // line 99: "
		}
		else                                                            // line 100: @else
		{
			out.print(app.getResourceConfig().getPathParams().getConstant(info.getPathParam())); // line 101: <%app.getResourceConfig().getPathParams().getConstant(info.getPathParam())%>
		}
	}
	
	
	private void printSetCtrlSeg(ResourceInfo info)
	{
		ControllerSignature sig = info.getControllerSignature();        // line 107: @ControllerSignature sig = info.getControllerSignature();
		String packageName = ClassUtil.getPackageName(sig.getClassName()); // line 108: @String packageName = ClassUtil.getPackageName(sig.getClassName());
		String simpleName  = ClassUtil.cutPackageName(sig.getClassName()); // line 109: @String simpleName  = ClassUtil.cutPackageName(sig.getClassName());
		String packagePart = packageName.substring(ctrlRootPackage_.length()); // line 110: @String packagePart = packageName.substring(ctrlRootPackage_.length());
		out.print("setControllerSignature(cls(");                       // line 111: setControllerSignature(cls(
		out.print(stringArg(packagePart));                              // line 111: <%stringArg(packagePart)%>
		out.print(", ");                                                // line 111: ,
		out.print(stringArg(simpleName));                               // line 111: <%stringArg(simpleName)%>
		out.print("), ");                                               // line 111: ),
		out.print(stringArg(sig.getMethodName()));                      // line 111: <%stringArg(sig.getMethodName())%>
		out.println(");");                                              // line 111: );
	}
	
	
	private String stringArg(String s)
	{
		return s == null ? "null" : '"' + s + '"';
	}
	
	
	private String getJavaField(ResourceInfo info)
	{
		if (info.isRoot())
			return "root";
		else if (info.getSegment() != null)
			return javaName_.makeVar(info.getSegment());
		else
			return javaName_.makeParamVar(info.getPathParam().getName());
	}
	
	
	private String getJavaClass(ResourceInfo info)
	{
		if (info.isRoot())
			return "Root";
		else if (info.getSegment() != null)
			return javaName_.makeClass(info.getSegment());
		else
			return javaName_.makeParamClass(info.getPathParam().getName());
	}
	
	
	private String ctrlRootPackage_;
	private final JavaName javaName_ = new JavaName();


	private ResourceInfo root;
	private String outputPackage;
	private String outputName;
	private Application app;
	private boolean timestamp;
	protected TemplateWriter out;
}
