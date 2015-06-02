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


import org.civilian.response.ResponseWriter;
import org.civilian.template.HtmlUtil;
import org.civilian.text.keys.KeyList;
import org.civilian.util.Check;


/**
 * Select represents a HTML select element.
 * It is backed by a KeyList which defines the options of the select
 * element.
 */
public class Select<T> extends Control<T>
{
	private static final byte FLAG_CHECK_REQUEST_VALUE 	= (byte)1;
	private static final byte FLAG_USE_OPTION_GROUPS 	= (byte)2;
	
	
	/**
	 * Creates the Select.
	 * @param name the field name
	 * @param keyList the keylist which backs the select options
	 */
	public Select(String name, KeyList<T> keyList)
	{
		super(Check.notNull(keyList, "keyList").getType(), name);
		keyList_ = keyList;
	}

	
	/**
	 * Returns the KeyList passed to the Select constructor.
	 */
	public KeyList<T> getKeyList()
	{
		return keyList_;
	}
	

	/**
	 * Returns the text corresponding to the selected value within the KeyList of the field.
	 */
	public String getSelectedText()
	{
		return keyList_.getText(getValue());
	}
	

	/**
	 * Returns the index corresponding to the selected value within the KeyList of the field.
	 */
	public int getSelectedIndex()
	{
		return keyList_.indexOf(getValue());
	}

	
	/**
	 * Returns the value of the size attribute which determines the number of displayed rows.
	 */
	@Override public int getRows()
	{
		return rows_;
	}
	
	
	/**
	 * Sets the size of the select field which determines the number of displayed rows.
	 */
	public Select<T> setRows(int rows)
	{
		rows_ = rows;
		return this;
	}
	
	
	private boolean getFlag(byte f)
	{
		return (flags_ & f) > 0;
	}
	

	private Select<T> setFlag(byte f, boolean on)
	{
		if (on)
			flags_ |= f;
		else
			flags_ &= ~f;
		return this;
	}

	
	/**
	 * Returns if the Select prints out option groups.
	 * If true, then every item in the KeyList with a null value
	 * is interpreted as option group, instead of a normal key entry.
	 * The default is not to use option groups.
	 */
	public boolean useOptionGroups()
	{
		return getFlag(FLAG_USE_OPTION_GROUPS);
	}

	
	public Select<T> setUseOptionGroups(boolean on)
	{
		return setFlag(FLAG_USE_OPTION_GROUPS, on);
	}
	

	public Select<T> setDefaultOption()
	{
		return setDefaultOption("", null);
	}
	
	
	public Select<T> setDefaultOption(String text, T key)
	{
		defaultKey_  = key;
		defaultText_ = text;
		return this;
	}
	
	
	public Select<T> removeDefaultOption()
	{
		return setDefaultOption(null, null);
	}

	
	/**
	 * Prints the field markup.
	 */
	@Override public void print(ResponseWriter out, String... attrs)
	{
		start(out, attrs);
		out.increaseTab();
		printDefaultOption(out);
		printOptions(out);
		out.decreaseTab();
		end(out);
	}
	
	
	/**
	 * Prints the start tag of the select element.
	 */
	public void start(ResponseWriter out)
	{
		start(out, (String[])null);
	}
	
		
	/**
	 * Prints the start tag of the select element.
	 */
	public void start(ResponseWriter out, String... attrs)
	{
		out.print("<select");
		HtmlUtil.attr(out, "name", getName());
		if (rows_ > 1)
			HtmlUtil.attr(out, "size", rows_);
		if (isReadOnly())
			out.print(" readonly");
		if (isDisabled())
			out.print(" disabled");
		if (isRequired())
			out.print(" required");
		printAttrs(out, attrs);
		out.println(">");
	}
	

	/**
	 * Prints the end tag of the select element.
	 */
	public void end(ResponseWriter out)
	{
		out.print("</select>");
	}
	
	
	/**
	 * Prints the option list of the select elements.
	 */
	public void printOptions(ResponseWriter out)
	{
		int keySize 			= keyList_.size();
		boolean useOptionGroups = useOptionGroups();
		boolean isInGroup 		= false;
		
		for (int i=0; i<keySize; i++)
		{
			if ((keyList_.getValue(i) == null) && useOptionGroups)
			{
				if (isInGroup)
					printGroupEnd(out);
				isInGroup = true;
				printGroupStart(out, keyList_.getText(i));
			}
			else
				printOption(out, i);
		}
		if (isInGroup)
			printGroupEnd(out);
	}
	
	
	/**
	 * Prints a single option in the select field's option list
	 * @param value the option value
	 * @param text the option text
	 * @param selected is the option selected?
	 */
	public void printOption(ResponseWriter out, T value, String text, boolean selected)
	{
		String s = getType().format(getResponseSerializer(), value);
		printOptionImpl(out, s, text, selected);
	}
	
	
	/**
	 * Prints a single option.
	 */
	public void printOption(ResponseWriter out, int i)
	{
		T value				= keyList_.getValue(i);
		String svalue		= keyList_.getType().format(getResponseSerializer(), value, i);
		String text 		= keyList_.getText(i);
		boolean selected	= equals(value, getValue());
		printOptionImpl(out, svalue, text, selected);
	}
	
	
	private void printOptionImpl(ResponseWriter out, String value, String text, boolean selected)
	{
		out.print("<option");
		HtmlUtil.attr(out, "value", value);
		if (selected)
			out.print(" selected");
		out.print(">");
		out.print(text);
		out.println("</option>");
	}

	
	public void printDefaultOption(ResponseWriter out)
	{
		if (defaultText_ != null)
			printOption(out, defaultKey_, defaultText_, false);
	}
	
	
	public void printGroupStart(ResponseWriter out, String label)
	{
		out.print("<optgroup");
		HtmlUtil.attr(out, "label", label);
		out.println(">");
		out.decreaseTab();
	}


	public void printGroupEnd(ResponseWriter out)
	{
		out.decreaseTab();
		out.print("</optgroup>");
	}
	
	
	/**
	 * Returns the text of the seletec item.
	 */
	public String getText()
	{
		return keyList_.getText(getValue());
	}

	
	/**
	 * Returns this.
	 */
	@Override public Control<T> toFocusControl()
	{
		return this;
	}
	
	
	private KeyList<T> keyList_;
	private int rows_;
	private T defaultKey_;
	private byte flags_ = FLAG_CHECK_REQUEST_VALUE;
	private String defaultText_ = "";  // by default the Select has a default option
}
