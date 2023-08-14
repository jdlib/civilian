/**
 * Generated from ServerTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resource;


import java.time.LocalDateTime;
import org.civilian.Application;
import org.civilian.controller.ControllerSignature;
import org.civilian.resource.PathParam;
import org.civilian.resource.Resource;
import org.civilian.resource.scan.ResourceInfo;
import org.civilian.template.TemplateWriter;
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
			out.print(LocalDateTime.now());                             // line 18: <%LocalDateTime.now()%>
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
		boolean hasController = info.getControllerSignature() != null;  // line 47: @boolean hasController = info.getControllerSignature() != null;
		out.print("public static class ");                              // line 48: public static class
		out.print(className);                                           // line 48: <%className%>
		if (hasController)                                              // line 48: <%?hasController%>
		{
			out.print(" extends ");                                     // line 48: extends
			out.print(Resource.class.getName());                        // line 48: <%Resource.class.getName()%>
		}
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 49: {
		out.increaseTab();
		out.print("public ");                                           // line 50: public
		out.print(className);                                           // line 50: <%className%>
		out.print("(");                                                 // line 50: (
		if (!info.isRoot())                                             // line 50: <%?!info.isRoot()%>
		{
			out.print(Resource.class.getName());                        // line 50: <%Resource.class.getName()%>
			out.print(" parent");                                       // line 50: parent
		}
		out.println(")");                                               // line 50: )
		out.println("{");                                               // line 51: {
		out.increaseTab();
		String thisRes = hasController ? "this" : "this.resource_";     // line 52: @String thisRes = hasController ? "this" : "this.resource_";
		if (hasController)                                              // line 53: @if (hasController)
		{
			out.print("super");                                         // line 54: super
		}
		else                                                            // line 55: @else
		{
			out.print(thisRes);                                         // line 56: <%thisRes%>
			out.print(" = new ");                                       // line 56: = new
			out.print(Resource.class.getName());                        // line 56: <%Resource.class.getName()%>
		}
		out.print("(");                                                 // line 57: (
		printCtorArgs(info, "parent");                                  // line 57: <%printCtorArgs(info, "parent");%>
		out.println(");");                                              // line 57: );
		if (hasController)                                              // line 58: @if (hasController)
		{
			printSetCtrlSeg(info);                                      // line 59: @printSetCtrlSeg(info);
			out.println();
		}
		// print field definitions                                      // line 61: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 62: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 63: @ResourceInfo child = info.getChild(i);
			String field = getJavaField(child);                         // line 64: @String field = getJavaField(child);
			out.print("this.");                                         // line 65: this.
			out.print(field);                                           // line 65: <%field%>
			out.print(" = new ");                                       // line 65: = new
			if (child.getChildCount() == 0)                             // line 66: @if (child.getChildCount() == 0)
			{
				out.print(Resource.class.getName());                    // line 67: <%Resource.class.getName()%>
				out.print("(");                                         // line 67: (
				printCtorArgs(child, thisRes);                          // line 67: <%printCtorArgs(child, thisRes);%>
				out.println(");");                                      // line 67: );
				if (child.getControllerSignature() != null)             // line 68: @if (child.getControllerSignature() != null)
				{
					out.print("this.");                                 // line 69: this.
					out.print(field);                                   // line 69: <%field%>
					out.print(".");                                     // line 69: .
					printSetCtrlSeg(child);                             // line 69: <%printSetCtrlSeg(child);%>
					out.printlnIfNotEmpty();
				}
			}
			else                                                        // line 70: @else
			{
				out.print(getJavaClass(child));                         // line 71: <%getJavaClass(child)%>
				out.print("(");                                         // line 71: (
				out.print(thisRes);                                     // line 71: <%thisRes%>
				out.println(");");                                      // line 71: );
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 72: }
		if (!hasController && info.isRoot())                            // line 73: @if (!hasController && info.isRoot())
		{
			out.println();
			out.print("public ");                                       // line 75: public
			out.print(Resource.class.getName());                        // line 75: <%Resource.class.getName()%>
			out.println(" getResource()");                              // line 75: getResource()
			out.println("{");                                           // line 76: {
			out.increaseTab();
			out.println("return resource_;");                           // line 77: return resource_;
			out.decreaseTab();
			out.println("}");                                           // line 78: }
		}
		// print field declarations                                     // line 79: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 80: @for (int i=0; i<childCount; i++)
		{
			out.println();
			ResourceInfo child = info.getChild(i);                      // line 82: @ResourceInfo child = info.getChild(i);
			printResourceComment(child);                                // line 83: @printResourceComment(child);
			out.print("public final ");                                 // line 84: public final
			out.print(child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()); // line 84: <%child.getChildCount() > 0 ? getJavaClass(child) : Resource.class.getName()%>
			out.print(" ");                                             // line 84: 
			out.print(getJavaField(child));                             // line 84: <%getJavaField(child)%>
			out.println(";");                                           // line 84: ;
		}
		if (!hasController)                                             // line 85: @if (!hasController)
		{
			out.println();
			out.println("// hidden since not mapped to a controller");  // line 87: // hidden since not mapped to a controller
			out.print("private final ");                                // line 88: private final
			out.print(Resource.class.getName());                        // line 88: <%Resource.class.getName()%>
			out.println(" resource_;");                                 // line 88: resource_;
		}
		for (int i=0; i<childCount; i++)                                // line 89: @for (int i=0; i<childCount; i++)
		{
			ResourceInfo child = info.getChild(i);                      // line 90: @ResourceInfo child = info.getChild(i);
			if (child.getChildCount() > 0)                              // line 91: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 92: @printResourceClass(child);
			}
		}
		if (info.isRoot())                                              // line 93: @if (info.isRoot())
		{
			out.println();
			out.println();
			out.println("private static ControllerSignature sig(String subPackage, String className)"); // line 96: private static ControllerSignature sig(String subPackage, String className)
			out.println("{");                                           // line 97: {
			out.increaseTab();
			out.print("return new ControllerSignature(\"");             // line 98: return new ControllerSignature("
			out.print(ctrlRootPackage_);                                // line 98: <%ctrlRootPackage_%>
			out.println("\" + subPackage + '.' + className);");         // line 98: " + subPackage + '.' + className);
			out.decreaseTab();
			out.println("}");                                           // line 99: }
		}
		out.decreaseTab();
		out.println("}");                                               // line 100: }
	}
	
	
	
	private void printResourceComment(ResourceInfo resInfo)
	{
		ControllerSignature ctrlSig = resInfo.getControllerSignature(); // line 107: @ControllerSignature ctrlSig = resInfo.getControllerSignature();
		out.println("/**");                                             // line 108: /**
		out.print(" * \"");                                             // line 109: * "
		out.print(resInfo);                                             // line 109: <%resInfo%>
		out.print("\"");                                                // line 109: "
		if (ctrlSig != null)                                            // line 109: <%?ctrlSig != null%>
		{
			out.print(" -> ");                                          // line 109: ->
			out.print(ctrlSig);                                         // line 109: <%ctrlSig%>
		}
		out.printlnIfNotEmpty();
		out.println(" */");                                             // line 110: */
	}
	
	
	private void printCtorArgs(ResourceInfo info, String parent)
	{
		if (!info.isRoot())                                             // line 116: @if (!info.isRoot())
		{
			out.print(parent);                                          // line 117: <%parent%>
			out.print(", ");                                            // line 117: ,
			if (info.getSegment() != null)                              // line 118: @if (info.getSegment() != null)
			{
				out.print("\"");                                        // line 119: "
				out.print(info.getSegment());                           // line 119: <%info.getSegment()%>
				out.print("\"");                                        // line 119: "
			}
			else                                                        // line 120: @else
			{
				out.print(pathParamConst(info.getPathParam()));         // line 121: <%pathParamConst(info.getPathParam())%>
			}
		}
	}
	
	
	private void printSetCtrlSeg(ResourceInfo info)
	{
		ControllerSignature sig = info.getControllerSignature();        // line 127: @ControllerSignature sig = info.getControllerSignature();
		String packageName = ClassUtil.getPackageName(sig.getClassName()); // line 128: @String packageName = ClassUtil.getPackageName(sig.getClassName());
		String simpleName  = ClassUtil.cutPackageName(sig.getClassName()); // line 129: @String simpleName  = ClassUtil.cutPackageName(sig.getClassName());
		String packagePart = packageName.substring(ctrlRootPackage_.length()); // line 130: @String packagePart = packageName.substring(ctrlRootPackage_.length());
		out.print("setControllerSignature(sig(");                       // line 131: setControllerSignature(sig(
		out.print(stringArg(packagePart));                              // line 131: <%stringArg(packagePart)%>
		out.print(", ");                                                // line 131: ,
		out.print(stringArg(simpleName));                               // line 131: <%stringArg(simpleName)%>
		out.print(")");                                                 // line 131: )
		if (sig.getMethodSegment() != null)                             // line 132: @if (sig.getMethodSegment() != null)
		{
			out.print(".withMethodSegment(\"");                         // line 133: .withMethodSegment("
			out.print(sig.getMethodSegment());                          // line 133: <%sig.getMethodSegment()%>
			out.print("\")");                                           // line 133: ")
		}
		else if (sig.getMethodPathParam() != null)                      // line 134: @else if (sig.getMethodPathParam() != null)
		{
			out.print(".withMethodPathParam(");                         // line 135: .withMethodPathParam(
			out.print(pathParamConst(sig.getMethodPathParam()));        // line 135: <%pathParamConst(sig.getMethodPathParam())%>
			out.print(")");                                             // line 135: )
		}
		out.println(");");                                              // line 136: );
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
	protected TemplateWriter out;
}
