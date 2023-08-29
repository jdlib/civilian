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


import org.civilian.resource.Path;
import org.civilian.text.type.StandardSerializer;
import org.civilian.type.Type;
import org.civilian.util.Check;
import org.civilian.util.PathScanner;
import org.civilian.util.http.UriEncoder;


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
 * 		and stored in the {@link org.civilian.request.Request#getPathParam(PathParam) request}.
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
	 * the method should return null. In this case the PathParam is allowed
	 * to have consumed segments from the scanner - the caller 
	 * must make sure to revert the scanner to the previous state if 
	 * he wants to try other scans.
	 * @param scanner a PathScanner object which provides access to the 
	 * 		path segments.
	 */
	public abstract T parse(PathScanner scanner);

	
	/**
	 * Converts a path parameter value into a path string. 
	 * The returned path should be a valid path string according to the rules
	 * of {@link Path}.
	 * @param value path parameter value
	 */
	public String buildPath(T value)
	{
		StringBuilder s = new StringBuilder();
		buildPath(value, s);
		return s.toString();
	}

	
	/**
	 * Converts a path parameter value into a path string. 
	 * The returned path should be a valid path string according to the rules
	 * of {@link Path}.
	 * @param value path parameter value
	 * @param path the path of the pattern is appended to the StringBuilder 
	 */
	public abstract void buildPath(T value, StringBuilder path);

	
	/**
	 * Helper method foe buildPath. Appends a single segment to the path.
	 */
	protected void buildPathSegment(String segment, StringBuilder path)
	{
		Check.notEmpty(segment, "segment");
		path.append('/');
		UriEncoder.encode(segment, path);
	}
	

	/**
	 * Returns a String containing the match pattern of the PathParam.
	 * Used by {@link #toDetailedString()}
	 */
	protected abstract String getPatternString();
	

	protected static String getPatternString(PathParam<?> other)
	{
		return other.getPatternString();
	}
	
	
	/**
	 * Returns a PathParam based on this PathParam which converts the value
	 * to a value of the given type. Parsing and formatting is done using
	 * the {@link StandardSerializer}.
	 * @param type a Type
	 * @param name if not null use that name else use the name of this PathParam. 
	 * @return the new PathParam
	 */
	@SuppressWarnings("unchecked")
	public <S> PathParam<S> converting(Type<S> type, String name)
	{
		if (getType() != String.class)
			throw new IllegalStateException(this.toDetailedString() + ": only can convert String based path params");
		Check.notNull(type, "type");
		return new ConvertingPathParam<>(name, (PathParam<String>)this, type);
	}

	
	/**
	 * Calls {@link #converting(Type, String)} with a null name.
	 */
	public <S> PathParam<S> converting(Type<S> type)
	{
		return converting(type, null);
	}
	
	
	/**
	 * Returns a PathParam which recognizes a constant segment followed
	 * by the value of another PathParams. 
	 * The name of the converting param is the name of the inner PathParam.
	 * @param segment a "prefix" segment
	 */
	public PathParam<T> precededBySegment(String segment)
	{
		return new PrecededPathParam<>(null, segment, this);
	}
	
	
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
	
	
	/**
	 * Helper method to determine the name of a proxy path param.
	 * @param name an optional name.  
	 * @param inner a wrapped param
	 * @return if the name param is not null, else the name of the param is returned.
	 */
	protected static String buildName(String name, PathParam<?> inner)
	{
		Check.notNull(inner, "inner");
		return name != null ? name : inner.getName();
	}
	
	
	private String name_;
}
