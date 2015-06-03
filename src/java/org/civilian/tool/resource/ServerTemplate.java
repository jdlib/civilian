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
		ctrlRootPackage_ = app.getControllerConfig().getRootPackage();  // line 14: @ctrlRootPackage_ = app.getControllerConfig().getRootPackage();
		out.println("/**");                                             // line 15: /**
		out.print(" * Generated ");                                     // line 16: * Generated
		if (timestamp)                                                  // line 16: <%?timestamp%>
		{
			out.print("at ");                                           // line 16: at
			out.print(new DateTime());                                  // line 16: <%new DateTime()%>
			out.print(" ");                                             // line 16: 
		}
		out.print("by ");                                               // line 16: by
		out.print(ServerConstGenerator.class.getName());                // line 16: <%ServerConstGenerator.class.getName()%>
		out.println(".");                                               // line 16: .
		out.println(" * Do not edit.");                                 // line 17: * Do not edit.
		out.println(" */");                                             // line 18: */
		out.print("package ");                                          // line 19: package
		out.print(outputPackage);                                       // line 19: <%outputPackage%>
		out.println(";");                                               // line 19: ;
		out.println();
		out.println();
		out.println("/**");                                             // line 22: /**
		out.print(" * Defines the resources of application ");          // line 23: * Defines the resources of application
		out.print(app.getClass().getName());                            // line 23: <%app.getClass().getName()%>
		out.println(".");                                               // line 23: .
		out.println(" */");                                             // line 24: */
		out.print("public interface ");                                 // line 25: public interface
		out.print(outputName);                                          // line 25: <%outputName%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 26: {
		out.increaseTab();
		printResourceComment(root);                                     // line 27: @printResourceComment(root);
		out.println("public static final Root root = new Root();");     // line 28: public static final Root root = new Root();
		printResourceClass(root);                                       // line 29: @printResourceClass(root);
		out.decreaseTab();
		out.println("}");                                               // line 30: }
	}
	
	
	private void printResourceClass(ResourceInfo info)
	{
		// two line spacer                                              // line 36: @// two line spacer
		out.println();
		out.println();
		int childCount = info.getChildCount();                          // line 39: @int childCount = info.getChildCount();
		printResourceComment(info);                                     // line 40: @printResourceComment(info);
		out.print("public static class ");                              // line 41: public static class
		out.print(info.getJavaClass());                                 // line 41: <%info.getJavaClass()%>
		out.print(" extends ");                                         // line 41: extends
		out.print(Resource.class.getName());                            // line 41: <%Resource.class.getName()%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 42: {
		out.increaseTab();
		out.print("public ");                                           // line 43: public
		out.print(info.getJavaClass());                                 // line 43: <%info.getJavaClass()%>
		out.print("(");                                                 // line 43: (
		if (!info.isRoot())                                             // line 43: <%?!info.isRoot()%>
		{
			out.print(Resource.class.getName());                        // line 43: <%Resource.class.getName()%>
			out.print(" parent");                                       // line 43: parent
		}
		out.println(")");                                               // line 43: )
		out.println("{");                                               // line 44: {
		out.increaseTab();
		if (!info.isRoot())                                             // line 45: @if (!info.isRoot())
		{
			out.print("super(");                                        // line 46: super(
			printCtorArgs(info, false);                                 // line 46: <%printCtorArgs(info, false);%>
			out.println(");");                                          // line 46: );
		}
		if (info.getControllerSignature() != null)                      // line 47: @if (info.getControllerSignature() != null)
		{
			printSetCtrlSeg(info);                                      // line 48: @printSetCtrlSeg(info);
			out.println();
		}
		// print field definitions                                      // line 50: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 51: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 52: @ResourceInfo child = info.getChild(i);
			String field = child.getJavaField();                        // line 53: @String field = child.getJavaField();
			out.print("this.");                                         // line 54: this.
			out.print(field);                                           // line 54: <%field%>
			out.print(" = new ");                                       // line 54: = new
			if (child.getChildCount() == 0)                             // line 55: @if (child.getChildCount() == 0)
			{
				out.print(Resource.class.getName());                    // line 56: <%Resource.class.getName()%>
				out.print("(");                                         // line 56: (
				printCtorArgs(child, true);                             // line 56: <%printCtorArgs(child, true);%>
				out.println(");");                                      // line 56: );
				if (child.getControllerSignature() != null)             // line 57: @if (child.getControllerSignature() != null)
				{
					out.print("this.");                                 // line 58: this.
					out.print(field);                                   // line 58: <%field%>
					out.print(".");                                     // line 58: .
					printSetCtrlSeg(child);                             // line 58: <%printSetCtrlSeg(child);%>
					out.printlnIfNotEmpty();
				}
			}
			else                                                        // line 59: @else
			{
				out.print(child.getJavaClass());                        // line 60: <%child.getJavaClass()%>
				out.println("(this);");                                 // line 60: (this);
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 61: }
		// print field declarations                                     // line 62: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 63: @for (int i=0; i<childCount; i++)
		{
			out.println();
			ResourceInfo child = info.getChild(i);                      // line 65: @ResourceInfo child = info.getChild(i);
			printResourceComment(child);                                // line 66: @printResourceComment(child);
			out.print("public final ");                                 // line 67: public final
			out.print(child.getChildCount() > 0 ? child.getJavaClass() : Resource.class.getName()); // line 67: <%child.getChildCount() > 0 ? child.getJavaClass() : Resource.class.getName()%>
			out.print(" ");                                             // line 67: 
			out.print(child.getJavaField());                            // line 67: <%child.getJavaField()%>
			out.println(";");                                           // line 67: ;
		}
		for (int i=0; i<childCount; i++)                                // line 68: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 69: @ResourceInfo child = info.getChild(i);
			if (child.getChildCount() > 0)                              // line 70: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 71: @printResourceClass(child);
			}
		}
		if (info.isRoot())                                              // line 72: @if (info.isRoot())
		{
			out.println();
			out.println();
			out.println("private static String cls(String subPackage, String className)"); // line 75: private static String cls(String subPackage, String className)
			out.println("{");                                           // line 76: {
			out.increaseTab();
			out.print("return \"");                                     // line 77: return "
			out.print(ctrlRootPackage_);                                // line 77: <%ctrlRootPackage_%>
			out.println("\" + subPackage + '.' + className;");          // line 77: " + subPackage + '.' + className;
			out.decreaseTab();
			out.println("}");                                           // line 78: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 79: }
	}
	
	
	
	private void printResourceComment(ResourceInfo resInfo)
	{
		String ctrlSig = resInfo.getControllerSignature();              // line 86: @String ctrlSig = resInfo.getControllerSignature();
		out.println("/**");                                             // line 87: /**
		out.print(" * \"");                                             // line 88: * "
		out.print(resInfo);                                             // line 88: <%resInfo%>
		out.print("\"");                                                // line 88: "
		if (ctrlSig != null)                                            // line 88: <%?ctrlSig != null%>
		{
			out.print(" -> ");                                          // line 88: ->
			out.print(ctrlSig);                                         // line 88: <%ctrlSig%>
		}
		out.printlnIfNotEmpty();
		out.println(" */");                                             // line 89: */
	}
	
	
	private void printCtorArgs(ResourceInfo info, boolean isChild)
	{
		out.print(isChild ? "this" : "parent");                         // line 95: <%isChild ? "this" : "parent"%>
		out.print(", ");                                                // line 95: ,
		if (info.getSegment() != null)                                  // line 96: @if (info.getSegment() != null)
		{
			out.print("\"");                                            // line 97: "
			out.print(info.getSegment());                               // line 97: <%info.getSegment()%>
			out.print("\"");                                            // line 97: "
		}
		else                                                            // line 98: @else
		{
			out.print(app.getResourceConfig().getPathParams().getConstant(info.getPathParam())); // line 99: <%app.getResourceConfig().getPathParams().getConstant(info.getPathParam())%>
		}
	}
	
	
	private void printSetCtrlSeg(ResourceInfo info)
	{
		String csig        = info.getControllerSignature();             // line 105: @String csig        = info.getControllerSignature();
		String className   = ControllerSignature.getClassName(csig);    // line 106: @String className   = ControllerSignature.getClassName(csig);
		String methodPath  = ControllerSignature.getMethodFilter(csig); // line 107: @String methodPath  = ControllerSignature.getMethodFilter(csig);
		String packageName = ClassUtil.getPackageName(className);       // line 108: @String packageName = ClassUtil.getPackageName(className);
		String simpleName  = ClassUtil.cutPackageName(className);       // line 109: @String simpleName  = ClassUtil.cutPackageName(className);
		String packagePart = packageName.substring(ctrlRootPackage_.length()); // line 110: @String packagePart = packageName.substring(ctrlRootPackage_.length());
		out.print("setControllerSignature(cls(");                       // line 111: setControllerSignature(cls(
		out.print(stringArg(packagePart));                              // line 111: <%stringArg(packagePart)%>
		out.print(", ");                                                // line 111: ,
		out.print(stringArg(simpleName));                               // line 111: <%stringArg(simpleName)%>
		out.print("), ");                                               // line 111: ),
		out.print(stringArg(methodPath));                               // line 111: <%stringArg(methodPath)%>
		out.println(");");                                              // line 111: );
	}
	
	
	private String stringArg(String s)
	{
		return s == null ? "null" : '"' + s + '"';
	}
	
	
	private String ctrlRootPackage_;


	private ResourceInfo root;
	private String outputPackage;
	private String outputName;
	private Application app;
	private boolean timestamp;
	protected TemplateWriter out;
}
