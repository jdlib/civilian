/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.form;


import org.civilian.Template;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;


public class IndexTemplate extends Template
{
	public IndexTemplate(IndexForm form)
	{
		this.form = form;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		formTable = new FormTableMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 3: <!DOCTYPE html>
		out.println("<html>");                                          // line 4: <html>
		out.println("<head>");                                          // line 5: <head>
		out.increaseTab();
		out.println("<title>Civilian Form Sample</title>");             // line 6: <title>Civilian Form Sample</title>
		html.metaContentType();                                         // line 7: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 8: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 9: @html.linkCss("css/samples.css");
		out.decreaseTab();
		out.println("</head>");                                         // line 10: </head>
		out.println("<body>");                                          // line 11: <body>
		out.increaseTab();
		out.println("<div class=\"container-fluid\">");                 // line 12: <div class="container-fluid">
		out.increaseTab();
		out.println("<div class=\"row-fluid\">");                       // line 13: <div class="row-fluid">
		out.increaseTab();
		out.println("<h1>Form Sample</h1>");                            // line 14: <h1>Form Sample</h1>
		form.start(out);                                                // line 15: @form.start(out);
		out.println("<table class=\"table table-form\">");              // line 16: <table class="table table-form">
		out.increaseTab();
		formTable.row(form.lastName);                                   // line 17: @formTable.row(form.lastName);
		formTable.row(form.firstName);                                  // line 18: @formTable.row(form.firstName);
		out.println("<tr>");                                            // line 19: <tr>
		out.increaseTab();
		out.print("<td>");                                              // line 20: <td>
		formTable.label(form.zip);                                      // line 20: <%formTable.label(form.zip);%>
		out.print(", ");                                                // line 20: ,
		formTable.label(form.city);                                     // line 20: <%formTable.label(form.city);%>
		out.println("</td>");                                           // line 20: </td>
		out.print("<td>");                                              // line 21: <td>
		formTable.control(form.zip);                                    // line 21: <%formTable.control(form.zip);%>
		out.print(" ");                                                 // line 21: 
		formTable.control(form.city);                                   // line 21: <%formTable.control(form.city);%>
		out.println("</td>");                                           // line 21: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 22: </tr>
		out.println("<tr>");                                            // line 23: <tr>
		out.increaseTab();
		formTable.labelCell(form.range);                                // line 24: @formTable.labelCell(form.range);
		out.print("<td>");                                              // line 25: <td>
		out.print(form.range.getMin());                                 // line 25: <%form.range.getMin()%>
		out.print(" ");                                                 // line 25: 
		formTable.control(form.range);                                  // line 25: <%formTable.control(form.range);%>
		out.print(" ");                                                 // line 25: 
		out.print(form.range.getMax());                                 // line 25: <%form.range.getMax()%>
		out.println("</td>");                                           // line 25: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 26: </tr>
		formTable.row(form.password);                                   // line 27: @formTable.row(form.password);
		formTable.row(form.newsletter);                                 // line 28: @formTable.row(form.newsletter);
		if (form.email != null)                                         // line 29: @if (form.email != null)
		{
			formTable.row(form.email);                                  // line 30: @formTable.row(form.email);
		}
		formTable.buttonRow(form);                                      // line 31: @formTable.buttonRow(form);
		out.println("<tr>");                                            // line 32: <tr>
		out.increaseTab();
		out.println("<td></td>");                                       // line 33: <td></td>
		out.println("<td>");                                            // line 34: <td>
		out.increaseTab();
		out.println("<br>");                                            // line 35: <br>
		out.print("You clicked OK <span class=\"badge badge-success\">"); // line 36: You clicked OK <span class="badge badge-success">
		out.print(form.counter.getValue());                             // line 36: <%form.counter.getValue()%>
		out.println("</span> times.");                                  // line 36: </span> times.
		out.decreaseTab();
		out.println("</td>");                                           // line 37: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 38: </tr>
		out.decreaseTab();
		out.println("</table>");                                        // line 39: </table>
		form.end(out);                                                  // line 40: @form.end(out);
		out.decreaseTab();
		out.println("</div>");                                          // line 41: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 42: </div>
		out.decreaseTab();
		out.println("</body>");                                         // line 43: </body>
		out.println("</html>");                                         // line 44: </html>
	}


	private IndexForm form;
	private HtmlMixin html;
	private FormTableMixin formTable;
}
