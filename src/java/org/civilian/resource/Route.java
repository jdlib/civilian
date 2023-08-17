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


import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParamProvider;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.UriEncoder;


/**
 * Route presents a route from a resource root down to a single resource.
 * A route can be converted into a path string, given values for all path parameter contained
 * in the Route.  
 */
public abstract class Route
{
	private static final Route ROOT = new RootRoute();
	
	
	/**
	 * Returns a Route which represents the root path "/". 
	 */
	public static final Route root()
	{
		return ROOT;
	}

	
	/**
	 * Returns a Route for a constant path or url.
	 */
	public static final Route constant(String path)
	{
		return (path == null) || (path.length() == 0) || path.equals("/") ? 
			ROOT : 
			new ConstantRoute(path);
	}

	
	/**
	 * Returns if this Route represents the root path.
	 */
	public boolean isRoot()
	{
		return false;
	}
	
	
	/**
	 * Returns a Route which represents this route + the given path.
	 * @param segment a non null segment.
	 */
	public Route addSegment(String segment)
	{
		Check.notNull(segment, "segment");
		return segment.length() == 0 ? this : addEscapedSegment(UriEncoder.encodeString(segment));
	}

	
	protected Route addEscapedSegment(String segment)
	{
		return add(new ConstantRoute('/' + segment));
	}

	
	/**
	 * Returns a Route which represents this route + the path schema defined
	 * by the PathParam.
	 */
	public Route addPathParam(PathParam<?> pathParam)
	{
		Check.notNull(pathParam, "pathParam");
		return add(new PathParamRoute<>(pathParam, getPathParamCount()));
	}

	
	/**
	 * Returns the number of constant and variable (PathParam) parts contained in the route.
	 */
	int size()
	{
		return 1;
	}
	
	
	protected abstract Route add(Route route);

	
	/**
	 * Returns the number of PathParams contained in this route.
	 */
	public abstract int getPathParamCount();


	/**
	 * Returns the i-th PathParam contained in this route.
	 */
	public abstract PathParam<?> getPathParam(int index);

	
	/**
	 * Returns the index of the PathParam within this route.
	 */
	public abstract int indexOf(PathParam<?> pathParam);


	/**
	 * Extracts the value of all PathParams of this Route from the 
	 * PathParamProvider (e.g. a request)
	 * and puts them in the value array. If the request does not contain
	 * an parameter contained in the route, then the value will be null.
	 * @param provider the request containing path param values
	 * @param pathParamValues receives the PathParam values. The length should 
	 * 		equal {@link #getPathParamCount()}.
	 */
	public abstract void extractPathParams(PathParamProvider provider, Object[] pathParamValues);

	
	/**
	 * Builds a path string from the route, using the given PathParam values and UriEncoder.  
	 * @param pathParamValues contains the values for all path parameters of the route. The length should 
	 * 		equal {@link #getPathParamCount()}.
	 * @return the path
	 */
	public String build(Object[] pathParamValues, UriEncoder encoder)
	{
		StringBuilder s = new StringBuilder();
		build(pathParamValues, encoder, s);
		return s.toString();
	}
		
	
	/**
	 * Builds a path string from the route, using the given PathParam values and UriEncoder.
	 * @param s receives the constructed path  
	 */
	public abstract void build(Object[] pathParams, UriEncoder encoder, StringBuilder s);

	
	/**
	 * Returns if the last character in the StringBuilder is a slash. 
	 */
	protected void removeLastSlash(StringBuilder s)
	{
		int length = s.length();
		if ((length > 0) && (s.charAt(length - 1) == '/'))
			s.setLength(length - 1); 
	}
	

	/**
	 * Returns a debug representation of the Route.
	 */
	@Override public abstract String toString();
}


/**
 * The root route.
 */
class RootRoute extends Route
{
	@Override public boolean isRoot()
	{
		return true;
	}
	
	
	@Override protected Route add(Route route)
	{
		return route;
	}

	
	@Override public int getPathParamCount()
	{
		return 0;
	}
	
	
	@Override public PathParam<?> getPathParam(int index)
	{
		return null;
	}

	
	@Override public int indexOf(PathParam<?> pattern)
	{
		return -1;
	}
	
	
	@Override public void extractPathParams(PathParamProvider provider, Object[] pathParams)
	{
	}
	

	@Override public void build(Object[] pathParams, UriEncoder encoder, StringBuilder s)
	{
		if (s.length() == 0)
			s.append('/');
	}


	@Override public String toString()
	{
		return "/";
	}
}


class ConstantRoute extends Route
{
	/**
	 * @param path a normed path
	 */
	protected ConstantRoute(String path)
	{
		path_ = path;
	}
	

