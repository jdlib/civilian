/**
 * Generated from SettingsTemplate.csp
 * Do not edit.
 */
package org.civilian.application.admin.app;


import java.util.Map;
import org.civilian.application.Application;
import org.civilian.content.ContentSerializer;
import org.civilian.processor.Processor;
import org.civilian.processor.ProcessorList;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.text.service.LocaleServiceList;


public class SettingsTemplate extends CspTemplate
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


	@Override protected void exit()
	{
		html = null;
		super.exit();
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
		out.print(app.getClass().getName());                            // line 19: ^{app.getClass().getName()}
		out.println("</td>");                                           // line 19: </td>
		out.println("<td>org.civilian.Application.getClass()</td>");    // line 20: <td>org.civilian.Application.getClass()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 21: </tr>
		out.println("<tr>");                                            // line 22: <tr>
		out.increaseTab();
		out.println("<td>App Path</td>");                               // line 23: <td>App Path</td>
		out.print("<td><a target=\"_blank\" href=\"");                  // line 24: <td><a target="_blank" href="
		out.print(html.url(app));                                       // line 24: ^{html.url(app)}
		out.print("\">");                                               // line 24: ">
		out.print(app.getPath());                                       // line 24: ^{app.getPath()}
		out.println("</a></td>");                                       // line 24: </a></td>
		out.println("<td>org.civilian.Application.getPath()</td>");     // line 25: <td>org.civilian.Application.getPath()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 26: </tr>
		out.println("<tr>");                                            // line 27: <tr>
		out.increaseTab();
		out.println("<td>Status</td>");                                 // line 28: <td>Status</td>
		out.print("<td>");                                              // line 29: <td>
		out.print(app.getStatus());                                     // line 29: ^{app.getStatus()}
		out.println("</td>");                                           // line 29: </td>
		out.println("<td>org.civilian.Application.getStatus()</td>");   // line 30: <td>org.civilian.Application.getStatus()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 31: </tr>
		out.println("<tr>");                                            // line 32: <tr>
		out.increaseTab();
		out.println("<td>Encoding</td>");                               // line 33: <td>Encoding</td>
		out.print("<td>");                                              // line 34: <td>
		out.print(app.getDefaultEncoding());                            // line 34: ^{app.getDefaultEncoding()}
		out.println("</td>");                                           // line 34: </td>
		out.println("<td>org.civilian.Application.getDefaultEncoding()</td>"); // line 35: <td>org.civilian.Application.getDefaultEncoding()</td>
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
			if (i > 0)                                                  // line 42: ^?{i > 0}
			{
				out.print(", ");                                        // line 42: ,
			}
			out.print(services.getLocale(i));                           // line 42: ^{services.getLocale(i)}
			out.printlnIfNotEmpty();
		}
		out.decreaseTab();
		out.println("</td>");                                           // line 43: </td>
		out.println("<td>org.civilian.Application.getLocaleServices().getLocale(i)</td>"); // line 44: <td>org.civilian.Application.getLocaleServices().getLocale(i)</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 45: </tr>
		out.println("</table>");                                        // line 46: </table>
		printProcessors(app.getProcessors());                           // line 47: @printProcessors(app.getProcessors());
		printContentSerializers(app.getContentSerializers().toMap());   // line 48: @printContentSerializers(app.getContentSerializers().toMap());
	}
	
	
	private void printContentSerializers(Map<String,ContentSerializer> map)
	{
		out.println("<h4>Content Serializers</h4>");                    // line 54: <h4>Content Serializers</h4>
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 55: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 56: <tr>
		out.increaseTab();
		out.println("<th>#</th>");                                      // line 57: <th>#</th>
		out.println("<th>Content Type</th>");                           // line 58: <th>Content Type</th>
		out.println("<th>Class</th>");                                  // line 59: <th>Class</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 60: </tr>
		int i=1;                                                        // line 61: @int i=1;
		for (Map.Entry<String,ContentSerializer> entry : app.getContentSerializers().toMap().entrySet()) // line 62: @for (Map.Entry<String,ContentSerializer> entry : app.getContentSerializers().toMap().entrySet())
		{
			out.println("<tr>");                                        // line 63: <tr>
			out.increaseTab();
			out.print("<td>");                                          // line 64: <td>
			out.print(i++);                                             // line 64: ^{i++}
			out.println("</td>");                                       // line 64: </td>
			out.print("<td>");                                          // line 65: <td>
			out.print(entry.getKey());                                  // line 65: ^{entry.getKey()}
			out.println("</td>");                                       // line 65: </td>
			out.print("<td>");                                          // line 66: <td>
			out.print(entry.getValue().getClass().getName());           // line 66: ^{entry.getValue().getClass().getName()}
			out.println("</td>");                                       // line 66: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 67: </tr>
		}
		out.println("</table>");                                        // line 68: </table>
	}
	
	
	private void printProcessors(ProcessorList processors)
	{
		out.println("<h4>Processor Pipeline</h4>");                     // line 74: <h4>Processor Pipeline</h4>
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 75: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 76: <tr>
		out.increaseTab();
		out.println("<th>#</th>");                                      // line 77: <th>#</th>
		out.println("<th>Processor</th>");                              // line 78: <th>Processor</th>
		out.println("<th>Info</th>");                                   // line 79: <th>Info</th>
		out.println("<th>Class</th>");                                  // line 80: <th>Class</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 81: </tr>
		int i=1;                                                        // line 82: @int i=1;
		for (Processor processor : processors)                          // line 83: @for (Processor processor : processors)
		{
			String[] infos = processor.getInfo().split("\\n");          // line 84: @String[] infos = processor.getInfo().split("\\n");
			out.println("<tr>");                                        // line 85: <tr>
			out.increaseTab();
			out.print("<td>");                                          // line 86: <td>
			out.print(i++);                                             // line 86: ^{i++}
			out.println("</td>");                                       // line 86: </td>
			out.print("<td>");                                          // line 87: <td>
			out.print(processor.getClass().getSimpleName());            // line 87: ^{processor.getClass().getSimpleName()}
			out.println("</td>");                                       // line 87: </td>
			out.println("<td>");                                        // line 88: <td>
			out.increaseTab();
			for (int j=0; j<infos.length; j++)                          // line 89: @for (int j=0; j<infos.length; j++)
			{
				if (j > 0)                                              // line 90: ^?{j > 0}
				{
					out.print("<br>");                                  // line 90: <br>
				}
				html.text(infos[j]);                                    // line 90: ^{html.text(infos[j]);}
				out.printlnIfNotEmpty();
			}
			out.decreaseTab();
			out.println("</td>");                                       // line 91: </td>
			out.print("<td>");                                          // line 92: <td>
			out.print(processor.getClass().getName());                  // line 92: ^{processor.getClass().getName()}
			out.println("</td>");                                       // line 92: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 93: </tr>
		}
		out.println("</table>");                                        // line 94: </table>
	}


	protected Application app;
	protected HtmlMixin html;
}
