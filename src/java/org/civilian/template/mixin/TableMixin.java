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


import java.util.ArrayList;
import org.civilian.template.ComponentBuilder;
import org.civilian.template.HtmlUtil;
import org.civilian.template.CspWriter;
import org.civilian.util.Check;
import org.civilian.util.Scanner;


/**
 * TableMixin can be used to conveniently build complex HTML table output.
 * It is intended to be used for irregular tables with lot of colspan and rowspan attributes on
 * single cells. 
 * These are the steps to use it:
 * <ol>
 * <li>You define the column layout of the table, by calling {@link #columns(int)},
 * 	 	{@link #columns(String)} or {@link #columns(Model)}.
 * <li>You start the table output by calling {@link #startTable()} or {@link #startTable(String...)}.
 * <li>Then you call the {@link #startCell()} or one of its variants, to start the next table cell,
 * 		print the cell content and then call {@link #endCell()} to end the cell.
 * 		The TableMixin will keep track of what has been printed before, and automatically inserts
 * 		tr-elements.
 * <li>You can set rowspan and colspan of the next cell. Again TableMixin does the book-keeping for you.
 * <li>Custom attributes for row and cell elements can be set as default or for the next printed row or cell. 
 * <li>You end printing the table by calling {@link #endTable()} 
 * </ol>
 * When you define the column model of the table with a {{@link #columns(String) definition string},
 * you can set width of columns, gap width, and column attributes.<br>
 * Example: The definition <code>[50]5[30%, align=left][class="help red"]</code> will define these
 * columns:
 * <ol>
 * <li>a column with width 50px,
 * <li>a gap column with width 5px
 * <li>a left aligned column with width 30% 
 * <li>a column with class attribute set to "help red"
 * </ol>
 * See {@link #columns(String)} for a complete definition. 
 */
public class TableMixin implements ComponentBuilder
{
	/**
	 * Creates a new TableMixin.
	 * @param out a CspWriter
	 */
	public TableMixin(CspWriter out)
	{
		this.out = Check.notNull(out, "out");
	}

	
	//---------------------------------
	// column definition
	//---------------------------------
	
	
	/**
	 * Defines the column model which the TableMixin should use.
	 * @param model the model
	 * @return this
	 */
	public TableMixin columns(Model model)
	{
		if (tableStarted_)
			throw new IllegalStateException("can't set columns during table outout");
		
		Check.notNull(model, "model");
		columns_ 		= model.columns_;
		restRowSpans_	= new int[columns_.length];
		rowIndex_		= -1;
		colIndex_		= -1;
		colspan_		= 1;
		rowspan_		= 1;
		cellStarted_	= false;
		nextCellAttrs_	= null;
		nextRowAttrs_	= null;
		return this;
	}
	
	
	/**
	 * Defines to use a column layout with the given number of columns.
	 * @param count the number of columns, &gt;= 1.
	 * @return this
	 */
	public TableMixin columns(int count)
	{
		return columns(new Model(count));
	}

	
	/**
	 * Specifies to use a column layout defined by the string.
	 * The grammar of the definition is as follows:
	 * <pre>
	 * definition := column-list
	 * column-list := column ( gap? column )*
	 * column := '[' column-def ']'
	 * col-def := ( col-def-item ( ',' col-def-item )* )?
	 * col-def-item := col-width | col-attr
	 * col-width = integer '%'?
	 * col-attr = col-attr-name '=' col-attr-value
	 * col-attr-name := [.^=\s]]+
	 * col-attr-value := simplestring | quotedstring  
	 * simplestring := [.^\s]
	 * quotedstring := "'" [.^']+ "'"  
	 * gap := integer?
	 * integer := [0-9]+ 
	 * </pre>
	 * @param definition the definition
	 * @return this
	 */
	public TableMixin columns(String definition)
	{
		return columns(new Model(definition));
	}
	
	
	/**
	 * @return the number of columns.
	 */
	public int columns()
	{
		return columns_.length;
	}
	

	//---------------------------------
	// table level methods
	//---------------------------------
	
	
	/**
	 * Prints the table start tag.
	 * @return this
	 */
	public TableMixin startTable()
	{
		return startTable((String[])null);
	}
	

