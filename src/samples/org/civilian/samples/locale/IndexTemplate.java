/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.locale;


import java.time.LocalDate;
import java.util.Locale;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;
import org.civilian.text.service.LocaleServiceList;


public class IndexTemplate extends CspTemplate
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


	@Override protected void exit()
	{
		html = null;
		lang = null;
		super.exit();
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
		LocalDate date = LocalDate.now();                               // line 19: @LocalDate date = LocalDate.now();
		for (Locale locale : locales)                                   // line 20: @for (Locale locale : locales)
		{
			lang.init(services.getService(locale));                     // line 21: @lang.init(services.getService(locale));
			printLocale(locale, date);                                  // line 22: @printLocale(locale, date);
		}
		out.decreaseTab();
		out.println("</table>");                                        // line 23: </table>
		out.decreaseTab();
		out.println("</body>");                                         // line 24: </body>
		out.println("</html>");                                         // line 25: </html>
	}
	
	
	private void printLocale(Locale locale, LocalDate date)
	{
		out.println("<tr>");                                            // line 31: <tr>
		out.increaseTab();
		out.println("<td colspan=\"2\"><hr></td>");                     // line 32: <td colspan="2"><hr></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 33: </tr>
		out.println("<tr>");                                            // line 34: <tr>
		out.increaseTab();
		out.println("<td><b>current locale is</b></td>");               // line 35: <td><b>current locale is</b></td>
		out.print("<td><b>");                                           // line 36: <td><b>
		out.print(locale);                                              // line 36: ^locale
		out.print(", ");                                                // line 36: ,
		out.print(locale.getDisplayName());                             // line 36: <%locale.getDisplayName()%>
		out.println("</b></td>");                                       // line 36: </b></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 37: </tr>
		out.println("<tr>");                                            // line 38: <tr>
		out.increaseTab();
		out.println("<td>number</td>");                                 // line 39: <td>number</td>
		out.print("<td>");                                              // line 40: <td>
		out.print(lang.format(1234567890));                             // line 40: <%lang.format(1234567890)%>
		out.println("</td>");                                           // line 40: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 41: </tr>
		out.println("<tr>");                                            // line 42: <tr>
		out.increaseTab();
		out.println("<td>decimal</td>");                                // line 43: <td>decimal</td>
		out.print("<td>");                                              // line 44: <td>
		out.print(lang.format(334455.6677));                            // line 44: <%lang.format(334455.6677)%>
		out.println("</td>");                                           // line 44: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 45: </tr>
		out.println("<tr>");                                            // line 46: <tr>
		out.increaseTab();
		out.println("<td>date</td>");                                   // line 47: <td>date</td>
		out.print("<td>");                                              // line 48: <td>
		out.print(lang.format(date));                                   // line 48: <%lang.format(date)%>
		out.println("</td>");                                           // line 48: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 49: </tr>
		out.println("<tr>");                                            // line 50: <tr>
		out.increaseTab();
		out.println("<td>translation</td>");                            // line 51: <td>translation</td>
		out.print("<td>");                                              // line 52: <td>
		out.print(lang.msg("civilian"));                                // line 52: <%lang.msg("civilian")%>
		out.println("</td>");                                           // line 52: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 53: </tr>
	}


	protected LocaleServiceList services;
	protected Locale[] locales;
	protected HtmlMixin html;
	protected LangMixin lang;
}
