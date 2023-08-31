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


import org.civilian.template.CspWriter;
import org.civilian.template.HtmlUtil;


/**
 * Attribute is a name-value pair used to present a HTML attribute.
 * Attribute has a reference to another attribute and can therefore 
 * be used to build a linked list of Attribute objects.
 */
public class Attribute implements CspWriter.Printable
{
	/**
	 * Returns the attribute with a given name from the linked list
	 * of attributes starting with the given Attribute object.
	 * @return the attribute with the specified name or null if not found
	 */
	public static Attribute getAttribute(Attribute attribute, String name)
	{
		while(attribute != null)
		{
			if (attribute.name.equals(name))
				return attribute;
			attribute = attribute.next_;
		}
		return null;
	}

	
	/**
	 * Returns the value of an attribute with a certain name within
	 * the linked list of attributes starting with the given attribute.
	 * @return the value or null if not found
	 */
	public static String getValue(Attribute attribute, String name)
	{
		Attribute a = getAttribute(attribute, name);
		return a == null ? null : a.value;
	}
	
	
	/**
	 * Sets the value of an attribute with a given name within the linked list
	 * of attributes starting with the given Attribute object.
	 * @return the new start of the linked list.
	 */
	public static Attribute setAttribute(Attribute start, String name, String value)
	{
		Attribute attr = start;
		while(attr != null)
		{
			if (attr.name.equals(name))
			{
				attr.value = value;
				return start;
			}
			if (attr.next_ == null)
				break;
			attr = attr.next_;
		}
		Attribute newAttr = new Attribute(name, value, null);
		if (attr == null)
			start = newAttr;
		else
			attr.next_ = newAttr;
		return start;
	}

	
	/**
	 * Creates a new attribute.
	 * @param name the attribute name
	 * @param value the value
	 * @param next the next attribute in the linked list of attributes. 
	 */
	public Attribute(String name, String value, Attribute next)
	{
		this.name = name;
		this.value = (value == null) ? "" : value;
		this.next_ = next;
	}
	
	
	/**
	 * Returns the next attribute.
	 */
	public Attribute next()
	{
		return next_;
	}
	

	/**
	 * Prints this attribute and the following attributes
	 * to the response writer.
	 */
	@Override public void print(CspWriter out)
	{
		Attribute attr = this;
		do
		{
			HtmlUtil.attr(out, attr.name, attr.value);
			attr = attr.next_;
		}
		while(attr != null);
	}
	

	public final String name;
	public String value;
	private Attribute next_;
}
