/**
 * Generated from PageTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root;


import org.civilian.Resource;
import org.civilian.Response;
import org.civilian.resource.Path;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.CrmConstants;
import org.civilian.samples.crm.web.CrmResources;
import org.civilian.samples.crm.web.SessionUser;
import org.civilian.samples.crm.web.util.NavItem;
import org.civilian.samples.crm.web.util.Script;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;
import org.civilian.text.msg.MsgId;


public class PageTemplate extends Template
{
	public PageTemplate(Response response, Template content, Script script, SessionUser sessionUser, boolean showNavigation, Path reloadPath)
	{
		this.response = response;
		this.content = content;
		this.script = script;
		this.sessionUser = sessionUser;
		this.showNavigation = showNavigation;
		this.reloadPath = reloadPath;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
		lang = new LangMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<!DOCTYPE html>");                                 // line 17: <!DOCTYPE html>
		out.println("<html ng-app=\"crm\" class=\"ng-cloak\" ng-controller=\"CrmController\">"); // line 18: <html ng-app="crm" class="ng-cloak" ng-controller="CrmController">
		out.println("<head>");                                          // line 19: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 20: @html.metaContentType();
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"); // line 21: <meta http-equiv="X-UA-Compatible" content="IE=edge">
		out.println("<title>Civilian CRM</title>");                     // line 22: <title>Civilian CRM</title>
		out.print(CrmConstants.CSS_ASSETS);                             // line 23: <%CrmConstants.CSS_ASSETS%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</head>");                                         // line 24: </head>
		out.print("<body");                                             // line 25: <body
		if (showNavigation)                                             // line 25: <%?showNavigation%>
		{
			out.print(" ng-controller=\"MenuController\"");             // line 25: ng-controller="MenuController"
		}
		out.println(">");                                               // line 25: >
		out.println("<div class=\"navbar navbar-fixed-top\">");         // line 26: <div class="navbar navbar-fixed-top">
		out.increaseTab();
		out.println("<div class=\"navbar-inner\">");                    // line 27: <div class="navbar-inner">
		out.increaseTab();
		out.println("<span class=\"brand brand-main\">civilian crm</span>"); // line 28: <span class="brand brand-main">civilian crm</span>
		if (showNavigation || (reloadPath != null))                     // line 29: @if (showNavigation || (reloadPath != null))
		{
			out.println("<ul class=\"nav\">");                          // line 30: <ul class="nav">
			out.increaseTab();
			if (showNavigation)                                         // line 31: @if (showNavigation)
			{
				printMenuItems();                                       // line 32: @printMenuItems();
			}
			if (reloadPath != null)                                     // line 33: @if (reloadPath != null)
			{
				out.print("<li><button class=\"btn btn-success\" style=\"display:inline\" onclick=\"reload('"); // line 34: <li><button class="btn btn-success" style="display:inline" onclick="reload('
				out.print(reloadPath);                                  // line 34: <%reloadPath%>
				out.println("')\">Reload</button></li>");               // line 34: ')">Reload</button></li>
			}
			out.decreaseTab();
			out.println("</ul>");                                       // line 35: </ul>
		}
		out.println("<p class=\"text-right banner-info\">");            // line 36: <p class="text-right banner-info">
		out.increaseTab();
		html.text(sessionUser.name);                                    // line 37: <%html.text(sessionUser.name);%>
		out.printlnIfNotEmpty();
		out.print("| <a href=\"#\">");                                  // line 38: | <a href="#">
		out.print(lang.msg(Message.Help));                              // line 38: <%lang.msg(Message.Help)%>
		out.println("</a>");                                            // line 38: </a>
		out.print("| <a href=\"#\">");                                  // line 39: | <a href="#">
		out.print(lang.msg(Message.Profile));                           // line 39: <%lang.msg(Message.Profile)%>
		out.println("</a>");                                            // line 39: </a>
		out.print("| <a href=\"");                                      // line 40: | <a href="
		out.print(html.url(CrmResources.root.logout));                  // line 40: <%html.url(CrmResources.root.logout)%>
		out.print("\">");                                               // line 40: ">
		out.print(lang.msg(Message.Logout));                            // line 40: <%lang.msg(Message.Logout)%>
		out.println("</a>");                                            // line 40: </a>
		out.decreaseTab();
		out.println("</p>");                                            // line 41: </p>
		out.decreaseTab();
		out.println("</div>");                                          // line 42: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 43: </div>
		out.println("<div class=\"container-fluid\">");                 // line 44: <div class="container-fluid">
		out.increaseTab();
		out.print(content);                                             // line 45: <%content%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 46: </div>
		html.script("js/lib/jquery-2.1.1.min.js");                      // line 47: @html.script("js/lib/jquery-2.1.1.min.js");
		html.script("js/lib/jquery-ui-1.10.3.custom.min.js");           // line 48: @html.script("js/lib/jquery-ui-1.10.3.custom.min.js");
		html.script("js/lib/toastr.min.js");                            // line 49: @html.script("js/lib/toastr.min.js");
		html.script("js/lib/script.js");                                // line 50: @html.script("js/lib/script.js");
		html.script("js/lib/angular1.2.0.min.js");                      // line 51: @html.script("js/lib/angular1.2.0.min.js");
		html.script("js/lib/ui/date.js");                               // line 52: @html.script("js/lib/ui/date.js");
		html.script("js/lib/ui-bootstrap-tpls-0.11.0.min.js");          // line 53: @html.script("js/lib/ui-bootstrap-tpls-0.11.0.min.js");
		html.script("civilian/angular/civ.js");                         // line 54: @html.script("civilian/angular/civ.js");
		html.script("civilian/angular/civ-auth.js");                    // line 55: @html.script("civilian/angular/civ-auth.js");
		html.script("civilian/angular/civ-load.js");                    // line 56: @html.script("civilian/angular/civ-load.js");
		html.script("civilian/angular/civ-init.js");                    // line 57: @html.script("civilian/angular/civ-init.js");
		if (reloadPath != null)                                         // line 58: @if (reloadPath != null)
		{
			html.script("civilian/dev/reload-tomcat.js");               // line 59: @html.script("civilian/dev/reload-tomcat.js");
		}
		loadScript(script);                                             // line 60: @loadScript(script);
		out.println("<script type=\"text/ng-template\" id=\"login.template\">"); // line 61: <script type="text/ng-template" id="login.template">
		out.println("<form name=\"form\">");                            // line 62: <form name="form">
		out.println("<div class=\"modal-header\">");                    // line 63: <div class="modal-header">
		out.increaseTab();
		out.print("<h3>CRM ");                                          // line 64: <h3>CRM
		out.print(lang.msg(Message.Login));                             // line 64: <%lang.msg(Message.Login)%>
		out.println("</h3>");                                           // line 64: </h3>
		out.decreaseTab();
		out.println("</div>");                                          // line 65: </div>
		out.println("<div class=\"modal-body\">");                      // line 66: <div class="modal-body">
		out.increaseTab();
		out.print("<table class=\"table table-form\" ng-init=\"lang='"); // line 67: <table class="table table-form" ng-init="lang='
		out.print(sessionUser.localeService.toString());                // line 67: <%sessionUser.localeService.toString()%>
		out.println("'\">");                                            // line 67: '">
		out.println("<tr>");                                            // line 68: <tr>
		out.increaseTab();
		out.print("<td>");                                              // line 69: <td>
		out.print(lang.msg(Message.Name));                              // line 69: <%lang.msg(Message.Name)%>
		out.println("</td>");                                           // line 69: </td>
		out.println("<td><input type=\"text\" ng-model=\"name\" ng-required=\"true\"></td>"); // line 70: <td><input type="text" ng-model="name" ng-required="true"></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 71: </tr>
		out.println("<tr>");                                            // line 72: <tr>
		out.increaseTab();
		out.print("<td>");                                              // line 73: <td>
		out.print(lang.msg(Message.Password));                          // line 73: <%lang.msg(Message.Password)%>
		out.println("</td>");                                           // line 73: </td>
		out.println("<td><input type=\"password\" ng-model=\"password\" ng-required=\"true\"></td>"); // line 74: <td><input type="password" ng-model="password" ng-required="true"></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 75: </tr>
		out.println("</table>");                                        // line 76: </table>
		out.decreaseTab();
		out.println("</div>");                                          // line 77: </div>
		out.println("<div class=\"modal-footer\">");                    // line 78: <div class="modal-footer">
		out.increaseTab();
		out.print("<button ng-click=\"login(name, password, lang)\" class=\"btn\" ng-disabled=\"form.$invalid\">"); // line 79: <button ng-click="login(name, password, lang)" class="btn" ng-disabled="form.$invalid">
		out.print(lang.msg(Message.Login));                             // line 79: <%lang.msg(Message.Login)%>
		out.println("</button>");                                       // line 79: </button>
		out.decreaseTab();
		out.println("</div>");                                          // line 80: </div>
		out.println("</form>");                                         // line 81: </form>
		out.println("</script>");                                       // line 82: </script>
		out.println("</body>");                                         // line 83: </body>
		out.println("</html>");                                         // line 84: </html>
	}
	
	
	private void loadScript(Script script)
	{
		if (script.dependsOn != null)
		{
			for (int i=0; i<script.dependsOn.length; i++)
				html.script(script.dependsOn[i].path);
		}
		html.script(script.path);
	}
	
	
	private void printMenuItems()
	{
		printMenuItem(Message.Customers, CrmResources.root.customers, Script.CUSTOMER, "customers.png");
		printMenuItem(Message.Contacts,  CrmResources.root.contacts,  Script.CONTACT,  "contact.png");
		printMenuItem(Message.Opportunities, CrmResources.root.opportunities, Script.OPPORTUNITY, "opportunity.png");
		if (sessionUser.isAdmin)
			printMenuItem(Message.Users, CrmResources.root.users, Script.USER, "settings.png");
	}
	
	
	private void printMenuItem(MsgId labelId, Resource resource, Script script, String image)
	{
		NavItem navItem = new NavItem(response);                        // line 111: @NavItem navItem = new NavItem(response);
		navItem.setScript(script);                                      // line 112: @navItem.setScript(script);
		navItem.setTemplateUrl(resource);                               // line 113: @navItem.setTemplateUrl(resource);
		String label = lang.msg(labelId);                               // line 114: @String label = lang.msg(labelId);
		out.println("<li class=\"border-left\">");                      // line 115: <li class="border-left">
		out.increaseTab();
		out.print("<a ng-click=\"openModule('");                        // line 116: <a ng-click="openModule('
		out.print(label);                                               // line 116: <%label%>
		out.print("', '");                                              // line 116: ', '
		out.print(navItem.templateUrl);                                 // line 116: <%navItem.templateUrl%>
		out.print("', '");                                              // line 116: ', '
		out.print(navItem.scriptUrls);                                  // line 116: <%navItem.scriptUrls%>
		out.println("')\" href=\"\">");                                 // line 116: ')" href="">
		out.increaseTab();
		out.print("<img ng-src=\"");                                    // line 117: <img ng-src="
		html.path("/img/crm/");                                         // line 117: <%html.path("/img/crm/");%>
		out.print(image);                                               // line 117: <%image%>
		out.print("\"> ");                                              // line 117: ">
		out.print(label);                                               // line 117: <%label%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</a>");                                            // line 118: </a>
		out.decreaseTab();
		out.println("</li>");                                           // line 119: </li>
	}


	protected Response response;
	protected Template content;
	protected Script script;
	protected SessionUser sessionUser;
	protected boolean showNavigation;
	protected Path reloadPath;
	protected HtmlMixin html;
	protected LangMixin lang;
}
