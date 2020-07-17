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
package org.civilian.response;


import org.civilian.internal.PercentEncoder;
import org.civilian.util.Check;


/**
 * UriEncoder is a helper class to 
 * <a href="https://en.wikipedia.org/wiki/Percent_encoding">percent encode</a>
 * URIs or URLs. Reserved characters in a string are converted in a percent-encoded
 * representation.  
 */
public class UriEncoder
{
	/**
	 * Creates a UriEncoder and invokes {@link #encode(String)}.
	 */
	public static String encodeString(String value)
	{
		Check.notNull(value, "value");
		int n = firstToEncode(value);
		return n < 0 ? value : new UriEncoder().encodeSlow(value, n);
	}
	

	/**
	 * Encodes a string. 
	 */
	public String encode(String value)
	{
		Check.notNull(value, "value");
		int n = firstToEncode(value);
		return n < 0 ? value : encodeSlow(value, n);
	}
	
	
	private String encodeSlow(String value, int first)
	{
		StringBuilder out = new StringBuilder();
		out.append(value, 0, first);
		encode(value, out, first);
		return out.toString();
	}
	
	
	/**
	 * Encodes a string and appends the result to a StringBuilder. 
	 */
	public void encode(String value, StringBuilder out)
	{
		Check.notNull(value, "value");
		encode(value, out, 0);
	}
	
	
	public void encode(String value, StringBuilder out, int first)
	{
		int length = value.length();
		for (int i=first; i<length; i++)
		{
			char c = value.charAt(i);
			if (needsNoEncoding(c))
				out.append(c);
			else
			{
				if (percentEncoder_ == null)
					percentEncoder_ = new PercentEncoder();
				percentEncoder_.escape(c, out);
			}
		}
	}
	
	
	private static boolean needsNoEncoding(char c)
	{
		return (c < 128) && UNRESERVED_CHARS[c];
	}
	
	
	private static int firstToEncode(String value)
	{
		int length = value.length();
		for (int i=0; i<length; i++)
		{
			if (!needsNoEncoding(value.charAt(i)))
				return i;
		}
		return -1;
	}

	
	private PercentEncoder percentEncoder_;
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
}