	/**
	 * Prints the table start tag.
	 * @param attrs a list of attribute name-value-pairs to be printed in the start tag, or null.
	 * @return this
	 */
	public TableMixin startTable(String... attrs)
	{
		if (columns_.length == 0)
			throw new IllegalArgumentException("no columns defined");
		tableStarted_ = true;
		printStartTag("table", attrs, null);
		return this;
	}

	
	/**
	 * Ends the table.
	 * @return this
	 */
	public TableMixin endTable()
	{
		if (tableStarted_)
		{
			tableStarted_ = false;
			endRow();
			out.println("</table>");
		}
		return this;
	}
	
	
	//---------------------------------
	// col groups
	//---------------------------------

	
	private void printColGroups()
	{
		for (int i=0; i<columns_.length; i++)
		{
			if (columns_[i].wantsColDefinition())
			{
				out.println("<colgroup>");
				out.increaseTab();
				for (int j=0; j<columns_.length; j++)
					columns_[j].writeColDefintion(out);
				out.decreaseTab();
				out.println("</colgroup>");
				break;
			}
		}
	}
	
	
	//---------------------------------
	// row level methods
	//---------------------------------
	

	/**
	 * Specifies the default attributes which should be printed on every
	 * opening tr-tag.
	 * @param attrs a list of attribute name-value-pairs, or null.
	 * @return this
	 */
	public TableMixin defaultRowAttrs(String... attrs)
	{
		defaultRowAttrs_ = attrs;
		return this;
	}
	
	
	/**
	 * Specifies the attributes which should be printed on the next
	 * opening tr-tag. These will override the default row attributes.
	 * @param attrs a list of attribute name-value-pairs, or null.
	 * @return this
	 */
	public TableMixin rowAttrs(String... attrs)
	{
		nextRowAttrs_ = attrs;
		return this;
	}
	

