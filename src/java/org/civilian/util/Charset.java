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
 * Charset is a small helper class  to determine if a character
 * is printable in a certain charset. (E.g. IS=-8859-1 can only
 * print characters <= 0xff). We use this class
 * in HTML output to determine if we need to print a character reference
 * instead of the character itself.
 * This is just used as a conservative heuristic so we do not intent
 * comprehensive coverage of charsets (We wish we could get 
 * the information from java.nio.charset.Charset).
 */
public abstract class Charset
{
	public static final Charset UNRESTRICTED 	= new UnrestrictedCharset();
	public static final Charset SEVEN_BIT		= new SimpleCharset(0x007f);
	public static final Charset EIGHT_BIT 		= new SimpleCharset(0x00ff);
	
	
	/**
	 * Returns a Charset for the charset name. If not known a eight-bit charset
	 * is returned.
	 * @param charsetName a charset name like UTF-8, ISO-8859-1
	 */
	public static Charset getCharset(String charsetName)
	{
		if (charsetName == null)
			return EIGHT_BIT; // don't bother 
		
		charsetName = charsetName.toUpperCase();
		if (charsetName.startsWith("UTF-"))
			return UNRESTRICTED;
		else if (startsWith(charsetName, "ISO-8859-"))
			return EIGHT_BIT; // actually ISO-8859-2 could print more 
		else if (startsWith(charsetName, "EBCDIC-CP-"))
			return EIGHT_BIT;
		else if (startsWith(charsetName, "EUC-"))
			return UNRESTRICTED;
		else if (startsWith(charsetName, "ASCII"))
			return SEVEN_BIT;
		else
			return EIGHT_BIT;
	}
	
	
	private static boolean startsWith(String name, String prefix)
	{
		return name.regionMatches(true /*ignore-case*/, 0, prefix, 0, prefix.length());
	}
	
	
	/**
	 * Tests if the charset can print the character.
	 */
	public abstract boolean isPrintable(char c);
}


class SimpleCharset extends Charset
{
	public SimpleCharset(int lastPrintable)
	{
		lastPrintable_ = lastPrintable;
	}
	
	
	@Override
	public boolean isPrintable(char c)
	{
		return c <= lastPrintable_;
	}
	
	
	private int lastPrintable_;
}



class UnrestrictedCharset extends Charset
{
	@Override public boolean isPrintable(char c)
	{
        // in unicode not entirely correct for one half of surrogate pairs
		// (c<0xD800) || (c>0xDBFF)
        return true;
	}
}