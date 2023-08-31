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
import org.civilian.text.keys.KeyList;
import org.civilian.text.type.TypeSerializer;
import org.civilian.type.Type;
import org.civilian.util.Check;


/**
 * A Control for a group of radio buttons.
 */
public class RadioGroup<T> extends Control<T>
{
	/**
	 * Creates a RadioGroup.
	 * @param name the name of the radio items
	 * @param keyList the KeyList backing the radio group, defining 
	 * 		the internal values and the display text of the radio buttons.
	 */
	public RadioGroup(String name, KeyList<T> keyList)
	{
		super(name);
		keyList_ = Check.notNull(keyList, "keyList");
	}

	
	/**
	 * Returns the type of the keylist.
	 */
	@Override public Type<T> getType()
	{
		return keyList_.getType();
	}
	
	
	/**
	 * Returns the KeyList.
	 */
	public KeyList<T> getKeyList()
	{
		return keyList_;
	}
	
	
	public RadioGroup<T> setHorizontal(boolean horizontal)
	{
		horizontal_ = horizontal;
		return this;
	}
	
	/**
	 * Prints all radio buttons of the group.
	 */
	@Override public void print(CspWriter out, String... attrs)
	{
		Printer printer = getPrinter(out);
		printer.setAttrs(attrs);
		int count = keyList_.size();
		for (int i=0; i<count; i++)
		{
			printer.print(i);
			if (!horizontal_)
				out.print("<br>");
			out.println();
		}
	}
	
	
	/**
	 * Returns a printer which allows you to print single radio buttons and 
	 * control the layout. 
	 */
	public Printer getPrinter(CspWriter out)
	{
		return new Printer(out);
	}
	

	/**
	 * Printer allows to print the radio buttons of the group and at the same 
	 * time control its layout.
	 */
	public class Printer
	{
		public Printer(CspWriter out)
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
		 * Prints the i-th radio button.
		 */
		public void print(int i)
		{
			print(keyList_.getValue(i), keyList_.getText(i));
		}

		
		/**
		 * Prints the radio button with the given values.
		 * Call this method for every radio button, if you explicitly want
		 * to control its layout.
		 */
		public void print(T value)
		{
			print(value, keyList_.getText(value));
		}

		
		/**
		 * Prints a single radio button. The button is printed as checked 
		 * if the value is contained in the array value of the RadioGroup.
		 * @param value the value of the radio button
		 * @param text a text printed right to the button.
		 */
		public void print(T value, String text)
		{
			print(value, text, RadioGroup.equals(value, getValue()));
		}
		
		
		/**
		 * Prints a single radio button.
		 * @param value the value of the radio button
		 * @param text a text printed right to the button.
		 * @param checked is the radio button checked?
		 */
		public void print(T value, String text, boolean checked)
		{
			if (text != null)
				out.print("<label>");
			out.print("<input");
			HtmlUtil.attr(out, "type", "radio", false);
			HtmlUtil.attr(out, "name", getName());
			String v = value != null ? serializer_.format(getType(), value) : "";
			HtmlUtil.attr(out, "value", v);
			if (checked)
				out.print(" checked");
			printAttrs(out, nextAttrs_);
			out.print('>');
			if (text != null)
			{
				HtmlUtil.text(out, text);
				out.print("</label>");
			}
			nextAttrs_ = attrs_;
		}

		
		private String[] attrs_;
		private String[] nextAttrs_;
		private CspWriter out;
		private TypeSerializer serializer_; 
	}


	private KeyList<T> keyList_;
	private boolean horizontal_ = true;
}
