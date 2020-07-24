/**
 * Generated from PageTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin;


import org.civilian.Application;
import org.civilian.Resource;
import org.civilian.resource.Url;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


public class PageTemplate extends Template
{
	public PageTemplate(Template content, AdminApp adminApp, Application viewedApp, Resource appResource)
	{
		this.content = content;
		this.adminApp = adminApp;
		this.viewedApp = viewedApp;
		this.appResource = appResource;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 9: <!DOCTYPE html>
		out.println("<html lang=\"en\">");                              // line 10: <html lang="en">
		out.println("<head>");                                          // line 11: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 12: @html.metaContentType();
		html.linkCss("civilian/admin/bootstrap.css");                   // line 13: @html.linkCss("civilian/admin/bootstrap.css");
		html.linkCss("civilian/admin/admin.css");                       // line 14: @html.linkCss("civilian/admin/admin.css");
		out.println("<title>Civilian Admin</title>");                   // line 15: <title>Civilian Admin</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 16: </head>
		out.println("<body>");                                          // line 17: <body>
		out.println("<div class=\"navbar navbar-fixed-top\">");         // line 18: <div class="navbar navbar-fixed-top">
		out.increaseTab();
		out.println("<div class=\"navbar-inner\">");                    // line 19: <div class="navbar-inner">
		out.increaseTab();
		out.println("<div class=\"container\">");                       // line 20: <div class="container">
		out.increaseTab();
		out.println("<div class=\"row\">");                             // line 21: <div class="row">
		out.increaseTab();
		out.println("<div class=\"span10 offset2\">");                  // line 22: <div class="span10 offset2">
		out.increaseTab();
		out.println("<h2><span class=\"title\">civilian</span></h2>");  // line 23: <h2><span class="title">civilian</span></h2>
		out.decreaseTab();
		out.println("</div>");                                          // line 24: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 25: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 26: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 27: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 28: </div>
		out.println("<div class=\"container\">");                       // line 29: <div class="container">
		out.increaseTab();
		out.println("<div class=\"row\">");                             // line 30: <div class="row">
		out.increaseTab();
		out.println("<div class=\"span2\">");                           // line 31: <div class="span2">
		out.increaseTab();
		out.println("<div class=\"well sidebar-nav\">");                // line 32: <div class="well sidebar-nav">
		out.increaseTab();
		out.println("<ul class=\"nav nav-list\">");                     // line 33: <ul class="nav nav-list">
		out.increaseTab();
		printNavItem("Context", html.url(adminApp), viewedApp == null); // line 34: @printNavItem("Context", html.url(adminApp), viewedApp == null);
		out.println("<li class=\"nav-header\">Applications</li>");      // line 35: <li class="nav-header">Applications</li>
		Url appUrl = html.url(appResource);                             // line 36: @Url appUrl = html.url(appResource);
		for (Application app : adminApp.getContext().getApplications()) // line 37: @for (Application app : adminApp.getContext().getApplications())
		{
			appUrl.setPathParam(app.getId());                           // line 38: @appUrl.setPathParam(app.getId());
			printNavItem(app.getId(), appUrl, viewedApp == app);        // line 39: @printNavItem(app.getId(), appUrl, viewedApp == app);
		}
		out.decreaseTab();
		out.println("</ul>");                                           // line 40: </ul>
		out.decreaseTab();
		out.println("</div>");                                          // line 41: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 42: </div>
		out.println("<div class=\"span10\">");                          // line 43: <div class="span10">
		out.increaseTab();
		out.print(content);                                             // line 44: <%content%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 45: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 46: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 47: </div>
		out.println("</body>");                                         // line 48: </body>
		out.println("</html>");                                         // line 49: </html>
	}
	
	
	private void printNavItem(String label, Url url, boolean active)
	{
		out.print("<li");                                               // line 55: <li
		if (active)                                                     // line 55: <%?active%>
		{
			out.print(" class=\"active\"");                             // line 55: class="active"
		}
		out.print("><a href=\"");                                       // line 55: ><a href="
		out.print(url);                                                 // line 55: <%url%>
		out.print("\">");                                               // line 55: ">
		out.print(label);                                               // line 55: <%label%>
		out.println("</a></li>");                                       // line 55: </a></li>
	}


	protected Template content;
	protected AdminApp adminApp;
	protected Application viewedApp;
	protected Resource appResource;
	protected HtmlMixin html;
}
