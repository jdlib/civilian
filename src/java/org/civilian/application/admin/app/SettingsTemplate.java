/**
 * Generated from SettingsTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin.app;


import java.util.Iterator;

import org.civilian.application.Application;
import org.civilian.processor.Processor;
import org.civilian.processor.ProcessorList;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.text.service.LocaleServiceList;


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
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 10: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 11: <tr>
		out.increaseTab();
		out.println("<th>Parameter</th>");                              // line 12: <th>Parameter</th>
		out.println("<th>Value</th>");                                  // line 13: <th>Value</th>
		out.println("<th>API</th>");                                    // line 14: <th>API</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 15: </tr>
		out.println("<tr>");                                            // line 16: <tr>
		out.increaseTab();
		out.println("<td>App Class</td>");                              // line 17: <td>App Class</td>
		out.print("<td>");                                              // line 18: <td>
		out.print(app.getClass().getName());                            // line 18: <%app.getClass().getName()%>
		out.println("</td>");                                           // line 18: </td>
		out.println("<td>org.civilian.Application.getClass()</td>");    // line 19: <td>org.civilian.Application.getClass()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 20: </tr>
		out.println("<tr>");                                            // line 21: <tr>
		out.increaseTab();
		out.println("<td>App Path</td>");                               // line 22: <td>App Path</td>
		out.print("<td><a target=\"_blank\" href=\"");                  // line 23: <td><a target="_blank" href="
		out.print(html.url(app));                                       // line 23: <%html.url(app)%>
		out.print("\">");                                               // line 23: ">
		out.print(app.getPath());                                       // line 23: <%app.getPath()%>
		out.println("</a></td>");                                       // line 23: </a></td>
		out.println("<td>org.civilian.Application.getPath()</td>");     // line 24: <td>org.civilian.Application.getPath()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 25: </tr>
		out.println("<tr>");                                            // line 26: <tr>
		out.increaseTab();
		out.println("<td>Status</td>");                                 // line 27: <td>Status</td>
		out.print("<td>");                                              // line 28: <td>
		out.print(app.getStatus());                                     // line 28: <%app.getStatus()%>
		out.println("</td>");                                           // line 28: </td>
		out.println("<td>org.civilian.Application.getStatus()</td>");   // line 29: <td>org.civilian.Application.getStatus()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 30: </tr>
		out.println("<tr>");                                            // line 31: <tr>
		out.increaseTab();
		out.println("<td>Encoding</td>");                               // line 32: <td>Encoding</td>
		out.print("<td>");                                              // line 33: <td>
		out.print(app.getDefaultCharEncoding());                        // line 33: <%app.getDefaultCharEncoding()%>
		out.println("</td>");                                           // line 33: </td>
		out.println("<td>org.civilian.Application.getDefaultCharEncoding()</td>"); // line 34: <td>org.civilian.Application.getDefaultCharEncoding()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 35: </tr>
		out.println("<tr>");                                            // line 36: <tr>
		out.increaseTab();
		out.println("<td>Locale</td>");                                 // line 37: <td>Locale</td>
		out.println("<td>");                                            // line 38: <td>
		out.increaseTab();
		LocaleServiceList services = app.getLocaleServices();           // line 39: @LocaleServiceList services = app.getLocaleServices();
		for (int i=0; i<services.size(); i++)                           // line 40: @for (int i=0; i<services.size(); i++)
		{
			if (i > 0)                                                  // line 41: <%?i > 0%>
			{
				out.print(", ");                                        // line 41: ,
			}
			out.print(services.getLocale(i));                           // line 41: <%services.getLocale(i)%>
			out.printlnIfNotEmpty();
		}
		out.decreaseTab();
		out.println("</td>");                                           // line 42: </td>
		out.println("<td>org.civilian.Application.getLocaleServices().getLocale(i)</td>"); // line 43: <td>org.civilian.Application.getLocaleServices().getLocale(i)</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 44: </tr>
		out.println("<tr>");                                            // line 45: <tr>
		out.increaseTab();
		out.println("<td>ContentSerializers</td>");                     // line 46: <td>ContentSerializers</td>
		out.println("<td>");                                            // line 47: <td>
		out.increaseTab();
		Iterator<String> cts = app.getContentSerializerTypes();         // line 48: @Iterator<String> cts = app.getContentSerializerTypes();
		for (int i=0; cts.hasNext(); i++)                               // line 49: @for (int i=0; cts.hasNext(); i++)
		{
			String ct = cts.next();                                     // line 50: @String ct = cts.next();
			if (i > 0)                                                  // line 51: <%?i > 0%>
			{
				out.print("<br>");                                      // line 51: <br>
			}
			out.print(ct);                                              // line 51: <%ct%>
			out.print(": ");                                            // line 51: :
			out.print(app.getContentSerializer(ct).getClass().getName()); // line 51: <%app.getContentSerializer(ct).getClass().getName()%>
			out.printlnIfNotEmpty();
		}
		out.decreaseTab();
		out.println("</td>");                                           // line 52: </td>
		out.println("<td>org.civilian.Application.getContentSerializerTypes()</td>"); // line 53: <td>org.civilian.Application.getContentSerializerTypes()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 54: </tr>
		out.println("</table>");                                        // line 55: </table>
		out.println("<h4>Processor Pipeline</h4>");                     // line 56: <h4>Processor Pipeline</h4>
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 57: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 58: <tr>
		out.increaseTab();
		out.println("<th>#</th>");                                      // line 59: <th>#</th>
		out.println("<th>Processor</th>");                              // line 60: <th>Processor</th>
		out.println("<th>Info</th>");                                   // line 61: <th>Info</th>
		out.println("<th>Class</th>");                                  // line 62: <th>Class</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 63: </tr>
		ProcessorList processors = app.getProcessors();                 // line 64: @ProcessorList processors = app.getProcessors();
		for (int i=0; i<processors.size(); i++)                         // line 65: @for (int i=0; i<processors.size(); i++)
		{
			Processor processor = processors.get(i);                    // line 66: @Processor processor = processors.get(i);
			String[] infos      = processor.getInfo().split("\\n");     // line 67: @String[] infos      = processor.getInfo().split("\\n");
			out.println("<tr>");                                        // line 68: <tr>
			out.increaseTab();
			out.print("<td>");                                          // line 69: <td>
			out.print(i+1);                                             // line 69: <%i+1%>
			out.println("</td>");                                       // line 69: </td>
			out.print("<td>");                                          // line 70: <td>
			out.print(processor.getClass().getSimpleName());            // line 70: <%processor.getClass().getSimpleName()%>
			out.println("</td>");                                       // line 70: </td>
			out.println("<td>");                                        // line 71: <td>
			out.increaseTab();
			for (int j=0; j<infos.length; j++)                          // line 72: @for (int j=0; j<infos.length; j++)
			{
				if (j > 0)                                              // line 73: <%?j > 0%>
				{
					out.print("<br>");                                  // line 73: <br>
				}
				html.text(infos[j]);                                    // line 73: <%html.text(infos[j]);%>
				out.printlnIfNotEmpty();
			}
			out.decreaseTab();
			out.println("</td>");                                       // line 74: </td>
			out.print("<td>");                                          // line 75: <td>
			out.print(processor.getClass().getName());                  // line 75: <%processor.getClass().getName()%>
			out.println("</td>");                                       // line 75: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 76: </tr>
		}
		out.println("</table>");                                        // line 77: </table>
	}


	protected Application app;
	protected HtmlMixin html;
}
