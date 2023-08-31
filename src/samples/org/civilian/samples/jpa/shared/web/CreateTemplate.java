/**
 * Generated from CreateTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.jpa.shared.web;


import org.civilian.template.CspTemplate;
import org.civilian.template.mixin.FormTableMixin;


public class CreateTemplate extends CspTemplate
{
	public CreateTemplate(CreateForm form)
	{
		this.form = form;
	}


	@Override protected void init()
	{
		super.init();
		formTable = new FormTableMixin(out);
	}


	@Override protected void exit()
	{
		formTable = null;
		super.exit();
	}


	@Override protected void print() throws Exception
	{
		out.println("<h1>Create Person</h1>");                          // line 3: <h1>Create Person</h1>
		formTable.print(form);                                          // line 4: @formTable.print(form);
	}


	protected CreateForm form;
	protected FormTableMixin formTable;
}
