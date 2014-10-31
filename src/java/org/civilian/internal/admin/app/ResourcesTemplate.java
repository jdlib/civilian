/**
 * Generated from ResourcesTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin.app;


import java.util.Iterator;
import org.civilian.Application;
import org.civilian.Resource;
import org.civilian.Template;
import org.civilian.response.ResponseWriter;
import org.civilian.template.mixin.HtmlMixin;


public class ResourcesTemplate extends Template
{
	public ResourcesTemplate(Application app)
	{
		this.app = app;
	}


	@Override public synchronized void print(ResponseWriter out) throws Exception
	{
		try
		{
			html = new HtmlMixin(out);
			super.print(out);
		}
		finally
		{
			html = null;
		}
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
			Iterator<Resource> it = rootRes.iterator();                 // line 15: @Iterator<Resource> it = rootRes.iterator();
			while(it.hasNext())                                         // line 16: @while(it.hasNext())
			{
				Resource resource = it.next();                          // line 17: @Resource resource = it.next();
				String route = resource.getRoute().toString();          // line 18: @String route = resource.getRoute().toString();
				printControllerEntry(resource.getControllerSignature(), route); // line 19: @printControllerEntry(resource.getControllerSignature(), route);
			}
		}
		out.decreaseTab();
		out.println("];");                                              // line 20: ];
		out.println();
		out.println("$scope.showDetails = function(resource) {");       // line 22: $scope.showDetails = function(resource) {
		out.increaseTab();
		out.println("if (!resource.details) {");                        // line 23: if (!resource.details) {
		out.increaseTab();
		out.println("$http({ url: window.location.pathname, method: \"GET\", params: { controller: resource.controller}})."); // line 24: $http({ url: window.location.pathname, method: "GET", params: { controller: resource.controller}}).
		out.increaseTab();
		out.println("success(function(data, status, headers, config) {"); // line 25: success(function(data, status, headers, config) {
		out.increaseTab();
		out.println("resource.details = data;");                        // line 26: resource.details = data;
		out.decreaseTab();
		out.println("});");                                             // line 27: });
		out.decreaseTab();
		out.decreaseTab();
		out.println("}");                                               // line 28: }
		out.println("resource.show = !resource.show;");                 // line 29: resource.show = !resource.show;
		out.decreaseTab();
		out.println("}");                                               // line 30: }
		out.decreaseTab();
		out.println("}");                                               // line 31: }
		out.println("</script>");                                       // line 32: </script>
		out.println("<table class=\"table table-bordered table-condensed\" ng-app ng-controller=\"Ctrl\">"); // line 33: <table class="table table-bordered table-condensed" ng-app ng-controller="Ctrl">
		out.println("<tr>");                                            // line 34: <tr>
		out.increaseTab();
		out.println("<td width=\"30px\">#</td>");                       // line 35: <td width="30px">#</td>
		out.println("<th width=\"33%\">Path</th>");                     // line 36: <th width="33%">Path</th>
		out.println("<th>mapped to class</th>");                        // line 37: <th>mapped to class</th>
		out.println("<th></th>");                                       // line 38: <th></th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 39: </tr>
		out.println("<tr>");                                            // line 40: <tr>
		out.increaseTab();
		out.println("<td></td>");                                       // line 41: <td></td>
		out.println("<td>");                                            // line 42: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.path\" placeholder=\"filter path\">"); // line 43: <input type="search" ng-model="search.path" placeholder="filter path">
		out.decreaseTab();
		out.println("</td>");                                           // line 44: </td>
		out.println("<td>");                                            // line 45: <td>
		out.increaseTab();
		out.println("<input type=\"search\" ng-model=\"search.controller\" placeholder=\"filter class\">"); // line 46: <input type="search" ng-model="search.controller" placeholder="filter class">
		out.decreaseTab();
		out.println("</td>");                                           // line 47: </td>
		out.println("<td></td>");                                       // line 48: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 49: </tr>
		out.println("<tr ng-repeat-start=\"r in resources | filter:search:strict\">"); // line 50: <tr ng-repeat-start="r in resources | filter:search:strict">
		out.increaseTab();
		out.println("<td>{{$index+1}}</td>");                           // line 51: <td>{{$index+1}}</td>
		out.println("<td><a href=\"javascript:;\" ng-click=\"showDetails(r)\">{{r.path}}</a></td>"); // line 52: <td><a href="javascript:;" ng-click="showDetails(r)">{{r.path}}</a></td>
		out.println("<td>{{r.controller}}</td>");                       // line 53: <td>{{r.controller}}</td>
		out.print("<td><a ng-href=\"");                                 // line 54: <td><a ng-href="
		out.print(app.getPath());                                       // line 54: <%app.getPath()%>
		out.print("{{r.path}}\" target=\"");                            // line 54: {{r.path}}" target="
		out.print(app.getId());                                         // line 54: <%app.getId()%>
		out.println("\"><i class=\"icon-share-alt\"></i></a></td>");    // line 54: "><i class="icon-share-alt"></i></a></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 55: </tr>
		out.println("<tr ng-repeat-end ng-show=\"r.show\">");           // line 56: <tr ng-repeat-end ng-show="r.show">
		out.increaseTab();
		out.println("<td colspan=\"2\"></td>");                         // line 57: <td colspan="2"></td>
		out.println("<td>");                                            // line 58: <td>
		out.increaseTab();
		out.println("<ul>");                                            // line 59: <ul>
		out.increaseTab();
		out.println("<li ng-repeat=\"detail in r.details\">{{detail.method}}(): {{detail.info}}</li>"); // line 60: <li ng-repeat="detail in r.details">{{detail.method}}(): {{detail.info}}</li>
		out.decreaseTab();
		out.println("</ul>");                                           // line 61: </ul>
		out.decreaseTab();
		out.println("</td>");                                           // line 62: </td>
		out.println("<td></td>");                                       // line 63: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 64: </tr>
		out.println("</table>");                                        // line 65: </table>
	}
	
	
	private void printControllerEntry(String ctrlSig, String path)
	{
		if (ctrlSig != null)                                            // line 71: @if (ctrlSig != null)
		{
			out.print("{ controller: \"");                              // line 72: { controller: "
			out.print(ctrlSig);                                         // line 72: <%ctrlSig%>
			out.print("\", path: \"");                                  // line 72: ", path: "
			out.print(path.length() != 0 ? path : "/");                 // line 72: <%path.length() != 0 ? path : "/"%>
			out.println("\" },");                                       // line 72: " },
		}
	}


	private Application app;
	private HtmlMixin html;
}