	private void startRow()
	{
		// if we are before the first row, we print the colgroups (if we have one)
		// we cannot do it directly within startTable() since a table caption may be used
		if (rowIndex_ < 0)
			printColGroups();
		printStartTag("tr", nextRowAttrs_, defaultRowAttrs_);
		nextRowAttrs_ = null;
		out.increaseTab();
		rowIndex_++;
	}
	
	
	/**
	 * Ends the current row.
	 * @param printEmptyCells if true then empty td-cells are printed
	 * 		for every missing column. 
	 * @return this
	 */
	public TableMixin endRow(boolean printEmptyCells)
	{
		if (colIndex_ >= 0)
		{
			if (printEmptyCells)
			{
				for (int i=colIndex_ + 1; i<columns_.length; i++)
					out.println("<td></td>");
			}
			colIndex_ = -1;
			out.decreaseTab();
			out.println("</tr>");
		}
		return this;
	}
	
	
	/**
	 * Ends the current row.
	 * @return this
	 */
	public TableMixin endRow()
	{
		return endRow(false);
	}

	
	/**
	 * @return the current row index.
	 */
	public int rowIndex()
	{
		return rowIndex_;
	}
	
	
	/**
	 * Sets the current row index. This lets you correct
	 * the row index if you print a row for yourself.
	 * @param index the index 
	 * @return this
	 */
	public TableMixin setRowIndex(int index)
	{
		rowIndex_ = index;
		return this;
	}

	
	/**
	 * Increases the current row index by the given amount. 
	 * This lets you correct the row index if you print a row for yourself.
	 * @param amount the amount 
	 * @return this
	 */
	public TableMixin increaseRowIndex(int amount)
	{
		rowIndex_ += amount;
		return this;
	}

	
	//---------------------------------
	// cell level methods
	//---------------------------------

	
	/**
	 * Specifies that the next cell will span this number of columns.
	 * @param colspan the number of columns to span. Must be &gt;= 1. If it
	 * 		is greater than the number of available cells, it is
	 * 		is silently truncated.
	 * @see #startCell(int)
	 * @return this
	 */
	public TableMixin colspan(int colspan)
	{
		Check.greaterEquals(colspan, 1, "colspan");
		colspan_ = colspan;
		return this;
	}

	
	/**
	 * Specifies that the next cell will span this number of rows.
	 * @param rowspan the number of rows to span. Must be &gt;= 1.
	 * @return this
	 */
	public TableMixin rowspan(int rowspan)
	{
		Check.greaterEquals(rowspan, 1, "rowspan");
		rowspan_ = rowspan;
		return this;
	}

	
	/**
	 * Specifies colspan and rowspan for the next cell.
	 * @param colspan the colspan
	 * @param rowspan the rowspan
	 * @see #colspan(int)
	 * @see #rowspan(int)
	 * @return this
	 */
	public TableMixin span(int colspan, int rowspan)
	{
		colspan(colspan);
		return rowspan(rowspan);
	}
	
	
	/**
	 * Specifies the attributes which should be printed on the next
	 * opening td-tag. These will override the default column attributes.
	 * @param attrs a list of attribute name-value-pairs, or null.
	 * @return this
	 */
	public TableMixin attrs(String... attrs)
	{
		nextCellAttrs_ = attrs;
		return this;
	}
	
	
	/**
	 * Starts a cell which spans the given number of columns.
	 * @param colspan the colspan
	 * @return this
	 */
	public TableMixin startCell(int colspan)
	{
		colspan(colspan);
		return startCell();
	}
	
		
	/**
	 * Starts a cell which spans the given number of columns.
	 * @param attrs a list of attribute name-value-pairs, or null.
	 * @return this
	 */
	public TableMixin startCell(String... attrs)
	{
		attrs(attrs);
		return startCell();
	}
	
		
	/**
	 * Starts a cell which spans the given number of columns.
	 * @param colspan the colspan
	 * @param attrs a list of attribute name-value-pairs, or null.
	 * @return this
	 */
	public TableMixin startCell(int colspan, String... attrs)
	{
		colspan(colspan);
		return startCell(attrs);
	}
	
		
	/**
	 * Starts a new cell. Automatically ends any currently started cell,
	 * ends a row, if it was the last cell in the row, and starts a new
	 * row if needed, and inserts gap cells for columns which are defined
	 * as gap.
	 * @return this
	 */
	public TableMixin startCell()
	{
		if (cellStarted_)
			endCell();
		
		while(true)
		{
			if (colIndex_ < 0)
				startRow();
			if (colIndex_ == columns_.length - 1)
				endRow();
			
			if (restRowSpans_[++colIndex_] > 0)
			{
				restRowSpans_[colIndex_]--;
				continue;
			}

			Column column = columns_[colIndex_];
			

			if (column.isGap())
			{
				column.startCell(out, null, 1, 1);
				endCellImpl();
			}
			else
			{
				startCell(column);
				break;
			}
		}
		return this;
	}
	

	private void startCell(Column column)
	{
		cellStarted_ = true;

		// calc colspan
		int colspan;
		if (colspan_ > 1)
		{
			colspan = calcEffectiveColspan();
			colIndex_ += colspan - 1;
			colspan_ = 1;
		}
		else
			colspan = 1;
		
		// calc rowspan
		int rowspan = rowspan_;
		if (rowspan_ > 1)
		{
			int index = colIndex_;
			int cols  = colspan;
			int rest  = rowspan_ - 1;
			while ((cols > 0) && (index < columns_.length)) 
			{
				restRowSpans_[index] += rest;
				if (!columns_[index++].isGap())
					cols--;
			}
			rowspan_ = 1;
		}
		
		column.startCell(out, nextCellAttrs_, colspan, rowspan);
		nextCellAttrs_ = null;
	}
	
	
	private int calcEffectiveColspan()
	{
		int effective 	= 0;
		int toSpan 		= colspan_;
		int index  		= colIndex_;
		while ((toSpan > 0) && (index < columns_.length)) 
		{
			effective++;
			if (!columns_[index++].isGap())
				toSpan--;
		}
		return effective;
	}
		
		
	/**
	 * Ends the currently started table cell. 
	 * The call has no effect if no table cell is started.
	 * @return this
	 */
	public TableMixin endCell()
	{
		if (cellStarted_)
		{
			cellStarted_ = false;
			endCellImpl();
		}
		return this;
	}
	
	
	private void endCellImpl()
	{
		out.println("</td>");
		if (colIndex_ == columns_.length - 1)
			endRow();
	}

	
	/**
	 * Starts a new cell, prints the content and then ends the cell.
	 * @param content the content
	 * @return this
	 */
	public TableMixin cell(Object content)
	{
		startCell();
		out.print(content);
		return endCell();
	}

	
	/**
	 * @return the column index of the current cell.
	 */
	public int colIndex()
	{
		return colIndex_;
	}
	
	
	/**
	 * Sets the current row index. This lets you correct
	 * the row index if you print a row for yourself.
	 * @param index the index 
	 * @return this
	 */
	public TableMixin setColIndex(int index)
	{
		colIndex_ = index;
		return this;
	}

	
	/**
	 * Increases the current row index by the given amount. 
	 * This lets you correct the row index if you print a row for yourself. 
	 * @param amount the amount
	 * @return this
	 */
	public TableMixin increaseColIndex(int amount)
	{
		rowIndex_ += amount;
		return this;
	}

	
	//---------------------------------
	// ComponentBuilder
	//---------------------------------

	
	/**
	 * Implements ComponentBuilder. Calls startCell()  
	 */
	@Override public void startComponent(boolean multiLine)
	{
		startCell();
		if (multiLine)
			out.println();
	}


