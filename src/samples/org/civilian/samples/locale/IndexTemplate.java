/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.locale;


import java.util.Date;
import java.util.Locale;
import org.civilian.Template;
import org.civilian.response.ResponseWriter;
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


	@Override public synchronized void print(ResponseWriter out) throws Exception
	{
		try
		{
			html = new HtmlMixin(out);
			lang = new LangMixin(out);
			super.print(out);
		}
		finally
		{
			html = null;
			lang = null;
		}
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 8: <!DOCTYPE html>
		out.println("<html>");                                          // line 9: <html>
		out.println("<head>");                                          // line 10: <head>
		out.increaseTab();
		out.println("<title>Civilian Form Sample</title>");             // line 11: <title>Civilian Form Sample</title>
		html.metaContentType();                                         // line 12: @html.metaContentType();
		out.decreaseTab();
		out.println("</head>");                                         // line 13: </head>
		out.println("<body>");                                          // line 14: <body>
		out.increaseTab();
		out.println("<h1>Locale Sample</h1>");                          // line 15: <h1>Locale Sample</h1>
		out.println("<table>");                                         // line 16: <table>
		out.increaseTab();
		for (Locale locale : locales)                                   // line 17: @for (Locale locale : locales)
		{
			printLocale(locale);                                        // line 18: @printLocale(locale);
		}
		out.decreaseTab();
		out.println("</table>");                                        // line 19: </table>
		out.decreaseTab();
		out.println("</body>");                                         // line 20: </body>
		out.println("</html>");                                         // line 21: </html>
	}
	
	
	private void printLocale(Locale locale)
	{
		out.setLocaleService(services.getService(locale));              // line 27: @out.setLocaleService(services.getService(locale));
		out.println("<tr>");                                            // line 28: <tr>
		out.increaseTab();
		out.println("<td colspan=\"2\"><hr></td>");                     // line 29: <td colspan="2"><hr></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 30: </tr>
		out.println("<tr>");                                            // line 31: <tr>
		out.increaseTab();
		out.println("<td><b>current locale is</b></td>");               // line 32: <td><b>current locale is</b></td>
		out.print("<td><b>");                                           // line 33: <td><b>
		out.print(locale);                                              // line 33: <%locale%>
		out.print(", ");                                                // line 33: ,
		out.print(locale.getDisplayName());                             // line 33: <%locale.getDisplayName()%>
		out.println("</b></td>");                                       // line 33: </b></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 34: </tr>
		out.println("<tr>");                                            // line 35: <tr>
		out.increaseTab();
		out.println("<td>numbers</td>");                                // line 36: <td>numbers</td>
		out.print("<td>");                                              // line 37: <td>
		out.print(lang.format(1234567890));                             // line 37: <%lang.format(1234567890)%>
		out.println("</td>");                                           // line 37: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 38: </tr>
		out.println("<tr>");                                            // line 39: <tr>
		out.increaseTab();
		out.println("<td>decimal</td>");                                // line 40: <td>decimal</td>
		out.print("<td>");                                              // line 41: <td>
		out.print(lang.format(334455.6677));                            // line 41: <%lang.format(334455.6677)%>
		out.println("</td>");                                           // line 41: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 42: </tr>
		out.println("<tr>");                                            // line 43: <tr>
		out.increaseTab();
		out.println("<td>date</td>");                                   // line 44: <td>date</td>
		out.print("<td>");                                              // line 45: <td>
		out.print(lang.format(new Date()));                             // line 45: <%lang.format(new Date())%>
		out.println("</td>");                                           // line 45: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 46: </tr>
		out.println("<tr>");                                            // line 47: <tr>
		out.increaseTab();
		out.println("<td>translation</td>");                            // line 48: <td>translation</td>
		out.print("<td>");                                              // line 49: <td>
		out.print(lang.msg("civilian"));                                // line 49: <%lang.msg("civilian")%>
		out.println("</td>");                                           // line 49: </td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 50: </tr>
	}


	private LocaleServiceList services;
	private Locale[] locales;
	private HtmlMixin html;
	private LangMixin lang;
}
