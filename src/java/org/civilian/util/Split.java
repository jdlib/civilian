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


import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Split is a small helper class for splitting strings.
 */
public class Split
{
	/**
	 * Creates a Split
	 * @param n the number of parts into which a string is splitted.
	 * @param pattern a split pattern
	 */
	public Split(int n, String pattern)
	{
		this(n, Pattern.compile(pattern));
	}
	
	
	/**
	 * Creates a Split
	 * @param n the number of parts into which a string is splitted.
	 * @param pattern a split pattern
	 */
	public Split(int n, Pattern pattern)
	{
		this.s = new String[n];
		this.pattern_ = pattern;
	}
	
	
	/**
	 * Advises the split to trim splitted parts.
	 * @return this
	 */
	public Split trim()
	{
		trim_ = true;
		return this;
	}
	
	
	private void set(int n, String value)
	{
		s[n] = trim_ ? value.trim() : value;
	}
	
	
	/**
	 * Split the given input.
	 * @param input the input
	 * @return the number of actual parts contained in the input.
	 */
	public int apply(String input)
	{
		Matcher m = pattern_.matcher(input);
		int next  = 0;
        int index = 0;
		while(true) 
		{
			if (!m.find() || (next == s.length - 1))
			{
            	set(next++, input.substring(index));
            	break;
			}
			else
			{
				set(next++, input.substring(index, m.start()));
				index = m.end();
			}
		}
		for (int i=next; i<s.length; i++)
			s[i] = null;
		return next;
	}
	
	
	private boolean trim_;
	private final Pattern pattern_;
	
	/**
	 * Receives the splitted parts of an input.
	 * If the string contains less than the expected parts,
	 * then the remaining array elements are set to null.
	 */
	public final String[] s;
}
