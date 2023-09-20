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
package org.civilian.util;


/**
 * Scanner helps to scan strings.
 */
public class Scanner
{
	/**
	 * Input presents the lines used as input for the scanner. 
	 */
	public class Input
	{
		/**
		 * Sets the line strings which should be scanned and positions on the first line.
		 * If no lines are given, an empty line is used as input.
		 * @param lines the lines
		 * @return this
		 */
		public Input lines(String... lines)
		{
			lines_		= lines != null ? lines : new String[0];
			lineIndex_	= 0;
			initLine(lines_.length > 0 ? lines_[lineIndex_] : "");
			return this;
		}
	
		
		/**
		 * @return the current line index, starting at 0.
		 */
		public int getLineIndex()
		{
			return lineIndex_;
		}
		
		
		/**
		 * @return the number of lines of the input.
		 */
		public int getLineCount()
		{
			return lines_.length;
		}
	
		
		/**
		 * @return the lines on which the scanner is operating.
		 */
		public String[] getLines()
		{
			return lines_;
		}

		
		/**
		 * @return if the input has more lines.
		 */
		public boolean hasMoreLines()
		{
			return getLineIndex() < getLineCount();
		}
		
		
		/**
		 * Sets the source of the scanner input.
		 * @param source the source, e.g. a file name whose lines make up the scanner input
		 * @return this
		 */
		public Input source(String source)
		{
			source_ = source;
			return this;
		}
		
		
		/**
		 * @return the source of the scanner input.
		 * @see #source(String)
		 */
		public String getSource()
		{
			return source_; 
		}

	
		/**
		 * Positions the scanner on the next line.
		 * @return true if there was another line in the input lines. False if no more line was available. 
		 * In this case the current line of the scanner is initialised to an empty line- 
		 */
		public boolean nextLine()
		{
			if (lineIndex_ < lines_.length - 1)
			{
				initLine(lines_[++lineIndex_]);
				return true;
			}
			else 
			{
				lineIndex_ = lines_.length;
				initLine("");
				return false;
			}
		}
		
		
		private int lineIndex_;
		private String[] lines_;
		private String source_;
	}
	
	
	/**
	 * Creates a new Scanner with empty input.
	 */
	public Scanner()
	{
		input((String)null);
	}
	
	
	/**
	 * Creates a new Scanner using the given lines as input.
	 * @param lines the lines
	 */
	public Scanner(String... lines)
	{
		input(lines);
	}

	
	/**
	 * Sets the line strings which should be scanned and positions on the first line.
	 * If no lines are given, an empty line is used as input.
	 * @param lines the lines
	 * @return this
	 */
	public Scanner input(String... lines)
	{
		input.lines(lines);
		return this;
	}
	
	
	private void initLine(String line)
	{
		currentLine_ 		= line != null ? line : "";
		length_ 	 		= currentLine_.length();
		needSkipWhitespace_ = autoSkipWhitespace_;
		setPos(0);
	}
	
	
	/**
	 * @return the current scan position within in the current line, starting with index 0. 
	 * A position which equals the line length signifies that all the current line has been processed.
	 */
	public int getPos()
	{
		return pos_;
	}
	

