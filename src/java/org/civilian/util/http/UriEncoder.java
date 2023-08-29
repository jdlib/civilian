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
package org.civilian.util.http;


import org.civilian.util.Check;


/**
 * UriEncoder is a helper class to 
 * <a href="https://en.wikipedia.org/wiki/Percent_encoding">percent encode</a>
 * URIs or URLs. Reserved characters in a string are converted in a percent-encoded
 * representation.  Non-ASCII characters are converted to their UTF-8 byte representation
 * and then percent encoded.
 */
public class UriEncoder
{
	private UriEncoder()
	{
	}
	
	
	/**
	 * Encodes a string. 
	 */
	public static String encode(String s)
	{
		Check.notNull(s, "s");
		int n = s.length();
		for (int i=0; i<n; ) 
		{
		    int codePoint = s.codePointAt(i);
		    if (needsEncoding(codePoint))
		    	return encodeSlow(s, i);
		    i += Character.charCount(codePoint);
		}
		return s;
	}
	
	
	private static String encodeSlow(String s, int start)
	{
		StringBuilder out = new StringBuilder();
		out.append(s, 0, start);
		encode(s, out, start);
		return out.toString();
	}
	

	/**
	 * Encodes a string and appends the result to a StringBuilder. 
	 */
	public static void encode(String s, StringBuilder out)
	{
		Check.notNull(s, "s");
		Check.notNull(out, "out");
		encode(s, out, 0);
	}

	
	private static void encode(String s, StringBuilder out, int start)
	{
		int n = s.length();
		for (int i=start; i<n; ) 
		{
		    int codePoint = s.codePointAt(i);
		    if (needsEncoding(codePoint))
		    	escapeCodePoint(out, codePoint);
		    else
		    	out.append((char)codePoint);
		    	
		    i += Character.charCount(codePoint);
		}
	}
	
	
	private static boolean needsEncoding(int codePoint)
	{
		return (codePoint >= 128) || !UNRESERVED_CHARS[codePoint];
	}
	
	
	private static void escapeCodePoint(StringBuilder out, int codePoint)
	{
        if (codePoint < 0x80)
        {
        	// 1 byte, at most 7 bits
        	escapeByte(out, codePoint);
        }
        else if (codePoint < 0x800) 
        {
        	// 2 bytes, 11 bits
        	escapeByte(out, 0xc0 | (codePoint >> 6));
        	escapeByte(out, 0x80 | (codePoint & 0x3f));
        } 
        else if (codePoint < 0x10000)
        {
            escapeByte(out, 0xe0 | ((codePoint >> 12)));
            escapeByte(out, 0x80 | ((codePoint >> 6) & 0x3f));
            escapeByte(out, 0x80 | (codePoint & 0x3f));
        }
        else 
        {
        	escapeByte(out, 0xf0 | ((codePoint >> 18)));
        	escapeByte(out, 0x80 | ((codePoint >> 12) & 0x3f));
        	escapeByte(out, 0x80 | ((codePoint >>  6) & 0x3f));
        	escapeByte(out, 0x80 | (codePoint & 0x3f));
        } 
	}
	

	private static void escapeByte(StringBuilder out, int n)
	{
		out.append('%');
		out.append(HEX_DIGITS[n / 16]);
		out.append(HEX_DIGITS[n % 16]);
	}
	

    private static final boolean UNRESERVED_CHARS[] = new boolean[128];
	static
	{
		for (char c = 'a'; c<='z'; c++)
			UNRESERVED_CHARS[c] = true;
		for (char c = 'A'; c<='Z'; c++)
			UNRESERVED_CHARS[c] = true;
		for (char c = '0'; c<='9'; c++)
			UNRESERVED_CHARS[c] = true;
		UNRESERVED_CHARS['.'] = true;
		UNRESERVED_CHARS['-'] = true;
		UNRESERVED_CHARS['~'] = true;
		UNRESERVED_CHARS['_'] = true;
	}
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
}
