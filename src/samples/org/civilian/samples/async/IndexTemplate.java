/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.async;


import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.HtmlMixin;


public class IndexTemplate extends CspTemplate
{
	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void exit()
	{
		html = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 3: <!DOCTYPE html>
		out.println("<html>");                                          // line 4: <html>
		out.println("<head>");                                          // line 5: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 6: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 7: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 8: @html.linkCss("css/samples.css");
		out.println("<title>Civilian Async Sample</title>");            // line 9: <title>Civilian Async Sample</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 10: </head>
		out.println("<body>");                                          // line 11: <body>
		out.increaseTab();
		out.println("<h1>Async Sample</h1>");                           // line 12: <h1>Async Sample</h1>
		out.print("<a href=\"");                                        // line 13: <a href="
		out.print(html.url(RunnableController.class));                  // line 13: <%html.url(RunnableController.class)%>
		out.println("\" target=\"_blank\">Running async in a new thread</a>."); // line 13: " target="_blank">Running async in a new thread</a>.
		out.decreaseTab();
		out.println("</body>");                                         // line 14: </body>
		out.println("</html>");                                         // line 15: </html>
	}


	protected HtmlMixin html;
}