	/**
	 * Sets the current scanner zero-based position within in the current line.
	 * Invalid positions (e.g. &lt; 0 or &gt; {@link #getLength()} length) are silently corrected.
	 * @param pos the position
	 * @return this
	 */
	public Scanner setPos(int pos)
	{
		pos_ = Math.max(0, Math.min(pos, length_));
		needSkipWhitespace_ = autoSkipWhitespace_;
		return this;
	}
	
	
	/**
	 * @return the length of the current line.
	 */
	public int getLength()
	{
		return length_;
	}
	
	
	/**
	 * Sets the end of the current line.
	 * Invalid positions (e.g. &lt; 0 or &gt; the real length of the current line) are silently corrected.
	 * The current position is also adjusted to equal the length if it exceeds the new length.
	 * @return this
	 */
	public Scanner setLength(int length)
	{
		length_ = Math.max(0, Math.min(length, currentLine_.length()));
		if (pos_ > length_)
			setPos(length);
		return this;
	}

	
	/**
	 * @return the current line.
	 */
	public String getLine()
	{
		return currentLine_;
	}

	
	/**
	 * Sets if whitespace should automatically be skipped,
	 * before consume*, next* or expect*-operations are 
	 * performed.
	 * @param flag the skip flag
	 * @return this; 
	 */
	public Scanner setAutoSkipWhitespace(boolean flag)
	{
		needSkipWhitespace_ = autoSkipWhitespace_ = flag;
		return this;
	}
	
	
	/**
	 * @return the autoskip whitespace flag.
	 */
	public boolean getAutoSkipWhitespace()
	{
		return autoSkipWhitespace_;
	}
	
	
	private void autoSkipWhitespace()
	{
		if (needSkipWhitespace_)
			skipWhitespace();
	}

	
	/**
	 * @return the current character or -1 if there are no more characters.
	 */
	public int current()
	{
		return hasMoreChars() ? currentLine_.charAt(pos_) : -1;
	}
	
	
	/**
	 * @return if the current character has the given character type.
	 * @param charType a character type defined in the Character class
	 * @see Character#getType(char)
	 */
	public boolean currentHasType(byte charType)
	{
		return Character.getType(current()) == charType;
	}
	
	
	/**
	 * @return if the current character is a digit.
	 */
	public boolean currentIsDigit()
	{
		return currentHasType(Character.DECIMAL_DIGIT_NUMBER);
	}
	
	
	/**
	 * @return if there are more characters left in the 
	 * current line.
	 */
	public boolean hasMoreChars()
	{
		return pos_ < length_;
	}
	
	
	/**
	 * @return if there are that much characters left in the current input string.
	 */
	public boolean hasMoreChars(int count)
	{
		return pos_ + count <= length_;
	}
	
	
	public int indexOf(char c)
	{
		return normIndexOf(currentLine_.indexOf(c, pos_), 1);
	}

	
	public int indexOf(String s)
	{
		return normIndexOf(currentLine_.indexOf(s, pos_), s.length());
	}
	
	
	private int normIndexOf(int p, int chars)
	{
		return p + chars <= length_ ? p : -1;
	}
	
	
	/**
	 * Positions on the next character in the current line. (Does not
	 * skip any whitespace).
	 * @return true if positioned, false if there are no more characters left
	 */
	public boolean skip()
	{
		return skip(1);
	}
	
	
	/**
	 * Positions on the next character in the current line. (Does not
	 * skip any whitespace).
	 * @return true if positioned, false if there are no more characters left
	 */
	public boolean skip(int n)
	{
		if (n > 0 && hasMoreChars())
			setPos(pos_ + n);
		return hasMoreChars();
	}
	
	
	/**
	 * Autoskips whitespace, and then tests (but does not consume) 
	 * if the scanner string starts with the given string.
	 */
	public boolean match(String s)
	{
		autoSkipWhitespace();
		int length = s.length();
		return hasMoreChars(length) && currentLine_.regionMatches(pos_, s, 0, length);
	}
	
	
	//----------------------------------------------
	// next-operations: consume from the current pos
	//----------------------------------------------
	

	/**
	 * Tells the scanner to expect a result from the following consume operation
	 * (all next* methods). If the result is false or null an exception is thrown
	 * @return this 
	 */
	public Scanner expect()
	{
		return expect("");
	}
	
	
	public Scanner expect(String what)
	{
		expect_ = what;
		return this;
	}

	
	public int expectInt()
	{
		return expect("int").nextInt().intValue();
	}

	
	public double expectDouble()
	{
		return expect("double").nextDouble().doubleValue();
	}

	
	private boolean nextResult(boolean result, String what, Object param)
	{
		String expect = expect_;
		if (expect != null)
		{
			expect_ = null;
			if (!result)
				nextFail(expect, what, param);
		}
		needSkipWhitespace_ = autoSkipWhitespace_;
		return result;
	}

	
	private <T> T nextResult(T result, String what, Object param)
	{
		String expect = expect_;
		if (expect != null)
		{
			expect_ = null;
			if (result == null)
				nextFail(expect, what, param);
		}
		needSkipWhitespace_ = autoSkipWhitespace_;
		return result;
	}
	
	
	private void nextFail(String expect, String method, Object param)
	{
		StringBuilder s = new StringBuilder();
		s.append("expected ");
		if (expect.length() > 0) 
			s.append(expect);
		else
		{
			s.append(method);
			if (param != null)
			{
				s.append('(');
				paramString(param, s);
				s.append(')');
			}
		}
		exception(s.toString());
	}
	
	
	private void paramString(Object param, StringBuilder s)
	{
		String p = param.toString();
		if (param instanceof String)
			s.append('"').append(p).append('"');
		else if (param instanceof Character)
			s.append('\'').append(p).append('\'');
		else
			s.append(p);
	}
	

