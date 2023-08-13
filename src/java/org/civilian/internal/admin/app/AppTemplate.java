/**
 * Generated from AppTemplate.csp
 * Do not edit.
 */
package org.civilian.internal.admin.app;


import org.civilian.Application;
import org.civilian.template.Template;
import org.civilian.template.mixin.HtmlMixin;


public class AppTemplate extends Template
{
	public AppTemplate(Application app, Template content, int activeTab)
	{
		this.app = app;
		this.content = content;
		this.activeTab = activeTab;
	}


	@Override protected void init()
	{
		super.init();
		html = new HtmlMixin(out);
	}


	@Override protected void print() throws Exception
	{
		out.print("<h3>Application ");                                  // line 7: <h3>Application
		out.print(app.getId());                                         // line 7: <%app.getId()%>
		out.println("</h3>");                                           // line 7: </h3>
		out.println("<div class=\"tabbable\">");                        // line 8: <div class="tabbable">
		out.increaseTab();
		out.println("<ul class=\"nav nav-tabs\">");                     // line 9: <ul class="nav nav-tabs">
		out.increaseTab();
		printTab("Settings", 0, "settings");                            // line 10: @printTab("Settings", 0, "settings");
		printTab("Resources", 1, "resources");                          // line 11: @printTab("Resources", 1, "resources");
		out.decreaseTab();
		out.println("</ul>");                                           // line 12: </ul>
		out.decreaseTab();
		out.println("</div>");                                          // line 13: </div>
		out.println("<div class=\"tab-content\">");                     // line 14: <div class="tab-content">
		out.increaseTab();
		out.print(content);                                             // line 15: <%content%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 16: </div>
	}
	
	
	private void printTab(String label, int tabIndex, String path)
	{
		boolean active = activeTab == tabIndex;                         // line 22: @boolean active = activeTab == tabIndex;
		out.print("<li");                                               // line 23: <li
		if (active)                                                     // line 23: <%?active%>
		{
			out.print(" class=\"active\"");                             // line 23: class="active"
		}
		out.println('>');                                               // line 23: >
		out.increaseTab();
		out.print("<a");                                                // line 24: <a
		if (!active)                                                    // line 24: <%?!active%>
		{
			out.print(" href=\"");                                      // line 24: href="
			out.print(html.url(path));                                  // line 24: <%html.url(path)%>
			out.print("\"");                                            // line 24: "
		}
		out.println('>');                                               // line 24: >
		out.increaseTab();
		out.print(label);                                               // line 25: <%label%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</a>");                                            // line 26: </a>
		out.decreaseTab();
		out.println("</li>");                                           // line 27: </li>
	}


	protected Application app;
	protected Template content;
	protected int activeTab;
	protected HtmlMixin html;
}
