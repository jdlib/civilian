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
package org.civilian.form;


import org.civilian.Request;
import org.civilian.template.HtmlUtil;
import org.civilian.template.TemplateWriter;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;


/**
 * A {@link Control} implementation for a boolean valued checkbox.
 */
public class Checkbox extends Control<Boolean>
{
	/**
	 * Creates a Checkbox with a null name. 
	 */
	public Checkbox()
	{
		this(null);
	}
	
	
	/**
	 * Creates a Checkbox with the given name. 
	 */
	public Checkbox(String name)
	{
		super(name);
	}
	

	/**
	 * Creates a Checkbox with the given name and text.
	 * The text is printed to the right of the checkbox. 
	 */
	public Checkbox(String name, String text)
	{
		this(name);
		text_ = text;
	}
	

	@Override public Type<Boolean> getType()
	{
		return TypeLib.BOOLEAN;
	}
	

	/**
	 * Returns the text of the Checkbox. 
	 */
	public String getText()
	{
		return text_;
	}
	

	/**
	 * Sets the text of the Checkbox. 
	 * The text is printed to the right of the checkbox.
	 * @return this 
	 */
	public Checkbox setText(String text)
	{
		text_ = text;
		return this;
	}
	
	
	/**
	 * Does nothing: a checkbox always has a value,
	 * so it cannot be required. 
	 * @return this 
	 */
	@Override public Checkbox setRequired(boolean required)
	{
		return this;
	}


	/**
	 * Reads the Checkbox value from the request.
	 */
	@Override protected void parse(Request request)
	{
		String value = request.getParameter(getName());
		setBooleanValue("on".equals(value)); // we send value="on" 
		setStatus(Status.OK);
	}
	

	/**
	 * Always returns true.
	 */
	@Override public boolean isOk()
	{
		return true;
	}
	
	
	/**
	 * Prints the checkbox markup.
	 */
	@Override public void print(TemplateWriter out, String... attrs)
	{
		if (text_ != null)
			out.print("<label>");
		out.print("<input");
		HtmlUtil.attr(out, "type", "checkbox", false);
		HtmlUtil.attr(out, "name", getName());
		if (isReadOnly())
			out.print(" readonly");
		if (isDisabled())
			out.print(" disabled");
		if (getBooleanValue())
			HtmlUtil.attr(out, "checked", "checked", false);
		// we set value="on" to have a defined parameter
		HtmlUtil.attr(out, "value", "on", false);
		printAttrs(out, attrs);
		out.print('>');
		if (text_ != null)
		{
			HtmlUtil.text(out, text_);
			out.print("</label>");
		}
	}

		
	/**
	 * Returns this.
	 */
	@Override public Control<?> toFocusControl()
	{
		return this;
	}
	
	
	private String text_;
}