	/**
	 * Tests if the current line at the current positions starts with the given char.
	 * If true positions on the index after that char.
	 * Else does not move the scanner position.
	 * Autoskips whitespace before the test is made, if autoskip is turned on.
	 * @param c a character
	 * @return char consumed?
	 */
	public boolean next(char c)
	{
		autoSkipWhitespace();
		boolean result = c == current();
		if (result)
			skip();
		return nextResult(result, "next", Character.valueOf(c));
	}
	

	/**
	 * Tests if the current line at the current positions starts with the given string.
	 * If true positions on the index after that prefix.
	 * Else does not move the scanner position.
	 * Autoskips whitespace before the test is made, if autoskip is turned on.
	 */
	public boolean next(String s)
	{
		boolean result = false;
		if (match(s)) // also skips whitespace
		{
			int last = pos_ + s.length();
			if (last <= length_)
			{
				setPos(last);
				result = true;
			}
		}
		return nextResult(result, "next", s);
	}

	
	/**
	 * Tests if the rest of the current line starts with the given string
	 * and is not immediately followed by a Java identifier char.
	 * If true positions on the index after that prefix.
	 * Else does not move the scanner position.
	 * Autoskips whitespace before the test is made, if autoskip is turned on.
	 */
	public boolean nextKeyword(String s)
	{
		boolean result = false;
		if (match(s))
		{
			int length = s.length();
			int last = pos_ + length;
			if (last <= length_)
			{
				if ((last == length_) || !Character.isJavaIdentifierPart(currentLine_.charAt(last)))
				{
					setPos(last); // also sets 
					result = true;
				}
			}
		}
		return nextResult(result, "nextKeyword", s);
	}
	
	
	/**
	 * Returns the string upto the next whitespace boundary.
	 * If that string is empty or the end is reached null is returned.
	 */
	public String nextToken()
	{
		return nextToken("");
	}
	
	
	/**
	 * Returns the string upto the next whitespace boundary or upto
	 * one of the characters on the delimiter string is reached.
	 * If that string is empty or the end is reached null is returned.
	 */
	public String nextToken(String delimiters)
	{
		return nextUpto(delimiters, true, false, false, true);
	}
	
	
	/**
	 * Does not skip whitespace.
	 * 
	 */
	public String nextUptoPos(int pos)
	{
		String result = null;
		if ((pos > pos_) && hasMoreChars())
		{
			int curpos = pos_;
			setPos(pos);
			result = norm(currentLine_.substring(curpos, pos_), true);
		}
		return nextResult(result, "nextUptoPos", null);
	}


	/**
	 * Moves the scanner position until one of the characters in the delimiter string
	 * is encountered. 
	 * @param delimiters defines the delimiter characters
	 * @param whitespaceLimits if true encountered whitespace is also treated as delimiter
	 * @param needDelim if true and no delimiter is found, an exception is thrown.
	 * 		Else the whole rest of the line is consumed.
	 * @param skipDelim if true, position after the delimiter, else stop at the delimiter
	 * @param norm if true and the result string is empty, it is normed to a null string
	 * @return the consumed string
	 */
	public String nextUpto(String delimiters, boolean whitespaceLimits, boolean needDelim, boolean skipDelim, boolean norm)
	{
		autoSkipWhitespace();
		return nextResult(nextUptoNoSkip(delimiters, whitespaceLimits, needDelim, skipDelim, norm), "nextUpTo", delimiters);
	}

	
	private String nextUptoNoSkip(String delimiters, boolean whitespaceLimits, boolean needDelim, boolean skipDelim, boolean norm)
	{
		int start = pos_;
		if (delimiters == null)
			delimiters = "";
		
		while(hasMoreChars())
		{
			char c = currentLine_.charAt(pos_++);
			if ((delimiters.indexOf(c) >= 0))
				return nextUptoFound(start, skipDelim, norm);
			else if (whitespaceLimits && Character.isWhitespace(c))  
				return nextUptoFound(start, false, norm);
		}
		if (needDelim)
			exception("expected one of '" + delimiters + "'");
		return norm(currentLine_.substring(start), norm);
	}
	
	
	private String nextUptoFound(int start, boolean skipDelim, boolean norm)
	{
		String s = currentLine_.substring(start, pos_ - 1);
		if (!skipDelim)
			pos_--;
		return norm(s, norm);
	}
	
	
	private static String norm(String s, boolean norm)
	{
		return norm && s.length() == 0 ? null : s;
	}
	
	
	/**
	 * Consumes all digit characters and return the string converted to an integer.
	 */
	public Integer nextInt()
	{
		String s = nextWhileCharType(Character.DECIMAL_DIGIT_NUMBER);
		try
		{
			return s != null ? Integer.valueOf(s) : null;
		}
		catch (NumberFormatException e)
		{
			exception("cannot parse number: " + s, e);
			return null; // does not happen
		}
	}


