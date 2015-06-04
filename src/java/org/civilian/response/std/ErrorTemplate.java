/**
 * Generated from ErrorTemplate.csp
 * Do not edit.
 */
package org.civilian.response.std;


import org.civilian.Request;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


/**
* Used by ErrorResponse to print out error information
* during development.
*/
public class ErrorTemplate extends Template
{
	public ErrorTemplate(Request request, int status, String message, Throwable error)
	{
		this.request = request;
		this.status = status;
		this.message = message;
		this.error = error;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 11: <!DOCTYPE html>
		out.println("<html>");                                          // line 12: <html>
		out.println("<head>");                                          // line 13: <head>
		out.increaseTab();
		out.println("<title>Dev Error</title>");                        // line 14: <title>Dev Error</title>
		out.println("<style>");                                         // line 15: <style>
		out.increaseTab();
		out.println("code { font-size: 90% }");                         // line 16: code { font-size: 90% }
		out.println(".stacktrace { padding: 0 0 0 50px; margin: 0 }");  // line 17: .stacktrace { padding: 0 0 0 50px; margin: 0 }
		out.decreaseTab();
		out.println("</style>");                                        // line 18: </style>
		out.decreaseTab();
		out.println("</head>");                                         // line 19: </head>
		out.println("<body>");                                          // line 20: <body>
		out.print("<h1>Civilian Dev Error - ");                         // line 21: <h1>Civilian Dev Error -
		out.print(status);                                              // line 21: <%status%>
		out.println("</h1>");                                           // line 21: </h1>
		out.println("<table>");                                         // line 22: <table>
		printInfo("Message", message);                                  // line 23: @printInfo("Message", message);
		printInfo("URL", request.getMethod() + " " + request.getUrl(false, true).toString()); // line 24: @printInfo("URL", request.getMethod() + " " + request.getUrl(false, true).toString());
		printClass("Application", request.getApplication());            // line 25: @printClass("Application", request.getApplication());
		if (error != null)                                              // line 26: @if (error != null)
		{
			out.println("<tr valign=\"baseline\">");                    // line 27: <tr valign="baseline">
			out.increaseTab();
			out.println("<td>Error</td>");                              // line 28: <td>Error</td>
			out.println("<td>");                                        // line 29: <td>
			out.increaseTab();
			out.println("<code>");                                      // line 30: <code>
			html.stackTrace(error);                                     // line 31: @html.stackTrace(error);
			out.println("</code>");                                     // line 32: </code>
			out.decreaseTab();
			out.println("</td>");                                       // line 33: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 34: </tr>
		}
		out.println("</table>");                                        // line 35: </table>
		out.println("</body>");                                         // line 36: </body>
		out.println("</html>");                                         // line 37: </html>
	}
	
	
	private void printClass(String what, Object item)
	{
		if (item != null)
			printInfo(what, item.getClass().getName());
	}
	
	
	private void printInfo(String what, String text)
	{
		if (text != null)                                               // line 50: @if (text != null)
		{
			out.println("<tr>");                                        // line 51: <tr>
			out.increaseTab();
			out.print("<td>");                                          // line 52: <td>
			out.print(what);                                            // line 52: <%what%>
			out.println("</td>");                                       // line 52: </td>
			out.print("<td>");                                          // line 53: <td>
			html.text(text);                                            // line 53: <%html.text(text);%>
			out.println("</td>");                                       // line 53: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 54: </tr>
		}
	}


	private Request request;
	private int status;
	private String message;
	private Throwable error;
	private HtmlMixin html;
}
