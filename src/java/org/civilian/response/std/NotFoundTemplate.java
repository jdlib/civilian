/**
 * Generated from NotFoundTemplate.csp
 * Do not edit.
 */
package org.civilian.response.std;


import org.civilian.Request;
import org.civilian.Template;


/**
* Used by NotFoundResponse to print out context information
* during development.
*/
public class NotFoundTemplate extends Template
{
	public NotFoundTemplate(Request request)
	{
		this.request = request;
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 10: <!DOCTYPE html>
		out.println("<html>");                                          // line 11: <html>
		out.println("<head>");                                          // line 12: <head>
		out.increaseTab();
		out.println("<title>Dev NotFound</title>");                     // line 13: <title>Dev NotFound</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 14: </head>
		out.println("<body>");                                          // line 15: <body>
		out.increaseTab();
		out.println("<h1>Civilian Dev Not Found - 404</h1>");           // line 16: <h1>Civilian Dev Not Found - 404</h1>
		out.print("<b>request: ");                                      // line 17: <b>request:
		out.print(request.getPath().print());                           // line 17: <%request.getPath().print()%>
		out.println("</b>");                                            // line 17: </b>
		out.decreaseTab();
		out.println("</body>");                                         // line 18: </body>
		out.println("</html>");                                         // line 19: </html>
	}


	private Request request;
}
