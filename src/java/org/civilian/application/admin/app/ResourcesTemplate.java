/**
 * Generated from ResourcesTemplate.csp
 * Do not edit.
 */
package org.civilian.application.admin.app;


import org.civilian.application.Application;
import org.civilian.resource.Resource;
import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.HtmlMixin;


public class ResourcesTemplate extends CspTemplate
{
	public ResourcesTemplate(Application app)
	{
		this.app = app;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void exit()
	{
		html = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		html.script("civilian/admin/angular.min.1.2.13.js");            // line 8: @html.script("civilian/admin/angular.min.1.2.13.js");
		out.println("<script>");                                        // line 9: <script>
		out.println("function Ctrl($scope, $http) {");                  // line 10: function Ctrl($scope, $http) {
		out.increaseTab();
		out.println("$scope.resources = [");                            // line 11: $scope.resources = [
		out.increaseTab();
		Resource rootRes = app.getRootResource();                       // line 12: @Resource rootRes = app.getRootResource();
		if (rootRes != null)                                            // line 13: @if (rootRes != null)
		{
			for (Resource descendant : rootRes.tree())                  // line 14: @for (Resource descendant : rootRes.tree())
			{
				printControllerEntry(descendant);                       // line 15: @printControllerEntry(descendant);
			}
		}
		out.decreaseTab();
		out.println("];");                                              // line 16: ];
		out.println();
		out.println("$scope.showDetails = function(resource) {");       // line 18: $scope.showDetails = function(resource) {
		out.increaseTab();
		out.println("if (!resource.details) {");                        // line 19: if (!resource.details) {
		out.increaseTab();
		out.println("$http({ url: window.location.pathname, method: \"GET\", params: { controller: resource.controller}})."); // line 20: $http({ url: window.location.pathname, method: "GET", params: { controller: resource.controller}}).
		out.increaseTab();
		out.println("success(function(data, status, headers, config) {"); // line 21: success(function(data, status, headers, config) {
		out.increaseTab();
		out.println("resource.details = data;");                        // line 22: resource.details = data;
		out.decreaseTab();
		out.println("});");                                             // line 23: });
		out.decreaseTab();
		out.decreaseTab();
		out.println("}");                                               // line 24: }
		out.println("resource.show = !resource.show;");                 // line 25: resource.show = !resource.show;
		out.decreaseTab();
		out.println("}");                                               // line 26: }
		out.decreaseTab();
		out.println("}");                                               // line 27: }
		out.println("</script>");                                       // line 28: </script>
		out.println("<table class=\"table table-bordered table-condensed\" ng-app ng-controller=\"Ctrl\">"); // line 29: <table class="table table-bordered table-condensed" ng-app ng-controller="Ctrl">
		out.println("<tr>");                                            // line 30: <tr>
		out.increaseTab();
		out.println("<td width=\"30px\">#</td>");                       // line 31: <td width="30px">#</td>
		out.println("<th width=\"33%\">Path</th>");                     // line 32: <th width="33%">Path</th>
		out.println("<th>mapped to class</th>");                        // line 33: <th>mapped to class</th>
		out.println("<th></th>");                                       // line 34: <th></th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 35: </tr>
		out.println("<tr>");                                            // line 36: <tr>
		out.increaseTab();
		out.println("<td></td>");                                       // line 37: <td></td>
		out.println("<td>");                                            // line 38: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.path\" placeholder=\"filter path\">"); // line 39: <input type="search" ng-model="search.path" placeholder="filter path">
		out.decreaseTab();
		out.println("</td>");                                           // line 40: </td>
		out.println("<td>");                                            // line 41: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.controller\" placeholder=\"filter class\">"); // line 42: <input type="search" ng-model="search.controller" placeholder="filter class">
		out.decreaseTab();
		out.println("</td>");                                           // line 43: </td>
		out.println("<td></td>");                                       // line 44: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 45: </tr>
		out.println("<tr ng-repeat-start=\"r in resources | filter:search:strict\">"); // line 46: <tr ng-repeat-start="r in resources | filter:search:strict">
		out.increaseTab();
		out.println("<td>{{$index+1}}</td>");                           // line 47: <td>{{$index+1}}</td>
		out.println("<td><a href=\"javascript:;\" ng-click=\"showDetails(r)\">{{r.path}}</a></td>"); // line 48: <td><a href="javascript:;" ng-click="showDetails(r)">{{r.path}}</a></td>
		out.println("<td>{{r.controller}}</td>");                       // line 49: <td>{{r.controller}}</td>
		out.print("<td><a ng-href=\"");                                 // line 50: <td><a ng-href="
		out.print(app.getPath());                                       // line 50: <%app.getPath()%>
		out.print("{{r.path}}\" target=\"");                            // line 50: {{r.path}}" target="
		out.print(app.getId());                                         // line 50: <%app.getId()%>
		out.println("\"><i class=\"icon-share-alt\"></i></a></td>");    // line 50: "><i class="icon-share-alt"></i></a></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 51: </tr>
		out.println("<tr ng-repeat-end ng-show=\"r.show\">");           // line 52: <tr ng-repeat-end ng-show="r.show">
		out.increaseTab();
		out.println("<td colspan=\"2\"></td>");                         // line 53: <td colspan="2"></td>
		out.println("<td>");                                            // line 54: <td>
		out.increaseTab();
		out.println("<ul>");                                            // line 55: <ul>
		out.increaseTab();
		out.println("<li ng-repeat=\"detail in r.details\">{{detail.method}}(): {{detail.info}}</li>"); // line 56: <li ng-repeat="detail in r.details">{{detail.method}}(): {{detail.info}}</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 57: </ul>
		out.decreaseTab();
		out.println("</td>");                                           // line 58: </td>
		out.println("<td></td>");                                       // line 59: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 60: </tr>
		out.println("</table>");                                        // line 61: </table>
	}
	
	
	private void printControllerEntry(Resource resource)
	{
		Object data = resource.getData(); // is the controllersignature // line 67: @Object data = resource.getData(); // is the controllersignature
		if (data != null)                                               // line 68: @if (data != null)
		{
			String path = resource.getRoute().toString();               // line 69: @String path = resource.getRoute().toString();
			out.print("{ controller: \"");                              // line 70: { controller: "
			out.print(data);                                            // line 70: ^data
			out.print("\", path: \"");                                  // line 70: ", path: "
			out.print(path.length() != 0 ? path : "/");                 // line 70: <%path.length() != 0 ? path : "/"%>
			out.println("\" },");                                       // line 70: " },
		}
	}


	protected Application app;
	protected HtmlMixin html;
}
