/**
 * Generated from MasterdataTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.root.customers.id;


import org.civilian.samples.crm.text.Message;
import org.civilian.template.Template;
import org.civilian.template.mixin.LangMixin;
import org.civilian.template.mixin.TableMixin;


public class MasterdataTemplate extends Template
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
		out.println("<form name=\"form\" ng-submit=\"nothing()\" ng-controller=\"MasterDataController\">"); // line 6: <form name="form" ng-submit="nothing()" ng-controller="MasterDataController">
		t.columns("[]10[90, class=fill][100, class=fill][100, class=fill]30[][80]"); // line 7: @t.columns("[]10[90, class=fill][100, class=fill][100, class=fill]30[][80]");
		t.startTable("class", "table table-form");                      // line 8: @t.startTable("class", "table table-form");
		org.civilian.template.ComponentBuilder cspCb0 = t.colspan(4);
		cspCb0.startComponent(false);                                   // line 9: t.colspan(4)
		out.print("<b>");                                               // line 9: <b>
		out.print(lang.msg(Message.Company));                           // line 9: <%lang.msg(Message.Company)%>
		out.print("</b>");                                              // line 9: </b>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 10: t.colspan(2)
		out.print("<b>");                                               // line 10: <b>
		out.print(lang.msg(Message.Internal));                          // line 10: <%lang.msg(Message.Internal)%>
		out.print("</b>");                                              // line 10: </b>
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 11: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 12: t
		out.print(lang.msg(Message.Name));                              // line 12: <%lang.msg(Message.Name)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 13: t.colspan(3)
		out.print("<input type=\"text\" ng-model=\"object.name\">");    // line 13: <input type="text" ng-model="object.name">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 14: t
		out.print(lang.msg(Message.Type));                              // line 14: <%lang.msg(Message.Type)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 15: t
		out.print("<input type=\"text\">");                             // line 15: <input type="text">
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 16: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 17: t
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 18: t.colspan(3)
		out.print("<input type=\"text\">");                             // line 18: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 19: @t.endRow();
		//-----------------------------------------------               // line 20: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 21: t
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 22: t.colspan(3)
		out.print("<input type=\"text\">");                             // line 22: <input type="text">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 23: t
		out.print(lang.msg(Message.Number));                            // line 23: <%lang.msg(Message.Number)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 24: t
		out.print("<input type=\"text\">");                             // line 24: <input type="text">
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 25: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 26: t
		out.print(lang.msg(Message.ParentCompany));                     // line 26: <%lang.msg(Message.ParentCompany)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 27: t.colspan(2)
		out.print("<input readonly type=\"text\" ng-model=\"object.parent.name\">"); // line 27: <input readonly type="text" ng-model="object.parent.name">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 28: t
		out.print("<button class=\"btn btn-small\" ng-click=\"lookupCustomer()\"><i class=\"icon-search\"></i></button>"); // line 28: <button class="btn btn-small" ng-click="lookupCustomer()"><i class="icon-search"></i></button>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 29: t
		out.print(lang.msg(Message.Status));                            // line 29: <%lang.msg(Message.Status)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 30: t
		out.print("<input type=\"text\">");                             // line 30: <input type="text">
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 31: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 32: t
		out.print(lang.msg(Message.Enterprise));                        // line 32: <%lang.msg(Message.Enterprise)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 33: t.colspan(2)
		out.print("<input type=\"text\">");                             // line 33: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 34: @t.endRow();
		//-----------------------------------------------               // line 35: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 36: t
		out.print(lang.msg(Message.Country));                           // line 36: <%lang.msg(Message.Country)%>
		out.print(", ");                                                // line 36: ,
		out.print(lang.msg(Message.Code));                              // line 36: <%lang.msg(Message.Code)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 37: t.colspan(2)
		out.print("<input type=\"text\">");                             // line 37: <input type="text">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 38: t
		out.print("<input type=\"text\">");                             // line 38: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 39: @t.endRow();
		//-----------------------------------------------               // line 40: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 41: t
		out.print(lang.msg(Message.State));                             // line 41: <%lang.msg(Message.State)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 42: t.colspan(2)
		out.print("<input type=\"text\">");                             // line 42: <input type="text">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 43: t
		out.print("<input type=\"text\">");                             // line 43: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 44: @t.endRow();
		//-----------------------------------------------               // line 45: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 46: t
		out.print(lang.msg(Message.Zip));                               // line 46: <%lang.msg(Message.Zip)%>
		out.print(", ");                                                // line 46: ,
		out.print(lang.msg(Message.City));                              // line 46: <%lang.msg(Message.City)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 47: t
		out.print("<input type=\"text\" ng-model=\"object.zip\">");     // line 47: <input type="text" ng-model="object.zip">
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 48: t.colspan(2)
		out.print("<input type=\"text\" ng-model=\"object.city\">");    // line 48: <input type="text" ng-model="object.city">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 49: t
		out.print(lang.msg(Message.createdAt));                         // line 49: <%lang.msg(Message.createdAt)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 50: t
		out.print("<input type=\"text\">");                             // line 50: <input type="text">
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 51: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 52: t
		out.print(lang.msg(Message.Street));                            // line 52: <%lang.msg(Message.Street)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 53: t.colspan(3)
		out.print("<input type=\"text\">");                             // line 53: <input type="text">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 54: t
		out.print(lang.msg(Message.changedAt));                         // line 54: <%lang.msg(Message.changedAt)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 55: t
		out.print("<input type=\"text\">");                             // line 55: <input type="text">
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 56: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 57: t
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 58: t.colspan(3)
		out.print("<input type=\"text\">");                             // line 58: <input type="text">
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 59: t
		out.print(lang.msg(Message.by));                                // line 59: <%lang.msg(Message.by)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 60: t
		out.print("<input type=\"text\">");                             // line 60: <input type="text">
		cspCb0.endComponent(false);
		//-----------------------------------------------               // line 61: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 62: t
		out.print(lang.msg(Message.Zip));                               // line 62: <%lang.msg(Message.Zip)%>
		out.print(", ");                                                // line 62: ,
		out.print(lang.msg(Message.Postbox));                           // line 62: <%lang.msg(Message.Postbox)%>
		cspCb0.endComponent(false);
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 63: t
		out.print("<input type=\"text\">");                             // line 63: <input type="text">
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(2);
		cspCb0.startComponent(false);                                   // line 64: t.colspan(2)
		out.print("<input type=\"text\">");                             // line 64: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 65: @t.endRow();
		//-----------------------------------------------               // line 66: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 67: t
		out.print(lang.msg(Message.Telephone));                         // line 67: <%lang.msg(Message.Telephone)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 68: t.colspan(3)
		out.print("<input type=\"text\">");                             // line 68: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 69: @t.endRow();
		//-----------------------------------------------               // line 70: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 71: t
		out.print(lang.msg(Message.Fax));                               // line 71: <%lang.msg(Message.Fax)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 72: t.colspan(3)
		out.print("<input type=\"text\">");                             // line 72: <input type="text">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 73: @t.endRow();
		//-----------------------------------------------               // line 74: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 75: t
		out.print(lang.msg(Message.Homepage));                          // line 75: <%lang.msg(Message.Homepage)%>
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(false);                                   // line 76: t.colspan(3)
		out.print("<input type=\"url\" ng-model=\"object.homepage\">"); // line 76: <input type="url" ng-model="object.homepage">
		cspCb0.endComponent(false);
		t.endRow();                                                     // line 77: @t.endRow();
		//-----------------------------------------------               // line 78: @//-----------------------------------------------
		cspCb0 = t;
		cspCb0.startComponent(false);                                   // line 79: t
		cspCb0.endComponent(false);
		cspCb0 = t.colspan(3);
		cspCb0.startComponent(true);                                    // line 80: t.colspan(3)
		out.increaseTab();
		out.println("<br>");                                            // line 81: <br>
		out.print("<button ng-disabled=\"form.$pristine || form.$invalid\" class=\"btn btn-primary\">"); // line 82: <button ng-disabled="form.$pristine || form.$invalid" class="btn btn-primary">
		out.print(lang.msg(Message.Save));                              // line 82: <%lang.msg(Message.Save)%>
		out.println("</button>");                                       // line 82: </button>
		out.print("<button ng-disabled=\"form.$pristine\" class=\"btn\">"); // line 83: <button ng-disabled="form.$pristine" class="btn">
		out.print(lang.msg(Message.Cancel));                            // line 83: <%lang.msg(Message.Cancel)%>
		out.println("</button>");                                       // line 83: </button>
		out.decreaseTab();
		cspCb0.endComponent(true);                                      // line 84: ]
		t.endTable();                                                   // line 85: @t.endTable();
		out.println("</form>");                                         // line 86: </form>
	}


	protected TableMixin t;
	protected LangMixin lang;
}
