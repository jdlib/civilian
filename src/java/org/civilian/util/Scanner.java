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


import java.io.File;
import org.civilian.tool.csp.CspException;


/**
 * Scanner helps to scan or parse strings.
 */
public class Scanner
{
	/**
	 * Creates a new Scanner.
	 */
	public Scanner()
	{
		init((String)null);
	}
	
	
	/**
	 * Creates a new Scanner for the string.
	 */
	public Scanner(String input)
	{
		init(input);
	}
	
	
	/**
	 * Creates a new Scanner for the string.
	 * @param start start scanning at that index
	 */
	public Scanner(String input, int start)
	{
		init(input, start);
	}
	
	
	/**
	 * Creates a new Scanner for multiple lines.
	 */
	public Scanner(String... lines)
	{
		init(lines);
	}
	
	
	/**
	 * Sets the string which should be scanned.
	 */
	public void init(String input)
	{
		init(input, 0);
	}
	
	
	/**
	 * Sets the string which should be scanned.
	 * @param start start scanning at that index
	 */
	public void init(String input, int start)
	{
		initInput(input, start);
		
		lines_		= new String[] { currentLine_ };
		lineIndex_	= 0;
	}

	
	/**
	 * Sets the strings which should be scanned.
	 */
	public void init(String... lines)
	{
		if ((lines == null) || (lines.length == 0))
			init("", 0);
		else
		{
			initInput(lines[0], 0);
			lines_		= lines;
			lineIndex_	= 0;
		}
	}
	
	
	private void initInput(String input, int start)
	{
		currentLine_ = input != null ? input : "";
		length_ 	 = currentLine_.length();
		pos_ 		 = Math.max(0, Math.min(start, length_));
		needSkipWhitespace_ = autoSkipWhitespace_;
	}

	
	/**
	 * Stores the source of the scanner input.
	 */
	public void setSource(File source)
	{
		setSource(source != null ? source.getAbsolutePath() : null);
	}
	
	  
	/**
	 * Stores the source of the scanner input.
	 */
	public void setSource(String source)
	{
		source_ = source;
	}
	
	
	/**
	 * Returns the source of the scanner data.
	 */
	public String getSource()
	{
		return source_; 
	}

	
	/**
	 * Returns the lines on which the scanner is operating.
	 */
	public String[] getLines()
	{
		return lines_;
	}
	
	
	/**
	 * Returns the current scanner line index, starting at 0.
	 */
	public int getLineIndex()
	{
		return lineIndex_;
	}
	
	
	/**
	 * Returns the current scanner line index, starting at 0.
	 */
	public int getLineCount()
	{
		return lines_.length;
	}
	
	
	/**
	 * Returns the current scanner position within in the current line.
	 */
	public int getPos()
	{
		return pos_;
	}
	

