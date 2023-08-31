/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.quickstart;


import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


/**
* Template for IndexController.
*/
public class IndexTemplate extends Template
{
	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void exit()
	{
		super.exit();
		html = null;
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 6: <!DOCTYPE html>
		out.println("<html>");                                          // line 7: <html>
		out.increaseTab();
		out.println("<head>");                                          // line 8: <head>
		out.increaseTab();
		out.println("<title>Hello World</title>");                      // line 9: <title>Hello World</title>
		html.linkCss("css/lib/bootstrap.css");                          // line 10: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 11: @html.linkCss("css/samples.css");
		out.decreaseTab();
		out.println("</head>");                                         // line 12: </head>
		out.println("<body>");                                          // line 13: <body>
		out.println("<h1>Hello World</h1>");                            // line 14: <h1>Hello World</h1>
		out.print("<a href=\"");                                        // line 15: <a href="
		out.print(html.url(QsResources.root.users));                    // line 15: <%html.url(QsResources.root.users)%>
		out.println("\">Users</a><br>");                                // line 15: ">Users</a><br>
		out.print("<a href=\"");                                        // line 16: <a href="
		out.print(html.url(QsResources.root.users.$userId).setPathParam(Integer.valueOf(123))); // line 16: <%html.url(QsResources.root.users.$userId).setPathParam(Integer.valueOf(123))%>
		out.println("\">User 123</a><br>");                             // line 16: ">User 123</a><br>
		out.println("</body>");                                         // line 17: </body>
		out.decreaseTab();
		out.println("</html>");                                         // line 18: </html>
	}


	protected HtmlMixin html;
}
