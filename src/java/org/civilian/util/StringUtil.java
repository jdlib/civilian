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


import java.util.ArrayList;


/**
 * Provides string related utility functions.
 */
public abstract class StringUtil
{
	/**
	 * Returns a string which starts with an upper case character.
	 */
	public static String startUpperCase(String s)
	{
		return (s.length() == 0) || Character.isUpperCase(s.charAt(0)) ? 
			s : 
			Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}


	/**
	 * Returns a string which starts with a lower case character.
	 */
	public static String startLowerCase(String s)
	{
		return (s.length() == 0) || Character.isLowerCase(s.charAt(0)) ? 
			s : 
			Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	
	/**
	 * Splits a string.
	 * @param s the input string
	 * @param separator the part separator
	 * @return an array with at least one element.
	 */
	public static String[] split(String s, String separator)
	{
		int sepLength = separator.length();
		if (sepLength == 0)
			return new String[] { s };
		
		ArrayList<String> tokens = new ArrayList<>();
		int length		= s.length();
		int start		= 0;
		do
		{
			int p = s.indexOf(separator, start);
			if (p == -1)
				p = length;
			if (p > start)
				tokens.add(s.substring(start, p));
			
			start = p + sepLength;
		}
		while(start < length);
		
		return tokens.toArray(new String[tokens.size()]);
	}


	/**
	 * Returns the part of the string before the given character
	 * or the whole string if the input does not contain
	 * the character.
	 */
	public static String before(String s, char c)
	{
		int p = s != null ? s.indexOf(c) : -1;
		return p == -1 ? s : s.substring(0, p);
	}

	
	/**
	 * Returns the part of the string after the given character
	 * or null if the input does not contain
	 * the character.
	 */
	public static String after(String s, char c)
	{
		int p = s != null ? s.indexOf(c) : -1;
		return p == -1 ? null : s.substring(p + 1);
	}

	
	/**
	 * Returns a string which is the input string with the
	 * prefix prepended to the left. If the string already starts with
	 * the prefix it is returned unchanged.
	 */
	public static String haveLeft(String s, String prefix)
	{
		return (s != null) && !s.startsWith(prefix) ? prefix + s : s; 
	}


	/**
	 * Returns a string which is the input string with the
	 * suffix appended to the right. If the string already ends with
	 * the suffix it is returned unchanged.
	 */
	public static String haveRight(String s, String suffix)
	{
		return (s != null) && !s.endsWith(suffix) ? s + suffix : s; 
	}
	
	
	/**
	 * Returns a string which is the input string with the
	 * prefix removed from the left. If the string does not start with
	 * the prefix it is returned unchanged.
	 */
	public static String cutLeft(String s, String prefix)
	{
		return (s != null) && s.startsWith(prefix) ? s.substring(prefix.length()) : s; 
	}


	/**
	 * Returns a string which is the input string with the
	 * suffix removed from the right. If the string does not end with
	 * the suffix it is returned unchanged.
	 */
	public static String cutRight(String s, String suffix)
	{
		return (s != null) && s.endsWith(suffix) ? s.substring(0, s.length() - suffix.length()) : s; 
	}


	/**
	 * Returns a string with length newlen, starting with the given input
	 * string. If the input is longer than newlen, it is cut.
	 * If it is less than newlen then it is filled up with the fillchar at
	 * the end.
	 */
	public static String fillRight(String s, int newlen, char fillchar)
	{
		return fill(s, newlen, fillchar, false);
	}
		
	
	/**
	 * A shortcut for fillRight(s, newlen, ' ')
	 */
	public static String fillRight(String s, int newlen)
	{
		return fillRight(s, newlen, ' ');
	}

	
	/**
	 * A shortcut for fillRight(String.valueOf(n), newlen, '0')
	 */
	public static String fillRight(int n, int newlen)
	{
		return fillRight(String.valueOf(n), newlen, '0');
	}

	
	/**
	 * Returns a string with length newlen, ending with the given input
	 * string. If the input is longer than newlen, it is cut.
	 * If it is less than newlen then it is filled up with the fillchar at
	 * the beginning.
	 */
	public static String fillLeft(String s, int newlen, char fillchar)
	{
		return fill(s, newlen, fillchar, true);
	}


	/**
	 * A shortcut for fillLeft(String s, int newlen).
	 */
	public static String fillLeft(String s, int newlen)
	{
		return fillLeft(s, newlen, ' ');
	}


	/**
	 * A shortcut for fillLeft(n, newlen, '0').
	 */
	public static String fillLeft(int n, int newlen)
	{
		return fillLeft(n, newlen, '0');
	}


	/**
	 * A shortcut for fillLeft(String.valueOf(n), newlen, fillChar)).
	 */
	public static String fillLeft(int n, int newlen, char fillChar)
	{
		return fillLeft(String.valueOf(n), newlen, fillChar);
	}
	
	
	private static String fill(String s, int newlen, char fillchar, boolean left)
	{
		int curlen = s.length();
		if (newlen > curlen)
		{
			if (left)
				return replicate(newlen - curlen, fillchar) + s;
			else
				return s + replicate(newlen - curlen, fillchar);
		}
		else if (newlen < curlen)
			return s.substring(0, newlen);
		else
			return s;
	}


	/**
	 * Returns a string consisting of the character replicated
	 * x times.
	 */
	public static String replicate(int x, char c)
	{
		StringBuilder sb = new StringBuilder(x);
		for (int i=0; i<x; i++)
			sb.append(c);
		return sb.toString();
	}


	/**
	 * Returns true iif the String is null or empty.
	 */
	public static boolean isBlank(String s)
	{
		return (s == null) || (s.length() == 0);
	}
	
	
	public static String norm(String s)
	{
		if (s != null)
			s = s.trim();
		return isBlank(s) ? null : s;
	}


	public static String rtrim(String s)
	{
		if (s != null)
		{
			int last = s.length()-1;  
			int i = last;
			while (i >= 0 && Character.isWhitespace(s.charAt(i))) 
				i--;
			if (i < last)
				s = s.substring(0, i+1);
		}
		return s;
	}
}