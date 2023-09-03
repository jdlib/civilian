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
package org.civilian.template;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import org.civilian.util.Check;
import org.civilian.util.ClosedWriter;
import org.civilian.util.Data;


/**
 * CspWriter is a PrintWriter to write pretty indented files.
 * CspWriter maintains a {@link #getTabCount() tab count}. When a new line is started
 * then tab characters are automatically inserted at the beginning of the line, according
 * to the tab count. The default tab characters is a single '\t' character, but you can
 * chose any string instead (e.g. "  "). 
 */
public class CspWriter extends PrintWriter
{
	/**
	 * Sets the default characters used by a new CspWriter to indent a line.
	 * By default a indent consists of a single tab character.
	 * @param chars the chars
	 * @see #setTabChars(String)
	 */ 
	public static void setDefaultTabChars(String chars)
	{
		defaultTabChars_ = getChars(chars, "chars");
	}

	
	/**
	 * @return the default characters used by a new CspWriter to indent a line.
	 */ 
	public static String getDefaultTabChars()
	{
		return new String(defaultTabChars_);
	}

	
	/**
	 * Sets the default line separator used by a new CspWriter.
	 * By default this is '\n' (in accordance to our primary goal to produce web content.
	 * @param separator the separator
	 */ 
	public static void setDefaultLineSeparator(String separator)
	{
		defaultLineSeparator_ = getChars(separator, "separator");
	}

	
	/**
	 * Returns the default line separator used by a new CspWriter.
	 */ 
	public static String getDefaultLineSeparator()
	{
		return new String(defaultLineSeparator_);
	}

	
	/**
	 * Creates a new CspWriter.
	 * @param out a Writer
	 */
	public CspWriter(Writer out)
	{
		this(out, false);
	}


	/**
	 * Creates a CspWriter.
	 * @param out a Writer
	 * @param autoFlush - a boolean; if true, the println() methods will flush
	 *      the output buffer
	 */
	public CspWriter(Writer out, boolean autoFlush)
	{
		super(out, false);
		autoFlush_ = autoFlush;
	}


	/**
	 * Sets the string used for a tab. 
	 * If you do not explicitly specify the tab chars, 
	 * the value of {@link #getDefaultTabChars()} is used.
	 */
	public void setTabChars(String chars)
	{
		tabChars_ = getChars(chars, "chars");
	}


	/**
	 * Sets the characters used to separate lines. The default is
	 * system dependent (e.g. "0xD0xA" on Windows).
	 */
	public void setLineSeparator(String separator)
	{
		lineSeparator_ = getChars(separator, "separator");
	}


	/**
	 * Increases the indent tab.
	 */
	public void increaseTab()
	{
		tabCount_++;
	}


	/**
	 * Decreases the indent tab.
	 */
	public void decreaseTab()
	{
		if (tabCount_ > 0)
			tabCount_--;
	}


	/**
	 * @return the current number of indent tabs.
	 */
	public int getTabCount()
	{
		return tabCount_;
	}


	public void setTabCount(int count)
	{
		if (count < 0)
			throw new IllegalArgumentException();
		tabCount_ = count;
	}

	
	protected void writeNewLineTab()
	{
		for (int j=tabCount_; j>0; j--)
			super.write(tabChars_, 0, tabChars_.length);
		newLineStarted_ = false;
	}


	public boolean newLineStarted()
	{
		return newLineStarted_;
	}


	//-------------------------------------------------------
	// In fact the whole printwriter class is duplicated here
	// since this class should be a printwriter but
	// not increase the performance overhead significantly
	// because of the newline mechanism
	//-------------------------------------------------------
	

	/**
	 * Closes the stream.
	 */
	@Override public void close()
	{
		try
		{
			flush();
			synchronized(lock)
			{
				Writer out = this.out;
				this.out   = ClosedWriter.INSTANCE;
				if (out != null)
					out.close();
			}
		}
		catch(IOException e)
		{
			setError(e);
		}
	}


	/**
	 * Flushes the stream and check its error state.  Errors are cumulative;
	 * once the stream encounters an error, this routine will return true on
	 * all successive calls.
	 * @return true if the print stream has encountered an error, either on the
	 * underlying output stream or during a format conversion.
	 */
	@Override public boolean checkError()
	{
		if (out != ClosedWriter.INSTANCE)
			flush();
		return error_ != null;
	}


