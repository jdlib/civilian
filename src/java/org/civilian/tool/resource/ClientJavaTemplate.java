/**
 * Generated from ClientJavaTemplate.csp
 * Do not edit.
 */
package org.civilian.tool.resource;


import org.civilian.Application;
import org.civilian.Resource;
import org.civilian.client.WebResource;
import org.civilian.util.DateTime;
import org.civilian.util.StringUtil;
import org.civilian.util.TabWriter;


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


	public synchronized void print(TabWriter out)
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
		out.println("/**");                                             // line 13: /**
		out.print(" * Generated ");                                     // line 14: * Generated
		if (timestamp)                                                  // line 14: <%?timestamp%>
		{
			out.print("at ");                                           // line 14: at
			out.print(new DateTime());                                  // line 14: <%new DateTime()%>
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
	
	
	private void printResourceClass(Resource resource)
	{
		if (!resource.isRoot())                                         // line 26: @if (!resource.isRoot())
		{
			// two line spacer                                          // line 27: @// two line spacer
			out.println();
			out.println();
		}
		int childCount = resource.getChildCount();                      // line 30: @int childCount = resource.getChildCount();
		printResourceComment(resource);                                 // line 31: @printResourceComment(resource);
		String className;                                               // line 32: @String className;
		if (resource.isRoot())                                          // line 33: @if (resource.isRoot())
		{
			className = outputName;                                     // line 34: @className = outputName;
			out.print("public class ");                                 // line 35: public class
			out.print(className);                                       // line 35: <%className%>
			out.print(" extends ");                                     // line 35: extends
			out.print(WebResource.class.getName());                     // line 35: <%WebResource.class.getName()%>
			out.printlnIfNotEmpty();
		}
		else                                                            // line 36: @else
		{
			className = buildClassName(resource);                       // line 37: @className = buildClassName(resource);
			out.print("public static class ");                          // line 38: public static class
			out.print(className);                                       // line 38: <%className%>
			out.print(" extends ");                                     // line 38: extends
			out.print(WebResource.class.getName());                     // line 38: <%WebResource.class.getName()%>
			out.printlnIfNotEmpty();
		}
		out.println("{");                                               // line 39: {
		out.increaseTab();
		out.print("public ");                                           // line 40: public
		out.print(className);                                           // line 40: <%className%>
		if (resource.isRoot())                                          // line 41: @if (resource.isRoot())
		{
			out.println("(String url)");                                // line 42: (String url)
		}
		else                                                            // line 43: @else
		{
			out.print("(");                                             // line 44: (
			out.print(WebResource.class.getName());                     // line 44: <%WebResource.class.getName()%>
			out.println(" parent)");                                    // line 44: parent)
		}
		out.println("{");                                               // line 45: {
		out.increaseTab();
		out.print("super(");                                            // line 46: super(
		printCtorArgs(resource, false);                                 // line 46: <%printCtorArgs(resource, false);%>
		out.println(");");                                              // line 46: );
		out.println();
		// print field definitions                                      // line 48: @// print field definitions
		for (int i=0; i<childCount; i++)                                // line 49: @for (int i=0; i<childCount; i++)
		{
			Resource child = resource.getChild(i);                      // line 50: @Resource child = resource.getChild(i);
			out.print("addChild(this.");                                // line 51: addChild(this.
			out.print(buildFieldName(child));                           // line 51: <%buildFieldName(child)%>
			out.print(" = new ");                                       // line 51: = new
			if (child.getChildCount() == 0)                             // line 52: @if (child.getChildCount() == 0)
			{
				out.print(WebResource.class.getName());                 // line 53: <%WebResource.class.getName()%>
				out.print("(");                                         // line 53: (
				printCtorArgs(child, true);                             // line 53: <%printCtorArgs(child, true);%>
				out.println("));");                                     // line 53: ));
			}
			else                                                        // line 54: @else
			{
				out.print(buildClassName(child));                       // line 55: <%buildClassName(child)%>
				out.println("(this));");                                // line 55: (this));
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 56: }
		// print field declarations                                     // line 57: @// print field declarations
		for (int i=0; i<childCount; i++)                                // line 58: @for (int i=0; i<childCount; i++)
		{
			out.println();
			Resource child = resource.getChild(i);                      // line 60: @Resource child = resource.getChild(i);
			printResourceComment(child);                                // line 61: @printResourceComment(child);
			out.print("public final ");                                 // line 62: public final
			out.print(child.getChildCount() > 0 ? buildClassName(child) : WebResource.class.getName()); // line 62: <%child.getChildCount() > 0 ? buildClassName(child) : WebResource.class.getName()%>
			out.print(" ");                                             // line 62: 
			out.print(buildFieldName(child));                           // line 62: <%buildFieldName(child)%>
			out.println(";");                                           // line 62: ;
		}
		for (int i=0; i<childCount; i++)                                // line 63: @for (int i=0; i<childCount; i++)
		{
			Resource child = resource.getChild(i);                      // line 64: @Resource child = resource.getChild(i);
			if (child.getChildCount() > 0)                              // line 65: @if (child.getChildCount() > 0)
			{
				printResourceClass(child);                              // line 66: @printResourceClass(child);
			}
		}
		out.decreaseTab();
		out.println("}");                                               // line 67: }
	}
	
	
	
	private void printResourceComment(Resource resource)
	{
		out.println("/**");                                             // line 74: /**
		out.print(" * Resource \"");                                    // line 75: * Resource "
		out.print(resource);                                            // line 75: <%resource%>
		out.print("\"");                                                // line 75: "
		if (resource.isRoot())                                          // line 75: <%?resource.isRoot()%>
		{
			out.print(" of application ");                              // line 75: of application
			out.print(app.getClass().getName());                        // line 75: <%app.getClass().getName()%>
		}
		out.println(".");                                               // line 75: .
		out.println(" */");                                             // line 76: */
	}
	
	
	private void printCtorArgs(Resource resource, boolean isChild)
	{
		if (!resource.isRoot())                                         // line 82: @if (!resource.isRoot())
		{
			out.print(isChild ? "this" : "parent");                     // line 83: <%isChild ? "this" : "parent"%>
			out.print(", ");                                            // line 83: ,
			if (resource.getSegment() != null)                          // line 84: @if (resource.getSegment() != null)
			{
				out.print("\"");                                        // line 85: "
				out.print(resource.getSegment());                       // line 85: <%resource.getSegment()%>
				out.print("\"");                                        // line 85: "
			}
			else                                                        // line 86: @else
			{
				out.print(app.getResourceConfig().getPathParams().getConstant(resource.getPathParam())); // line 87: <%app.getResourceConfig().getPathParams().getConstant(resource.getPathParam())%>
			}
		}
		else                                                            // line 88: @else
		{
			out.print("url");                                           // line 89: url
		}
	}
	
	
	private String buildClassName(Resource resource)
	{
		return resource.getSegment() != null ? 
			StringUtil.startUpperCase(resource.getSegment()) : 
			"$" + StringUtil.startUpperCase(resource.getPathParam().getName());
	} 
	
	
	private String buildFieldName(Resource resource)
	{
		return resource.getSegment() != null ? 
			StringUtil.startLowerCase(resource.getSegment()) : 
			"$" + StringUtil.startLowerCase(resource.getPathParam().getName());
	} 
	


	private Resource root;
	private String outputPackage;
	private String outputName;
	private Application app;
	private boolean timestamp;
	protected TabWriter out;
}
