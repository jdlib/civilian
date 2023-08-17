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
package org.civilian.resource.pathparam;


import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.civilian.type.DateType;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;
import org.civilian.util.PathScanner;
import org.civilian.util.UriEncoder;


/**
 * YMDPathParam is PathParam which encodes a date in
 * three path segments: yyyy/mm/dd, a 4 digit year, a 2 digit month (from 01 to 12)
 * and a 2 digit day of month (01-31).
 */
public class YMDPathParam<T> extends PathParam<T>
{
	/**
	 * Creates a YMDPPFormat.  
	 * @param dateType decouples the pattern from the concrete date class which is
	 * 		used for path parameter values of this pattern. The {@link TypeLib} provides constants
	 * 		for common DateTypes.
	 */
	public YMDPathParam(String name, DateType<T> type)
	{
		super(name);
		type_ = Check.notNull(type, "type");
	}

	
	@Override public Class<T> getType()
	{
		return type_.getJavaType();
	}

	
	/**
	 * Implementation.
	 */
	@Override public T parse(PathScanner scanner)
	{
		MatchResult result = scanner.matchPattern(PATTERN);
		if (result != null)
		{
			// pattern matched
			// now try to construct a date object from the three segments
			try
			{
				int year  = Integer.parseInt(result.group(1)); 
				int month = Integer.parseInt(result.group(2)); 
				int day   = Integer.parseInt(result.group(3));
				T date    = type_.create(year, month, day);
				// success: date is valid: advance the scanner
				scanner.next(result);
				return date;
			}
			catch(Exception e)
			{
			}
		}
		return null;
	}

	
	/**
	 * Implementation.
	 */
	@Override public void buildPath(T value, UriEncoder encoder, StringBuilder path)
	{
		DateType<T> dateType = type_;
		appendSegment(dateType.getYear(value),  4, path);
		appendSegment(dateType.getMonth(value), 2, path);
		appendSegment(dateType.getDay(value),   2, path);
	}
	
	
	private void appendSegment(int value, int length, StringBuilder path)
	{
		path.append('/');
		String v = String.valueOf(value);
		for (int i=v.length(); i<length; i++)
			path.append('0');
		path.append(v);
	}
	

	/**
	 * Returns "yyyy/mm/dd".
	 */
	@Override protected String getPatternString()
	{
		return "yyyy/mm/dd";
	}
	
	
	protected final DateType<T> type_;
	private static final Pattern PATTERN = Pattern.compile("([0-9]{4})/([0-1][0-9])/([0-3][0-9])");
}
