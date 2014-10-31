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
	public static String encodeString(String s)
	{
		return new UriEncoder().encode(s);
	}
	

	/**
	 * Encodes a string. 
	 */
	public String encode(String value)
	{
		StringBuilder s = new StringBuilder();
		encode(value, s);
		return s.toString();
	}
	
	
	/**
	 * Encodes a string and appends the result to a StringBuilder. 
	 */
	public void encode(String value, StringBuilder out)
	{
		int length = value.length();
		for (int i=0; i<length; i++)
		{
			char c = value.charAt(i);
			if ((c < 128) && UNRESERVED_CHARS[c])
				out.append(c);
			else
			{
				if (percentEncoder_ == null)
					percentEncoder_ = new PercentEncoder();
				percentEncoder_.escape(c, out);
			}
		}
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
