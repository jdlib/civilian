/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.civilian.template.mixin;


import org.civilian.form.Control;
import org.civilian.form.Control.Category;
import org.civilian.form.Form;
import org.civilian.template.HtmlUtil;
import org.civilian.template.CspWriter;
import org.civilian.util.Check;


/**
 * A TemplateMixin to print {@link Form forms} in a table layout.
 */
public class FormTableMixin
{
	/**
	 * Creates a new FormTableMixin.
	 * @param out a CspWriter
	 */
	public FormTableMixin(CspWriter out)
	{
		this.out = Check.notNull(out, "out");
	}
	

	/**
	 * Defines a css class value which is added to labels
	 * of required controls.
	 * @param s a css class
	 * @return this
	 */
	public FormTableMixin setRequiredLabelClass(String s)
	{
		requiredLabelClass_ = s;
		return this;
	}
	
	
	/**
	 * Defines a css class value which is added to error controls.
	 * @param s a css class
	 * @return this
	 */
	public FormTableMixin setErrorControlClass(String s)
	{
		errorControlClass_ = s;
		return this;
	}

	
	private String translate(Object label)
	{
		if (label == null)
			return null;
		else if (label instanceof String)
			return (String)label;
		
		if (lang_ == null)
			lang_ = new LangMixin(out);
		return lang_.msg(label);
	}
	
	
	/**
	 * Prints the label of the control.
	 * The default implementation just prints the label of the field.
	 * If the field required and a {@link #setRequiredLabelClass(String) required css class}
	 * is defined then the label is wrapped in a span with that css class.
	 * @param control a control
	 * @see Control#getLabel()
	 */
	public void label(Control<?> control)
	{
		String label = translate(control.getLabel());
		if ((requiredLabelClass_ != null) && control.isRequired())
		{
			out.print("<span class=\"");
			out.print(requiredLabelClass_);
			out.print("\">");
			label(label);
			out.print("</span>");
		}
		else
			label(label);
	}

	
	/**
	 * Escapes and prints a label.
	 * @param text a text
	 */
	public void label(String text)
	{
		if (text != null)
			HtmlUtil.escape(out, text, false);
	}
	
	
	/**
	 * Prints a table cell containing the label of the control.
	 * @param control a control
	 */
	public void labelCell(Control<?> control)
	{
		out.print("<td");
		if (control.getRows() > 1)
			out.print(" valign=\"top\"");
		out.print(">");
		label(control);
		out.println("</td>");
	}
	
	
	/**
	 * Prints a table cell containing the label.
	 * @param label a label
	 */
	public void labelCell(String label)
	{
		out.print("<td>");
		if (label != null)
			label(label);
		out.println("</td>");
	}
	
	
	/**
	 * Prints the control.
	 * If the control has an error and an {@link #setErrorControlClass(String) error css class}
	 * is defined, then this class will be added to the control element.
	 * @param control a control
	 * @see Control#hasError()
	 */
	public void control(Control<?> control)
	{
		if (control != null)
		{
			if ((errorControlClass_ != null) && control.hasError())
				control.print(out, "class", errorControlClass_);
			else
				control.print(out);
		}
	}

	
	/**
	 * Prints a table cell containing the control.
	 * @param control a control
	 */
	public void cell(Control<?> control)
	{
		out.print("<td>");
		control(control);
		out.println("</td>");
	}
	
	
	/**
	 * Prints two table cells containing the control label
	 * and the control.
	 * @param control a control
	 */
	public void cells(Control<?> control)
	{
		labelCell(control);
		cell(control);
	}
	

	/**
	 * Calls {@link #cells(Control)} for every control in the list.
	 * @param controls some controls
	 */
	public void cells(Control<?>... controls)
	{
		for (int i=0; i<controls.length; i++) 
			cells(controls[i]);
	}
	

	/**
	 * Prints a table row with two table cells containing the control label
	 * and the control.
	 * @param control a control
	 */
	public void row(Control<?> control)
	{
		out.println("<tr>");
			out.increaseTab();
			cells(control);
			out.decreaseTab();
		out.println("</tr>");
	}
	

	/**
	 * Prints a table row with and cells for all the controls and its labels.
	 * @param controls some controls
	 */
	public void row(Control<?>... controls)
	{
		out.println("<tr>");
			out.increaseTab();
			cells(controls);
			out.decreaseTab();
		out.println("</tr>");
	}

	
	/**
	 * Prints the buttons of a form.
	 * @param form a form
	 */
	public void buttons(Form form)
	{
		int size = form.size();
		for (int i=0; i<size; i++)
		{
			Control<?> control = form.get(i);
			if (control.getCategory() == Category.BUTTON)
			{
				control.print(out);
				out.println();
			}
		}
	}

	
	/**
	 * Prints a table row with two table cells, the first empty, the second
	 * containing all the buttons of the form.
	 * @param form a form
	 */
	public void buttonRow(Form form)
	{
		out.println("<tr>");
			out.increaseTab();
			out.println("<td></td>");
			out.println("<td>");
				out.increaseTab();
				buttons(form);
				out.decreaseTab();
			out.println("</td>");
			out.decreaseTab();
		out.println("</tr>");
	}

	
	/**
	 * Prints table rows for all input fields of the form.
	 * @see Category#INPUT
	 * @return returns the first inputfield of the form which can receive the focus. 
	 * @param form a form
	 */
	public Control<?> rows(Form form)
	{
		int size = form.size();
		Control<?> focusField = null;
		
		for (int i=0; i<size; i++)
		{
			Control<?> control = form.get(i);
			if (control.getCategory() == Category.INPUT)
			{
				row(control);
				if (focusField == null)
					focusField = control.toFocusControl();
			}
		}
		return focusField;
	}
	
	
	/**
	 * Prints a simple HTML table containing the form controls.
	 * @return returns the first inputfield of the form which can receive the focus. 
	 * @param form a form
	 */
	public Control<?> table(Form form)
	{
		out.println("<table>");
		Control<?>focusField = rows(form);
		buttonRow(form);
		out.println("</table>");
		return focusField;
	}
	
	
	/**
	 * Prints a simple HTML table containing the form controls,
	 * surrounded by a form start and end tag.
	 * @param form a form
	 */
	public void print(Form form)
	{
		form.start(out);
		Control<?>focusField = table(form);
		form.end(out, focusField);
	}


	private final CspWriter out;
	private String requiredLabelClass_; 
	private String errorControlClass_ = "error"; 
	private LangMixin lang_; 
}
