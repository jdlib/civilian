/**
 * Generated from IndexTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin;


import java.util.Date;
import org.civilian.Application;
import org.civilian.Server;
import org.civilian.Version;
import org.civilian.resource.Url;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


public class IndexTemplate extends Template
{
	public IndexTemplate(Server server)
	{
		this.server = server;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.println("<h3>Context Settings</h3>");                       // line 10: <h3>Context Settings</h3>
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 11: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 12: <tr>
		out.increaseTab();
		out.println("<th>Parameter</th>");                              // line 13: <th>Parameter</th>
		out.println("<th>Value</th>");                                  // line 14: <th>Value</th>
		out.println("<th>API</th>");                                    // line 15: <th>API</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 16: </tr>
		out.println("<tr>");                                            // line 17: <tr>
		out.increaseTab();
		out.println("<td>Develop</td>");                                // line 18: <td>Develop</td>
		out.print("<td>");                                              // line 19: <td>
		out.print(server.develop());                                    // line 19: <%server.develop()%>
		out.println("</td>");                                           // line 19: </td>
		out.println("<td>org.civilian.Server.develop()</td>");          // line 20: <td>org.civilian.Server.develop()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 21: </tr>
		out.println("<tr>");                                            // line 22: <tr>
		out.increaseTab();
		out.println("<td>Server Path</td>");                            // line 23: <td>Server Path</td>
		out.print("<td>");                                              // line 24: <td>
		out.print(server.getPath());                                    // line 24: <%server.getPath()%>
		out.println("</td>");                                           // line 24: </td>
		out.println("<td>org.civilian.Server.getPath()</td>");          // line 25: <td>org.civilian.Server.getPath()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 26: </tr>
		out.println("<tr>");                                            // line 27: <tr>
		out.increaseTab();
		out.println("<td>Server Class</td>");                           // line 28: <td>Server Class</td>
		out.print("<td>");                                              // line 29: <td>
		out.print(server.getClass().getName());                         // line 29: <%server.getClass().getName()%>
		out.println("</td>");                                           // line 29: </td>
		out.println("<td>org.civilian.Server.getClass()</td>");         // line 30: <td>org.civilian.Server.getClass()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 31: </tr>
		out.println("<tr>");                                            // line 32: <tr>
		out.increaseTab();
		out.println("<td>Civilian Version</td>");                       // line 33: <td>Civilian Version</td>
		out.print("<td>");                                              // line 34: <td>
		out.print(Version.VALUE);                                       // line 34: <%Version.VALUE%>
		out.println("</td>");                                           // line 34: </td>
		out.println("<td>org.civilian.Version.VALUE</td>");             // line 35: <td>org.civilian.Version.VALUE</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 36: </tr>
		out.println("<tr>");                                            // line 37: <tr>
		out.increaseTab();
		out.println("<td>Server</td>");                                 // line 38: <td>Server</td>
		out.print("<td>");                                              // line 39: <td>
		out.print(server.getServerInfo());                              // line 39: <%server.getServerInfo()%>
		out.print(" ");                                                 // line 39: 
		out.print(server.getServerVersion());                           // line 39: <%server.getServerVersion()%>
		out.println("</td>");                                           // line 39: </td>
		out.println("<td>org.civilian.Server.getServerInfo(), .getServerVersion()</td>"); // line 40: <td>org.civilian.Server.getServerInfo(), .getServerVersion()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 41: </tr>
		out.println("<tr>");                                            // line 42: <tr>
		out.increaseTab();
		out.println("<td>Java Version, VM</td>");                       // line 43: <td>Java Version, VM</td>
		out.print("<td>");                                              // line 44: <td>
		out.print(System.getProperty("java.version"));                  // line 44: <%System.getProperty("java.version")%>
		out.print(", ");                                                // line 44: ,
		out.print(System.getProperty("java.vm.name"));                  // line 44: <%System.getProperty("java.vm.name")%>
		out.println("</td>");                                           // line 44: </td>
		out.println("<td>java.lang.System.getProperty()</td>");         // line 45: <td>java.lang.System.getProperty()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 46: </tr>
		out.println("<tr>");                                            // line 47: <tr>
		out.increaseTab();
		out.println("<td>Java Total Used Memory</td>");                 // line 48: <td>Java Total Used Memory</td>
		out.print("<td>");                                              // line 49: <td>
		out.print(getMemory());                                         // line 49: <%getMemory()%>
		out.println("</td>");                                           // line 49: </td>
		out.println("<td>java.lang.Runtime.totalMemory()</td>");        // line 50: <td>java.lang.Runtime.totalMemory()</td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 51: </tr>
		out.println("<tr>");                                            // line 52: <tr>
		out.increaseTab();
		out.println("<td>System Date</td>");                            // line 53: <td>System Date</td>
		out.print("<td>");                                              // line 54: <td>
		out.print(new Date());                                          // line 54: <%new Date()%>
		out.println("</td>");                                           // line 54: </td>
		out.println("<td></td>");                                       // line 55: <td></td>
		out.decreaseTab();
		out.println("</tr>");                                           // line 56: </tr>
		out.println("</table>");                                        // line 57: </table>
		out.println();
		out.println("<h3>Installed Applications</h3>");                 // line 59: <h3>Installed Applications</h3>
		out.println("<table class=\"table table-striped table-bordered table-condensed\">"); // line 60: <table class="table table-striped table-bordered table-condensed">
		out.println("<tr>");                                            // line 61: <tr>
		out.increaseTab();
		out.println("<th>Application</th>");                            // line 62: <th>Application</th>
		out.println("<th>Path</th>");                                   // line 63: <th>Path</th>
		out.println("<th>Status</th>");                                 // line 64: <th>Status</th>
		out.println("<th>App Class</th>");                              // line 65: <th>App Class</th>
		out.decreaseTab();
		out.println("</tr>");                                           // line 66: </tr>
		Url appUrl = html.url(AdminResources.root.$appId.settings);     // line 67: @Url appUrl = html.url(AdminResources.root.$appId.settings);
		for (Application app : server.getApplications())                // line 68: @for (Application app : server.getApplications())
		{
			appUrl.setPathParam(app.getId());                           // line 69: @appUrl.setPathParam(app.getId());
			out.println("<tr>");                                        // line 70: <tr>
			out.increaseTab();
			// link within the admin app                                // line 71: @// link within the admin app
			out.print("<td><a href=\"");                                // line 72: <td><a href="
			out.print(appUrl);                                          // line 72: <%appUrl%>
			out.print("\">");                                           // line 72: ">
			out.print(app.getId());                                     // line 72: <%app.getId()%>
			out.println("</a></td>");                                   // line 72: </a></td>
			// link to the application itself                           // line 73: @// link to the application itself
			out.print("<td><a target=\"_blank\" href=\"");              // line 74: <td><a target="_blank" href="
			out.print(html.url(app));                                   // line 74: <%html.url(app)%>
			out.print("\">");                                           // line 74: ">
			out.print(app.getPath());                                   // line 74: <%app.getPath()%>
			out.println("</a></td>");                                   // line 74: </a></td>
			out.print("<td>");                                          // line 75: <td>
			out.print(app.getStatus());                                 // line 75: <%app.getStatus()%>
			out.println("</td>");                                       // line 75: </td>
			out.print("<td>");                                          // line 76: <td>
			out.print(app.getClass().getName());                        // line 76: <%app.getClass().getName()%>
			out.println("</td>");                                       // line 76: </td>
			out.decreaseTab();
			out.println("</tr>");                                       // line 77: </tr>
		}
		out.println("</table>");                                        // line 78: </table>
	}
	
	
	private String getMemory()
	{
		long memoryKb   = Runtime.getRuntime().totalMemory() / 1024;
		String memoryMb = String.valueOf(((double)memoryKb) / 1024);
		int p = memoryMb.indexOf('.');
		if (memoryMb.length() > p + 4)
			memoryMb = memoryMb.substring(0, p + 4);
		return memoryMb + " MB";
	}


	protected Server server;
	protected HtmlMixin html;
}
