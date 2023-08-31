/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root.contacts.id;


import org.civilian.samples.crm.text.Message;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.LangMixin;
import org.civilian.template.mixin.TableMixin;


public class IndexTemplate extends CspTemplate
{
	@Override protected void init()
	{
		super.init();
		table = new TableMixin(out);
		lang = new LangMixin(out);
	}


	@Override protected void exit()
	{
		table = null;
		lang = null;
		super.exit();
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
		out.print(lang.msg(Message.FirstName));                         // line 11: <%lang.msg(Message.FirstName)%>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 12: table
		out.print("<input type=\"text\" ng-model=\"object.firstName\">"); // line 12: <input type="text" ng-model="object.firstName">
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 13: table
		out.print(lang.msg(Message.Customer));                          // line 13: <%lang.msg(Message.Customer)%>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 14: table
		out.print("<input type=\"text\" ng-model=\"object.customer\">"); // line 14: <input type="text" ng-model="object.customer">
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 15: table
		out.print(lang.msg(Message.Telephone));                         // line 15: <%lang.msg(Message.Telephone)%>
		cspCb0.endComponent(false);
		cspCb0 = table;
		cspCb0.startComponent(false);                                   // line 16: table
		out.print("<input type=\"text\" ng-model=\"object.phone\">");   // line 16: <input type="text" ng-model="object.phone">
		cspCb0.endComponent(false);
		table.endTable();                                               // line 17: @table.endTable();
		out.decreaseTab();
		out.println("</form>");                                         // line 18: </form>
	}


	protected TableMixin table;
	protected LangMixin lang;
}
