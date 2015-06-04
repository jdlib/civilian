/**
 * Generated from ControllerTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.template;


import org.civilian.template.Template;


public class ControllerTemplate extends Template
{
	public ControllerTemplate(Template content, String controller)
	{
		this.content = content;
		this.controller = controller;
	}


	@Override protected void print() throws Exception
	{
		out.print("<div ng-controller=\"");                             // line 3: <div ng-controller="
		out.print(controller);                                          // line 3: <%controller%>
		out.println("\">");                                             // line 3: ">
		out.increaseTab();
		out.print(content);                                             // line 4: <%content%>
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 5: </div>
	}


	private Template content;
	private String controller;
}
