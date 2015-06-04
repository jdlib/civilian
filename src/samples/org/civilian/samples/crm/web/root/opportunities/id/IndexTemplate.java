/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root.opportunities.id;


import org.civilian.samples.crm.text.Message;
import org.civilian.template.Template;
import org.civilian.template.mixin.LangMixin;
import org.civilian.template.mixin.TableMixin;


public class IndexTemplate extends Template
{
	@Override protected void init()
	{
		super.init();
		table = new TableMixin(out);
		lang = new LangMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<form name=\"form\" ng-submit=\"nothing()\">");    // line 6: <form name="form" ng-submit="nothing()">
		out.increaseTab();
		table.columns("[]10[200, class=fill]");                         // line 7: @table.columns("[]10[200, class=fill]");
		table.startTable("class", "table table-form");                  // line 8: @table.startTable("class", "table table-form");
		org.civilian.template.ComponentBuilder cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 9: table
		out.print(lang.msg(Message.Name));                              // line 9: <%lang.msg(Message.Name)%>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 10: table
		out.print("<input type=\"text\" ng-model=\"object.name\" required>"); // line 10: <input type="text" ng-model="object.name" required>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 11: table
		out.print(lang.msg(Message.Volume));                            // line 11: <%lang.msg(Message.Volume)%>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 12: table
		out.print("<input type=\"number\" ng-model=\"object.volume\">"); // line 12: <input type="number" ng-model="object.volume">
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 13: table
		out.print(lang.msg(Message.Probability));                       // line 13: <%lang.msg(Message.Probability)%>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 14: table
		out.print("<input type=\"range\" min=\"0\" max=\"100\" ng-model=\"object.probability\">"); // line 14: <input type="range" min="0" max="100" ng-model="object.probability">
		cspCb0.endComponent(false);
		table.endTable();                                               // line 15: @table.endTable();
		out.decreaseTab();
		out.println("</form>");                                         // line 16: </form>
	}


	private TableMixin table;
	private LangMixin lang;
}
