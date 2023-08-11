/**
 * Generated from ModuleTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.template;


import org.civilian.template.Template;


public class ModuleTemplate extends Template
{
	public ModuleTemplate(Template detailTemplate)
	{
		this.detailTemplate = detailTemplate;
	}


	@Override protected void print() throws Exception
	{
		out.println("<div class=\"ng-cloak\">");                        // line 6: <div class="ng-cloak">
		out.increaseTab();
		out.println("<div ng-show=\"showSearch\">");                    // line 7: <div ng-show="showSearch">
		out.increaseTab();
		out.print(new SearchTemplate(true));                            // line 8: <%new SearchTemplate(true)%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 9: </div>
		out.println("<div ng-hide=\"showSearch\" class=\"row-fluid row-stack\">"); // line 10: <div ng-hide="showSearch" class="row-fluid row-stack">
		out.increaseTab();
		out.println("<div class=\"btn-group btn-group-vertical pull-left\">"); // line 11: <div class="btn-group btn-group-vertical pull-left">
		out.increaseTab();
		printToolbarButton("showSearch = true", null, "arrow-left");    // line 12: @printToolbarButton("showSearch = true", null, "arrow-left");
		printToolbarButton("openNextResult()", "searchResult.isLastSelected()", "chevron-right"); // line 13: @printToolbarButton("openNextResult()", "searchResult.isLastSelected()", "chevron-right");
		printToolbarButton("openPrevResult()", "searchResult.isFirstSelected()", "chevron-left"); // line 14: @printToolbarButton("openPrevResult()", "searchResult.isFirstSelected()", "chevron-left");
		out.decreaseTab();
		out.println("</div>");                                          // line 15: </div>
		out.println("<div class=\"margin-left\">");                     // line 16: <div class="margin-left">
		out.increaseTab();
		out.print(detailTemplate);                                      // line 17: <%detailTemplate%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 18: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 19: </div>
		out.decreaseTab();
		out.println("</div>");                                          // line 20: </div>
	}
	
	
	private void printToolbarButton(String ngClick, String disabled, String icon)
	{
		out.print("<button class=\"btn\" ng-click=\"");                 // line 26: <button class="btn" ng-click="
		out.print(ngClick);                                             // line 26: <%ngClick%>
		out.print("\"");                                                // line 26: "
		if (disabled != null)                                           // line 27: @if (disabled != null)
		{
			out.print(" ng-disabled=\"");                               // line 28: ng-disabled="
			out.print(disabled);                                        // line 28: <%disabled%>
			out.print("\"");                                            // line 28: "
		}
		out.print("><i class=\"icon-");                                 // line 29: ><i class="icon-
		out.print(icon);                                                // line 29: <%icon%>
		out.println("\"></i></button>");                                // line 29: "></i></button>
	}


	protected Template detailTemplate;
}
