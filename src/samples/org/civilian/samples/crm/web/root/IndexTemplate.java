/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root;


import org.civilian.resource.Url;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.CrmResources;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.template.mixin.LangMixin;


public class IndexTemplate extends Template
{
	@Override protected void init()
	{
		super.init();
		lang = new LangMixin(out);
		html = new HtmlMixin(out);
	}


	@Override protected void exit()
	{
		super.exit();
		lang = null;
		html = null;
	}


	@Override protected void print() throws Exception
	{
		out.println("<div class=\"row-fluid\">");                       // line 8: <div class="row-fluid">
		out.println("<tabset>");                                        // line 9: <tabset>
		printWelcome();                                                 // line 10: @printWelcome();
		// tabs is a variable in the MenuController                     // line 11: @// tabs is a variable in the MenuController
		out.println("<tab ng-repeat=\"tab in tabs\" active=\"tab.active\">"); // line 12: <tab ng-repeat="tab in tabs" active="tab.active">
		out.increaseTab();
		out.println("<tab-heading>{{tab.label}}<i ng-click=\"closeModule(tab)\" class=\"tabclose icon-remove-sign\"></i></tab-heading>"); // line 13: <tab-heading>{{tab.label}}<i ng-click="closeModule(tab)" class="tabclose icon-remove-sign"></i></tab-heading>
		out.println("<civ-include src=\"tab\"></civ-include>");         // line 14: <civ-include src="tab"></civ-include>
		out.decreaseTab();
		out.println("</tab>");                                          // line 15: </tab>
		out.println("</tabset>");                                       // line 16: </tabset>
		out.println("</div>");                                          // line 17: </div>
	}
	
	
	private void link(String url, String text)
	{
		out.print("<b><a href=\"");                                     // line 23: <b><a href="
		out.print(url);                                                 // line 23: <%url%>
		out.print("\" target=\"_blank\">");                             // line 23: " target="_blank">
		out.print(text);                                                // line 23: <%text%>
		out.print("</a></b>");                                          // line 23: </a></b>
	}
	
	
	private void printWelcome()
	{
		out.println("<tab>");                                           // line 29: <tab>
		out.increaseTab();
		out.print("<tab-heading>");                                     // line 30: <tab-heading>
		out.print(lang.msg(Message.Start));                             // line 30: <%lang.msg(Message.Start)%>
		out.println("</tab-heading>");                                  // line 30: </tab-heading>
		out.println("<h3>Welcome</h3>to the Customer Relationship (CRM) sample of"); // line 31: <h3>Welcome</h3>to the Customer Relationship (CRM) sample of
		link("http://www.civilian-framework.org/", "Civilian Framework"); // line 32: <%link("http://www.civilian-framework.org/", "Civilian Framework");%>
		out.println(".");                                               // line 32: .
		out.println("<p>");                                             // line 33: <p>
		out.println("Civilian CRM is a proof of concept application how to build an <b>arbitrarily complex Rich"); // line 34: Civilian CRM is a proof of concept application how to build an <b>arbitrarily complex Rich
		out.println("Internet Application</b>, using a REST-based server and modern JavaScript frameworks.<br>"); // line 35: Internet Application</b>, using a REST-based server and modern JavaScript frameworks.<br>
		out.println("Complex translates mainly to a lot of input forms which interact with database content."); // line 36: Complex translates mainly to a lot of input forms which interact with database content.
		out.println("</p>");                                            // line 37: </p>
		out.println("<p>");                                             // line 38: <p>
		out.print("It uses ");                                          // line 39: It uses
		link("http://www.civilian-framework.org/", "Civilian");         // line 39: <%link("http://www.civilian-framework.org/", "Civilian");%>
		out.println(" on the server-side,");                            // line 39: on the server-side,
		out.print("and ");                                              // line 40: and
		link("http://www.angularjs.org/", "AngularJS");                 // line 40: <%link("http://www.angularjs.org/", "AngularJS");%>
		out.println(",");                                               // line 40: ,
		link("http://angular-ui.github.io/", "AngularUI");              // line 41: <%link("http://angular-ui.github.io/", "AngularUI");%>
		out.println(",");                                               // line 41: ,
		link("http://jquery.com/", "JQuery");                           // line 42: <%link("http://jquery.com/", "JQuery");%>
		out.println(",");                                               // line 42: ,
		link("http://getbootstrap.com/2.3.2/", "Twitter Bootstrap");    // line 43: <%link("http://getbootstrap.com/2.3.2/", "Twitter Bootstrap");%>
		out.println(" to implement an interactive client.");            // line 43: to implement an interactive client.
		out.println("</p>");                                            // line 44: </p>
		out.println("The <b>navigation design</b> is as follows:<br>"); // line 45: The <b>navigation design</b> is as follows:<br>
		out.println("<ul>");                                            // line 46: <ul>
		out.println("<li>The application consists of a set of <b>modules</b> for the main entities of the CRM"); // line 47: <li>The application consists of a set of <b>modules</b> for the main entities of the CRM
		out.increaseTab();
		out.println("(customers, contacts, opportunities, etc).</li>"); // line 48: (customers, contacts, opportunities, etc).</li>
		out.decreaseTab();
		out.println("<li>You <b>open a module</b> by clicking its menu item on the main navigation bar, which will add a tab to"); // line 49: <li>You <b>open a module</b> by clicking its menu item on the main navigation bar, which will add a tab to
		out.increaseTab();
		out.println("the main tab row. You can open a module several times.</li>"); // line 50: the main tab row. You can open a module several times.</li>
		out.decreaseTab();
		out.println("<li>The entry page of a module typically presents a <b>search form</b>. From a row in a search result"); // line 51: <li>The entry page of a module typically presents a <b>search form</b>. From a row in a search result
		out.increaseTab();
		out.println("you can then navigate to a single entity.");       // line 52: you can then navigate to a single entity.
		out.decreaseTab();
		out.println("<li>A <b>single entity</b> can have multiple panels to present its data, with typical CRUD semantics.</li>"); // line 53: <li>A <b>single entity</b> can have multiple panels to present its data, with typical CRUD semantics.</li>
		out.println("<li><b>Complex state</b> is preserved on all panels. This essentially allows you to interact with your data in multiple"); // line 54: <li><b>Complex state</b> is preserved on all panels. This essentially allows you to interact with your data in multiple
		out.increaseTab();
		out.println("views within the same browser window.</li>");      // line 55: views within the same browser window.</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 56: </ul>
		out.println("<h3>Please note</h3>");                            // line 57: <h3>Please note</h3>
		out.println("The database backend in this sample is just a mock, but could easily replaced by a real database.<br>"); // line 58: The database backend in this sample is just a mock, but could easily replaced by a real database.<br>
		out.println("The number of implemented entities, forms and panels is also very limited but hopefully we convinced you that the"); // line 59: The number of implemented entities, forms and panels is also very limited but hopefully we convinced you that the
		out.println("navigation design and the implementation techniques scale to very complex data models.<br>"); // line 60: navigation design and the implementation techniques scale to very complex data models.<br>
		out.println("<h3>What to take away</h3>");                      // line 61: <h3>What to take away</h3>
		out.println("Several main features of Civilian interacting with AngularJS are demonstrated in the sample. Please consult the source code of the"); // line 62: Several main features of Civilian interacting with AngularJS are demonstrated in the sample. Please consult the source code of the
		out.println("Civilian distribution to see how these are implemented."); // line 63: Civilian distribution to see how these are implemented.
		out.println("<ul>");                                            // line 64: <ul>
		out.println("<li><b>Lazy loading</b> of template and script code: When you open modules or sub-panels for the first time,"); // line 65: <li><b>Lazy loading</b> of template and script code: When you open modules or sub-panels for the first time,
		out.increaseTab();
		out.println("the HTML templates and accompanying JavaSccript code are transparently loaded from the server.</li>"); // line 66: the HTML templates and accompanying JavaSccript code are transparently loaded from the server.</li>
		out.decreaseTab();
		out.println("<li>All <b>templates</b> are <b>dynamically generated</b>, to account for the users language chosen in the login form and"); // line 67: <li>All <b>templates</b> are <b>dynamically generated</b>, to account for the users language chosen in the login form and
		out.increaseTab();
		out.println("access right settings. (This text appears only in English, though).</li>"); // line 68: access right settings. (This text appears only in English, though).</li>
		out.decreaseTab();
		out.println("<li>When initial templates have been sent to the client, all further communication between browser and"); // line 69: <li>When initial templates have been sent to the client, all further communication between browser and
		out.increaseTab();
		out.println("server is data-centric using <b>Ajax</b> calls with <b>JSON</b> messages.</li>"); // line 70: server is data-centric using <b>Ajax</b> calls with <b>JSON</b> messages.</li>
		out.decreaseTab();
		out.println("<li>Civilian helps to implement a transparent <b>login popup</b> during Ajax calls when the server-session"); // line 71: <li>Civilian helps to implement a transparent <b>login popup</b> during Ajax calls when the server-session
		out.increaseTab();
		out.println("had a timeout and requires new authentication.</li>"); // line 72: had a timeout and requires new authentication.</li>
		out.decreaseTab();
		out.println("<li>Being essentially a <b>single page application</b>, the REST-API of Civilian CRM nevertheless allows to"); // line 73: <li>Being essentially a <b>single page application</b>, the REST-API of Civilian CRM nevertheless allows to
		out.increaseTab();
		out.println("open <b>multiple windows</b> with different entry points. For example<br>"); // line 74: open <b>multiple windows</b> with different entry points. For example<br>
		out.println("<ul>");                                            // line 75: <ul>
		out.increaseTab();
		Url url = html.url(CrmResources.root.customers);                // line 76: @Url url = html.url(CrmResources.root.customers);
		out.print("<li>Open the <a href=\"");                           // line 77: <li>Open the <a href="
		out.print(url);                                                 // line 77: <%url%>
		out.println("\" target=\"_blank\">Customer</a> module in a new window.</li>"); // line 77: " target="_blank">Customer</a> module in a new window.</li>
		url = html.url(CrmResources.root.customers.$customerId);        // line 78: @url = html.url(CrmResources.root.customers.$customerId);
		url.setPathParam(Integer.valueOf(100)); // :customerId          // line 79: @url.setPathParam(Integer.valueOf(100)); // :customerId
		out.print("<li>Open the detail page of <a href=\"");            // line 80: <li>Open the detail page of <a href="
		out.print(url);                                                 // line 80: <%url%>
		out.println("\" target=\"_blank\">Customer 100</a>.</li>");     // line 80: " target="_blank">Customer 100</a>.</li>
		url = html.url(CrmResources.root.customers.$customerId.masterdata); // line 81: @url = html.url(CrmResources.root.customers.$customerId.masterdata);
		url.setPathParam(Integer.valueOf(100)); // :customerId          // line 82: @url.setPathParam(Integer.valueOf(100)); // :customerId
		out.print("<li>Open the masterdata page of <a href=\"");        // line 83: <li>Open the masterdata page of <a href="
		out.print(url);                                                 // line 83: <%url%>
		out.println("\" target=\"_blank\">Customer 100</a>.</li>");     // line 83: " target="_blank">Customer 100</a>.</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 84: </ul>
		out.println("This technique allows effective reuse of the server-side API: E.g. search dialogs for lookup of CRM entities"); // line 85: This technique allows effective reuse of the server-side API: E.g. search dialogs for lookup of CRM entities
		out.println("use the same search URL as the main search page of a module.<br>"); // line 86: use the same search URL as the main search page of a module.<br>
		out.println("This also helps development: Simply open a window with a specific URL to directly see the resource in"); // line 87: This also helps development: Simply open a window with a specific URL to directly see the resource in
		out.println("action which you are currently working on.</li>"); // line 88: action which you are currently working on.</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 89: </ul>
		out.decreaseTab();
		out.println("</tab>");                                          // line 90: </tab>
	}


	protected LangMixin lang;
	protected HtmlMixin html;
}
