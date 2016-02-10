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


import org.civilian.response.UriEncoder;
import org.civilian.util.Check;


/**
 * PathParam defines how one or more path segments of a request path
 * map to a path parameter value.
 * To use path parameters in your application you need to perform these steps:
 * <ol>
 * <li>Create a PathParam object for every path parameter in your application URLs
 * 		and store it into a variable (since a PathParam doesn't have any runtime dependency it is fine
 * 		to store it into a static final constant).
 * <li>Add each PathParam object to a {@link PathParamMap}. Again the PathParamMap can be stored
 * 		as a constant.
 * <li>In the constructor of your application class pass the PathParamMap to the application base class.
 * <li>When a request is dispatched to a controller, path params are automatically recognized.
 * 		and stored in the {@link org.civilian.Request#getPathParam(PathParam) request}.
 * </ol>
 * @param <T> the type of the path parameter values
 */
public abstract class PathParam<T>
{
	/**
	 * Creates a new PathParam. 
	 * @param name the name of the path parameter
	 */
	public PathParam(String name)
	{
		name_ = Check.notNull(name, "name");
	}
	

	/**
	 * Returns the name of the path parameter.
	 */
	public String getName()
	{
		return name_;
	}
	
	
	/**
	 * Returns the type of path parameter values.
	 */
	public abstract Class<T> getType();

	
	/**
	 * Parses a path parameter value from a path.
	 * If a valid path parameter is recognized, the method should
	 * position the scanner after the matching segments and return
	 * the value. If no valid path parameter is recognized
	 * the method should return null and leave the scanner position
	 * unchanged.
	 * @param scanner a PathScanner object which provides access to the 
	 * 		path segments.
	 */
	public abstract T parse(PathScanner scanner);

	
	/**
	 * Converts a path parameter value into a path string. 
	 * The returned path should be a valid path string according to the rules
	 * of {@link Path}.
	 * @param value path parameter value
	 * @param encoder a UriEncoder which can be used to encode reserved characters.
	 */
	public String buildPath(T value, UriEncoder encoder)
	{
		StringBuilder s = new StringBuilder();
		buildPath(value, encoder, s);
		return s.toString();
	}

	
	/**
	 * Converts a path parameter value into a path string. 
	 * The returned path should be a valid path string according to the rules
	 * of {@link Path}.
	 * @param value path parameter value
	 * @param encoder a UriEncoder which can be used to encode reserved characters
	 * @param path the path of the pattern is appended to the StringBuilder 
	 */
	public abstract void buildPath(T value, UriEncoder encoder, StringBuilder path);

	
	/**
	 * Helper method foe buildPath. Appends a single segment to the path.
	 */
	protected void buildPathSegment(String segment, UriEncoder encoder, StringBuilder path)
	{
		Check.notEmpty(segment, "segment");
		path.append('/');
		encoder.encode(segment, path);
	}
	

	/**
	 * Returns a String containing the match pattern of the PathParam.
	 * Used by {@link #toDetailedString()}
	 */
	protected abstract String getPatternString();
	

	/**
	 * Returns a detailed string representation of the path parameter for debug purposes. 
	 */
	public String toDetailedString()
	{
		String p = getPatternString();
		return "/{" + name_ + " : " + getType().getSimpleName() + (p == null ? "" : "=" + p ) + '}';
	}

	
	/**
	 * Returns a string representation of the path parameter for debug purposes. 
	 */
	@Override public String toString()
	{
		return "/{" + name_ + '}';
	}
	
	
	private String name_;
}