	/**
	 * Returns the length of the current line.
	 */
	public int getLength()
	{
		return length_;
	}

	
	/**
	 * Returns the current line.
	 * @return the current line or "" if not yet initialized or scanner advanced over all input lines
	 */
	public String getLine()
	{
		return currentLine_;
	}

	
	/**
	 * Returns if the scanner has parsed all line and is now 
	 * positioned after the last line.
	 */
	public boolean isEOF()
	{
		return lineIndex_ >= lines_.length;
	}

	
	/**
	 * Returns the current character or -1 if there are no more characters.
	 */
	public int current()
	{
		return hasMoreChars() ? currentLine_.charAt(pos_) : -1;
	}
	
	
	/**
	 * Returns if the current character has the given character type.
	 * @param charType a character type defined in the Character class
	 * @see Character#getType(char)
	 */
	public boolean currentHasType(byte charType)
	{
		return Character.getType(current()) == charType;
	}
	
	
	/**
	 * Returns if the current character is a digit.
	 */
	public boolean currentIsDigit()
	{
		return currentHasType(Character.DECIMAL_DIGIT_NUMBER);
	}

	
	/**
	 * Sets if whitespace should automatically be skipped,
	 * before consume*, next* or expect*-operations are 
	 * performed. 
	 */
	public void autoSkipWhitespace(boolean flag)
	{
		autoSkipWhitespace_ = flag;
		needSkipWhitespace_ = flag;
	}
	
	
	private void autoSkipWhitespace()
	{
		if (needSkipWhitespace_)
			skipWhitespace();
	}
	
	
	/**
	 * Returns if there are more characters left in the 
	 * current line.
	 */
	public boolean hasMoreChars()
	{
		return pos_ < length_;
	}
	
	
	/**
	 * Returns if there are that much characters left in the current input string.
	 */
	public boolean hasMoreChars(int length)
	{
		return pos_ + length <= length_;
	}

	
	private void advancePos(int n)
	{
		pos_ += n;
		needSkipWhitespace_ = autoSkipWhitespace_; 
	}
	
	
	/**
	 * Positions on the next character in the current line. (Does not
	 * skip any whitespace).
	 * @return true if positioned, false if there are no more characters left
	 */
	public boolean skip()
	{
		if (hasMoreChars())
			advancePos(1);
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
	
	
	/**
	 * Positions the scanner on the next line
	 * @return false if the end of lines was reached
	 */
	public boolean nextLine()
	{
		if (lineIndex_ < lines_.length - 1)
		{
			initInput(lines_[++lineIndex_], 0);
			return true;
		}
		else 
		{
			if (lineIndex_ == lines_.length - 1)
			{
				// empty line
				lineIndex_++;
				length_ = 0;
				pos_ = 0;
				currentLine_ = "";
			}
			return false;
		}
	}

	
	/**
	 * Tests if the current line at the current positions starts with the given string.
	 * If true positions on the index after that prefix.
	 * Else does not move the scanner position.
	 * Autoskips whitespace before the test is made, if autoskip is turned on.
	 */
	public boolean next(String s)
	{
		return next(s, false);
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
		return next(s, true);
	}
	
	
	private boolean next(String s, boolean testKeyword)
	{
		autoSkipWhitespace();
		
		int length = s.length();
		if (hasMoreChars(length) && currentLine_.regionMatches(pos_, s, 0, length))
		{
			int last = pos_ + length;
			if (last <= length_)
			{
				if (!testKeyword || (last == length_) || !Character.isJavaIdentifierPart(currentLine_.charAt(last)))
				{
					advancePos(length);
					return true;
				}
			}
		}
		return false;
	}
	

	/**
	 * Consumes the first matching string. 
	 * @return the matched string, or null if no string matches 
	 */
	public String consumeAny(String... strings)
	{
		for (String s : strings)
		{
			if (next(s))
				return s;
		}
		return null;
	}
	
	
	public String nextToken(String what) throws CspException
	{
		return nextToken(what, "");
	}


	public String nextToken(String what, String delims) throws CspException
	{
		String token = consumeToken(delims);
		if (token == null)
			throw exception("missing " + what + "-value");
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
			throw exception("expected one of '" + delimiters + "'");
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
			throw exception("expected a integer");
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
		if (next("."))
			increaseWhile(Character.DECIMAL_DIGIT_NUMBER, true);
		if (pos_ == start)
			throw exception("expected a double");
		return Double.parseDouble(currentLine_.substring(start, pos_)); 
	}

	
	/**
	 * Consumes hex encoded bytes.
	 */
	public byte[] consumeBytes()
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
	 * Scans a quoted string and returns the string without quotes.
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
	 * Returns the rest of the current line.
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
		if (!next(s))
			throw exception("expected '" + s + "'");
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
		throw exception("expected one of '" + s + "'");
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
			if (!nextLine())
				return;
		}
	}
	
	
	/**
	 * Returns the rest of the current line.
	 */
	public String getRest()
	{
		return hasMoreChars() ? currentLine_.substring(pos_) : ""; 
	}
	

	/**
	 * Raises an exception with context information about input and current position.
	 */
	public IllegalArgumentException exception(String message)
	{
		if (errorHandler_ != null)
			errorHandler_.scanError(this, message);
		StringBuilder s = new StringBuilder(message);
		s.append(" (");
		if (getLineCount() > 1)
			s.append(getLineIndex() + 1).append(':');
		s.append(pos_ + 1).append("): '").append(currentLine_);
		return new IllegalArgumentException(s.toString()); 
	}
	
	
	public void setErrorHandler(ErrorHandler errorHandler)
	{
		errorHandler_ = errorHandler;
	}
	
	
	/**
	 * A callback interface for scanners when they encounter an error.
	 */
	public static interface ErrorHandler
	{
		public void scanError(Scanner scanner, String message);
	}

	
	private int pos_;
	private int length_;
	private int lineIndex_;
	private String currentLine_;
	private String[] lines_;
	private boolean autoSkipWhitespace_ = true;
	private boolean needSkipWhitespace_;
	private ErrorHandler errorHandler_;
	private String source_;
}