	@Override public int getPathParamCount()
	{
		return 0;
	}
	
	
	@Override public PathParam<?> getPathParam(int index)
	{
		return null;
	}

	
	@Override public int indexOf(PathParam<?> pattern)
	{
		return -1;
	}
	
	
	@Override public void extractPathParams(PathParamProvider provider, Object[] pathParams)
	{
	}
	

	@Override protected Route addEscapedSegment(String segment)
	{
		StringBuilder s = new StringBuilder(path_);
		if (s.charAt(s.length() - 1) != '/')
			s.append('/');
		s.append(segment);
		return new ConstantRoute(s.toString());
	}

	
	@Override protected Route add(Route route)
	{
		return new RouteList(this, route);
	}

	
	@Override public void build(Object[] pathParams, UriEncoder encoder, StringBuilder s)
	{
		removeLastSlash(s);
		s.append(path_);
	}


	@Override public String toString()
	{
		return path_;
	}
	
	
	private final String path_;
}


class PathParamRoute<T> extends Route
{
	public PathParamRoute(PathParam<T> pathParam, int paramIndex)
	{
		pathParam_	= pathParam;
		ppIndex_ 	= paramIndex;
	}
	
	
	@Override public int getPathParamCount()
	{
		return 1;
	}
	
	
	@Override public PathParam<?> getPathParam(int index)
	{
		return index == ppIndex_ ? pathParam_ : null;
	}

	
	@Override public int indexOf(PathParam<?> pattern)
	{
		return pathParam_ == pattern ? ppIndex_ : -1;
	}
	
	
	@Override public void extractPathParams(PathParamProvider provider, Object[] pathParams)
	{
		pathParams[ppIndex_] = provider.getPathParam(pathParam_);
	}
	

	@Override protected Route add(Route route)
	{
		return new RouteList(this, route);
	}

	
	@SuppressWarnings("unchecked")
	@Override public void build(Object[] pathParams, UriEncoder encoder, StringBuilder s)
	{
		Check.notNull(pathParams, "pathParams");
		Object value = pathParams[ppIndex_];
		if (value == null)
			throw new IllegalStateException("no value was set for path parameter '" + pathParam_ + "'");
		if (!ClassUtil.isA(value, pathParam_.getType()))
			throw new IllegalArgumentException("path parameter '" + value + " does not match the type of path parameter '" + pathParam_.toDetailedString() + "'"); 
		removeLastSlash(s);
		pathParam_.buildPath((T)value, encoder, s);
	}
	

	@Override public String toString()
	{
		return pathParam_.toString();
	}
	
	
	private final int ppIndex_;
	private final PathParam<T> pathParam_;
}


class RouteList extends Route
{
	public RouteList(Route... list)
	{
		list_ = list;
		int count = 0;
		for (Route route : list)
			count += route.getPathParamCount();
		pathParamCount_ = count;
	}
	
	
	public RouteList(int pathParamCount, Route... list)
	{
		list_ = list;
		pathParamCount_ = pathParamCount;
	}

	
	@Override public int getPathParamCount()
	{
		return pathParamCount_;
	}
	
	
	@Override int size()
	{
		return list_.length;
	}

	
	@Override public PathParam<?> getPathParam(int index)
	{
		for (int i=0; i<list_.length; i++)
		{
			PathParam<?> pattern = list_[i].getPathParam(index);
			if (pattern != null)
				return pattern;
		}
		return null;
	}

	
	@Override public int indexOf(PathParam<?> pathParam)
	{
		for (int i=0; i<list_.length; i++)
		{
			int index = list_[i].indexOf(pathParam);
			if (index >= 0)
				return index;
		}
		return -1;
	}
	
	
	@Override public void extractPathParams(PathParamProvider provider, Object[] pathParams)
	{
		for (int i=0; i<list_.length; i++)
			list_[i].extractPathParams(provider, pathParams);
	}
	

	@Override protected Route addEscapedSegment(String path)
	{
		Route last = list_[list_.length - 1]; 
		if (last instanceof ConstantRoute)
		{
			Route[] newList = list_.clone();
			newList[newList.length - 1] = last.addEscapedSegment(path);
			return new RouteList(pathParamCount_, newList);
		}
		else
			return super.addEscapedSegment( path);
	}

	
	@Override protected Route add(Route route)
	{
		Route[] newList = ArrayUtil.addLast(list_, route);
		return new RouteList(pathParamCount_ + route.getPathParamCount(), newList);
	}

	
	@Override public void build(Object[] pathParams, UriEncoder encoder, StringBuilder s)
	{
		for (int i=0; i<list_.length; i++)
			list_[i].build(pathParams, encoder, s);
	}
	
	
	@Override public String toString()
	{
		StringBuilder s = new StringBuilder();
		for (int i=0; i<list_.length; i++)
			s.append(list_[i]).toString();
		return s.toString();
	}
	
	
	private final Route[] list_;
	private final int pathParamCount_;
}