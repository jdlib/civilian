/**
 * Generated from MultiPanelTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.template;


import org.civilian.Template;
import org.civilian.template.mixin.LangMixin;
import org.civilian.text.msg.MsgId;


public class MultiPanelTemplate extends Template
{
	public MultiPanelTemplate(MsgId type)
	{
		this.type = type;
	}


	@Override protected void init()
	{
		super.init();
		lang = new LangMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<div class=\"row-fluid\">");                       // line 7: <div class="row-fluid">
		out.increaseTab();
		out.println("<div class=\"span2 well\">");                      // line 8: <div class="span2 well">
		out.increaseTab();
		out.println("<ul class=\"nav nav-list\">");                     // line 9: <ul class="nav nav-list">
		out.increaseTab();
		out.println("<li ng-repeat=\"nav in navItems\" ng-class=\"{active: nav.active}\"><a ng-click=\"openPanel(nav)\">{{nav.label}}</a></li>"); // line 10: <li ng-repeat="nav in navItems" ng-class="{active: nav.active}"><a ng-click="openPanel(nav)">{{nav.label}}</a></li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 11: </ul>
		out.decreaseTab();
		out.println("</div>");                                          // line 12: </div>
		out.println("<div class=\"span10 tab-content\">");              // line 13: <div class="span10 tab-content">
		out.increaseTab();
		out.print("<h3>");                                              // line 14: <h3>
		out.print(lang.msg(type));                                      // line 14: <%lang.msg(type)%>
		out.println(" {{object.name}}</h3>");                           // line 14: {{object.name}}</h3>
		out.println("<div ng-repeat=\"panel in panels\" class=\"tab-pane\" ng-class=\"{active: panel.nav.active}\">"); // line 15: <div ng-repeat="panel in panels" class="tab-pane" ng-class="{active: panel.nav.active}">
		out.increaseTab();
		out.println("<civ-include src=\"panel.nav\"></civ-include>");   // line 16: <civ-include src="panel.nav"></civ-include>
		out.decreaseTab();
		out.println("</div>");                                          // line 17: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 18: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 19: </div>
	}
	
	


	private MsgId type;
	private LangMixin lang;
}