	/**
	 * Consumes all digit characters and an optional fraction part
	 * and returns the string converted to a double.
	 */
	public Double nextDouble()
	{
		autoSkipWhitespace();
		int start = pos_;
		advanceWhileCharType(Character.DECIMAL_DIGIT_NUMBER, true);
		if (pos_ > start)
		{
			if (next('.'))
				advanceWhileCharType(Character.DECIMAL_DIGIT_NUMBER, true);
		}
		Double result = null;
		if (pos_ > start)
		{
			String s = currentLine_.substring(start, pos_);
			try
			{
				return result = Double.valueOf(s);
			}
			catch (NumberFormatException e)
			{
				exception("cannot parse number: " + s, e);
				return null; // does not happen
			}
		}
		return nextResult(result, "nextDouble", null); 
	}

	
	/**
	 * @return consumes and returns hex encoded bytes.
	 */
	public byte[] nextHexBytes()
	{
		String hex = nextWhile("0123456789abcdefABCDEF");
		byte[] b = null;
		if (hex != null)
		{
			b = new byte[hex.length() / 2];
			int next   = 0;
			for (int i=0; i<b.length; i++)
			{
				b[i] = (byte)Integer.parseInt(hex.substring(next, next + 2), 16);
				next += 2;
			}
		}
		return b;
	}
	
	
	/**
	 * Move the scanner position while seeing characters of that type.
	 */
	public String nextWhileCharType(byte charType)
	{
		return nextWhileCharType(charType, true);
	}

	
	public String nextWhileCharType(byte charType, boolean equals)
	{
		autoSkipWhitespace();

		int start = pos_;
		advanceWhileCharType(charType, equals);
		return nextResult(pos_ > start ? currentLine_.substring(start, pos_) : null, "nextWhileCharType", null);
	}
	
	
	private void advanceWhileCharType(byte charType, boolean equals)
	{
		while (hasMoreChars())
		{
			if ((Character.getType(currentLine_.charAt(pos_)) == charType) != equals)
				break;
			pos_++;
		}
	}

	
	/**
	 * Move the scanner position while seeing characters contained in
	 * the chars parameter.
	 */
	public String nextWhile(String chars)
	{
		autoSkipWhitespace();

		int start = pos_;
		while (hasMoreChars())
		{
			if (chars.indexOf(currentLine_.charAt(pos_)) == -1)
				break;
			pos_++;
		}
		
		return nextResult(pos_ > start ? currentLine_.substring(start, pos_) : null, "nextWhile", chars);
	}
	
	
	/**
	 * @return Scans a quoted string and returns the string without quotes.
	 * The quote character is the current char
	 */
	public String nextQuotedString()
	{
		return nextQuotedString(false);
	}

	
	/**
	 * @return Scans a quoted string and returns the string without quotes.
	 */
	public String nextQuotedString(boolean includeQuotes)
	{
		autoSkipWhitespace();
		char quote = hasMoreChars() ? (char)current() : 0;
		return nextQuotedString(quote, includeQuotes);
	}

	
	/**
	 * @return Scans a quoted string and returns the string without quotes.
	 */
	public String nextQuotedString(char quote)
	{
		return nextQuotedString(quote, false);
	}
	
	
	/**
	 * Scans a quoted string and returns it.
	 * The current character is used as quote char.
	 * Does not autoskip whitespace. 
	 * @param quote the quote char
	 * @param includeQuotes should the quotes be included?
	 * @return the consumed string
	 */
	public String nextQuotedString(char quote, boolean includeQuotes)
	{
		autoSkipWhitespace();
		String result = null;
		if (current() == quote)
		{
			pos_++;
			result = nextUptoNoSkip(String.valueOf(quote), false, true, true, false);
			if (includeQuotes)
				result = quote + result + quote;
		}
		return nextResult(result, "nextQuotedString", quote);
	}
	
	
	/**
	 * @return consumes and returns the rest of the current line.
	 */
	public String nextRest()
	{
		autoSkipWhitespace();

		String s = getRest();
		pos_ 	 = length_;
		return nextResult(s, "nextRest", null); 
	}
	

