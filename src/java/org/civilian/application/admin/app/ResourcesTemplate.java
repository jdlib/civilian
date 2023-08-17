/**
 * Generated from ResourcesTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin.app;


import java.util.Iterator;

import org.civilian.application.Application;
import org.civilian.controller.ControllerSignature;
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
		html.script("civilian/admin/angular.min.1.2.13.js");            // line 10: @html.script("civilian/admin/angular.min.1.2.13.js");
		out.println("<script>");                                        // line 11: <script>
		out.println("function Ctrl($scope, $http) {");                  // line 12: function Ctrl($scope, $http) {
		out.increaseTab();
		out.println("$scope.resources = [");                            // line 13: $scope.resources = [
		out.increaseTab();
		Resource rootRes = app.getRootResource();                       // line 14: @Resource rootRes = app.getRootResource();
		if (rootRes != null)                                            // line 15: @if (rootRes != null)
		{
			Iterator<Resource> it = rootRes.iterator();                 // line 16: @Iterator<Resource> it = rootRes.iterator();
			while(it.hasNext())                                         // line 17: @while(it.hasNext())
			{
				Resource resource = it.next();                          // line 18: @Resource resource = it.next();
				String route = resource.getRoute().toString();          // line 19: @String route = resource.getRoute().toString();
				printControllerEntry(resource.getControllerSignature(), route); // line 20: @printControllerEntry(resource.getControllerSignature(), route);
			}
		}
		out.decreaseTab();
		out.println("];");                                              // line 21: ];
		out.println();
		out.println("$scope.showDetails = function(resource) {");       // line 23: $scope.showDetails = function(resource) {
		out.increaseTab();
		out.println("if (!resource.details) {");                        // line 24: if (!resource.details) {
		out.increaseTab();
		out.println("$http({ url: window.location.pathname, method: \"GET\", params: { controller: resource.controller}})."); // line 25: $http({ url: window.location.pathname, method: "GET", params: { controller: resource.controller}}).
		out.increaseTab();
		out.println("success(function(data, status, headers, config) {"); // line 26: success(function(data, status, headers, config) {
		out.increaseTab();
		out.println("resource.details = data;");                        // line 27: resource.details = data;
		out.decreaseTab();
		out.println("});");                                             // line 28: });
		out.decreaseTab();
		out.decreaseTab();
		out.println("}");                                               // line 29: }
		out.println("resource.show = !resource.show;");                 // line 30: resource.show = !resource.show;
		out.decreaseTab();
		out.println("}");                                               // line 31: }
		out.decreaseTab();
		out.println("}");                                               // line 32: }
		out.println("</script>");                                       // line 33: </script>
		out.println("<table class=\"table table-bordered table-condensed\" ng-app ng-controller=\"Ctrl\">"); // line 34: <table class="table table-bordered table-condensed" ng-app ng-controller="Ctrl">
		out.println("<tr>");                                            // line 35: <tr>
		out.increaseTab();
		out.println("<td width=\"30px\">#</td>");                       // line 36: <td width="30px">#</td>
		out.println("<th width=\"33%\">Path</th>");                     // line 37: <th width="33%">Path</th>
		out.println("<th>mapped to class</th>");                        // line 38: <th>mapped to class</th>
		out.println("<th></th>");                                       // line 39: <th></th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 40: </tr>
		out.println("<tr>");                                            // line 41: <tr>
		out.increaseTab();
		out.println("<td></td>");                                       // line 42: <td></td>
		out.println("<td>");                                            // line 43: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.path\" placeholder=\"filter path\">"); // line 44: <input type="search" ng-model="search.path" placeholder="filter path">
		out.decreaseTab();
		out.println("</td>");                                           // line 45: </td>
		out.println("<td>");                                            // line 46: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.controller\" placeholder=\"filter class\">"); // line 47: <input type="search" ng-model="search.controller" placeholder="filter class">
		out.decreaseTab();
		out.println("</td>");                                           // line 48: </td>
		out.println("<td></td>");                                       // line 49: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 50: </tr>
		out.println("<tr ng-repeat-start=\"r in resources | filter:search:strict\">"); // line 51: <tr ng-repeat-start="r in resources | filter:search:strict">
		out.increaseTab();
		out.println("<td>{{$index+1}}</td>");                           // line 52: <td>{{$index+1}}</td>
		out.println("<td><a href=\"javascript:;\" ng-click=\"showDetails(r)\">{{r.path}}</a></td>"); // line 53: <td><a href="javascript:;" ng-click="showDetails(r)">{{r.path}}</a></td>
		out.println("<td>{{r.controller}}</td>");                       // line 54: <td>{{r.controller}}</td>
		out.print("<td><a ng-href=\"");                                 // line 55: <td><a ng-href="
		out.print(app.getPath());                                       // line 55: <%app.getPath()%>
		out.print("{{r.path}}\" target=\"");                            // line 55: {{r.path}}" target="
		out.print(app.getId());                                         // line 55: <%app.getId()%>
		out.println("\"><i class=\"icon-share-alt\"></i></a></td>");    // line 55: "><i class="icon-share-alt"></i></a></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 56: </tr>
		out.println("<tr ng-repeat-end ng-show=\"r.show\">");           // line 57: <tr ng-repeat-end ng-show="r.show">
		out.increaseTab();
		out.println("<td colspan=\"2\"></td>");                         // line 58: <td colspan="2"></td>
		out.println("<td>");                                            // line 59: <td>
		out.increaseTab();
		out.println("<ul>");                                            // line 60: <ul>
		out.increaseTab();
		out.println("<li ng-repeat=\"detail in r.details\">{{detail.method}}(): {{detail.info}}</li>"); // line 61: <li ng-repeat="detail in r.details">{{detail.method}}(): {{detail.info}}</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 62: </ul>
		out.decreaseTab();
		out.println("</td>");                                           // line 63: </td>
		out.println("<td></td>");                                       // line 64: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 65: </tr>
		out.println("</table>");                                        // line 66: </table>
	}
	
	
	private void printControllerEntry(ControllerSignature sig, String path)
	{
		if (sig != null)                                                // line 72: @if (sig != null)
		{
			out.print("{ controller: \"");                              // line 73: { controller: "
			out.print(sig);                                             // line 73: <%sig%>
			out.print("\", path: \"");                                  // line 73: ", path: "
			out.print(path.length() != 0 ? path : "/");                 // line 73: <%path.length() != 0 ? path : "/"%>
			out.println("\" },");                                       // line 73: " },
		}
	}


	protected Application app;
	protected HtmlMixin html;
}
