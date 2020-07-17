/**
 * Generated from ServerTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resource;


import org.civilian.Application;
import org.civilian.Resource;
import org.civilian.controller.ControllerSignature;
import org.civilian.resource.PathParam;
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
		ctrlRootPackage_ = app.getControllerConfig().getRootPackage();  // line 16: @ctrlRootPackage_ = app.getControllerConfig().getRootPackage();
		out.println("/**");                                             // line 17: /**
		out.print(" * Generated ");                                     // line 18: * Generated
		if (timestamp)                                                  // line 18: <%?timestamp%>
		{
			out.print("at ");                                           // line 18: at
			out.print(new DateTime());                                  // line 18: <%new DateTime()%>
			out.print(" ");                                             // line 18: 
		}
		out.print("by ");                                               // line 18: by
		out.print(ServerConstGenerator.class.getName());                // line 18: <%ServerConstGenerator.class.getName()%>
		out.println(".");                                               // line 18: .
		out.println(" * Do not edit.");                                 // line 19: * Do not edit.
		out.println(" */");                                             // line 20: */
		out.print("package ");                                          // line 21: package
		out.print(outputPackage);                                       // line 21: <%outputPackage%>
		out.println(";");                                               // line 21: ;
		out.println();
		out.println();
		out.print("import ");                                           // line 24: import
		out.print(ControllerSignature.class.getName());                 // line 24: <%ControllerSignature.class.getName()%>
		out.println(";");                                               // line 24: ;
		out.println();
		out.println();
		out.println("/**");                                             // line 27: /**
		out.print(" * Defines the resources of application ");          // line 28: * Defines the resources of application
		out.print(app.getClass().getName());                            // line 28: <%app.getClass().getName()%>
		out.println(".");                                               // line 28: .
		out.println(" */");                                             // line 29: */
		out.print("public interface ");                                 // line 30: public interface
		out.print(outputName);                                          // line 30: <%outputName%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 31: {
		out.increaseTab();
		printResourceComment(root);                                     // line 32: @printResourceComment(root);
		out.println("public static final Root root = new Root();");     // line 33: public static final Root root = new Root();
		printResourceClass(root);                                       // line 34: @printResourceClass(root);
		out.decreaseTab();
		out.println("}");                                               // line 35: }
	}
	
	
	private void printResourceClass(ResourceInfo info)
	{
		// two line spacer                                              // line 41: @// two line spacer
		out.println();
		out.println();
		int childCount = info.getChildCount();                          // line 44: @int childCount = info.getChildCount();
		printResourceComment(info);                                     // line 45: @printResourceComment(info);
		String className = getJavaClass(info);                          // line 46: @String className = getJavaClass(info);
		out.print("public static class ");                              // line 47: public static class
		out.print(className);                                           // line 47: <%className%>
		out.print(" extends ");                                         // line 47: extends
		out.print(Resource.class.getName());                            // line 47: <%Resource.class.getName()%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 48: {
		out.increaseTab();
		out.print("public ");                                           // line 49: public
		out.print(className);                                           // line 49: <%className%>
		out.print("(");                                                 // line 49: (
		if (!info.isRoot())                                             // line 49: <%?!info.isRoot()%>
		{
			out.print(Resource.class.getName());                        // line 49: <%Resource.class.getName()%>
			out.print(" parent");                                       // line 49: parent
		}
		out.println(")");                                               // line 49: )
		out.println("{");                                               // line 50: {
		out.increaseTab();
		if (!info.isRoot())                                             // line 51: @if (!info.isRoot())
		{
			out.print("super(");                                        // line 52: super(
			printCtorArgs(info, false);                                 // line 52: <%printCtorArgs(info, false);%>
			out.println(");");                                          // line 52: );
		}
		if (info.getControllerSignature() != null)                      // line 53: @if (info.getControllerSignature() != null)
		{
			printSetCtrlSeg(info);                                      // line 54: @printSetCtrlSeg(info);
			out.println();
		}
		// print field definitions                                      // line 56: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 57: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 58: @ResourceInfo child = info.getChild(i);
			String field = getJavaField(child);                         // line 59: @String field = getJavaField(child);
			out.print("this.");                                         // line 60: this.
			out.print(field);                                           // line 60: <%field%>
			out.print(" = new ");                                       // line 60: = new
			if (child.getChildCount() == 0)                             // line 61: @if (child.getChildCount() == 0)
			{
				out.print(Resource.class.getName());                    // line 62: <%Resource.class.getName()%>
				out.print("(");                                         // line 62: (
				printCtorArgs(child, true);                             // line 62: <%printCtorArgs(child, true);%>
				out.println(");");                                      // line 62: );
				if (child.getControllerSignature() != null)             // line 63: @if (child.getControllerSignature() != null)
				{
					out.print("this.");                                 // line 64: this.
					out.print(field);                                   // line 64: <%field%>
					out.print(".");                                     // line 64: .
					printSetCtrlSeg(child);                             // line 64: <%printSetCtrlSeg(child);%>
					out.printlnIfNotEmpty();
				}
			}
			else                                                        // line 65: @else
			{
				out.print(getJavaClass(child));                         // line 66: <%getJavaClass(child)%>
				out.println("(this);");                                 // line 66: (this);
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 67: }
		// print field declarations                                     // line 68: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 69: @for (int i=0; i<childCount; i++)
		{
			out.println();
			ResourceInfo child = info.getChild(i);                      // line 71: @ResourceInfo child = info.getChild(i);
			printResourceComment(child);                                // line 72: @printResourceComment(child);
			out.print("public final ");                                 // line 73: public final
			out.print(child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()); // line 73: <%child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()%>
			out.print(" ");                                             // line 73: 
			out.print(getJavaField(child));                             // line 73: <%getJavaField(child)%>
			out.println(";");                                           // line 73: ;
		}
		for (int i=0; i<childCount; i++)                                // line 74: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 75: @ResourceInfo child = info.getChild(i);
			if (child.getChildCount() > 0)                              // line 76: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 77: @printResourceClass(child);
			}
		}
		if (info.isRoot())                                              // line 78: @if (info.isRoot())
		{
			out.println();
			out.println();
			out.println("private static ControllerSignature sig(String subPackage, String className)"); // line 81: private static ControllerSignature sig(String subPackage, String className)
			out.println("{");                                           // line 82: {
			out.increaseTab();
			out.print("return new ControllerSignature(\"");             // line 83: return new ControllerSignature("
			out.print(ctrlRootPackage_);                                // line 83: <%ctrlRootPackage_%>
			out.println("\" + subPackage + '.' + className);");         // line 83: " + subPackage + '.' + className);
			out.decreaseTab();
			out.println("}");                                           // line 84: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 85: }
	}
	
	
	
	private void printResourceComment(ResourceInfo resInfo)
	{
		ControllerSignature ctrlSig = resInfo.getControllerSignature(); // line 92: @ControllerSignature ctrlSig = resInfo.getControllerSignature();
		out.println("/**");                                             // line 93: /**
		out.print(" * \"");                                             // line 94: * "
		out.print(resInfo);                                             // line 94: <%resInfo%>
		out.print("\"");                                                // line 94: "
		if (ctrlSig != null)                                            // line 94: <%?ctrlSig != null%>
		{
			out.print(" -> ");                                          // line 94: ->
			out.print(ctrlSig);                                         // line 94: <%ctrlSig%>
		}
		out.printlnIfNotEmpty();
		out.println(" */");                                             // line 95: */
	}
	
	
	private void printCtorArgs(ResourceInfo info, boolean isChild)
	{
		out.print(isChild ? "this" : "parent");                         // line 101: <%isChild ? "this" : "parent"%>
		out.print(", ");                                                // line 101: ,
		if (info.getSegment() != null)                                  // line 102: @if (info.getSegment() != null)
		{
			out.print("\"");                                            // line 103: "
			out.print(info.getSegment());                               // line 103: <%info.getSegment()%>
			out.print("\"");                                            // line 103: "
		}
		else                                                            // line 104: @else
		{
			out.print(pathParamConst(info.getPathParam()));             // line 105: <%pathParamConst(info.getPathParam())%>
		}
	}
	
	
	private void printSetCtrlSeg(ResourceInfo info)
	{
		ControllerSignature sig = info.getControllerSignature();        // line 111: @ControllerSignature sig = info.getControllerSignature();
		String packageName = ClassUtil.getPackageName(sig.getClassName()); // line 112: @String packageName = ClassUtil.getPackageName(sig.getClassName());
		String simpleName  = ClassUtil.cutPackageName(sig.getClassName()); // line 113: @String simpleName  = ClassUtil.cutPackageName(sig.getClassName());
		String packagePart = packageName.substring(ctrlRootPackage_.length()); // line 114: @String packagePart = packageName.substring(ctrlRootPackage_.length());
		out.print("setControllerSignature(sig(");                       // line 115: setControllerSignature(sig(
		out.print(stringArg(packagePart));                              // line 115: <%stringArg(packagePart)%>
		out.print(", ");                                                // line 115: ,
		out.print(stringArg(simpleName));                               // line 115: <%stringArg(simpleName)%>
		out.print(")");                                                 // line 115: )
		if (sig.getMethodSegment() != null)                             // line 116: @if (sig.getMethodSegment() != null)
		{
			out.print(".withMethodSegment(\"");                         // line 117: .withMethodSegment("
			out.print(sig.getMethodSegment());                          // line 117: <%sig.getMethodSegment()%>
			out.print("\")");                                           // line 117: ")
		}
		else if (sig.getMethodPathParam() != null)                      // line 118: @else if (sig.getMethodPathParam() != null)
		{
			out.print(".withMethodPathParam(");                         // line 119: .withMethodPathParam(
			out.print(pathParamConst(sig.getMethodPathParam()));        // line 119: <%pathParamConst(sig.getMethodPathParam())%>
			out.print(")");                                             // line 119: )
		}
		out.println(");");                                              // line 120: );
	}
	
	
	private String pathParamConst(PathParam<?> param)
	{
		return app.getResourceConfig().getPathParams().getConstant(param);
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
