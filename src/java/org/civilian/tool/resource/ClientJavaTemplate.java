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
		boolean isRoot        = resource.isRoot();                      // line 34: @boolean isRoot        = resource.isRoot();
		String className      = isRoot ? outputName : buildClassName(resource); // line 35: @String className      = isRoot ? outputName : buildClassName(resource);
		boolean hasController = resource.getData() != null;             // line 36: @boolean hasController = resource.getData() != null;
		out.print("public");                                            // line 37: public
		if (!isRoot)                                                    // line 37: <%?!isRoot%>
		{
			out.print(" static");                                       // line 37: static
		}
		out.print(" class ");                                           // line 37: class
		out.print(className);                                           // line 37: <%className%>
		if (hasController)                                              // line 37: <%?hasController%>
		{
			out.print(" extends ");                                     // line 37: extends
			out.print(WEBRESOURCE);                                     // line 37: <%WEBRESOURCE%>
		}
		out.printlnIfNotEmpty();
		out.println("{");                                               // line 38: {
		out.increaseTab();
		out.print("public ");                                           // line 39: public
		out.print(className);                                           // line 39: <%className%>
		if (isRoot)                                                     // line 40: @if (isRoot)
		{
			out.println("(String url)");                                // line 41: (String url)
		}
		else                                                            // line 42: @else
		{
			out.print("(");                                             // line 43: (
			out.print(WEBRESOURCE);                                     // line 43: <%WEBRESOURCE%>
			out.println(" parent)");                                    // line 43: parent)
		}
		out.println("{");                                               // line 44: {
		out.increaseTab();
		if (hasController)                                              // line 45: @if (hasController)
		{
			out.print("super");                                         // line 46: super
		}
		else                                                            // line 47: @else
		{
			out.print("this.resource_ = new ");                         // line 48: this.resource_ = new
			out.print(WEBRESOURCE);                                     // line 48: <%WEBRESOURCE%>
		}
		out.print("(");                                                 // line 49: (
		printCtorArgs(resource, "parent");                              // line 49: <%printCtorArgs(resource, "parent");%>
		out.println(");");                                              // line 49: );
		out.println();
		// print field definitions                                      // line 51: @// print field definitions
		for (Resource child : resource.children())                      // line 52: @for (Resource child : resource.children())
		{
			boolean childHasController = child.getData() != null;       // line 53: @boolean childHasController = child.getData() != null;
			if (!hasController)                                         // line 54: @if (!hasController)
			{
				out.print("this.resource_.");                           // line 55: this.resource_.
			}
			out.print("addChild(");                                     // line 56: addChild(
			if (!childHasController)                                    // line 57: @if (!childHasController)
			{
				out.print("(");                                         // line 58: (
			}
			out.print("this.");                                         // line 59: this.
			out.print(buildFieldName(child));                           // line 59: <%buildFieldName(child)%>
			out.print(" = new ");                                       // line 59: = new
			if (child.getChildCount() == 0)                             // line 60: @if (child.getChildCount() == 0)
			{
				out.print(WEBRESOURCE);                                 // line 61: <%WEBRESOURCE%>
				out.print("(");                                         // line 61: (
				printCtorArgs(child, hasController ? "this" : "this.resource_"); // line 61: <%printCtorArgs(child, hasController ? "this" : "this.resource_");%>
				out.print(")");                                         // line 61: )
			}
			else                                                        // line 62: @else
			{
				out.print(buildClassName(child));                       // line 63: <%buildClassName(child)%>
				out.print("(this)");                                    // line 63: (this)
			}
			if (!childHasController)                                    // line 64: @if (!childHasController)
			{
				out.print(").resource_");                               // line 65: ).resource_
			}
			out.println(");");                                          // line 66: );
		}
		out.decreaseTab();
		out.println("}");                                               // line 67: }
		// print field declarations                                     // line 68: @// print field declarations
		for (Resource child : resource.children())                      // line 69: @for (Resource child : resource.children())
		{
			out.println();
			printResourceComment(child);                                // line 71: @printResourceComment(child);
			out.print("public final ");                                 // line 72: public final
			out.print(child.getChildCount() > 0 ? buildClassName(child) : WEBRESOURCE); // line 72: <%child.getChildCount() > 0 ? buildClassName(child) : WEBRESOURCE%>
			out.print(" ");                                             // line 72: 
			out.print(buildFieldName(child));                           // line 72: <%buildFieldName(child)%>
			out.println(";");                                           // line 72: ;
		}
		for (Resource child : resource.children())                      // line 73: @for (Resource child : resource.children())
		{
			if (child.getChildCount() > 0)                              // line 74: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 75: @printResourceClass(child);
			}
		}
		if (!hasController)                                             // line 76: @if (!hasController)
		{
			out.println();
			out.println("// hidden since not mapped to a controller");  // line 78: // hidden since not mapped to a controller
			out.print("private final ");                                // line 79: private final
			out.print(WEBRESOURCE);                                     // line 79: <%WEBRESOURCE%>
			out.println(" resource_;");                                 // line 79: resource_;
		}
		out.decreaseTab();
		out.println("}");                                               // line 80: }
	}
	
	
	
	private void printResourceComment(Resource resource)
	{
		out.println("/**");                                             // line 87: /**
		out.print(" * Resource \"");                                    // line 88: * Resource "
		out.print(resource);                                            // line 88: <%resource%>
		out.print("\"");                                                // line 88: "
		if (resource.isRoot())                                          // line 88: <%?resource.isRoot()%>
		{
			out.print(" of application ");                              // line 88: of application
			out.print(app.getClass().getName());                        // line 88: <%app.getClass().getName()%>
		}
		out.println(".");                                               // line 88: .
		out.println(" */");                                             // line 89: */
	}
	
	
	private void printCtorArgs(Resource resource, String parent)
	{
		if (!resource.isRoot())                                         // line 95: @if (!resource.isRoot())
		{
			out.print(parent);                                          // line 96: <%parent%>
			out.print(", ");                                            // line 96: ,
			if (resource.getSegment() != null)                          // line 97: @if (resource.getSegment() != null)
			{
				out.print("\"");                                        // line 98: "
				out.print(resource.getSegment());                       // line 98: <%resource.getSegment()%>
				out.print("\"");                                        // line 98: "
			}
			else                                                        // line 99: @else
			{
				out.print(app.getControllerConfig().getPathParams().getConstant(resource.getPathParam())); // line 100: <%app.getControllerConfig().getPathParams().getConstant(resource.getPathParam())%>
			}
		}
		else                                                            // line 101: @else
		{
			out.print("url");                                           // line 102: url
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
