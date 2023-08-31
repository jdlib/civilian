/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.jpa.shared.web;


import org.civilian.controller.Controller;
import org.civilian.template.Template;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;


public class IndexTemplate extends Template
{
	public IndexTemplate(IndexForm form, String message, Class<? extends Controller> createCtrlClass)
	{
		this.form = form;
		this.message = message;
		this.createCtrlClass = createCtrlClass;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		formTable = new FormTableMixin(out);
	}


	@Override protected void exit()
	{
		html = null;
		formTable = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<h1>JPA Sample</h1>");                             // line 6: <h1>JPA Sample</h1>
		formTable.print(form);                                          // line 7: @formTable.print(form);
		if (message != null)                                            // line 8: @if (message != null)
		{
			out.print("<div>");                                         // line 9: <div>
			html.text(message);                                         // line 9: <%html.text(message);%>
			out.println("</div>");                                      // line 9: </div>
		}
		out.println("<p>");                                             // line 10: <p>
		out.print("<a href=\"");                                        // line 11: <a href="
		out.print(html.url(createCtrlClass));                           // line 11: <%html.url(createCtrlClass)%>
		out.println("\">Create</a>");                                   // line 11: ">Create</a>
	}


	protected IndexForm form;
	protected String message;
	protected Class<? extends Controller> createCtrlClass;
	protected HtmlMixin html;
	protected FormTableMixin formTable;
}
