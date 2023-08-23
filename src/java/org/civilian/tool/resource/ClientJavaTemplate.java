/**
 * Generated from ClientJavaTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resource;


import java.time.LocalDateTime;
import org.civilian.application.Application;
import org.civilian.client.WebResource;
import org.civilian.resource.Resource;
import org.civilian.template.TemplateWriter;
import org.civilian.util.JavaName;


class ClientJavaTemplate
{
	public ClientJavaTemplate(Resource root, String outputPackage, String outputName, Application app, boolean timestamp)
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
		out.println("/**");                                             // line 13: /**
		out.print(" * Generated ");                                     // line 14: * Generated
		if (timestamp)                                                  // line 14: <%?timestamp%>
		{
			out.print("at ");                                           // line 14: at
			out.print(LocalDateTime.now());                             // line 14: <%LocalDateTime.now()%>
			out.print(" ");                                             // line 14: 
		}
		out.print("by ");                                               // line 14: by
		out.print(ClientConstGenerator.class.getName());                // line 14: <%ClientConstGenerator.class.getName()%>
		out.println(".");                                               // line 14: .
		out.println(" * Do not edit.");                                 // line 15: * Do not edit.
		out.println(" */");                                             // line 16: */
		out.print("package ");                                          // line 17: package
		out.print(outputPackage);                                       // line 17: <%outputPackage%>
		out.println(";");                                               // line 17: ;
		out.println();
		out.println();
		printResourceClass(root);                                       // line 20: @printResourceClass(root);
	}
	
	
	private static final String WEBRESOURCE = WebResource.class.getName();
	
	
	private void printResourceClass(Resource resource)
	{
		if (!resource.isRoot())                                         // line 29: @if (!resource.isRoot())
		{
			// two line spacer                                          // line 30: @// two line spacer
			out.println();
			out.println();
		}
		printResourceComment(resource);                                 // line 33: @printResourceComment(resource);
		int childCount        = resource.getChildCount();               // line 34: @int childCount        = resource.getChildCount();
		boolean isRoot        = resource.isRoot();                      // line 35: @boolean isRoot        = resource.isRoot();
		String className      = isRoot ? outputName : buildClassName(resource); // line 36: @String className      = isRoot ? outputName : buildClassName(resource);
		boolean hasController = resource.getData() != null;             // line 37: @boolean hasController = resource.getData() != null;
		out.print("public");                                            // line 38: public
		if (!isRoot)                                                    // line 38: <%?!isRoot%>
		{
			out.print(" static");                                       // line 38: static
		}
		out.print(" class ");                                           // line 38: class
		out.print(className);                                           // line 38: <%className%>
		if (hasController)                                              // line 38: <%?hasController%>
		{
			out.print(" extends ");                                     // line 38: extends
			out.print(WEBRESOURCE);                                     // line 38: <%WEBRESOURCE%>
		}
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 39: {
		out.increaseTab();
		out.print("public ");                                           // line 40: public
		out.print(className);                                           // line 40: <%className%>
		if (isRoot)                                                     // line 41: @if (isRoot)
		{
			out.println("(String url)");                                // line 42: (String url)
		}
		else                                                            // line 43: @else
		{
			out.print("(");                                             // line 44: (
			out.print(WEBRESOURCE);                                     // line 44: <%WEBRESOURCE%>
			out.println(" parent)");                                    // line 44: parent)
		}
		out.println("{");                                               // line 45: {
		out.increaseTab();
		if (hasController)                                              // line 46: @if (hasController)
		{
			out.print("super");                                         // line 47: super
		}
		else                                                            // line 48: @else
		{
			out.print("this.resource_ = new ");                         // line 49: this.resource_ = new
			out.print(WEBRESOURCE);                                     // line 49: <%WEBRESOURCE%>
		}
		out.print("(");                                                 // line 50: (
		printCtorArgs(resource, "parent");                              // line 50: <%printCtorArgs(resource, "parent");%>
		out.println(");");                                              // line 50: );
		out.println();
		// print field definitions                                      // line 52: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 53: @for (int i=0; i<childCount; i++)
		{
			Resource child = resource.getChild(i);                      // line 54: @Resource child = resource.getChild(i);
			boolean childHasController = child.getData() != null;       // line 55: @boolean childHasController = child.getData() != null;
			if (!hasController)                                         // line 56: @if (!hasController)
			{
				out.print("this.resource_.");                           // line 57: this.resource_.
			}
			out.print("addChild(");                                     // line 58: addChild(
			if (!childHasController)                                    // line 59: @if (!childHasController)
			{
				out.print("(");                                         // line 60: (
			}
			out.print("this.");                                         // line 61: this.
			out.print(buildFieldName(child));                           // line 61: <%buildFieldName(child)%>
			out.print(" = new ");                                       // line 61: = new
			if (child.getChildCount() == 0)                             // line 62: @if (child.getChildCount() == 0)
			{
				out.print(WEBRESOURCE);                                 // line 63: <%WEBRESOURCE%>
				out.print("(");                                         // line 63: (
				printCtorArgs(child, hasController ? "this" : "this.resource_"); // line 63: <%printCtorArgs(child, hasController ? "this" : "this.resource_");%>
				out.print(")");                                         // line 63: )
			}
			else                                                        // line 64: @else
			{
				out.print(buildClassName(child));                       // line 65: <%buildClassName(child)%>
				out.print("(this)");                                    // line 65: (this)
			}
			if (!childHasController)                                    // line 66: @if (!childHasController)
			{
				out.print(").resource_");                               // line 67: ).resource_
			}
			out.println(");");                                          // line 68: );
		}
		out.decreaseTab();
		out.println("}");                                               // line 69: }
		// print field declarations                                     // line 70: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 71: @for (int i=0; i<childCount; i++)
		{
			out.println();
			Resource child = resource.getChild(i);                      // line 73: @Resource child = resource.getChild(i);
			printResourceComment(child);                                // line 74: @printResourceComment(child);
			out.print("public final ");                                 // line 75: public final
			out.print(child.getChildCount() > 0 ? buildClassName(child) : WEBRESOURCE); // line 75: <%child.getChildCount() > 0 ? buildClassName(child) : WEBRESOURCE%>
			out.print(" ");                                             // line 75: 
			out.print(buildFieldName(child));                           // line 75: <%buildFieldName(child)%>
			out.println(";");                                           // line 75: ;
		}
		for (int i=0; i<childCount; i++)                                // line 76: @for (int i=0; i<childCount; i++)
		{
			Resource child = resource.getChild(i);                      // line 77: @Resource child = resource.getChild(i);
			if (child.getChildCount() > 0)                              // line 78: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 79: @printResourceClass(child);
			}
		}
		if (!hasController)                                             // line 80: @if (!hasController)
		{
			out.println();
			out.println("// hidden since not mapped to a controller");  // line 82: // hidden since not mapped to a controller
			out.print("private final ");                                // line 83: private final
			out.print(WEBRESOURCE);                                     // line 83: <%WEBRESOURCE%>
			out.println(" resource_;");                                 // line 83: resource_;
		}
		out.decreaseTab();
		out.println("}");                                               // line 84: }
	}
	
	
	
	private void printResourceComment(Resource resource)
	{
		out.println("/**");                                             // line 91: /**
		out.print(" * Resource \"");                                    // line 92: * Resource "
		out.print(resource);                                            // line 92: <%resource%>
		out.print("\"");                                                // line 92: "
		if (resource.isRoot())                                          // line 92: <%?resource.isRoot()%>
		{
			out.print(" of application ");                              // line 92: of application
			out.print(app.getClass().getName());                        // line 92: <%app.getClass().getName()%>
		}
		out.println(".");                                               // line 92: .
		out.println(" */");                                             // line 93: */
	}
	
	
	private void printCtorArgs(Resource resource, String parent)
	{
		if (!resource.isRoot())                                         // line 99: @if (!resource.isRoot())
		{
			out.print(parent);                                          // line 100: <%parent%>
			out.print(", ");                                            // line 100: ,
			if (resource.getSegment() != null)                          // line 101: @if (resource.getSegment() != null)
			{
				out.print("\"");                                        // line 102: "
				out.print(resource.getSegment());                       // line 102: <%resource.getSegment()%>
				out.print("\"");                                        // line 102: "
			}
			else                                                        // line 103: @else
			{
				out.print(app.getControllerConfig().getPathParams().getConstant(resource.getPathParam())); // line 104: <%app.getControllerConfig().getPathParams().getConstant(resource.getPathParam())%>
			}
		}
		else                                                            // line 105: @else
		{
			out.print("url");                                           // line 106: url
		}
	}
	
	
	private String buildClassName(Resource resource)
	{
		return resource.getSegment() != null ?
			javaName_.makeClass(resource.getSegment()) : 
			javaName_.makeParamClass(resource.getPathParam().getName());
	} 
	
	
	private String buildFieldName(Resource resource)
	{
		return resource.getSegment() != null ?
			javaName_.makeVar(resource.getSegment()) : 
			javaName_.makeParamVar(resource.getPathParam().getName());
	} 
	
	
	private final JavaName javaName_ = new JavaName();


	protected Resource root;
	protected String outputPackage;
	protected String outputName;
	protected Application app;
	protected boolean timestamp;
	protected TemplateWriter out;
}
