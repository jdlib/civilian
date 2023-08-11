/**
 * Generated from PageTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.jpa.shared.web;


import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


public class PageTemplate extends Template
{
	public PageTemplate(Template content)
	{
		this.content = content;
	}


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
		html.metaContentType();                                         // line 9: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 10: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 11: @html.linkCss("css/samples.css");
		out.println("<title>Civilian JPA Sample</title>");              // line 12: <title>Civilian JPA Sample</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 13: </head>
		out.println("<body>");                                          // line 14: <body>
		out.increaseTab();
		out.println("<div class=\"container-fluid\">");                 // line 15: <div class="container-fluid">
		out.increaseTab();
		out.println("<div class=\"row-fluid\">");                       // line 16: <div class="row-fluid">
		out.increaseTab();
		out.print(content);                                             // line 17: <%content%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 18: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 19: </div>
		out.decreaseTab();
		out.println("</body>");                                         // line 20: </body>
		out.println("</html>");                                         // line 21: </html>
	}


	protected Template content;
	protected HtmlMixin html;
}
