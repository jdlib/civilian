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
		public Input setLines(String... lines)
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
		public Input setSource(String source)
		{
			source_ = source;
			return this;
		}
		
		
		/**
		 * @return the source of the scanner input.
		 * @see #setSource(String)
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
		input.setLines(lines);
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
	 * Invalid positions (e.g. < 0 or > {@link #getLength()} length) are silently corrected.
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
	 * Invalid positions (e.g. < 0 or > the real length of the current line) are silently corrected.
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
		return currentLine_.indexOf(c, pos_);
	}

	
	public int indexOf(String s)
	{
		return currentLine_.indexOf(s, pos_);
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
		return expect(true);
	}
	
	
	public Scanner expect(boolean flag)
	{
		expect_ = flag;
		return this;
	}

	
	private boolean nextResult(boolean result, String what, Object param)
	{
		if (expect_)
		{
			expect_ = false;
			if (!result)
				nextFail(what, param);
		}
		needSkipWhitespace_ = autoSkipWhitespace_;
		return result;
	}

	
	private <T> T nextResult(T result, String what, Object param)
	{
		if (expect_)
		{
			expect_ = false;
			if (result == null)
				nextFail(what, param);
		}
		needSkipWhitespace_ = autoSkipWhitespace_;
		return result;
	}
	
	
	private void nextFail(String what, Object param)
	{
		String message = "expected " + what + (param != null ? "(" +  param + ')' : "");
		exception(message);
	}
	

	/**
	 * Tests if the current line at the current positions starts with the given char.
	 * If true positions on the index after that char.
	 * Else does not move the scanner position.
	 * Autoskips whitespace before the test is made, if autoskip is turned on.
	 * @param char a character
	 * @return char consumed?
	 */
	public boolean consume(char c)
	{
		autoSkipWhitespace();
		if (c == current())
		{
			skip(); // also sets needSkipWhitespace_ = true 
			return true;
		}
		else if (!expect_)
			return false;
		else
			return nextResult(false, "next", c);
	}
	

	/**
	 * Tests if the current line at the current positions starts with the given string.
	 * If true positions on the index after that prefix.
	 * Else does not move the scanner position.
	 * Autoskips whitespace before the test is made, if autoskip is turned on.
	 */
	public boolean consume(String s)
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
	public boolean consumeKeyword(String s)
	{
		if (match(s))
		{
			int length = s.length();
			int last = pos_ + length;
			if (last <= length_)
			{
				if ((last == length_) || !Character.isJavaIdentifierPart(currentLine_.charAt(last)))
				{
					setPos(last);
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * Consumes the first matching string. 
	 * @param strings some strings
	 * @return the matched string, or null if no string matches
	 * @see #consume(String) 
	 */
	public String consumeAny(String... strings)
	{
		for (String s : strings)
		{
			if (consume(s))
				return s;
		}
		return null;
	}
	
	
	public String nextToken(String what)
	{
		return nextToken(what, "");
	}


	public String nextToken(String what, String delims)
	{
		String token = consumeToken(delims);
		if (token == null)
			exception("missing " + what + "-value");
		return token;
	}

	
	/**
	 * Returns the string upto the next whitespace boundary.
	 * If that string is empty or the end is reached null is returned.
	 */
	public String consumeToken()
	{
		return consumeToken("");
	}
	
	
	/**
	 * Returns the string upto the next whitespace boundary or upto
	 * one of the characters on the delimiter string is reached.
	 * If that string is empty or the end is reached null is returned.
	 */
	public String consumeToken(String delimiters)
	{
		String s = consumeUpto(delimiters, true, false, false);
		if (s.length() > 0)
		{
			return s;
		}
		else
			return null;
	}
	
	
	public String consumeUpto(int pos)
	{
		if (pos > pos_)
		{
			String s = currentLine_.substring(pos_, pos);
			pos_ = pos;
			return s;
		}
		else
			return "";
	}

	
	/**
	 * Moves the scanner position until one of the characters in the delimiter string
	 * is encountered. 
	 * @param delimiters defines the delimiter characters
	 * @param whitespaceLimits if true encountered whitespace is also treated as delimiter
	 * @param needDelim if true and no delimiter is found, an exception is thrown.
	 * 		Else the whole rest of the line is consumed.
	 * @param skipDelim if true, position after the delimiter, else stop at the delimiter
	 * @return the consumed string
	 */
	public String consumeUpto(String delimiters, boolean whitespaceLimits, boolean needDelim, boolean skipDelim)
	{
		autoSkipWhitespace();
		return consumeUptoNoSkip(delimiters, whitespaceLimits, needDelim, skipDelim);
	}


	private String consumeUptoNoSkip(String delimiters, boolean whitespaceLimits, boolean needDelim, boolean skipDelim)
	{
		int start = pos_;
		if (delimiters == null)
			delimiters = "";
		
		while(hasMoreChars())
		{
			char c = currentLine_.charAt(pos_++);
			if ((delimiters.indexOf(c) >= 0))
				return consumeUptoFound(start, skipDelim);
			else if (whitespaceLimits && Character.isWhitespace(c))  
				return consumeUptoFound(start, false);
		}
		if (needDelim)
			exception("expected one of '" + delimiters + "'");
		needSkipWhitespace_ = autoSkipWhitespace_;
		return currentLine_.substring(start);
	}
	
	
	private String consumeUptoFound(int start, boolean skipDelim)
	{
		needSkipWhitespace_ = autoSkipWhitespace_;
		String s = currentLine_.substring(start, pos_ - 1);
		if (!skipDelim)
			pos_--;
		return s;
	}
	
	
	/**
	 * Consumes all digit characters and return the string converted to an integer.
	 */
	public int consumeInt()
	{
		String s = consumeWhile(Character.DECIMAL_DIGIT_NUMBER);
		if (s == null)
			exception("expected a integer");
		return Integer.parseInt(s);
	}


	/**
	 * Consumes all digit characters and an optional fraction part
	 * and returns the string converted to a double.
	 */
	public double consumeDouble()
	{
		autoSkipWhitespace();
		int start = pos_;
		increaseWhile(Character.DECIMAL_DIGIT_NUMBER, true);
		if (consume('.'))
			increaseWhile(Character.DECIMAL_DIGIT_NUMBER, true);
		if (pos_ == start)
			exception("expected a double");
		return Double.parseDouble(currentLine_.substring(start, pos_)); 
	}

	
	/**
	 * @return consumes and returns hex encoded bytes.
	 */
	public byte[] consumeHexBytes()
	{
		String hex = consumeWhile("0123456789abcdefABCDEF");
		byte[] b   = new byte[hex.length() / 2];
		int next   = 0;
		for (int i=0; i<b.length; i++)
		{
			b[i] = (byte)Integer.parseInt(hex.substring(next, next + 2), 16);
			next += 2;
		}
		return b;
	}
	
	
	/**
	 * Move the scanner position while seeing characters of that type.
	 */
	public String consumeWhile(byte charType)
	{
		return consumeWhile(charType, true);
	}

	
	public String consumeWhile(byte charType, boolean equals)
	{
		autoSkipWhitespace();

		int start = pos_;
		increaseWhile(charType, equals);
		return pos_ > start ? currentLine_.substring(start, pos_) : null;
	}
	
	
	private void increaseWhile(byte charType, boolean equals)
	{
		while (hasMoreChars())
		{
			if ((Character.getType(currentLine_.charAt(pos_)) == charType) != equals)
				break;
			pos_++;
		}
		needSkipWhitespace_ = autoSkipWhitespace_;
	}

	
	/**
	 * Move the scanner position while seeing characters contained in
	 * the chars parameter.
	 */
	public String consumeWhile(String chars)
	{
		autoSkipWhitespace();

		int start = pos_;
		while (hasMoreChars())
		{
			if (chars.indexOf(currentLine_.charAt(pos_)) == -1)
				break;
			pos_++;
		}
		needSkipWhitespace_ = autoSkipWhitespace_;
		return pos_ > start ? currentLine_.substring(start, pos_) : null;
	}
	
	
	/**
	 * @return a quoted string and returns the string without quotes.
	 */
	public String consumeQuotedString()
	{
		return consumeQuotedString(false);
	}
	
	
	/**
	 * Scans a quoted string and returns it.
	 * The current character is used as quote char.
	 * Does not autoskip whitespace. 
	 * @param includeQuotes should the quotes be included?
	 * @return the consumed string
	 */
	public String consumeQuotedString(boolean includeQuotes)
	{
		if (!hasMoreChars())
			return null;
		
		char quote = currentLine_.charAt(pos_++);
		String s   = consumeUptoNoSkip(String.valueOf(quote), false, true, true);
		needSkipWhitespace_ = autoSkipWhitespace_;
		return includeQuotes ? quote + s + quote : s;
	}
	
	
	/**
	 * @return consumes and returns the rest of the current line.
	 */
	public String consumeRest()
	{
		autoSkipWhitespace();

		String s = getRest();
		pos_ 	 = length_;
		needSkipWhitespace_ = autoSkipWhitespace_;
		return s; 
	}
	

	/**
	 * Scans a Java identifier and returns it.
	 */
	public String consumeIdentifier()
	{
		autoSkipWhitespace();

		if (!Character.isJavaIdentifierStart(current()))
			return null;
		
		int start = pos_;
		do
		{
			pos_++;
		}
		while(Character.isJavaIdentifierPart(current()));
		needSkipWhitespace_ = autoSkipWhitespace_;
		return currentLine_.substring(start, pos_);
	}
	

	/**
	 * Consumes a string. If this fails raise an exception.
	 */
	public void expect(String s)
	{
		if (!consume(s))
			exception("expected '" + s + "'");
	}
	
	
	/**
	 * Consume a character which must be contained in the given parameter
	 * string. If this fails raise an exception.
	 */
	public char expectOneOf(String s)
	{
		autoSkipWhitespace();
		if (hasMoreChars())
		{
			char c = currentLine_.charAt(pos_);
			if (s.indexOf(c) >= 0)
			{
				pos_++;
				needSkipWhitespace_ = autoSkipWhitespace_;
				return c;
			}
		}
		exception("expected one of '" + s + "'");
		return 0;
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
		return hasMoreChars() ? currentLine_.substring(pos_) : ""; 
	}
	

	/**
	 * Raises an exception with context information about input and current position.
	 */
	public void exception(String message)
	{
		RuntimeException e; 
		if (errorHandler_ != null)
			e = errorHandler_.scanError(message, this);
		else
		{
			StringBuilder s = new StringBuilder(message);
			s.append(" (");
			if (input.getLineCount() > 1)
				s.append(input.getLineIndex() + 1).append(':');
			s.append(pos_ + 1).append("): '").append(currentLine_);
			e = new IllegalArgumentException(s.toString());
		}
		throw e;
	}
	
	
	public void setErrorHandler(ErrorHandler errorHandler)
	{
		errorHandler_ = errorHandler;
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
		public RuntimeException scanError(String message, Scanner scanner);
	}
	
	
	private int pos_;
	private int length_;
	private String currentLine_;
	private boolean needSkipWhitespace_;
	private boolean autoSkipWhitespace_ = true;
	private boolean expect_;
	private ErrorHandler errorHandler_;
	public final Input input = new Input();
}
