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
import org.civilian.type.Type;
import org.civilian.type.TypeSerializer;
import org.civilian.type.lib.ArrayType;
import org.civilian.util.Check;


/**
 * A Control to present a checkbox group which consists of a set of
 * checkboxes. The value of the CheckboxGroup is either null or an array containing the values
 * of the checked checkboxes.
 * A CheckboxGroup is backed by a KeyList which defines the values and display texts
 * of the checkboxes. 
 */
public class CheckboxGroup<T> extends Control<T[]>
{
	/**
	 * Creates a CheckboxGroup.
	 * @param name the name of the checkbox items
	 * @param keyList the key list defining the checkbox items
	 */
	public CheckboxGroup(String name, KeyList<T> keyList)
	{
		super(new ArrayType<>(Check.notNull(keyList, "keyList").getType()), name);
		keyList_ = keyList;
	}
	
	
	/**
	 * Returns the size of the key list.
	 */
	public int size()
	{
		return keyList_.size();
	}
	

	/**
	 * Returns the KeyList.
	 */
	public KeyList<T> getKeyList()
	{
		return keyList_;
	}
	
	
	/**
	 * Tests if the checkbox associated with the given value is checked.
	 */
	public boolean isChecked(T value)
	{
		T values[] = getValue();
		if (values != null)
		{
			for (int i=values.length - 1; i>=0; i--)
			{
				if (equals(value, values[i]))
					return true;
			}
		}
		return false;
	}


	/**
	 * Prints all checkboxes of the group, separated by a <br> element.
	 */
	@Override public void print(ResponseWriter out, String... attrs)
	{
		Printer printer = getPrinter(out);
		printer.setAttrs(attrs);
		int n = keyList_.size();
		for (int i=0; i<n; i++)
		{
			printer.print(i);
			out.println("<br>");
		}
	}
	
	 
	/**
	 * Returns a printer which allows you to print single radio buttons and 
	 * control the layout. 
	 */
	public Printer getPrinter(ResponseWriter out)
	{
		return new Printer(out);
	}
	

	/**
	 * Printer allows to print the radio buttons of the group and at the same 
	 * time control its layout.
	 */
	public class Printer
	{
		public Printer(ResponseWriter out)
		{
			Check.notNull(out, "out");
			this.out = out;
			serializer_ = getResponseSerializer();
		}
		
		
		public void setAttrs(String... attrs)
		{
			attrs_ 	= attrs; 
		}
		
		
		public void setNextAttrs(String... attrs)
		{
			nextAttrs_ = attrs; 
		}

		
		/**
		 * Prints the checkbox which corresponds to the 
		 * i-th entry in the KeyList.
		 */
		public void print(int i)
		{
			print(keyList_.getValue(i), keyList_.getText(i));
		}

		
		/**
		 * Prints a single Checkbox. The checkbox is printed as checked if the value is contained in the array value of
		 * the checkbox group.
		 * @param value the value of the checkbox
		 * @param text a text printed right to the checkbox. Can be null.
		 */
		public void print(T value, String text)
		{
			print(value, text, isChecked(value));
		}
		
			
		/**
		 * Prints a single Checkbox.
		 * @param value the value of the checkbox
		 * @param text a text printed right to the checkbox. Can be null.
		 * @param checked if the checkbox is checked.
		 */
		public void print(T value, String text, boolean checked)
		{
			if (text != null)
				out.print("<label>");
			out.print("<input");
			HtmlUtil.attr(out, "type", "checkbox", false);
			HtmlUtil.attr(out, "name", getName());
			String v = value != null ? elemType_.format(serializer_, value) : "";
			HtmlUtil.attr(out, "value", v);
			if (checked)
				out.print(" checked");
			printAttrs(out, nextAttrs_);
			out.print(">");
			if (text != null)
			{
				HtmlUtil.text(out, text);
				out.print("</label>");
			}
			nextAttrs_ = attrs_;
		}

		
		private String[] attrs_;
		private String[] nextAttrs_;
		private ResponseWriter out;
		private TypeSerializer serializer_; 
		private Type<T> elemType_ = ((ArrayType<T>)getType()).getElementType();
	}


	private KeyList<T> keyList_;
}
