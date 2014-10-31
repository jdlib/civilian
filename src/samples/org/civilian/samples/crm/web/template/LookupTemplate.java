/**
 * Generated from LookupTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.template;


import org.civilian.Template;
import org.civilian.response.ResponseWriter;
import org.civilian.samples.crm.text.Message;
import org.civilian.template.mixin.LangMixin;


public class LookupTemplate extends Template
{
	@Override public synchronized void print(ResponseWriter out) throws Exception
	{
		try
		{
			lang = new LangMixin(out);
			super.print(out);
		}
		finally
		{
			lang = null;
		}
	}


	@Override protected void print() throws Exception
	{
		out.println("<div class=\"lookup-table\">");                    // line 7: <div class="lookup-table">
		out.increaseTab();
		out.print(new SearchTemplate(false));                           // line 8: <%new SearchTemplate(false)%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 9: </div>
		out.println("<hr>");                                            // line 10: <hr>
		out.print("<button ng-click=\"onOk()\" ng-disabled=\"searchResult.empty()\" class=\"btn btn-primary\">"); // line 11: <button ng-click="onOk()" ng-disabled="searchResult.empty()" class="btn btn-primary">
		out.print(lang.msg(Message.OK));                                // line 11: <%lang.msg(Message.OK)%>
		out.println("</button>");                                       // line 11: </button>
		out.print("<button ng-click=\"onCancel()\" class=\"btn\">");    // line 12: <button ng-click="onCancel()" class="btn">
		out.print(lang.msg(Message.Cancel));                            // line 12: <%lang.msg(Message.Cancel)%>
		out.println("</button>");                                       // line 12: </button>
	}


	private LangMixin lang;
}
