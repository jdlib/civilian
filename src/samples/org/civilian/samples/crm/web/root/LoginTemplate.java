/**
 * Generated from LoginTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root;


import org.civilian.samples.crm.text.Message;
import org.civilian.template.Template;
import org.civilian.template.mixin.FormTableMixin;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;


public class LoginTemplate extends Template
{
	public LoginTemplate(LoginForm form, String errorMessage)
	{
		this.form = form;
		this.errorMessage = errorMessage;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		lang = new LangMixin(out);
		formTable = new FormTableMixin(out);
	}


	@Override protected void exit()
	{
		html = null;
		lang = null;
		formTable = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 6: <!DOCTYPE html>
		out.println("<html ng-app=\"crm\">");                           // line 7: <html ng-app="crm">
		out.println("<head>");                                          // line 8: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 9: @html.metaContentType();
		out.print("<title>");                                           // line 10: <title>
		out.print(lang.msg(Message.CRM));                               // line 10: <%lang.msg(Message.CRM)%>
		out.println("</title>");                                        // line 10: </title>
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"); // line 11: <meta http-equiv="X-UA-Compatible" content="IE=edge">
		html.linkCss("css/lib/bootstrap.css");                          // line 12: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/lib/toastr.css");                             // line 13: @html.linkCss("css/lib/toastr.css");
		html.linkCss("css/samples.css");                                // line 14: @html.linkCss("css/samples.css");
		html.script("js/lib/jquery-3.6.0.min.js");                      // line 15: @html.script("js/lib/jquery-3.6.0.min.js");
		out.decreaseTab();
		out.println("</head>");                                         // line 16: </head>
		out.println("<body>");                                          // line 17: <body>
		out.println("<div id=\"noJs\">");                               // line 18: <div id="noJs">
		out.increaseTab();
		out.println("Civilian CRM needs JavaScript. Please enable JavaScript in your browser."); // line 19: Civilian CRM needs JavaScript. Please enable JavaScript in your browser.
		out.decreaseTab();
		out.println("</div>");                                          // line 20: </div>
		out.println("<div class=\"container\" style=\"margin-top:50px\">"); // line 21: <div class="container" style="margin-top:50px">
		out.increaseTab();
		out.println("<div class=\"row\">");                             // line 22: <div class="row">
		out.increaseTab();
		out.println("<div class=\"span3\"></div>");                     // line 23: <div class="span3"></div>
		out.println("<h3 class=\"span9\">Civilian CRM Sample</h3>");    // line 24: <h3 class="span9">Civilian CRM Sample</h3>
		out.decreaseTab();
		out.println("</div>");                                          // line 25: </div>
		out.println("<div class=\"row hide\" id=\"login\">");           // line 26: <div class="row hide" id="login">
		out.increaseTab();
		out.println("<div class=\"span3\">");                           // line 27: <div class="span3">
		out.increaseTab();
		form.start(out);                                                // line 28: @form.start(out);
		out.println("<table class=\"table\">");                         // line 29: <table class="table">
		formTable.row(form.name);                                       // line 30: @formTable.row(form.name);
		formTable.row(form.password);                                   // line 31: @formTable.row(form.password);
		formTable.row(form.language);                                   // line 32: @formTable.row(form.language);
		formTable.row(form.ok.setAttribute("class", "btn"));            // line 33: @formTable.row(form.ok.setAttribute("class", "btn"));
		if (errorMessage != null)                                       // line 34: @if (errorMessage != null)
		{
			out.println("<tr>");                                        // line 35: <tr>
			out.increaseTab();
			out.println("<td></td>");                                   // line 36: <td></td>
			out.print("<td>");                                          // line 37: <td>
			html.text(errorMessage);                                    // line 37: <%html.text(errorMessage);%>
			out.println("</td>");                                       // line 37: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 38: </tr>
		}
		out.println("</table>");                                        // line 39: </table>
		form.end(out);                                                  // line 40: @form.end(out);
		out.decreaseTab();
		out.println("</div>");                                          // line 41: </div>
		out.println("<div class=\"span9\">");                           // line 42: <div class="span9">
		out.increaseTab();
		out.println("<p>");                                             // line 43: <p>
		out.println("Welcome to the CRM Sample of the <a href=\"http://www.civilian-framework.org/\" target=\"_blank\"><b>Civilian Framework</b></a>.<br>"); // line 44: Welcome to the CRM Sample of the <a href="http://www.civilian-framework.org/" target="_blank"><b>Civilian Framework</b></a>.<br>
		out.println("It is a show case for a Rich Internet Application, using <a href=\"http://www.angularjs.org/\" target=\"_blank\"><b>Angular JS</b></a> on the Client"); // line 45: It is a show case for a Rich Internet Application, using <a href="http://www.angularjs.org/" target="_blank"><b>Angular JS</b></a> on the Client
		out.println("and Civilian on the server.<br>");                 // line 46: and Civilian on the server.<br>
		out.println("Source code is available as part of the Civilian distribution."); // line 47: Source code is available as part of the Civilian distribution.
		out.println("</p>");                                            // line 48: </p>
		out.println("<br>");                                            // line 49: <br>
		out.println("<p>Please login with one the following login names: \"user\", \"power\", \"admin\".<br>"); // line 50: <p>Please login with one the following login names: "user", "power", "admin".<br>
		out.println("The password equals the login name, prefixed by an exclamation mark \"!\".</p>"); // line 51: The password equals the login name, prefixed by an exclamation mark "!".</p>
		out.println("<br>");                                            // line 52: <br>
		out.println("<p>To view the sample you need a HTML5 capable browser with Javascript enabled.</p>"); // line 53: <p>To view the sample you need a HTML5 capable browser with Javascript enabled.</p>
		out.decreaseTab();
		out.println("</div>");                                          // line 54: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 55: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 56: </div>
		out.println("<script>");                                        // line 57: <script>
		out.println("$(document).ready(function() {");                  // line 58: $(document).ready(function() {
		out.increaseTab();
		out.println("$(\"#noJs\").hide();");                            // line 59: $("#noJs").hide();
		out.println("$(\"#login\").show();");                           // line 60: $("#login").show();
		form.name.focus(out, false);                                    // line 61: @form.name.focus(out, false);
		out.decreaseTab();
		out.println("});");                                             // line 62: });
		out.println("</script>");                                       // line 63: </script>
		out.println("</body>");                                         // line 64: </body>
		out.println("</html>");                                         // line 65: </html>
	}


	protected LoginForm form;
	protected String errorMessage;
	protected HtmlMixin html;
	protected LangMixin lang;
	protected FormTableMixin formTable;
}