	/**
	 * Implements ComponentBuilder. Calls endCell()  
	 */
	@Override public void endComponent(boolean multiLine)
	{
		if (multiLine)
			out.printlnIfNotEmpty();
		endCell();
	}

	
	//---------------------------------
	// helper
	//---------------------------------

	
	private void printStartTag(String tag, String[] attrs1, String[] attrs2)
	{
		out.print('<');
		out.print(tag);
		String[] attrs = attrs1 != null ? attrs1 : attrs2;
		if (attrs != null)
			HtmlUtil.attrs(out, attrs);
		out.println('>');
	}
	
	
	//---------------------------------
	// Model
	//---------------------------------

	
	/**
	 * Model contains a list of columns, to be used by a TableMixin.
	 */
	public static class Model
	{
		/**
		 * Creates a Model with the given number of columns.
		 * @param count the number of columns
		 */
		public Model(int count)
		{
			Check.greaterEquals(count, 1, "count");
			columns_ = new Column[count];
			for (int i=0; i<count; i++)
				columns_[i] = new Column();
		}
		
		
		/**
		 * Creates a column array from a definition string.
		 * @param definition the columns definition
		 */
		public Model(String definition)
		{
			ArrayList<Column> columns = new ArrayList<>(); 
			Scanner scanner = new Scanner(definition);
			while(scanner.hasMoreChars())
			{
				Column column;
				if (scanner.next('['))
					column = parseColumn(scanner);
				else
					column = parseGap(scanner);
				if (column != null) 
					columns.add(column);
			}
			
			if (columns.size() == 0)
				throw new IllegalArgumentException("model '" + definition + "' does not define any columns");
			columns_ = columns.toArray(new Column[columns.size()]);
		}

		
		/**
		 * Parse column definition
		 */
		private Column parseColumn(Scanner scanner)
		{
			Column column = new Column();
			ArrayList<String> attrs = null;
			if (!scanner.next(']'))
			{
				while (true)
				{
					if (scanner.currentIsDigit())
					{
						int width = scanner.consumeInt();
						if (scanner.next('%'))
							column.setPercentWidth(width);
						else
							column.setWidth(width);
					}
					else
					{
						String name = scanner.consumeToken("]=");
						if (name == null)
							scanner.exception("invalid column definition");
						
						scanner.expect("=");

						String value = null;
						if (scanner.current() == '\'')
							value = scanner.consumeQuotedString();
						else
							value = scanner.consumeToken("],");
						
						if (attrs == null)
							attrs = new ArrayList<>();
						attrs.add(name);
						attrs.add(value);
					}

					if (scanner.next(']'))
						break;
					scanner.expect(",");
				}
			}
			
			if (attrs != null)
				column.setAttrs(attrs.toArray(new String[attrs.size()]));
			return column;
		}

		
		private Column parseGap(Scanner scanner)
		{
			if (scanner.currentIsDigit())
			{
				int width = scanner.consumeInt();
				if (width > 0)
				{
					Column gap = new Column();
					gap.setGap();
					gap.setWidth(width);
					return gap;
				}
			}
			return null;
		}
		
		
		/**
		 * @return the number of columns.
		 */
		public int getColumnCount()
		{
			return columns_.length;
		}
		
			
		/**
		 * @param i the column index
		 * @return the i-th column.
		 */
		public Column getColumn(int i)
		{
			return columns_[i];
		}

		
		/**
		 * Sets column attributes on every column.
		 * @see Column#setAttrs(String...)
		 * @param attrs the attributes 
		 */
		public void setColumnAttrs(String... attrs)
		{
			for (int i=0; i<columns_.length; i++)
				columns_[i].setAttrs(attrs);
		}
		
		
		private Column[] columns_ = EMPTY_COLUMNS;
	}

	
	//---------------------------------
	// Column
	//---------------------------------

	
	/**
	 * Column describes a table column.
	 */
	public static class Column
	{
		/**
		 * Is the column a gap (of a certain width, with no content) or
		 * is it a content column. Gap columns are used to build
		 * a vertical spacer between two content columns.
		 * @return isGap?
		 */
		public boolean isGap()
		{
			return isGap_;
		}
		
		
		/**
		 * Makes the column a gap column with a width of 20 pixels
		 * @return this 
		 */
		public Column setGap()
		{
			return setGap(20);
		}
		
		
		/**
		 * Makes the column a gap columns.
		 * @param width the width of the gap 
		 * @return this 
		 */
		public Column setGap(int width)
		{
			isGap_ = true;
			setWidth(width);
			return this;
		}

		
		/**
		 * Sets the column width.
		 * @param width a string which can be used as value
		 * 		of a html width attribute. 
		 * @return this 
		 */
		public Column setWidth(String width)
		{
			width_ = width;
			return this;
		}


