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
import org.civilian.template.CspWriter;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;


/**
 * HiddenField is a HTML input field with type "hidden".
 */
public class HiddenField<T> extends Control<T>
{
	/**
	 * Creates a HiddenField with string type.
	 * @param name the field name
	 * @return the HiddenField
	 */
	public static HiddenField<String> create(String name)
	{
		return new HiddenField<>(name, TypeLib.STRING);
	}
	
	
	/**
	 * Creates a HiddenField with string type.
	 * @param name the field name
	 * @param value the field value
	 * @return the HiddenField
	 */
	public static HiddenField<String> create(String name, String value)
	{
		HiddenField<String> f = create(name);
		f.setValue(value);
		return f;
	}

	
	/**
	 * Creates a HiddenField with integer type.
	 * @param name the field name
	 * @param value the field value
	 * @return the HiddenField
	 */
	public static HiddenField<Integer> create(String name, int value)
	{
		return create(name, Integer.valueOf(value));
	}
	
	
	/**
	 * Creates a HiddenField with integer type.
	 * @param name the field name
	 * @param value the field value
	 * @return the HiddenField
	 */
	public static HiddenField<Integer> create(String name, Integer value)
	{
		HiddenField<Integer> f = new HiddenField<>(name, TypeLib.INTEGER);
		f.setValue(value);
		return f;
	}

	
	/**
	 * Creates a HiddenField.
	 * @param name the field name
	 * @param type the field type
	 */
	public HiddenField(String name, Type<T> type)
	{
		super(name);
		type_ = Check.notNull(type, "type");
	}

	
	/**
	 * @return the type of the keylist.
	 */
	@Override public Type<T> getType()
	{
		return type_;
	}

	
	/**
	 * @return Category.HIDDEN. 
	 */
	@Override public Category getCategory()
	{
		return Category.HIDDEN;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override public void print(CspWriter out, String... attrs)
	{
		out.print("<input");
		HtmlUtil.attr(out, "type", "hidden", false);
		HtmlUtil.attr(out, "name", getName());
		HtmlUtil.attr(out, "value", format());
		printAttrs(out, attrs);
		out.print('>');
	}
	
	
	/**
	 * @return null.
	 */
	@Override public Control<?> toInputField()
	{
		return null;
	}
	
	
	private final Type<T> type_;
}