	/**
	 * Indicates that an error has occurred.
	 * @param e the error
	 */
	protected void setError(IOException e)
	{
		if (error_ == null)
			error_ = e;
	}

	
	/**
	 * @return the error or null if none has occurred.
	 */
	public IOException getError()
	{
		return error_;
	}
	

	/**
	 * Writes a single character.
	 * @param c the character
	 */
	@Override public void write(int c)
	{
		if (newLineStarted_)
			writeNewLineTab();
		super.write(c);
	}


	/**
	 * Writes a portion of an array of characters.
	 * @param buf a buffer
	 * @param off the offset
	 * @param length the length to write
	 */
	@Override public void write(char buf[], int off, int len)
	{
		if (newLineStarted_)
			writeNewLineTab();
		super.write(buf, off, len);
	}


	/**
	 * Writes a portion of a string.
	 * @param s a String
	 * @param off the offset
	 * @param len the length to write
	 */
	@Override public void write(String s, int off, int len)
	{
		if (newLineStarted_)
			writeNewLineTab();
		super.write(s, off, len);
	}


	/**
	 * Writes a string. This method cannot be inherited from the Writer class
	 * because it must suppress I/O exceptions.
	 * @param s a String
	 */
	@Override public void write(String s)
	{
		write(s, 0, s.length());
	}


	@Override public void print(char c)
	{
		write(c);
	}


	/**
	 * Write a line end, but only if the current line is not empty.
	 */
	public void printlnIfNotEmpty()
	{
		if (!newLineStarted_)
			println();
	}
	
	
	@Override public void println()
	{
		// empty lines are not indented
		if (newLineStarted_)
			newLineStarted_ = false;
		write(lineSeparator_, 0, lineSeparator_.length);
		if (autoFlush_)
			flush();
		newLineStarted_ = true;
	}


	@Override public void println(boolean value)
	{
		print(value);
		println();
	}


	@Override public void println(char value)
	{
		print(value);
		println();
	}


	@Override public void println(int value)
	{
		print(value);
		println();
	}


	@Override public void println(long value)
	{
		print(value);
		println();
	}


	@Override public void println(float value)
	{
		print(value);
		println();
	}


	@Override public void println(double value)
	{
		print(value);
		println();
	}


	@Override public void println(char[] value)
	{
		print(value);
		println();
	}


	@Override public void println(String value)
	{
		print(value);
		println();
	}


	@Override public void println(Object value)
	{
		print(value);
		println();
	}


	/**
	 * Checks if the object is a printable and in that case calls
	 * print(Printable), else it just calls the default implementation.
	 * @param object an object
	 */
	@Override public void print(Object object)
	{
		if (object instanceof Printable)
			print((Printable)object);
		else
			super.print(object);
	}
	
	
	/**
	 * If a not-null Printable is passed to the CspWriter,
	 * then the printable is asked to print itself. 
	 * @param printable a printable
	 */
	public void print(Printable printable)
	{
		if (printable != null)
		{
			try
			{
				printable.print(this);
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new IllegalStateException("error when printing '" + printable.getClass().getName() + "'", e);
			}
		}
	}
	
	
	/**
	 * Printable is a interface for print-aware classes who 
	 * implement a custom print strategy. Templates and form controls
	 * are examples of Printables.
	 * If you pass a Printable to {@link CspWriter#print(Object)} or
	 * CspWriter#print(Printable), then the {@link #print(CspWriter)}
	 * method of the Printable is called by the CspWriter, allowing
	 * to Printable to print itself.
	 */
	public static interface Printable 
	{
		public void print(CspWriter out) throws Exception;
	}

	
	private static char[] getChars(String s, String what)
	{
		Check.notNull(s, what);
		return s.toCharArray();
	}
	
	
	public Data getData()
	{
		if (data_ == null)
			data_ = new Data();
		return data_;
	}
	
	
	/**
	 * @return toString() of the wrapped writer. 
	 */
	@Override public String toString()
	{
		return out.toString();
	}


	private boolean newLineStarted_ = true;
	private int tabCount_ = 0;
	private char tabChars_[] = defaultTabChars_;
	private char lineSeparator_[] = defaultLineSeparator_;
	private boolean autoFlush_;
	private IOException error_;
	private Data data_;
	private static char[] defaultTabChars_ = { '\t' };
	private static char[] defaultLineSeparator_ = { '\n' };
}
