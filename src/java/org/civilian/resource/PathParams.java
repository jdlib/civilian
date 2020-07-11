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
package org.civilian.resource;


import java.util.regex.Pattern;
import org.civilian.type.DateType;
import org.civilian.type.Type;
import org.civilian.internal.pathparam.MultiSegmentPathParam;
import org.civilian.internal.pathparam.PrefixedPathParam;
import org.civilian.internal.pathparam.RegexPathParam;
import org.civilian.internal.pathparam.SegmentPathParam;
import org.civilian.internal.pathparam.SegmentWcPathParam;
import org.civilian.internal.pathparam.YMDPathParam;


/**
 * PathParams contains factory methods for PathParam objects.
 * The following table shows which paths the different patterns match and what 
 * path parameter values are extracted from the path:
 * <table>
 * <tr>
 * 		<th>Pattern</th>
 * 		<th>Accepted path or segment</th>
 * 		<th>Extracted path parameter</th>
 * </tr>
 * <tr>
 * 		<td>{@link #forSegment(String)}</td> 
 * 		<td>/abc</td> 
 * 		<td>String "abc"</td>
 * </tr>
 * <tr>
 * 		<td>{@link #forSegment(String)}.{@link PathParam#converting(Type) converting(TypeLib.INTEGER)}</td> 
 * 		<td>/123</td> 
 * 		<td>Integer 123</td>
 * </tr>
 * <tr>
 * 		<td>{@link #forSegment(String)}.{@link PathParam#converting(Type) converting(TypeLib.DATE_LOCAL)}</td> 
 * 		<td>/20130131</td> 
 * 		<td>LocalDate year=2013, month=01, day=31</td>
 * </tr>
 * <tr>
 * 		<td>{@link #forSegmentPattern(String, String) PathParams.forSegment(TypeLibrary.INTEGER, "id*")}</td> 
 * 		<td>/id567</td> 
 * 		<td>Integer 567</td>
 * </tr>
 * <tr>
 * 		<td>{@link #forYearMonthDay(String, DateType)}</td> 
 * 		<td>/2013/01/31</td> 
 * 		<td>Date year=2013, month=01, day=31</td>
 * </tr>
 * </table>
 */
public abstract class PathParams
{
	/**
	 * Creates a PathParam which matches any path segment. The associated 
	 * path parameter value is the path segment.
	 * @param name the name of the path parameter 
	 */
	public static PathParam<String> forSegment(String name)
	{
		return new SegmentPathParam(name);
	}

	
	/**
	 * Creates a PathParam which matches a path segment. The associated 
	 * path parameter value is the part of the segment which is matched by the
	 * wildcard '*' inside the segmentPattern.
	 * @param name the name of the path parameter 
	 * @param segmentPattern a string containing exactly one wildcard '*' character
	 * 		and no '/' characters.
	 */
	public static PathParam<String> forSegmentPattern(String name, String segmentPattern)
	{
		return new SegmentWcPathParam(name, segmentPattern);
	}


	/**
	 * Creates a PathParam which uses a regex pattern to match a path segment.
	 * @param name the name of the path parameter 
	 * @param matchPattern a regex pattern for the segment. The pattern must have a single
	 * 		match group and should not match any slashes.
	 * 		Example: Pattern.compile("id([^/]+)" to match segments of the form "id*" 
	 * @param buildPattern equals the matchPattern, whose match group is replaced by a '*'
	 * 		Example: "id*" 
	 */
	public static PathParam<String> forPattern(String name, Pattern matchPattern, String buildPattern)
	{
		return new RegexPathParam(name, matchPattern, buildPattern);
	}


	/**
	 * Creates a PathParam which matches three segments, encoding a date value:
	 * The segments have the form yyyy/mm/dd, i.e. a 4 digit year, a 2 digit month (from 01 to 12)
	 * and a 2 digit day of month (01-31).
	 * @param name the name of the path parameter 
	 */
	public static <T> PathParam<T> forYearMonthDay(String name, DateType<T> type)
	{
		return new YMDPathParam<>(name, type);
	}


	/**
	 * Creates a PathParam which matches multiple segment. The associated 
	 * path parameter value are the matched segments as string array.
	 * Since this path parameter has variable size, it only makes sense to
	 * use it at the end of a path. 
	 * @param name the name of the path parameter
	 * @param minSize the minimum number of segments which must be present on
	 * 		the path.
	 */
	public static PathParam<String[]> forMultiSegments(String name, int minSize)
	{
		return new MultiSegmentPathParam(name, minSize);
	}
	
	
	/**
	 * Returns a PathParam which recognizes a constant segment followed
	 * by the value of another PathParams. 
	 * The name of the converting param is the name of the inner PathParam.
	 * @param segment a "prefix" segment
	 * @param inner another param
	 */
	public static <T> PathParam<T> prefixed(String segment, PathParam<T> inner)
	{
		return new PrefixedPathParam<>(segment, inner);
	}
}
