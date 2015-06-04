/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.locale;


import java.util.Date;
import java.util.Locale;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;
import org.civilian.text.LocaleServiceList;


public class IndexTemplate extends Template
{
	public IndexTemplate(LocaleServiceList services, Locale... locales)
	{
		this.services = services;
		this.locales = locales;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		lang = new LangMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 8: <!DOCTYPE html>
		out.println("<html>");                                          // line 9: <html>
		out.println("<head>");                                          // line 10: <head>
		out.increaseTab();
		out.println("<title>Civilian Form Sample</title>");             // line 11: <title>Civilian Form Sample</title>
		html.metaContentType();                                         // line 12: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 13: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 14: @html.linkCss("css/samples.css");
		out.decreaseTab();
		out.println("</head>");                                         // line 15: </head>
		out.println("<body>");                                          // line 16: <body>
		out.increaseTab();
		out.println("<h1>Locale Sample</h1>");                          // line 17: <h1>Locale Sample</h1>
		out.println("<table>");                                         // line 18: <table>
		out.increaseTab();
		for (Locale locale : locales)                                   // line 19: @for (Locale locale : locales)
		{
			printLocale(locale);                                        // line 20: @printLocale(locale);
		}
		out.decreaseTab();
		out.println("</table>");                                        // line 21: </table>
		out.decreaseTab();
		out.println("</body>");                                         // line 22: </body>
		out.println("</html>");                                         // line 23: </html>
	}
	
	
	private void printLocale(Locale locale)
	{
		lang.init(services.getService(locale));                         // line 29: @lang.init(services.getService(locale));
		out.println("<tr>");                                            // line 30: <tr>
		out.increaseTab();
		out.println("<td colspan=\"2\"><hr></td>");                     // line 31: <td colspan="2"><hr></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 32: </tr>
		out.println("<tr>");                                            // line 33: <tr>
		out.increaseTab();
		out.println("<td><b>current locale is</b></td>");               // line 34: <td><b>current locale is</b></td>
		out.print("<td><b>");                                           // line 35: <td><b>
		out.print(locale);                                              // line 35: <%locale%>
		out.print(", ");                                                // line 35: ,
		out.print(locale.getDisplayName());                             // line 35: <%locale.getDisplayName()%>
		out.println("</b></td>");                                       // line 35: </b></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 36: </tr>
		out.println("<tr>");                                            // line 37: <tr>
		out.increaseTab();
		out.println("<td>numbers</td>");                                // line 38: <td>numbers</td>
		out.print("<td>");                                              // line 39: <td>
		out.print(lang.format(1234567890));                             // line 39: <%lang.format(1234567890)%>
		out.println("</td>");                                           // line 39: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 40: </tr>
		out.println("<tr>");                                            // line 41: <tr>
		out.increaseTab();
		out.println("<td>decimal</td>");                                // line 42: <td>decimal</td>
		out.print("<td>");                                              // line 43: <td>
		out.print(lang.format(334455.6677));                            // line 43: <%lang.format(334455.6677)%>
		out.println("</td>");                                           // line 43: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 44: </tr>
		out.println("<tr>");                                            // line 45: <tr>
		out.increaseTab();
		out.println("<td>date</td>");                                   // line 46: <td>date</td>
		out.print("<td>");                                              // line 47: <td>
		out.print(lang.format(new Date()));                             // line 47: <%lang.format(new Date())%>
		out.println("</td>");                                           // line 47: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 48: </tr>
		out.println("<tr>");                                            // line 49: <tr>
		out.increaseTab();
		out.println("<td>translation</td>");                            // line 50: <td>translation</td>
		out.print("<td>");                                              // line 51: <td>
		out.print(lang.msg("civilian"));                                // line 51: <%lang.msg("civilian")%>
		out.println("</td>");                                           // line 51: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 52: </tr>
	}


	private LocaleServiceList services;
	private Locale[] locales;
	private HtmlMixin html;
	private LangMixin lang;
}
