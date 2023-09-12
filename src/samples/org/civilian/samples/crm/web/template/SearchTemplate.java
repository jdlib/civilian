/**
 * Generated from SearchTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.template;


import org.civilian.samples.crm.text.Message;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.LangMixin;


/**
* Generic SearchTemplate
* @param toolbar: if embedded in a module panel, the searchpanel has a toolbar to navigate to
* 		the detail view
*/
public class SearchTemplate extends CspTemplate
{
	public SearchTemplate(boolean withToolbar)
	{
		this.withToolbar = withToolbar;
	}


	@Override protected void init()
	{
		super.init();
		lang = new LangMixin(out);
	}


	@Override protected void exit()
	{
		lang = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<div ng-controller=\"SearchController\" class=\"row-fluid\">"); // line 12: <div ng-controller="SearchController" class="row-fluid">
		out.increaseTab();
		if (withToolbar)                                                // line 13: @if (withToolbar)
		{
			out.println("<div class=\"btn-group btn-group-vertical pull-left\">"); // line 14: <div class="btn-group btn-group-vertical pull-left">
			out.increaseTab();
			out.println("<button ng-disabled=\"searchResult.empty()\" class=\"btn\" ng-click=\"openResult()\"><i class=\"icon-arrow-right\"></i></button>"); // line 15: <button ng-disabled="searchResult.empty()" class="btn" ng-click="openResult()"><i class="icon-arrow-right"></i></button>
			out.println("<button ng-disabled=\"searchResult.empty()\" class=\"btn\"><i class=\"icon-download\"></i></button>"); // line 16: <button ng-disabled="searchResult.empty()" class="btn"><i class="icon-download"></i></button>
			out.println("<button ng-disabled=\"searchResult.empty()\" class=\"btn\"><i class=\"icon-print\"></i></button>"); // line 17: <button ng-disabled="searchResult.empty()" class="btn"><i class="icon-print"></i></button>
			out.decreaseTab();
			out.println("</div>");                                      // line 18: </div>
			out.println("<div class=\"margin-left\">");                 // line 19: <div class="margin-left">
			out.increaseTab();
			printSearchPanel();                                         // line 20: @printSearchPanel();
			out.decreaseTab();
			out.println("</div>");                                      // line 21: </div>
		}
		else                                                            // line 22: @else
		{
			printSearchPanel();                                         // line 23: @printSearchPanel();
		}
		out.decreaseTab();
		out.println("</div>");                                          // line 24: </div>
	}
	
	
	private void printSearchPanel()
	{
		out.println("<div ng-repeat=\"param in searchParams\" class=\"gap-bottom\">"); // line 30: <div ng-repeat="param in searchParams" class="gap-bottom">
		out.increaseTab();
		out.println("<select ng-options=\"f.value as f.text for f in searchFilters\" ng-model=\"param.filter\" class=\"span2\"></select>"); // line 31: <select ng-options="f.value as f.text for f in searchFilters" ng-model="param.filter" class="span2"></select>
		out.println("<input type=\"text\" size=\"40\" ng-model=\"param.value\">"); // line 32: <input type="text" size="40" ng-model="param.value">
		out.println("<button class=\"btn\" ng-click=\"removeSearchParam($index)\" ng-disabled=\"searchParams.length == 1\"><i class=\"icon-minus\"></i></button>"); // line 33: <button class="btn" ng-click="removeSearchParam($index)" ng-disabled="searchParams.length == 1"><i class="icon-minus"></i></button>
		out.println("<button class=\"btn\" ng-click=\"addSearchParam()\" ng-show=\"$last\"><i class=\"icon-plus\"></i></button>"); // line 34: <button class="btn" ng-click="addSearchParam()" ng-show="$last"><i class="icon-plus"></i></button>
		out.decreaseTab();
		out.println("</div>");                                          // line 35: </div>
		out.print("<button class=\"btn btn-primary\" ng-click=\"search()\">"); // line 36: <button class="btn btn-primary" ng-click="search()">
		out.print(lang.msg(Message.Search));                            // line 36: ^{lang.msg(Message.Search)}
		out.println("</button>");                                       // line 36: </button>
		out.print("<button class=\"btn\" ng-click=\"reset()\">");       // line 37: <button class="btn" ng-click="reset()">
		out.print(lang.msg(Message.Reset));                             // line 37: ^{lang.msg(Message.Reset)}
		out.println("</button>");                                       // line 37: </button>
		out.println("<p></p>");                                         // line 38: <p></p>
		out.print("<span ng-hide=\"searchResult.empty()\">{{searchResult.items.length}} "); // line 39: <span ng-hide="searchResult.empty()">{{searchResult.items.length}}
		out.print(lang.msg(Message.Hits));                              // line 39: ^{lang.msg(Message.Hits)}
		out.println("</span>");                                         // line 39: </span>
		out.println("<p></p>");                                         // line 40: <p></p>
		out.println("<table ng-hide=\"searchResult.empty()\" class=\"table table-bordered table-striped table-condensed table-hover table-result no-select\">"); // line 41: <table ng-hide="searchResult.empty()" class="table table-bordered table-striped table-condensed table-hover table-result no-select">
		out.println("<tr class=\"result\">");                           // line 42: <tr class="result">
		out.increaseTab();
		out.println("<th>#</th>");                                      // line 43: <th>#</th>
		out.println("<th ng-repeat=\"col in searchResult.cols\">{{col}}</th>"); // line 44: <th ng-repeat="col in searchResult.cols">{{col}}</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 45: </tr>
		out.println("<tr ng-repeat=\"row in searchResult.rows\" ng-dblclick=\"openResult()\" ng-class=\"{info: $index == searchResult.selected}\" ng-click=\"searchResult.setSelected($index)\">"); // line 46: <tr ng-repeat="row in searchResult.rows" ng-dblclick="openResult()" ng-class="{info: $index == searchResult.selected}" ng-click="searchResult.setSelected($index)">
		out.increaseTab();
		out.println("<td width=\"30px\">{{$index+1}}</td>");            // line 47: <td width="30px">{{$index+1}}</td>
		out.println("<td ng-repeat=\"data in row.data\">{{data}}</td>"); // line 48: <td ng-repeat="data in row.data">{{data}}</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 49: </tr>
		out.println("</table>");                                        // line 50: </table>
	}


	protected boolean withToolbar;
	protected LangMixin lang;
}
