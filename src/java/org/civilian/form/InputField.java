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


import org.civilian.template.HtmlUtil;
import org.civilian.template.TemplateWriter;
import org.civilian.util.Check;


/**
 * An abstract Control base class for the HTML input element.
 */
public abstract class InputField<T> extends Control<T>
{
	/**
	 * A type constant for HTML5 date input fields. 
	 */
	public static final String INPUT_TYPE_DATE				= "date";

	/**
	 * A type constant for HTML5 datetime input fields. 
	 */
	public static final String INPUT_TYPE_DATETIME			= "datetime";

	/**
	 * A type constant for HTML5 datetime-local input fields. 
	 */
	public static final String INPUT_TYPE_DATETIME_LOCAL	= "datetime-local";

	/**
	 * A type constant for HTML5 color input fields. 
	 */
	public static final String INPUT_TYPE_DATETIME_COLOR	= "color";

	/**
	 * A type constant for HTML5 email input fields. 
	 */
	public static final String INPUT_TYPE_EMAIL 			= "email";

	/**
	 * A type constant for HTML5 month input fields. 
	 */
	public static final String INPUT_TYPE_MONTH				= "month";

	/**
	 * A type constant for HTML5 number input fields. 
	 */
	public static final String INPUT_TYPE_NUMBER			= "number";
	
	/**
	 * A type constant for password input fields. 
	 */
	public static final String INPUT_TYPE_PASSWORD			= "password";

	/**
	 * A type constant for HTML5 range input fields. 
	 */
	public static final String INPUT_TYPE_RANGE				= "range";
	
	/**
	 * A type constant for HTML5 search input fields. 
	 */
	public static final String INPUT_TYPE_SEARCH 			= "search";
	
	/**
	 * A type constant for HTML5 phone input fields. 
	 */
	public static final String INPUT_TYPE_TEL	 			= "tel";

	/**
	 * A type constant for text input fields. 
	 */
	public static final String INPUT_TYPE_TEXT 				= "text";
	
	/**
	 * A type constant for HTML5 url input fields. 
	 */
	public static final String INPUT_TYPE_URL				= "url";

	/**
	 * A type constant for HTML5 week input fields. 
	 */
	public static final String INPUT_TYPE_WEEK				= "week";
	
	
	/**
	 * Creates an InputField.
	 * @param name the field name
	 */
	public InputField(String name)
	{
		super(name);
	}

	
	/**
	 * Set the value of the input fields type attribute.
	 */
	public InputField<T> setInputType(String inputType)
	{
		inputType_ = Check.notNull(inputType, "inputType");
		return this;
	}
	
	
	/**
	 * Returns the value of the input fields type attribute.
	 */
	public String getInputType()
	{
		return inputType_;
	}

	
	/**
	 * Returns the number of displayed characters.
	 */
	public int getSize()
	{
		return size_;
	}

	
	/**
	 * Sets the number of displayed characters.
	 */
	public InputField<T> setSize(int size)
	{
		size_ = size;
		return this;
	}

	
	/**
	 * Returns the maximum number of characters which can be entered
	 * into the text field.
	 */
	public int getMaxLength()
	{
		return maxLength_;
	}
	
	
	/**
	 * Sets the maximum number of characters which can be entered
	 * into the text field.
	 */
	public InputField<T> setMaxLength(int maxLength)
	{
		maxLength_ = maxLength;
		if (size_ == 0)
			setSize(Math.min(50, maxLength));
		return this;
	}

	
	/**
	 * Returns the value of the HTML5 placeholder attribute.
	 */
	public String getPlaceholder()
	{
		return getAttribute("placeholder");
	}

	
	/**
	 * Sets the value of the HTML5 placeholder attribute.
	 */
	public InputField<T> setPlaceholder(String placeHolder)
	{
		setAttribute("placeholder", placeHolder);
		return this;
	}

	
	/**
	 * Returns the value of the HTML5 pattern attribute.
	 */
	public String getPattern()
	{
		return getAttribute("pattern");
	}

	
	/**
	 * Sets the value of the HTML5 pattern attribute.
	 */
	public InputField<T> setPattern(String pattern)
	{
		setAttribute("pattern", pattern);
		return this;
	}

	
	/**
	 * Helper method to prints the start tag of the InputField.
	 */
	protected void open(TemplateWriter out, String type, String... attrs)
	{
		out.print("<input");
		HtmlUtil.attr(out, "type", type);
		HtmlUtil.attr(out, "name", getName());
		HtmlUtil.attr(out, "value", format());
		if (size_ > 0)
			HtmlUtil.attr(out, "size", size_);
		if (maxLength_ > 0)
			HtmlUtil.attr(out, "maxlength", maxLength_);
		if (isReadOnly())
			out.print(" readonly");
		if (isDisabled())
			out.print(" disabled");
		if (isRequired())
			out.print(" required");
		printAttrs(out, attrs);
	}


	/**
	 * Prints the field markup.
	 */
	@Override public void print(TemplateWriter out, String... attrs)
	{
		open(out, getInputType(), attrs);
		out.print('>');
	}

	
	/**
	 * Returns this.
	 */
	@Override public Control<?> toFocusControl()
	{
		return this;
	}
	
	
	private String inputType_ = INPUT_TYPE_TEXT;
	private int size_;
	private int maxLength_;
}
