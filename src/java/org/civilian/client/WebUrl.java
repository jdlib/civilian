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
package org.civilian.client;


import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.civilian.resource.Path;
import org.civilian.resource.PathParam;
import org.civilian.resource.PathParamProvider;
import org.civilian.resource.Route;
import org.civilian.text.type.StandardSerializer;
import org.civilian.text.type.TypeSerializer;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;
import org.civilian.util.UriEncoder;


/**
 * WebUrl helps to build URLs to access a resource of a web application
 * from a client program.
 */
public class WebUrl implements PathParamProvider
{
	/**
	 * Creates a WebUrl consisting of the given URL string. 
	 */
	public WebUrl(String url)
	{
		this(Route.constant(url));
	}
	
	
	/**
	 * Creates a WebUrl consisting of the route to the given resource, prefixed
	 * by the application URL.
	 */
	public WebUrl(WebResource resource)
	{
		this(resource.getRoute());
	}
	
	
	/**
	 * Creates a WebUrl consisting of the route to the given resource, prefixed
	 * by the application URL.
	 */
	public WebUrl(Route route)
	{
		route_ 		= Check.notNull(route, "route");
		int ppCount = route_.getPathParamCount();
		pathParams_	= ppCount > 0 ? new Object[ppCount] : EMPTY_PATH_PARAMS;
	}
	
	
	/**
	 * Appends a path snippet to the Url.
	 */
	public WebUrl addPath(String path)
	{
		if (additionalPath_ != null)
			additionalPath_ = additionalPath_.add(path);
		else
			additionalPath_ = new Path(path);
		return this;
	}
	
	
	//---------------------------------
	// path parameters
	//---------------------------------
	
	
	/**
	 * Returns the number of path parameters within the Url.
	 * A positive count is only possible for Urls which have been
	 * constructed by passing a WebResource or Route object to the constructor.
	 */
	public int getPathParamCount()
	{
		return pathParams_.length;
	}
	
	
	/**
	 * Returns the i-th path param value.
	 */
	public Object getPathParam(int index)
	{
		return pathParams_[index];
	}
	
	
	/**
	 * Returns the path-param value for the given path parameter.
	 */
	@SuppressWarnings("unchecked")
	@Override public <T> T getPathParam(PathParam<T> pathParam)
	{
		int index = route_.indexOf(pathParam);
		return index >= 0 ? (T)pathParams_[index] : null;
	}
	
	
	/**
	 * Returns the i-th path-param object which defines the path parameter.
	 */
	public PathParam<?> getPathParamDef(int index)
	{
		return route_.getPathParam(index);
	}
	
	
	/**
	 * Sets the first path parameter value.
	 */
	public WebUrl setPathParam(Object value)
	{
		setPathParam(0, value);
		return this;
	}
	
	
	/**
	 * Sets the i-th path parameter value.
	 */
	public WebUrl setPathParam(int index, Object value)
	{
		if ((index < 0) || (index >= pathParams_.length))
			throw new NoSuchElementException(String.valueOf(index));
		pathParams_[index] = value;
		return this;
	}
	
	
	/**
	 * Sets the path parameters values.
	 */
	public WebUrl setPathParams(Object... values)
	{
		for (int i=0; i<values.length; i++)
			setPathParam(i, values[i]);
		return this;
	}
	
	
	/**
	 * Copies the path parameters from another url or
	 * PathParamProvider.
	 */
	public WebUrl copyPathParams(PathParamProvider provider)
	{
		Check.notNull(provider, "provider");
		route_.extractPathParams(provider, pathParams_);
		return this;
	}

	
	/**
	 * Sets the path parameter who is defined by the PathParam.
	 */
	public <T> WebUrl setPathParam(PathParam<T> param, T value)
	{
		int index = route_.indexOf(param);
		if (index < 0)
			throw new IllegalArgumentException("url does not contain a path parameter for " + param);
		pathParams_[index] = value;
		return this;
	}
	
	
	/**
	 * Sets the path parameter who is defined by the PathParam.
	 */
	public <T> WebUrl setPathParam(PathParam<Integer> param, int value)
	{
		return setPathParam(param, Integer.valueOf(value));
	}

	
	/**
	 * Clears all path parameters.
	 */
	public WebUrl clearPathParams()
	{
		for (int i=0; i<pathParams_.length; i++)
			setPathParam(i, null);
		return this;
	}
	
	
	//---------------------------------
	// query parameters
	//---------------------------------
	
	
	/**
	 * Clears all query parameters of the Url.
	 */
	public WebUrl clearQueryParams()
	{
		if (params_ != null)
			params_.clear();
		return this;
	}
	
	
	/**
	 * Returns the number of query parameters.
	 */
	public int getQueryParamCount()
	{
		return params_ != null ? params_.size() : 0;
	}
	
	
	/**
	 * Returns the i-th query parameter.
	 */
	public QueryParam getQueryParam(int i)
	{
		return params_.get(i);
	}
	
	
	/**
	 * Returns the first query parameter with the given name.
	 * @param create if true and the url does not contain such a parameter
	 * 		a new parameter with that name is added.
	 */
	public QueryParam getQueryParam(String name, boolean create)
	{
		for (int i=0; i<getQueryParamCount(); i++)
		{
			if (params_.get(i).name_.equals(name))
				return params_.get(i);
		}
		return create ? addQueryParam(name) : null;
	}
	
	
	/**
	 * Removes all query parameters with the specified name.
	 */
	public WebUrl removeQueryParam(String name)
	{
		for (int i=getQueryParamCount()-1; i>=0; i--)
		{
			if (params_.get(i).name_.equals(name))
				params_.remove(i);
		}
		return this;
	}
	
	
	/**
	 * Removes all query parameters with the specified name.
	 */
	public WebUrl removeQueryParam(QueryParam param)
	{
		if (params_ != null)
			params_.remove(param);
		return this;
	}
	
	
	/**
	 * Adds a new query parameter to the Url.
	 * Use the setters on the returned Url to set the parameter value.
	 */
	public QueryParam addQueryParam(String name)
	{
		QueryParam param = new QueryParam(name);
		if (params_ == null)
			params_ = new ArrayList<>();
		params_.add(param);
		return param;
	}

	
	/**
	 * Adds a new parameter to the Url.
	 * @return this
	 */
	public WebUrl addEmptyQueryParam(String name)
	{
		addQueryParam(name);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 */
	public WebUrl addQueryParam(String name, String value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}
	
	
	/**
	 * Adds a query parameter with that name and value.
	 */
	public WebUrl addQueryParam(String name, int value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 */
	public WebUrl addQueryParam(String name, Integer value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 */
	public WebUrl addQueryParam(String name, boolean value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 */
	public WebUrl addQueryParam(String name, Boolean value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name, type and value.
	 */
	public <T> WebUrl addQueryParam(String name, Type<T> type, T value)
	{
		addQueryParam(name).setValue(type, value);
		return this;
	}
	
	
	//---------------------------------
	// serializer
	//---------------------------------
	
	
	/**
	 * Explicitly sets the TypeSerializer used by the Url when it
	 * formats typed parameters to a parameter string.
	 * @see QueryParam#setValue(Type, Object) 
	 */
	public WebUrl setSerializer(TypeSerializer serializer)
	{
		serializer_ = Check.notNull(serializer, "serializer");
		return this;
	}
	
	
	/**
	 * Returns the TypeSerializer used by the Url when it formats
	 * typed parameters to a parameter string. 
	 */
	public TypeSerializer getSerializer()
	{
		return serializer_;
	}
	
	
	/**
	 * Sets the default TypeSerializer used by Urls.
	 */
	public void setDefaultTypeSerializer(TypeSerializer serializer)
	{
		defaultSerializer_ = Check.notNull(serializer, "serializer");
	}
	
	
	/**
	 * Returns the default TypeSerializer used by Urls.
	 */
	public static TypeSerializer getDefaultTypeSerializer()
	{
		return defaultSerializer_;
	}

	
	//---------------------------------
	// fragment
	//---------------------------------
	
	
	/**
	 * Specifies the fragment which should be added to the end of the Url.
	 */
	public WebUrl setFragment(String fragment)
	{
		fragment_ = fragment;
		return this;
	}
	
	
	/**
	 * Returns the fragment which will be added to the end of the Url.
	 */
	public String getFragment()
	{
		return fragment_;
	}
	
	
	//---------------------------------
	// uriencoder
	//---------------------------------

	
	/**
	 * Sets the UriEncoder which should be used to encode parameters.
	 */
	public WebUrl setUriEncoder(UriEncoder uriEncoder)
	{
		uriEncoder_ = uriEncoder;
		return this;
	}
	

	/**
	 * Returns the UriEncoder which should be used to encode parameters.
	 */
	public UriEncoder getUriEncoder()
	{
		if (uriEncoder_ == null)
			uriEncoder_ = new UriEncoder();
		return uriEncoder_;
	}
	
	
	//---------------------------------
	// inner classes
	//---------------------------------
	
	
	/**
	 * QueryParam models a URL query parameter.
	 */
	public class QueryParam
	{
		/**
		 * Creates a new QueryParam.
		 * @param name the name
		 */
		public QueryParam(String name)
		{
			name_ = Check.notNull(name, "name");
		}
		
		
		/**
		 * Returns the query parameter name.
		 */
		public String getName()
		{
			return name_;
		}
		

		/**
		 * Returns the query parameter value.
		 */
		public String getValue()
		{
			return value_;
		}

		
		/**
		 * Set the value of the query parameter to a string.
		 */
		public void setValue(String value)
		{
			value_ = value;
		}
		
		
		/**
		 * Sets the int value of the query parameter.
		 */
		public void setValue(int value)
		{
			setValue(Integer.valueOf(value));
		}

		
		/**
		 * Sets the Integer value of the query parameter.
		 */
		public void setValue(Integer value)
		{
			setValue(TypeLib.INTEGER, value);
		}
		
		
		/**
		 * Sets the boolean value of the query parameter.
		 */
		public void setValue(boolean value)
		{
			setValue(TypeLib.BOOLEAN, Boolean.valueOf(value));
		}

		
		/**
		 * Sets the boolean value of the query parameter.
		 */
		public void setValue(Boolean value)
		{
			setValue(TypeLib.BOOLEAN, value);
		}

		
		/**
		 * Sets the value of the query parameter.
		 * The Urls type serializer is used to convert the value to a string. 
		 * @see WebUrl#getSerializer()
		 */
		public <T> void setValue(Type<T> type, T value)
		{
			value_ = getSerializer().format(type, value);
		}

		
		void append(UriEncoder encoder, StringBuilder s)
		{
			encoder.encode(name_, s);
			s.append('=');
			if (value_ != null)
				encoder.encode(value_, s);
		}
		
		
		private String name_;
		private String value_;
	}
	
	
	//---------------------------------
	// serialize
	//---------------------------------
	
	
	/**
	 * Converts the WebUrl to a string.
	 */
	@Override public String toString()
	{
		StringBuilder s = new StringBuilder();
		
		route_.build(pathParams_, getUriEncoder(), s);
		if (additionalPath_ != null)
			additionalPath_.addTo(s);
		if (params_ != null)
		{
			int n = params_.size();
			for (int i=0; i<n; i++)
			{
				s.append(i == 0 ? '?' : '&');
				params_.get(i).append(getUriEncoder(), s);
			}
		}
		if (fragment_ != null)
		{
			s.append('#');
			s.append(fragment_);
		}
		
		return s.toString();
	}
	
	
	private UriEncoder uriEncoder_;
	private String fragment_;
	private Route route_;
	private Path additionalPath_;
	private Object[] pathParams_;
	private ArrayList<QueryParam> params_;
	private TypeSerializer serializer_ = defaultSerializer_;
	private static TypeSerializer defaultSerializer_ = StandardSerializer.INSTANCE;
	private static final Object[] EMPTY_PATH_PARAMS = new Object[0];
}
