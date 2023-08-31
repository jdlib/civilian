/**
 * Generated from PageTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root;


import org.civilian.resource.Path;
import org.civilian.resource.Resource;
import org.civilian.response.Response;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.CrmResources;
import org.civilian.samples.crm.web.SessionUser;
import org.civilian.samples.crm.web.util.NavItem;
import org.civilian.samples.crm.web.util.Script;
import org.civilian.template.CspTemplate;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;
import org.civilian.text.msg.MsgId;


public class PageTemplate extends CspTemplate
{
	public PageTemplate(Response response, Template content, Script script, SessionUser sessionUser, boolean showNavigation, Path reloadPath, Path assetPath)
	{
		this.response = response;
		this.content = content;
		this.script = script;
		this.sessionUser = sessionUser;
		this.showNavigation = showNavigation;
		this.reloadPath = reloadPath;
		this.assetPath = assetPath;
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
		out.println("<!DOCTYPE html>");                                 // line 16: <!DOCTYPE html>
		out.println("<html ng-app=\"crm\" class=\"ng-cloak\" ng-controller=\"CrmController\">"); // line 17: <html ng-app="crm" class="ng-cloak" ng-controller="CrmController">
		out.println("<head>");                                          // line 18: <head>
		out.increaseTab();
		html.metaContentType();                                         // line 19: @html.metaContentType();
		out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"); // line 20: <meta http-equiv="X-UA-Compatible" content="IE=edge">
		out.println("<title>Civilian CRM</title>");                     // line 21: <title>Civilian CRM</title>
		html.linkCss("css/lib/bootstrap.css");                          // line 22: @html.linkCss("css/lib/bootstrap.css");
		html.linkCss("css/lib/toastr.css");                             // line 23: @html.linkCss("css/lib/toastr.css");
		html.linkCss("css/lib/flick/jquery-ui-1.10.3.custom.min.css");  // line 24: @html.linkCss("css/lib/flick/jquery-ui-1.10.3.custom.min.css");
		html.linkCss("css/samples.css");                                // line 25: @html.linkCss("css/samples.css");
		html.linkCss("css/crm.css");                                    // line 26: @html.linkCss("css/crm.css");
		out.decreaseTab();
		out.println("</head>");                                         // line 27: </head>
		out.print("<body");                                             // line 28: <body
		if (showNavigation)                                             // line 28: <%?showNavigation%>
		{
			out.print(" ng-controller=\"MenuController\"");             // line 28: ng-controller="MenuController"
		}
		out.println(">");                                               // line 28: >
		out.println("<div class=\"navbar navbar-fixed-top\">");         // line 29: <div class="navbar navbar-fixed-top">
		out.increaseTab();
		out.println("<div class=\"navbar-inner\">");                    // line 30: <div class="navbar-inner">
		out.increaseTab();
		out.println("<span class=\"brand brand-main\">civilian crm</span>"); // line 31: <span class="brand brand-main">civilian crm</span>
		if (showNavigation || (reloadPath != null))                     // line 32: @if (showNavigation || (reloadPath != null))
		{
			out.println("<ul class=\"nav\">");                          // line 33: <ul class="nav">
			out.increaseTab();
			if (showNavigation)                                         // line 34: @if (showNavigation)
			{
				printMenuItems();                                       // line 35: @printMenuItems();
			}
			if (reloadPath != null)                                     // line 36: @if (reloadPath != null)
			{
				out.print("<li><button class=\"btn btn-success\" style=\"display:inline\" onclick=\"reload('"); // line 37: <li><button class="btn btn-success" style="display:inline" onclick="reload('
				out.print(reloadPath);                                  // line 37: <%reloadPath%>
				out.println("')\">Reload</button></li>");               // line 37: ')">Reload</button></li>
			}
			out.decreaseTab();
			out.println("</ul>");                                       // line 38: </ul>
		}
		out.println("<p class=\"text-right banner-info\">");            // line 39: <p class="text-right banner-info">
		out.increaseTab();
		html.text(sessionUser.name);                                    // line 40: <%html.text(sessionUser.name);%>
		out.printlnIfNotEmpty();
		out.print("| <a href=\"#\">");                                  // line 41: | <a href="#">
		out.print(lang.msg(Message.Help));                              // line 41: <%lang.msg(Message.Help)%>
		out.println("</a>");                                            // line 41: </a>
		out.print("| <a href=\"#\">");                                  // line 42: | <a href="#">
		out.print(lang.msg(Message.Profile));                           // line 42: <%lang.msg(Message.Profile)%>
		out.println("</a>");                                            // line 42: </a>
		out.print("| <a href=\"");                                      // line 43: | <a href="
		out.print(html.url(CrmResources.root.logout));                  // line 43: <%html.url(CrmResources.root.logout)%>
		out.print("\">");                                               // line 43: ">
		out.print(lang.msg(Message.Logout));                            // line 43: <%lang.msg(Message.Logout)%>
		out.println("</a>");                                            // line 43: </a>
		out.decreaseTab();
		out.println("</p>");                                            // line 44: </p>
		out.decreaseTab();
		out.println("</div>");                                          // line 45: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 46: </div>
		out.println("<div class=\"container-fluid\">");                 // line 47: <div class="container-fluid">
		out.increaseTab();
		out.print(content);                                             // line 48: <%content%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 49: </div>
		html.script("js/lib/jquery-3.6.0.min.js");                      // line 50: @html.script("js/lib/jquery-3.6.0.min.js");
		html.script("js/lib/jquery-ui-1.10.3.custom.min.js");           // line 51: @html.script("js/lib/jquery-ui-1.10.3.custom.min.js");
		html.script("js/lib/toastr.min.js");                            // line 52: @html.script("js/lib/toastr.min.js");
		html.script("js/lib/script.js");                                // line 53: @html.script("js/lib/script.js");
		html.script("js/lib/angular1.2.0.min.js");                      // line 54: @html.script("js/lib/angular1.2.0.min.js");
		html.script("js/lib/ui/date.js");                               // line 55: @html.script("js/lib/ui/date.js");
		html.script("js/lib/ui-bootstrap-tpls-0.11.0.min.js");          // line 56: @html.script("js/lib/ui-bootstrap-tpls-0.11.0.min.js");
		html.script("civilian/angular/civ.js");                         // line 57: @html.script("civilian/angular/civ.js");
		html.script("civilian/angular/civ-auth.js");                    // line 58: @html.script("civilian/angular/civ-auth.js");
		html.script("civilian/angular/civ-load.js");                    // line 59: @html.script("civilian/angular/civ-load.js");
		html.script("civilian/angular/civ-init.js");                    // line 60: @html.script("civilian/angular/civ-init.js");
		if (reloadPath != null)                                         // line 61: @if (reloadPath != null)
		{
			html.script("civilian/dev/reload-tomcat.js");               // line 62: @html.script("civilian/dev/reload-tomcat.js");
		}
		loadScript(script);                                             // line 63: @loadScript(script);
		out.println("<script type=\"text/ng-template\" id=\"login.template\">"); // line 64: <script type="text/ng-template" id="login.template">
		out.println("<form name=\"form\">");                            // line 65: <form name="form">
		out.println("<div class=\"modal-header\">");                    // line 66: <div class="modal-header">
		out.increaseTab();
		out.print("<h3>CRM ");                                          // line 67: <h3>CRM
		out.print(lang.msg(Message.Login));                             // line 67: <%lang.msg(Message.Login)%>
		out.println("</h3>");                                           // line 67: </h3>
		out.decreaseTab();
		out.println("</div>");                                          // line 68: </div>
		out.println("<div class=\"modal-body\">");                      // line 69: <div class="modal-body">
		out.increaseTab();
		out.print("<table class=\"table table-form\" ng-init=\"lang='"); // line 70: <table class="table table-form" ng-init="lang='
		out.print(sessionUser.localeService.toString());                // line 70: <%sessionUser.localeService.toString()%>
		out.println("'\">");                                            // line 70: '">
		out.println("<tr>");                                            // line 71: <tr>
		out.increaseTab();
		out.print("<td>");                                              // line 72: <td>
		out.print(lang.msg(Message.Name));                              // line 72: <%lang.msg(Message.Name)%>
		out.println("</td>");                                           // line 72: </td>
		out.println("<td><input type=\"text\" ng-model=\"name\" ng-required=\"true\"></td>"); // line 73: <td><input type="text" ng-model="name" ng-required="true"></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 74: </tr>
		out.println("<tr>");                                            // line 75: <tr>
		out.increaseTab();
		out.print("<td>");                                              // line 76: <td>
		out.print(lang.msg(Message.Password));                          // line 76: <%lang.msg(Message.Password)%>
		out.println("</td>");                                           // line 76: </td>
		out.println("<td><input type=\"password\" ng-model=\"password\" ng-required=\"true\"></td>"); // line 77: <td><input type="password" ng-model="password" ng-required="true"></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 78: </tr>
		out.println("</table>");                                        // line 79: </table>
		out.decreaseTab();
		out.println("</div>");                                          // line 80: </div>
		out.println("<div class=\"modal-footer\">");                    // line 81: <div class="modal-footer">
		out.increaseTab();
		out.print("<button ng-click=\"login(name, password, lang)\" class=\"btn\" ng-disabled=\"form.$invalid\">"); // line 82: <button ng-click="login(name, password, lang)" class="btn" ng-disabled="form.$invalid">
		out.print(lang.msg(Message.Login));                             // line 82: <%lang.msg(Message.Login)%>
		out.println("</button>");                                       // line 82: </button>
		out.decreaseTab();
		out.println("</div>");                                          // line 83: </div>
		out.println("</form>");                                         // line 84: </form>
		out.println("</script>");                                       // line 85: </script>
		out.println("</body>");                                         // line 86: </body>
		out.println("</html>");                                         // line 87: </html>
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
		NavItem navItem = new NavItem(response);
		if (script != null)
			navItem.setScriptUrls(assetPath + script.path);
		navItem.setTemplateUrl(resource);
		printMenuItem(navItem, image, lang.msg(labelId));
	}
	
	
	private void printMenuItem(NavItem navItem, String image, String label)
	{
		out.println("<li class=\"border-left\">");                      // line 124: <li class="border-left">
		out.increaseTab();
		out.print("<a ng-click=\"openModule('");                        // line 125: <a ng-click="openModule('
		out.print(label);                                               // line 125: <%label%>
		out.print("', '");                                              // line 125: ', '
		out.print(navItem.templateUrl);                                 // line 125: <%navItem.templateUrl%>
		out.print("', '");                                              // line 125: ', '
		out.print(navItem.scriptUrls);                                  // line 125: <%navItem.scriptUrls%>
		out.println("')\" href=\"\">");                                 // line 125: ')" href="">
		out.increaseTab();
		out.print("<img ng-src=\"");                                    // line 126: <img ng-src="
		html.path("/img/crm/");                                         // line 126: <%html.path("/img/crm/");%>
		out.print(image);                                               // line 126: <%image%>
		out.print("\"> ");                                              // line 126: ">
		out.print(label);                                               // line 126: <%label%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</a>");                                            // line 127: </a>
		out.decreaseTab();
		out.println("</li>");                                           // line 128: </li>
	}


	protected Response response;
	protected Template content;
	protected Script script;
	protected SessionUser sessionUser;
	protected boolean showNavigation;
	protected Path reloadPath;
	protected Path assetPath;
	protected HtmlMixin html;
	protected LangMixin lang;
}
