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
		out.print("import ");                                           // line 23: import
		out.print(ControllerSignature.class.getName());                 // line 23: <%ControllerSignature.class.getName()%>
		out.println(";");                                               // line 23: ;
		out.println();
		out.println();
		out.println("/**");                                             // line 26: /**
		out.print(" * Defines the resources of application ");          // line 27: * Defines the resources of application
		out.print(app.getClass().getName());                            // line 27: <%app.getClass().getName()%>
		out.println(".");                                               // line 27: .
		out.println(" */");                                             // line 28: */
		out.print("public interface ");                                 // line 29: public interface
		out.print(outputName);                                          // line 29: <%outputName%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 30: {
		out.increaseTab();
		printResourceComment(root);                                     // line 31: @printResourceComment(root);
		out.println("public static final Root root = new Root();");     // line 32: public static final Root root = new Root();
		printResourceClass(root);                                       // line 33: @printResourceClass(root);
		out.decreaseTab();
		out.println("}");                                               // line 34: }
	}
	
	
	private void printResourceClass(ResourceInfo info)
	{
		// two line spacer                                              // line 40: @// two line spacer
		out.println();
		out.println();
		int childCount = info.getChildCount();                          // line 43: @int childCount = info.getChildCount();
		printResourceComment(info);                                     // line 44: @printResourceComment(info);
		String className = getJavaClass(info);                          // line 45: @String className = getJavaClass(info);
		out.print("public static class ");                              // line 46: public static class
		out.print(className);                                           // line 46: <%className%>
		out.print(" extends ");                                         // line 46: extends
		out.print(Resource.class.getName());                            // line 46: <%Resource.class.getName()%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 47: {
		out.increaseTab();
		out.print("public ");                                           // line 48: public
		out.print(className);                                           // line 48: <%className%>
		out.print("(");                                                 // line 48: (
		if (!info.isRoot())                                             // line 48: <%?!info.isRoot()%>
		{
			out.print(Resource.class.getName());                        // line 48: <%Resource.class.getName()%>
			out.print(" parent");                                       // line 48: parent
		}
		out.println(")");                                               // line 48: )
		out.println("{");                                               // line 49: {
		out.increaseTab();
		if (!info.isRoot())                                             // line 50: @if (!info.isRoot())
		{
			out.print("super(");                                        // line 51: super(
			printCtorArgs(info, false);                                 // line 51: <%printCtorArgs(info, false);%>
			out.println(");");                                          // line 51: );
		}
		if (info.getControllerSignature() != null)                      // line 52: @if (info.getControllerSignature() != null)
		{
			printSetCtrlSeg(info);                                      // line 53: @printSetCtrlSeg(info);
			out.println();
		}
		// print field definitions                                      // line 55: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 56: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 57: @ResourceInfo child = info.getChild(i);
			String field = getJavaField(child);                         // line 58: @String field = getJavaField(child);
			out.print("this.");                                         // line 59: this.
			out.print(field);                                           // line 59: <%field%>
			out.print(" = new ");                                       // line 59: = new
			if (child.getChildCount() == 0)                             // line 60: @if (child.getChildCount() == 0)
			{
				out.print(Resource.class.getName());                    // line 61: <%Resource.class.getName()%>
				out.print("(");                                         // line 61: (
				printCtorArgs(child, true);                             // line 61: <%printCtorArgs(child, true);%>
				out.println(");");                                      // line 61: );
				if (child.getControllerSignature() != null)             // line 62: @if (child.getControllerSignature() != null)
				{
					out.print("this.");                                 // line 63: this.
					out.print(field);                                   // line 63: <%field%>
					out.print(".");                                     // line 63: .
					printSetCtrlSeg(child);                             // line 63: <%printSetCtrlSeg(child);%>
					out.printlnIfNotEmpty();
				}
			}
			else                                                        // line 64: @else
			{
				out.print(getJavaClass(child));                         // line 65: <%getJavaClass(child)%>
				out.println("(this);");                                 // line 65: (this);
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 66: }
		// print field declarations                                     // line 67: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 68: @for (int i=0; i<childCount; i++)
		{
			out.println();
			ResourceInfo child = info.getChild(i);                      // line 70: @ResourceInfo child = info.getChild(i);
			printResourceComment(child);                                // line 71: @printResourceComment(child);
			out.print("public final ");                                 // line 72: public final
			out.print(child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()); // line 72: <%child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()%>
			out.print(" ");                                             // line 72: 
			out.print(getJavaField(child));                             // line 72: <%getJavaField(child)%>
			out.println(";");                                           // line 72: ;
		}
		for (int i=0; i<childCount; i++)                                // line 73: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 74: @ResourceInfo child = info.getChild(i);
			if (child.getChildCount() > 0)                              // line 75: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 76: @printResourceClass(child);
			}
		}
		if (info.isRoot())                                              // line 77: @if (info.isRoot())
		{
			out.println();
			out.println();
			out.println("private static ControllerSignature sig(String subPackage, String className)"); // line 80: private static ControllerSignature sig(String subPackage, String className)
			out.println("{");                                           // line 81: {
			out.increaseTab();
			out.print("return new ControllerSignature(\"");             // line 82: return new ControllerSignature("
			out.print(ctrlRootPackage_);                                // line 82: <%ctrlRootPackage_%>
			out.println("\" + subPackage + '.' + className);");         // line 82: " + subPackage + '.' + className);
			out.decreaseTab();
			out.println("}");                                           // line 83: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 84: }
	}
	
	
	
	private void printResourceComment(ResourceInfo resInfo)
	{
		ControllerSignature ctrlSig = resInfo.getControllerSignature(); // line 91: @ControllerSignature ctrlSig = resInfo.getControllerSignature();
		out.println("/**");                                             // line 92: /**
		out.print(" * \"");                                             // line 93: * "
		out.print(resInfo);                                             // line 93: <%resInfo%>
		out.print("\"");                                                // line 93: "
		if (ctrlSig != null)                                            // line 93: <%?ctrlSig != null%>
		{
			out.print(" -> ");                                          // line 93: ->
			out.print(ctrlSig);                                         // line 93: <%ctrlSig%>
		}
		out.printlnIfNotEmpty();
		out.println(" */");                                             // line 94: */
	}
	
	
	private void printCtorArgs(ResourceInfo info, boolean isChild)
	{
		out.print(isChild ? "this" : "parent");                         // line 100: <%isChild ? "this" : "parent"%>
		out.print(", ");                                                // line 100: ,
		if (info.getSegment() != null)                                  // line 101: @if (info.getSegment() != null)
		{
			out.print("\"");                                            // line 102: "
			out.print(info.getSegment());                               // line 102: <%info.getSegment()%>
			out.print("\"");                                            // line 102: "
		}
		else                                                            // line 103: @else
		{
			out.print(app.getResourceConfig().getPathParams().getConstant(info.getPathParam())); // line 104: <%app.getResourceConfig().getPathParams().getConstant(info.getPathParam())%>
		}
	}
	
	
	private void printSetCtrlSeg(ResourceInfo info)
	{
		ControllerSignature sig = info.getControllerSignature();        // line 110: @ControllerSignature sig = info.getControllerSignature();
		String packageName = ClassUtil.getPackageName(sig.getClassName()); // line 111: @String packageName = ClassUtil.getPackageName(sig.getClassName());
		String simpleName  = ClassUtil.cutPackageName(sig.getClassName()); // line 112: @String simpleName  = ClassUtil.cutPackageName(sig.getClassName());
		String packagePart = packageName.substring(ctrlRootPackage_.length()); // line 113: @String packagePart = packageName.substring(ctrlRootPackage_.length());
		out.print("setControllerSignature(sig(");                       // line 114: setControllerSignature(sig(
		out.print(stringArg(packagePart));                              // line 114: <%stringArg(packagePart)%>
		out.print(", ");                                                // line 114: ,
		out.print(stringArg(simpleName));                               // line 114: <%stringArg(simpleName)%>
		out.print(")");                                                 // line 114: )
		if (sig.getMethodSegment() != null)                             // line 115: @if (sig.getMethodSegment() != null)
		{
			out.print(".withMethodSegment(\"");                         // line 116: .withMethodSegment("
			out.print(sig.getMethodSegment());                          // line 116: <%sig.getMethodSegment()%>
			out.print("\")");                                           // line 116: ")
		}
		else if (sig.getMethodPathParam() != null)                      // line 117: @else if (sig.getMethodPathParam() != null)
		{
			out.print(".withMethodPathParam(\"");                       // line 118: .withMethodPathParam("
			out.print(sig.getMethodPathParam());                        // line 118: <%sig.getMethodPathParam()%>
			out.print("\")");                                           // line 118: ")
		}
		out.println(");");                                              // line 119: );
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
