/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.inject;


import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.HtmlMixin;


public class IndexTemplate extends CspTemplate
{
	public IndexTemplate(String remoteIp, String acceptHeader, Registration registration)
	{
		this.remoteIp = remoteIp;
		this.acceptHeader = acceptHeader;
		this.registration = registration;
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
		out.println("<!DOCTYPE html>");                                 // line 3: <!DOCTYPE html>
		out.println("<html>");                                          // line 4: <html>
		out.println("<head>");                                          // line 5: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 6: @html.metaContentType();
		html.linkCss("css/lib/bootstrap.css");                          // line 7: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/samples.css");                                // line 8: @html.linkCss("css/samples.css");
		out.println("<title>Civilian Inject Sample</title>");           // line 9: <title>Civilian Inject Sample</title>
		out.decreaseTab();
		out.println("</head>");                                         // line 10: </head>
		out.println("<body>");                                          // line 11: <body>
		out.increaseTab();
		out.println("<div class=\"container\">");                       // line 12: <div class="container">
		out.increaseTab();
		out.println("<div class=\"row\">");                             // line 13: <div class="row">
		out.increaseTab();
		out.println("<div class=\"span6 offset3\">");                   // line 14: <div class="span6 offset3">
		out.increaseTab();
		out.println("<h1>Inject Sample</h1>");                          // line 15: <h1>Inject Sample</h1>
		out.println("Demonstrates use of annotations to inject request values into controller method parameters."); // line 16: Demonstrates use of annotations to inject request values into controller method parameters.
		out.println("<p style=\"margin-bottom: 20px\">");               // line 17: <p style="margin-bottom: 20px">
		if (registration == null)                                       // line 18: @if (registration == null)
		{
			out.println("<form method=\"POST\">");                      // line 19: <form method="POST">
			out.increaseTab();
			printInput("lastName", "Last Name", "text");                // line 20: @printInput("lastName", "Last Name", "text");
			printInput("firstName", "First Name", "text");              // line 21: @printInput("firstName", "First Name", "text");
			printInput("email", "Email", "email");                      // line 22: @printInput("email", "Email", "email");
			printInput("age", "Age", "number");                         // line 23: @printInput("age", "Age", "number");
			out.println("<div class=\"control-group\">");               // line 24: <div class="control-group">
			out.increaseTab();
			out.println("<div class=\"controls\">");                    // line 25: <div class="controls">
			out.increaseTab();
			out.println("<button type=\"submit\" class=\"btn\">Register</button>"); // line 26: <button type="submit" class="btn">Register</button>
			out.decreaseTab();
			out.println("</div>");                                      // line 27: </div>
			out.decreaseTab();
			out.println("</div>");                                      // line 28: </div>
			out.decreaseTab();
			out.println("</form>");                                     // line 29: </form>
		}
		else                                                            // line 30: @else
		{
			out.println("<table class=\"table\">");                     // line 31: <table class="table">
			out.println("<tr>");                                        // line 32: <tr>
			out.increaseTab();
			out.println("<td>Your IP</td>");                            // line 33: <td>Your IP</td>
			out.print("<td>");                                          // line 34: <td>
			html.text(remoteIp);                                        // line 34: ^{html.text(remoteIp);}
			out.println("</td>");                                       // line 34: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 35: </tr>
			out.println("<tr>");                                        // line 36: <tr>
			out.increaseTab();
			out.println("<td>Accept-Header</td>");                      // line 37: <td>Accept-Header</td>
			out.print("<td>");                                          // line 38: <td>
			html.text(acceptHeader);                                    // line 38: ^{html.text(acceptHeader);}
			out.println("</td>");                                       // line 38: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 39: </tr>
			out.println("<tr>");                                        // line 40: <tr>
			out.increaseTab();
			out.println("<td>Last Name</td>");                          // line 41: <td>Last Name</td>
			out.print("<td>");                                          // line 42: <td>
			html.text(registration.getLastName());                      // line 42: ^{html.text(registration.getLastName());}
			out.println("</td>");                                       // line 42: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 43: </tr>
			out.println("<tr>");                                        // line 44: <tr>
			out.increaseTab();
			out.println("<td>First Name</td>");                         // line 45: <td>First Name</td>
			out.print("<td>");                                          // line 46: <td>
			html.text(registration.getFirstName());                     // line 46: ^{html.text(registration.getFirstName());}
			out.println("</td>");                                       // line 46: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 47: </tr>
			out.println("<tr>");                                        // line 48: <tr>
			out.increaseTab();
			out.println("<td>Email</td>");                              // line 49: <td>Email</td>
			out.print("<td>");                                          // line 50: <td>
			html.text(registration.getEmail());                         // line 50: ^{html.text(registration.getEmail());}
			out.println("</td>");                                       // line 50: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 51: </tr>
			out.println("<tr>");                                        // line 52: <tr>
			out.increaseTab();
			out.println("<td>Age</td>");                                // line 53: <td>Age</td>
			out.print("<td>");                                          // line 54: <td>
			out.print(registration.getAge());                           // line 54: ^{registration.getAge()}
			out.println("</td>");                                       // line 54: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 55: </tr>
			out.println("</table>");                                    // line 56: </table>
			out.println("<a href=\"\">back</a>");                       // line 57: <a href="">back</a>
		}
		out.decreaseTab();
		out.println("</div>");                                          // line 58: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 59: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 60: </div>
		out.decreaseTab();
		out.println("</body>");                                         // line 61: </body>
		out.println("</html>");                                         // line 62: </html>
	}
	
	
	// add a no-args contructor
	public IndexTemplate()
	{
	}
	
	
	private void printInput(String name, String label, String type)
	{
		out.println("<div class=\"control-group\">");                   // line 74: <div class="control-group">
		out.increaseTab();
		out.print("<label class=\"control-label\" for=\"");             // line 75: <label class="control-label" for="
		out.print(name);                                                // line 75: ^name
		out.print("\">");                                               // line 75: ">
		html.text(label);                                               // line 75: ^{html.text(label);}
		out.println("</label>");                                        // line 75: </label>
		out.println("<div class=\"controls\">");                        // line 76: <div class="controls">
		out.increaseTab();
		out.print("<input type=\"");                                    // line 77: <input type="
		out.print(type);                                                // line 77: ^type
		out.print("\" id=\"");                                          // line 77: " id="
		out.print(name);                                                // line 77: ^name
		out.print("\" name=\"");                                        // line 77: " name="
		out.print(name);                                                // line 77: ^name
		out.println("\" value=\"\">");                                  // line 77: " value="">
		out.decreaseTab();
		out.println("</div>");                                          // line 78: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 79: </div>
	}


	protected String remoteIp;
	protected String acceptHeader;
	protected Registration registration;
	protected HtmlMixin html;
}
