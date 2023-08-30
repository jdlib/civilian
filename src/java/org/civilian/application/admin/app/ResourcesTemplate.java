/**
 * Generated from ResourcesTemplate.csp
 * Do not edit.
 */
package org.civilian.application.admin.app;


import org.civilian.application.Application;
import org.civilian.controller.ControllerResourceData;
import org.civilian.resource.Resource;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


public class ResourcesTemplate extends Template
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


	@Override protected void print() throws Exception
	{
		html.script("civilian/admin/angular.min.1.2.13.js");            // line 9: @html.script("civilian/admin/angular.min.1.2.13.js");
		out.println("<script>");                                        // line 10: <script>
		out.println("function Ctrl($scope, $http) {");                  // line 11: function Ctrl($scope, $http) {
		out.increaseTab();
		out.println("$scope.resources = [");                            // line 12: $scope.resources = [
		out.increaseTab();
		Resource rootRes = app.getRootResource();                       // line 13: @Resource rootRes = app.getRootResource();
		if (rootRes != null)                                            // line 14: @if (rootRes != null)
		{
			for (Resource descendant : rootRes.tree())                  // line 15: @for (Resource descendant : rootRes.tree())
			{
				printControllerEntry(descendant);                       // line 16: @printControllerEntry(descendant);
			}
		}
		out.decreaseTab();
		out.println("];");                                              // line 17: ];
		out.println();
		out.println("$scope.showDetails = function(resource) {");       // line 19: $scope.showDetails = function(resource) {
		out.increaseTab();
		out.println("if (!resource.details) {");                        // line 20: if (!resource.details) {
		out.increaseTab();
		out.println("$http({ url: window.location.pathname, method: \"GET\", params: { controller: resource.controller}})."); // line 21: $http({ url: window.location.pathname, method: "GET", params: { controller: resource.controller}}).
		out.increaseTab();
		out.println("success(function(data, status, headers, config) {"); // line 22: success(function(data, status, headers, config) {
		out.increaseTab();
		out.println("resource.details = data;");                        // line 23: resource.details = data;
		out.decreaseTab();
		out.println("});");                                             // line 24: });
		out.decreaseTab();
		out.decreaseTab();
		out.println("}");                                               // line 25: }
		out.println("resource.show = !resource.show;");                 // line 26: resource.show = !resource.show;
		out.decreaseTab();
		out.println("}");                                               // line 27: }
		out.decreaseTab();
		out.println("}");                                               // line 28: }
		out.println("</script>");                                       // line 29: </script>
		out.println("<table class=\"table table-bordered table-condensed\" ng-app ng-controller=\"Ctrl\">"); // line 30: <table class="table table-bordered table-condensed" ng-app ng-controller="Ctrl">
		out.println("<tr>");                                            // line 31: <tr>
		out.increaseTab();
		out.println("<td width=\"30px\">#</td>");                       // line 32: <td width="30px">#</td>
		out.println("<th width=\"33%\">Path</th>");                     // line 33: <th width="33%">Path</th>
		out.println("<th>mapped to class</th>");                        // line 34: <th>mapped to class</th>
		out.println("<th></th>");                                       // line 35: <th></th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 36: </tr>
		out.println("<tr>");                                            // line 37: <tr>
		out.increaseTab();
		out.println("<td></td>");                                       // line 38: <td></td>
		out.println("<td>");                                            // line 39: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.path\" placeholder=\"filter path\">"); // line 40: <input type="search" ng-model="search.path" placeholder="filter path">
		out.decreaseTab();
		out.println("</td>");                                           // line 41: </td>
		out.println("<td>");                                            // line 42: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.controller\" placeholder=\"filter class\">"); // line 43: <input type="search" ng-model="search.controller" placeholder="filter class">
		out.decreaseTab();
		out.println("</td>");                                           // line 44: </td>
		out.println("<td></td>");                                       // line 45: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 46: </tr>
		out.println("<tr ng-repeat-start=\"r in resources | filter:search:strict\">"); // line 47: <tr ng-repeat-start="r in resources | filter:search:strict">
		out.increaseTab();
		out.println("<td>{{$index+1}}</td>");                           // line 48: <td>{{$index+1}}</td>
		out.println("<td><a href=\"javascript:;\" ng-click=\"showDetails(r)\">{{r.path}}</a></td>"); // line 49: <td><a href="javascript:;" ng-click="showDetails(r)">{{r.path}}</a></td>
		out.println("<td>{{r.controller}}</td>");                       // line 50: <td>{{r.controller}}</td>
		out.print("<td><a ng-href=\"");                                 // line 51: <td><a ng-href="
		out.print(app.getPath());                                       // line 51: <%app.getPath()%>
		out.print("{{r.path}}\" target=\"");                            // line 51: {{r.path}}" target="
		out.print(app.getId());                                         // line 51: <%app.getId()%>
		out.println("\"><i class=\"icon-share-alt\"></i></a></td>");    // line 51: "><i class="icon-share-alt"></i></a></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 52: </tr>
		out.println("<tr ng-repeat-end ng-show=\"r.show\">");           // line 53: <tr ng-repeat-end ng-show="r.show">
		out.increaseTab();
		out.println("<td colspan=\"2\"></td>");                         // line 54: <td colspan="2"></td>
		out.println("<td>");                                            // line 55: <td>
		out.increaseTab();
		out.println("<ul>");                                            // line 56: <ul>
		out.increaseTab();
		out.println("<li ng-repeat=\"detail in r.details\">{{detail.method}}(): {{detail.info}}</li>"); // line 57: <li ng-repeat="detail in r.details">{{detail.method}}(): {{detail.info}}</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 58: </ul>
		out.decreaseTab();
		out.println("</td>");                                           // line 59: </td>
		out.println("<td></td>");                                       // line 60: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 61: </tr>
		out.println("</table>");                                        // line 62: </table>
	}
	
	
	private void printControllerEntry(Resource resource)
	{
		ControllerResourceData data = ControllerResourceData.of(resource); // line 68: @ControllerResourceData data = ControllerResourceData.of(resource);
		if (data != null)                                               // line 69: @if (data != null)
		{
			String path = resource.getRoute().toString();               // line 70: @String path = resource.getRoute().toString();
			out.print("{ controller: \"");                              // line 71: { controller: "
			out.print(data.getSignature());                             // line 71: <%data.getSignature()%>
			out.print("\", path: \"");                                  // line 71: ", path: "
			out.print(path.length() != 0 ? path : "/");                 // line 71: <%path.length() != 0 ? path : "/"%>
			out.println("\" },");                                       // line 71: " },
		}
	}


	protected Application app;
	protected HtmlMixin html;
}
