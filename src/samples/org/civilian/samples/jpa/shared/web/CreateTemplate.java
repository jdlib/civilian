/**
 * Generated from CreateTemplate.csp
 * Do not edit.
 */
package org.civilian.samples.jpa.shared.web;


import org.civilian.Template;
import org.civilian.response.ResponseWriter;
import org.civilian.template.mixin.FormTableMixin;


public class CreateTemplate extends Template
{
	public CreateTemplate(CreateForm form)
	{
		this.form = form;
	}


	@Override public synchronized void print(ResponseWriter out) throws Exception
	{
		try
		{
			formTable = new FormTableMixin(out);
			super.print(out);
		}
		finally
		{
			formTable = null;
		}
	}


	@Override protected void print() throws Exception
	{
		out.println("<h1>Create Person</h1>");                          // line 3: <h1>Create Person</h1>
		formTable.print(form);                                          // line 4: @formTable.print(form);
	}


	private CreateForm form;
	private FormTableMixin formTable;
}
