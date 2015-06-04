/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.error;


import org.civilian.resource.Url;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


public class IndexTemplate extends Template
{
	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 6: <!DOCTYPE html>
		out.println("<html>");                                          // line 7: <html>
		out.println("<head>");                                          // line 8: <head>
		out.increaseTab();
		out.println("<title>Civilian Error Sample</title>");            // line 9: <title>Civilian Error Sample</title>
		html.metaContentType();                                         // line 10: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 11: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 12: @html.linkCss("css/samples.css");
		out.decreaseTab();
		out.println("</head>");                                         // line 13: </head>
		out.println("<body>");                                          // line 14: <body>
		out.increaseTab();
		out.println("<div class=\"container-fluid\">");                 // line 15: <div class="container-fluid">
		out.increaseTab();
		out.println("<div class=\"row-fluid\">");                       // line 16: <div class="row-fluid">
		out.increaseTab();
		out.println("<h1>Error Sample</h1>");                           // line 17: <h1>Error Sample</h1>
		out.println("Demonstrates error message for runtime errors.");  // line 18: Demonstrates error message for runtime errors.
		out.println("You may also want to switch the develop flag in <code>civilian.ini</code> to see"); // line 19: You may also want to switch the develop flag in <code>civilian.ini</code> to see
		out.println("different output.");                               // line 20: different output.
		out.println();
		out.println("<h3>Injection error of a controller parameter</h3>"); // line 22: <h3>Injection error of a controller parameter</h3>
		out.println("The expected type of the parameter <code>n</code> is <code>int</code>code> and we pass an invalid value:<p>"); // line 23: The expected type of the parameter <code>n</code> is <code>int</code>code> and we pass an invalid value:<p>
		Url paramUrl = html.url(ParamController.class).addQueryParam("n", "a"); // line 24: @Url paramUrl = html.url(ParamController.class).addQueryParam("n", "a");
		out.print("<a href=\"");                                        // line 25: <a href="
		out.print(paramUrl);                                            // line 25: <%paramUrl%>
		out.print("\">");                                               // line 25: ">
		out.print(paramUrl);                                            // line 25: <%paramUrl%>
		out.println("</a>.");                                           // line 25: </a>.
		out.println();
		out.println("<h3>A Controller throws an exception during request processing</h3>"); // line 27: <h3>A Controller throws an exception during request processing</h3>
		Url failUrl = html.url(FailController.class);                   // line 28: @Url failUrl = html.url(FailController.class);
		out.print("<a href=\"");                                        // line 29: <a href="
		out.print(failUrl);                                             // line 29: <%failUrl%>
		out.print("\">");                                               // line 29: ">
		out.print(failUrl);                                             // line 29: <%failUrl%>
		out.println("</a>.");                                           // line 29: </a>.
		out.println();
		out.println("<h3>Broken Link</h3>");                            // line 31: <h3>Broken Link</h3>
		out.println("We construct a broken link to demonstrate the default not-found error message:<p>"); // line 32: We construct a broken link to demonstrate the default not-found error message:<p>
		Url notFoundUrl = html.url(html.path()).append("notfound");     // line 33: @Url notFoundUrl = html.url(html.path()).append("notfound");
		out.print("<a href=\"");                                        // line 34: <a href="
		out.print(notFoundUrl);                                         // line 34: <%notFoundUrl%>
		out.print("\">");                                               // line 34: ">
		out.print(notFoundUrl);                                         // line 34: <%notFoundUrl%>
		out.println("</a>.");                                           // line 34: </a>.
		out.decreaseTab();
		out.println("</div>");                                          // line 35: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 36: </div>
		out.decreaseTab();
		out.println("</body>");                                         // line 37: </body>
		out.println("</html>");                                         // line 38: </html>
	}


	private HtmlMixin html;
}
