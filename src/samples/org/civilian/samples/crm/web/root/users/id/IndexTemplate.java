/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root.users.id;


import org.civilian.samples.crm.text.Message;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.LangMixin;
import org.civilian.template.mixin.TableMixin;


public class IndexTemplate extends CspTemplate
{
	@Override protected void init()
	{
		super.init();
		t = new TableMixin(out);
		lang = new LangMixin(out);
	}


	@Override protected void exit()
	{
		t = null;
		lang = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<form name=\"form\" ng-submit=\"nothing()\">");    // line 6: <form name="form" ng-submit="nothing()">
		t.columns("[]10[200, class=fill]");                             // line 7: @t.columns("[]10[200, class=fill]");
		t.startTable("class", "table table-form");                      // line 8: @t.startTable("class", "table table-form");
		//-----------------------------                                 // line 9: @//-----------------------------
		org.civilian.template.ComponentBuilder cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 10: t
		out.print(lang.msg(Message.Name));                              // line 10: <%lang.msg(Message.Name)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 11: t
		out.print("<input type=\"text\" ng-model=\"object.name\" required>"); // line 11: <input type="text" ng-model="object.name" required>
		cspCb0.endComponent(false);
		//-----------------------------                                 // line 12: @//-----------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 13: t
		out.print(lang.msg(Message.FirstName));                         // line 13: <%lang.msg(Message.FirstName)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 14: t
		out.print("<input type=\"text\" ng-model=\"object.firstName\">"); // line 14: <input type="text" ng-model="object.firstName">
		cspCb0.endComponent(false);
		//-----------------------------                                 // line 15: @//-----------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 16: t
		out.print(lang.msg(Message.Email));                             // line 16: <%lang.msg(Message.Email)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 17: t
		out.print("<input type=\"email\" ng-model=\"object.email\" required>"); // line 17: <input type="email" ng-model="object.email" required>
		cspCb0.endComponent(false);
		//-----------------------------                                 // line 18: @//-----------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 19: t
		out.print(lang.msg(Message.Telephone));                         // line 19: <%lang.msg(Message.Telephone)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 20: t
		out.print("<input type=\"text\" ng-model=\"object.phone\">");   // line 20: <input type="text" ng-model="object.phone">
		cspCb0.endComponent(false);
		//-----------------------------                                 // line 21: @//-----------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 22: t
		out.print(lang.msg(Message.isAdmin));                           // line 22: <%lang.msg(Message.isAdmin)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 23: t
		out.print("<input type=\"checkbox\" ng-model=\"object.isAdmin\">"); // line 23: <input type="checkbox" ng-model="object.isAdmin">
		cspCb0.endComponent(false);
		//-----------------------------                                 // line 24: @//-----------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 25: t
		out.print(lang.msg(Message.Login));                             // line 25: <%lang.msg(Message.Login)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 26: t
		out.print("<input type=\"text\" ng-model=\"object.login\">");   // line 26: <input type="text" ng-model="object.login">
		cspCb0.endComponent(false);
		//-----------------------------                                 // line 27: @//-----------------------------
		t.endTable();                                                   // line 28: @t.endTable();
		out.println("</form>");                                         // line 29: </form>
	}


	protected TableMixin t;
	protected LangMixin lang;
}