	/**
	 * Scans a Java identifier and returns it.
	 * @return the identifier or null
	 */
	public String nextIdentifier()
	{
		autoSkipWhitespace();

		String result = null;
		if (Character.isJavaIdentifierStart(current()))
		{
			int start = pos_;
			do
			{
				pos_++;
			}
			while(Character.isJavaIdentifierPart(current()));
			result = currentLine_.substring(start, pos_);
		}
		return nextResult(result, "nextIdentifier", null);
	}

	
	/**
	 * Skip all whitespace character. Progresses to the next
	 * line If the end of the line is reached
	 */
	public void skipWhitespace()
	{
		needSkipWhitespace_ = false;
		while (true)
		{
			if (hasMoreChars())
			{
				switch (currentLine_.charAt(pos_))
				{
					case '\n':
					case ' ':
					case '\r':
					case '\t':
						pos_++;
						continue;
				}
				return;
			}
			if (!input.nextLine())
				return;
		}
	}
	
	
	/**
	 * @return the rest of the current line.
	 */
	public String getRest()
	{
		return hasMoreChars() ? currentLine_.substring(pos_, length_) : ""; // length_ can be smaller than currentLine_.length()
	}
	
	
	/**
	 * Raises an exception with context information about input and current position.
	 */
	public void exception(String message)
	{
		exception(message, null);
	}
	
	
	public void exception(String message, Throwable cause)
	{
		RuntimeException e; 
		if (errorHandler_ != null)
			e = errorHandler_.scanError(message, cause, this);
		else
		{
			String error = buildErrorMessage(message, cause, this);
			e = new IllegalArgumentException(error, cause);
		}
		throw e;
	}
	
	
	public Scanner setErrorHandler(ErrorHandler errorHandler)
	{
		errorHandler_ = errorHandler;
		return this;
	}
	
	
	public ErrorHandler getErrorHandler()
	{
		return errorHandler_;
	}

	
	/**
	 * A callback interface for scanners when they encounter an error.
	 */
	public static interface ErrorHandler
	{
		public RuntimeException scanError(String message, Throwable cause, Scanner scanner);
	}
	
	
	public static String buildErrorMessage(String msg, Throwable cause, Scanner scanner)
	{
		StringBuilder sb = new StringBuilder();

		// 1. line: show location
		if (scanner != null)
		{
			sb.append('[');
			String source = scanner.input.getSource();
			if (source != null)
				sb.append("src=").append(source).append(':');
			int lineIndex = scanner.input.getLineIndex();
			if (lineIndex >= 0)
				sb.append("ln=").append(lineIndex + 1).append(':');
			sb.append("col=").append(scanner.getPos() + 1);
			sb.append("]\n");
		}
		
		// 2. line: show message
		sb.append(effectiveErrorMessage(msg, cause)).append('\n');
		
		// 3. show line content + 4. pos
		if (scanner != null)
		{
			String line = scanner.getLine();
			if (line != null)
			{
				sb.append("line=");
				int len = line.length();
				for (int i=0; i<len; i++)
				{
					char c = line.charAt(i);
					sb.append(c < 33 ? '.' : c);
				}
				sb.append('\n');
				sb.append("pos ="); // should match "line="
				int pos = scanner.getPos();
				for (int i=0; i<pos; i++)
					sb.append(' ');
				sb.append('^');
			}
		}
		
		return sb.toString();
	}
	
	
	private static String effectiveErrorMessage(String msg, Throwable cause)
	{
		if (msg == null)
		{
			if (cause != null)
			{
				msg = cause.getMessage();
				if (msg == null)
					msg = cause.toString();
			}
			else
				msg = "error";
		}
		return msg;
	}
	
	
	
	private int pos_;
	private int length_;
	private String currentLine_;
	private boolean needSkipWhitespace_;
	private boolean autoSkipWhitespace_ = true;
	private String expect_;
	private ErrorHandler errorHandler_;
	public final Input input = new Input();
}