		/**
		 * Sets the column width as percentage value.
		 * @param percent a positive value
		 * @return this 
		 */
		public Column setPercentWidth(int percent)
		{
			return setWidth(Check.greaterEquals(percent, 1, "percent") + "%");
		}

		
		/**
		 * Sets the column width in pixels.
		 * @param pixels a positive value
		 * @return this 
		 */
		public Column setWidth(int pixels)
		{
			return setWidth(Check.greaterEquals(pixels, 1, "pixels") + "px");
		}

		
		/**
		 * @return the column width.
		 */
		public String getWidth()
		{
			return width_;
		}
		
		
		/**
		 * Sets the column attributes.
		 * If not null, then these attributes are included when 
		 * a opening td-tag is printed.
		 * @param attrs the attributes
		 * @return this 
		 */
		public Column setAttrs(String... attrs)
		{
			attrs_ = attrs;
			return this;
		}
		
		
		/**
		 * @return the column attributes.
		 */
		public String[] getAttrs()
		{
			return attrs_;
		}

		
		boolean wantsColDefinition()
		{
			return width_ != null;
		}


		void writeColDefintion(CspWriter out)
		{
			out.print("<col");
			if (width_ != null)
				HtmlUtil.attr(out, "width", width_);
			out.println(">");
		}
		
		
		void startCell(CspWriter out, String[] cellAttrs, int colspan, int rowspan)
		{
			out.print("<td");
			if (cellAttrs != null)
				HtmlUtil.attrs(out, cellAttrs);
			else
			{
				if (attrs_ != null)
					HtmlUtil.attrs(out, attrs_);
			}
			if (rowspan > 1)
				HtmlUtil.attr(out, "rowspan", rowspan);
			if (colspan > 1)
				HtmlUtil.attr(out, "colspan", colspan);
			out.print('>');
		}
		
		
		private boolean isGap_;
		private String width_;
		private String[] attrs_;
	}
	
	
	private CspWriter out;
	private boolean tableStarted_;
	private String[] defaultRowAttrs_;
	private String[] nextRowAttrs_;
	private String[] nextCellAttrs_;
	private int rowIndex_;
	private int colIndex_;
	private int colspan_;
	private int rowspan_;
	private boolean cellStarted_;
	private int restRowSpans_[] = EMPTY_ROWSPANS;
	private Column[] columns_ = EMPTY_COLUMNS;
	private static final Column[] EMPTY_COLUMNS = new Column[0];
	private static final int[] EMPTY_ROWSPANS = new int[0];
}
