/**
 * Generated from SettingsTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin.app;


import java.util.Iterator;
import org.civilian.Application;
import org.civilian.Processor;
import org.civilian.Template;
import org.civilian.content.ContentType;
import org.civilian.processor.ProcessorList;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.text.LocaleServiceList;


public class SettingsTemplate extends Template
{
	public SettingsTemplate(Application app)
	{
		this.app = app;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 11: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 12: <tr>
		out.increaseTab();
		out.println("<th>Parameter</th>");                              // line 13: <th>Parameter</th>
		out.println("<th>Value</th>");                                  // line 14: <th>Value</th>
		out.println("<th>API</th>");                                    // line 15: <th>API</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 16: </tr>
		out.println("<tr>");                                            // line 17: <tr>
		out.increaseTab();
		out.println("<td>App Class</td>");                              // line 18: <td>App Class</td>
		out.print("<td>");                                              // line 19: <td>
		out.print(app.getClass().getName());                            // line 19: <%app.getClass().getName()%>
		out.println("</td>");                                           // line 19: </td>
		out.println("<td>org.civilian.Application.getClass()</td>");    // line 20: <td>org.civilian.Application.getClass()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 21: </tr>
		out.println("<tr>");                                            // line 22: <tr>
		out.increaseTab();
		out.println("<td>App Path</td>");                               // line 23: <td>App Path</td>
		out.print("<td><a target=\"_blank\" href=\"");                  // line 24: <td><a target="_blank" href="
		out.print(html.url(app));                                       // line 24: <%html.url(app)%>
		out.print("\">");                                               // line 24: ">
		out.print(app.getPath());                                       // line 24: <%app.getPath()%>
		out.println("</a></td>");                                       // line 24: </a></td>
		out.println("<td>org.civilian.Application.getPath()</td>");     // line 25: <td>org.civilian.Application.getPath()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 26: </tr>
		out.println("<tr>");                                            // line 27: <tr>
		out.increaseTab();
		out.println("<td>Status</td>");                                 // line 28: <td>Status</td>
		out.print("<td>");                                              // line 29: <td>
		out.print(app.getStatus());                                     // line 29: <%app.getStatus()%>
		out.println("</td>");                                           // line 29: </td>
		out.println("<td>org.civilian.Application.getStatus()</td>");   // line 30: <td>org.civilian.Application.getStatus()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 31: </tr>
		out.println("<tr>");                                            // line 32: <tr>
		out.increaseTab();
		out.println("<td>Encoding</td>");                               // line 33: <td>Encoding</td>
		out.print("<td>");                                              // line 34: <td>
		out.print(app.getEncoding());                                   // line 34: <%app.getEncoding()%>
		out.println("</td>");                                           // line 34: </td>
		out.println("<td>org.civilian.Application.getEncoding()</td>"); // line 35: <td>org.civilian.Application.getEncoding()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 36: </tr>
		out.println("<tr>");                                            // line 37: <tr>
		out.increaseTab();
		out.println("<td>Locale</td>");                                 // line 38: <td>Locale</td>
		out.println("<td>");                                            // line 39: <td>
		out.increaseTab();
		LocaleServiceList services = app.getLocaleServices();           // line 40: @LocaleServiceList services = app.getLocaleServices();
		for (int i=0; i<services.size(); i++)                           // line 41: @for (int i=0; i<services.size(); i++)
		{
			if (i > 0)                                                  // line 42: <%?i > 0%>
			{
				out.print(", ");                                        // line 42: ,
			}
			out.print(services.getLocale(i));                           // line 42: <%services.getLocale(i)%>
			out.printlnIfNotEmpty();
		}
		out.decreaseTab();
		out.println("</td>");                                           // line 43: </td>
		out.println("<td>org.civilian.Application.getLocaleServices().getLocale(i)</td>"); // line 44: <td>org.civilian.Application.getLocaleServices().getLocale(i)</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 45: </tr>
		out.println("<tr>");                                            // line 46: <tr>
		out.increaseTab();
		out.println("<td>ContentSerializers</td>");                     // line 47: <td>ContentSerializers</td>
		out.println("<td>");                                            // line 48: <td>
		out.increaseTab();
		Iterator<ContentType> cts = app.getContentSerializerTypes();    // line 49: @Iterator<ContentType> cts = app.getContentSerializerTypes();
		for (int i=0; cts.hasNext(); i++)                               // line 50: @for (int i=0; cts.hasNext(); i++)
		{
			ContentType ct = cts.next();                                // line 51: @ContentType ct = cts.next();
			if (i > 0)                                                  // line 52: <%?i > 0%>
			{
				out.print("<br>");                                      // line 52: <br>
			}
			out.print(ct);                                              // line 52: <%ct%>
			out.print(": ");                                            // line 52: :
			out.print(app.getContentSerializer(ct).getClass().getName()); // line 52: <%app.getContentSerializer(ct).getClass().getName()%>
			out.printlnIfNotEmpty();
		}
		out.decreaseTab();
		out.println("</td>");                                           // line 53: </td>
		out.println("<td>org.civilian.Application.getContentSerializerTypes()</td>"); // line 54: <td>org.civilian.Application.getContentSerializerTypes()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 55: </tr>
		out.println("</table>");                                        // line 56: </table>
		out.println("<h4>Processor Pipeline</h4>");                     // line 57: <h4>Processor Pipeline</h4>
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 58: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 59: <tr>
		out.increaseTab();
		out.println("<th>#</th>");                                      // line 60: <th>#</th>
		out.println("<th>Processor</th>");                              // line 61: <th>Processor</th>
		out.println("<th>Info</th>");                                   // line 62: <th>Info</th>
		out.println("<th>Class</th>");                                  // line 63: <th>Class</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 64: </tr>
		ProcessorList processors = app.getProcessors();                 // line 65: @ProcessorList processors = app.getProcessors();
		for (int i=0; i<processors.size(); i++)                         // line 66: @for (int i=0; i<processors.size(); i++)
		{
			Processor processor = processors.get(i);                    // line 67: @Processor processor = processors.get(i);
			String[] infos      = processor.getInfo().split("\\n");     // line 68: @String[] infos      = processor.getInfo().split("\\n");
			out.println("<tr>");                                        // line 69: <tr>
			out.increaseTab();
			out.print("<td>");                                          // line 70: <td>
			out.print(i+1);                                             // line 70: <%i+1%>
			out.println("</td>");                                       // line 70: </td>
			out.print("<td>");                                          // line 71: <td>
			out.print(processor.getClass().getSimpleName());            // line 71: <%processor.getClass().getSimpleName()%>
			out.println("</td>");                                       // line 71: </td>
			out.println("<td>");                                        // line 72: <td>
			out.increaseTab();
			for (int j=0; j<infos.length; j++)                          // line 73: @for (int j=0; j<infos.length; j++)
			{
				if (j > 0)                                              // line 74: <%?j > 0%>
				{
					out.print("<br>");                                  // line 74: <br>
				}
				html.text(infos[j]);                                    // line 74: <%html.text(infos[j]);%>
				out.printlnIfNotEmpty();
			}
			out.decreaseTab();
			out.println("</td>");                                       // line 75: </td>
			out.print("<td>");                                          // line 76: <td>
			out.print(processor.getClass().getName());                  // line 76: <%processor.getClass().getName()%>
			out.println("</td>");                                       // line 76: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 77: </tr>
		}
		out.println("</table>");                                        // line 78: </table>
	}


	private Application app;
	private HtmlMixin html;
}
