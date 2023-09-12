/**
 * Generated from ControllerTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.crm.web.template;


import org.civilian.template.CspTemplate;
import org.civilian.template.Template;


public class ControllerTemplate extends CspTemplate
{
	public ControllerTemplate(Template content, String controller)
	{
		this.content = content;
		this.controller = controller;
	}


	@Override protected void print() throws Exception
	{
		out.print("<div ng-controller=\"");                             // line 6: <div ng-controller="
		out.print(controller);                                          // line 6: ^controller
		out.println("\">");                                             // line 6: ">
		out.increaseTab();
		out.print(content);                                             // line 7: ^content
		out.printlnIfNotEmpty();
		out.decreaseTab();
		out.println("</div>");                                          // line 8: </div>
	}


	protected Template content;
	protected String controller;
}
