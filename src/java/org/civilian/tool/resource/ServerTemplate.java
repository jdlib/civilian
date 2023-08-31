/**
 * Generated from ServerTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resource;


import java.time.LocalDateTime;
import org.civilian.application.Application;
import org.civilian.controller.ControllerResourceData;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.scan.ResourceInfo;
import org.civilian.resource.Resource;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.template.CspWriter;
import org.civilian.util.ClassUtil;
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


	public synchronized void print(CspWriter out)
	{
		if (out == null)
			throw new IllegalArgumentException("out is null");
		this.out = out;
		print();
	}


	protected void print()
	{
		ctrlRootPackage_ = app.getControllerConfig().getRootPackage();  // line 17: @ctrlRootPackage_ = app.getControllerConfig().getRootPackage();
		out.println("/**");                                             // line 18: /**
		out.print(" * Generated ");                                     // line 19: * Generated
		if (timestamp)                                                  // line 19: <%?timestamp%>
		{
			out.print("at ");                                           // line 19: at
			out.print(LocalDateTime.now());                             // line 19: <%LocalDateTime.now()%>
			out.print(" ");                                             // line 19: 
		}
		out.print("by ");                                               // line 19: by
		out.print(ServerConstGenerator.class.getName());                // line 19: <%ServerConstGenerator.class.getName()%>
		out.println(".");                                               // line 19: .
		out.println(" * Do not edit.");                                 // line 20: * Do not edit.
		out.println(" */");                                             // line 21: */
		out.print("package ");                                          // line 22: package
		out.print(outputPackage);                                       // line 22: <%outputPackage%>
		out.println(";");                                               // line 22: ;
		out.println();
		out.println();
		out.print("import ");                                           // line 25: import
		out.print(ControllerSignature.class.getName());                 // line 25: <%ControllerSignature.class.getName()%>
		out.println(";");                                               // line 25: ;
		out.print("import ");                                           // line 26: import
		out.print(ControllerResourceData.class.getName());              // line 26: <%ControllerResourceData.class.getName()%>
		out.println(";");                                               // line 26: ;
		out.println();
		out.println();
		out.println("/**");                                             // line 29: /**
		out.print(" * Defines the resources of application ");          // line 30: * Defines the resources of application
		out.print(app.getClass().getName());                            // line 30: <%app.getClass().getName()%>
		out.println(".");                                               // line 30: .
		out.println(" */");                                             // line 31: */
		out.print("public interface ");                                 // line 32: public interface
		out.print(outputName);                                          // line 32: <%outputName%>
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 33: {
		out.increaseTab();
		printResourceComment(root);                                     // line 34: @printResourceComment(root);
		out.println("public static final Root root = new Root();");     // line 35: public static final Root root = new Root();
		printResourceClass(root);                                       // line 36: @printResourceClass(root);
		out.decreaseTab();
		out.println("}");                                               // line 37: }
	}
	
	
	private void printResourceClass(ResourceInfo info)
	{
		// two line spacer                                              // line 43: @// two line spacer
		out.println();
		out.println();
		int childCount = info.getChildCount();                          // line 46: @int childCount = info.getChildCount();
		printResourceComment(info);                                     // line 47: @printResourceComment(info);
		String className = getJavaClass(info);                          // line 48: @String className = getJavaClass(info);
		boolean hasController = info.getControllerSignature() != null;  // line 49: @boolean hasController = info.getControllerSignature() != null;
		out.print("public static class ");                              // line 50: public static class
		out.print(className);                                           // line 50: <%className%>
		if (hasController)                                              // line 50: <%?hasController%>
		{
			out.print(" extends ");                                     // line 50: extends
			out.print(Resource.class.getName());                        // line 50: <%Resource.class.getName()%>
		}
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 51: {
		out.increaseTab();
		out.print("public ");                                           // line 52: public
		out.print(className);                                           // line 52: <%className%>
		out.print("(");                                                 // line 52: (
		if (!info.isRoot())                                             // line 52: <%?!info.isRoot()%>
		{
			out.print(Resource.class.getName());                        // line 52: <%Resource.class.getName()%>
			out.print(" parent");                                       // line 52: parent
		}
		out.println(")");                                               // line 52: )
		out.println("{");                                               // line 53: {
		out.increaseTab();
		String thisRes = hasController ? "this" : "this.resource_";     // line 54: @String thisRes = hasController ? "this" : "this.resource_";
		if (hasController)                                              // line 55: @if (hasController)
		{
			out.print("super");                                         // line 56: super
		}
		else                                                            // line 57: @else
		{
			out.print(thisRes);                                         // line 58: <%thisRes%>
			out.print(" = new ");                                       // line 58: = new
			out.print(Resource.class.getName());                        // line 58: <%Resource.class.getName()%>
		}
		out.print("(");                                                 // line 59: (
		printCtorArgs(info, "parent");                                  // line 59: <%printCtorArgs(info, "parent");%>
		out.println(");");                                              // line 59: );
		if (hasController)                                              // line 60: @if (hasController)
		{
			printSetCtrlSeg(info);                                      // line 61: @printSetCtrlSeg(info);
			out.println();
		}
		// print field definitions                                      // line 63: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 64: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 65: @ResourceInfo child = info.getChild(i);
			String field = getJavaField(child);                         // line 66: @String field = getJavaField(child);
			out.print("this.");                                         // line 67: this.
			out.print(field);                                           // line 67: <%field%>
			out.print(" = new ");                                       // line 67: = new
			if (child.getChildCount() == 0)                             // line 68: @if (child.getChildCount() == 0)
			{
				out.print(Resource.class.getName());                    // line 69: <%Resource.class.getName()%>
				out.print("(");                                         // line 69: (
				printCtorArgs(child, thisRes);                          // line 69: <%printCtorArgs(child, thisRes);%>
				out.println(");");                                      // line 69: );
				if (child.getControllerSignature() != null)             // line 70: @if (child.getControllerSignature() != null)
				{
					out.print("this.");                                 // line 71: this.
					out.print(field);                                   // line 71: <%field%>
					out.print(".");                                     // line 71: .
					printSetCtrlSeg(child);                             // line 71: <%printSetCtrlSeg(child);%>
					out.printlnIfNotEmpty();
				}
			}
			else                                                        // line 72: @else
			{
				out.print(getJavaClass(child));                         // line 73: <%getJavaClass(child)%>
				out.print("(");                                         // line 73: (
				out.print(thisRes);                                     // line 73: <%thisRes%>
				out.println(");");                                      // line 73: );
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 74: }
		if (!hasController && info.isRoot())                            // line 75: @if (!hasController && info.isRoot())
		{
			out.println();
			out.print("public ");                                       // line 77: public
			out.print(Resource.class.getName());                        // line 77: <%Resource.class.getName()%>
			out.println(" getResource()");                              // line 77: getResource()
			out.println("{");                                           // line 78: {
			out.increaseTab();
			out.println("return resource_;");                           // line 79: return resource_;
			out.decreaseTab();
			out.println("}");                                           // line 80: }
		}
		// print field declarations                                     // line 81: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 82: @for (int i=0; i<childCount; i++)
		{
			out.println();
			ResourceInfo child = info.getChild(i);                      // line 84: @ResourceInfo child = info.getChild(i);
			printResourceComment(child);                                // line 85: @printResourceComment(child);
			out.print("public final ");                                 // line 86: public final
			out.print(child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()); // line 86: <%child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()%>
			out.print(" ");                                             // line 86: 
			out.print(getJavaField(child));                             // line 86: <%getJavaField(child)%>
			out.println(";");                                           // line 86: ;
		}
		if (!hasController)                                             // line 87: @if (!hasController)
		{
			out.println();
			out.println("// hidden since not mapped to a controller");  // line 89: // hidden since not mapped to a controller
			out.print("private final ");                                // line 90: private final
			out.print(Resource.class.getName());                        // line 90: <%Resource.class.getName()%>
			out.println(" resource_;");                                 // line 90: resource_;
		}
		for (int i=0; i<childCount; i++)                                // line 91: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 92: @ResourceInfo child = info.getChild(i);
			if (child.getChildCount() > 0)                              // line 93: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 94: @printResourceClass(child);
			}
		}
		if (info.isRoot())                                              // line 95: @if (info.isRoot())
		{
			out.println();
			out.println();
			out.println("private static ControllerSignature sig(String subPackage, String className)"); // line 98: private static ControllerSignature sig(String subPackage, String className)
			out.println("{");                                           // line 99: {
			out.increaseTab();
			out.print("return new ControllerSignature(\"");             // line 100: return new ControllerSignature("
			out.print(ctrlRootPackage_);                                // line 100: <%ctrlRootPackage_%>
			out.println("\" + subPackage + '.' + className);");         // line 100: " + subPackage + '.' + className);
			out.decreaseTab();
			out.println("}");                                           // line 101: }
			out.println();
			out.println();
			out.println("private static ControllerResourceData data(ControllerSignature sig)"); // line 104: private static ControllerResourceData data(ControllerSignature sig)
			out.println("{");                                           // line 105: {
			out.increaseTab();
			out.println("return new ControllerResourceData(sig);");     // line 106: return new ControllerResourceData(sig);
			out.decreaseTab();
			out.println("}");                                           // line 107: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 108: }
	}
	
	
	
	private void printResourceComment(ResourceInfo resInfo)
	{
		ControllerSignature ctrlSig = resInfo.getControllerSignature(); // line 115: @ControllerSignature ctrlSig = resInfo.getControllerSignature();
		out.println("/**");                                             // line 116: /**
		out.print(" * \"");                                             // line 117: * "
		out.print(resInfo);                                             // line 117: <%resInfo%>
		out.print("\"");                                                // line 117: "
		if (ctrlSig != null)                                            // line 117: <%?ctrlSig != null%>
		{
			out.print(" = ");                                           // line 117: =
			out.print(ctrlSig);                                         // line 117: <%ctrlSig%>
		}
		out.printlnIfNotEmpty();
		out.println(" */");                                             // line 118: */
	}
	
	
	private void printCtorArgs(ResourceInfo info, String parent)
	{
		if (!info.isRoot())                                             // line 124: @if (!info.isRoot())
		{
			out.print(parent);                                          // line 125: <%parent%>
			out.print(", ");                                            // line 125: ,
			if (info.getSegment() != null)                              // line 126: @if (info.getSegment() != null)
			{
				out.print("\"");                                        // line 127: "
				out.print(info.getSegment());                           // line 127: <%info.getSegment()%>
				out.print("\"");                                        // line 127: "
			}
			else                                                        // line 128: @else
			{
				out.print(pathParamConst(info.getPathParam()));         // line 129: <%pathParamConst(info.getPathParam())%>
			}
		}
	}
	
	
	private void printSetCtrlSeg(ResourceInfo info)
	{
		ControllerSignature sig = info.getControllerSignature();        // line 135: @ControllerSignature sig = info.getControllerSignature();
		String packageName = ClassUtil.getPackageName(sig.getClassName()); // line 136: @String packageName = ClassUtil.getPackageName(sig.getClassName());
		String simpleName  = ClassUtil.cutPackageName(sig.getClassName()); // line 137: @String simpleName  = ClassUtil.cutPackageName(sig.getClassName());
		String packagePart = packageName.substring(ctrlRootPackage_.length()); // line 138: @String packagePart = packageName.substring(ctrlRootPackage_.length());
		out.print("setData(data(sig(");                                 // line 139: setData(data(sig(
		out.print(stringArg(packagePart));                              // line 139: <%stringArg(packagePart)%>
		out.print(", ");                                                // line 139: ,
		out.print(stringArg(simpleName));                               // line 139: <%stringArg(simpleName)%>
		out.print(")");                                                 // line 139: )
		if (sig.getMethodSegment() != null)                             // line 140: @if (sig.getMethodSegment() != null)
		{
			out.print(".withMethodSegment(\"");                         // line 141: .withMethodSegment("
			out.print(sig.getMethodSegment());                          // line 141: <%sig.getMethodSegment()%>
			out.print("\")");                                           // line 141: ")
		}
		else if (sig.getMethodPathParam() != null)                      // line 142: @else if (sig.getMethodPathParam() != null)
		{
			out.print(".withMethodPathParam(");                         // line 143: .withMethodPathParam(
			out.print(pathParamConst(sig.getMethodPathParam()));        // line 143: <%pathParamConst(sig.getMethodPathParam())%>
			out.print(")");                                             // line 143: )
		}
		out.println("));");                                             // line 144: ));
	}
	
	
	private String pathParamConst(PathParam<?> param)
	{
		return app.getControllerConfig().getPathParams().getConstant(param);
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


	protected ResourceInfo root;
	protected String outputPackage;
	protected String outputName;
	protected Application app;
	protected boolean timestamp;
	protected CspWriter out;
}
