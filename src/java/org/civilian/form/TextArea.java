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
import org.civilian.type.TypeLib;


/**
 * TextArea represents a text area element.
 */
public class TextArea extends Control<String>
{
	/**
	 * The default number of rows if not specified.
	 */
	public static final int DEFAULT_ROWS = 3;

	/**
	 * The default number of columns if not specified.
	 */
	public static final int DEFAULT_COLS = 50;
			

	/**
	 * Creates a new TextArea.
	 * @param name the field name.
	 */
	public TextArea(String name)
	{
		this(name, DEFAULT_ROWS, DEFAULT_COLS);
	}

	
	/**
	 * Creates a new TextArea.
	 * @param name the field name
	 * @param rows the number of visible rows
	 * @param cols the number of visible columns
	 */
	public TextArea(String name, int rows, int cols)
	{
		super(TypeLib.STRING, name);
		rows_ = rows;
		cols_ = cols;
	}


	/**
	 * Returns the number of rows.
	 */
	@Override public int getRows()
	{
		return rows_;
	}

	
	/**
	 * Sets the number of rows.
	 */
	public TextArea setRows(int rows)
	{
		rows_ = rows;
		return this;
	}

	
	/**
	 * Returns the number of columns.
	 */
	public int getCols()
	{
		return cols_;
	}

	
	/**
	 * Sets the number of columns.
	 */
	public TextArea setCols(int cols)
	{
		cols_ = cols;
		return this;
	}


	@Override protected String formatValue()
	{
		String s = getValue();   
		return s != null ? s : "";
	}

	
	/**
	 * Prints the field markup.
	 */
	@Override public void print(ResponseWriter out, String... attrs)
	{
		out.print("<textarea");
		HtmlUtil.attr(out, "name", getName());
		HtmlUtil.attr(out, "rows", rows_);
		HtmlUtil.attr(out, "cols", cols_);
		if (isReadOnly())
			out.print(" readonly");
		if (isDisabled())
			out.print(" disabled");
		if (isRequired())
			out.print(" required");
		printAttrs(out, attrs);
		out.print('>');
		out.increaseTab();
		
		// the text is escaped 
		HtmlUtil.text(out, format());
		
		out.decreaseTab();
		out.print("</textarea>");
	}
	
	
	/**
	 * Returns this.
	 */
	@Override public Control<?> toFocusControl()
	{
		return this;
	}
	
	
	private int rows_;
	private int cols_;
}
