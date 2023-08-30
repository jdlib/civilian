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


import java.util.ArrayList;
import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;
import org.civilian.resource.Resource;
import org.civilian.resource.Route;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParamProvider;
import org.civilian.text.service.LocaleService;
import org.civilian.text.service.LocaleServiceProvider;
import org.civilian.text.type.TypeSerializer;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;
import org.civilian.util.http.UriEncoder;


/**
 * Url allows to build URLs either to address resources within an Application 
 * or arbitrary URLs.<p> 
 * In the first case the target resource can be specified by a {@link Resource} object. 
 * The Url will automatically derive the (absolute) path to that resource.<p>
 * The path to the resource may contain segments mapped to path parameters. The Url class provides
 * an API to conveniently set these path parameters. If the request was dispatched to
 * a resource, the path parameters shared by the request are automatically initialized.
 * <p>
 * The Url class also allows to set query parameters. Locale dependent serialization of parameter objects
 * to parameter strings is supported.
 * <p>
 * Path parameter values and parameter values are automatically percent encoded, using the 
 * {@link Response#getUriEncoder()} UriEncoder} of the response. The Url class also takes
 * care to add the session id to URL if needed. 
 */
public class Url implements PathParamProvider
{
	/**
	 * Creates a Url consisting of the given string value. 
	 * @param lsProvider can provide a LocaleService
	 * @param url either a relative or absolute path to address a resource on
	 *		the same server or a fully qualified URL.
	 */
	public Url(LocaleServiceProvider lsProvider, String url)
	{
		lsProvider_		= Check.notNull(lsProvider, "lsProvider");
		prefix_ 		= Check.notNull(url, "url");
		pathParams_		= EMPTY_PATH_PARAMS;
		resource_		= null;
	}
	
	
	/**
	 * Creates a Url consisting of the path provided by the PathProvider.
	 * @param rp a Response or a ResponseProvider whose Response is used by the Url
	 * @param pathProvider provides a path. Classes like Path, Context, Application
	 * 		Request, etc. are examples of PathProviders.   
	 */
	public Url(LocaleServiceProvider lsProvider, PathProvider pathProvider)
	{
		lsProvider_		= Check.notNull(lsProvider, "lsProvider");
		additionalPath_ = Check.notNull(pathProvider, "pathProvider").getPath();
		pathParams_		= EMPTY_PATH_PARAMS;
		resource_		= null;
		prefix_			= null;
	}

	
	/**
	 * Creates a Url consisting of the path to the given resource.
	 * The path may contain path parameters for which you then must provide
	 * values. Path parameters shared by the current request are automatically initialized. 
	 * If the application defines a default extension for resource urls,
	 * it is also appended to the Url.
	 * @param rp a Response or a ResponseProvider whose Response is used by the Url
	 * @param resource the resource. The resource must not belong to the application which
	 * 		received the request.   
	 */
	public Url(ResponseProvider rp, Resource resource)
	{
		Response response 	= Check.notNull(rp, "response provider").getResponse();
		lsProvider_			= response;
		resource_			= Check.notNull(resource, "resource");
		prefix_ 			= response.getOwner().getPath().toString();

		int ppCount = resource_.getRoute().getPathParamCount();
		if (ppCount > 0)
		{
			pathParams_	= new Object[ppCount];
			copyPathParams(response.getRequest());
		}
		else
			pathParams_	= EMPTY_PATH_PARAMS;
	}
	
	
	private Route getRoute()
	{
		if (resource_ == null)
			throw new IllegalArgumentException("URL not defined from a resource");
		return resource_.getRoute();
	}

	
	/**
	 * Returns the resource if the Url was constructed from a resource or
	 * from a controller class. Else it returns null.	
	 */
	public Resource getResource()
	{
		return resource_;
	}
	
	
	/**
	 * Explicitly sets the TypeSerializer used by the Url when it
	 * formats typed parameters to a parameter string.
	 * @see QueryParam#setValue(Type, Object) 
	 * @return this
	 */
	public Url setSerializer(TypeSerializer serializer)
	{
		serializer_ = serializer;
		return this;
	}
	

	/**
	 * Returns the TypeSerializer used by the Url when it formats
	 * typed parameters to a parameter string. 
	 * By the default the Url uses the serializer of the response's LocaleService.
	 * @see QueryParam#setValue(Type, Object) 
	 * @see Response#getLocaleService()
	 * @see LocaleService#getSerializer()
	 */
	public TypeSerializer getSerializer()
	{
		if (serializer_ == null)
			serializer_ = lsProvider_.getLocaleService().getSerializer();
		return serializer_;
	}
	
	
	//---------------------------------
	// path parameters
	//---------------------------------


	/**
	 * Returns the number of path parameters within the Url.
	 * A positive count is only possible for Urls which have been
	 * constructed by passing a Resource object to the 
	 * {@link Url#Url(ResponseProvider, Resource) constructor}.
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
	 * Returns path param value belonging to the given
	 * PathParam.
	 */
	@SuppressWarnings("unchecked")
	@Override public <T> T getPathParam(PathParam<T> pathParam)
	{
		int index = getRoute().indexOf(pathParam);
		return index >= 0 ? (T)pathParams_[index] : null;
	}
	
	
	/**
	 * Returns the i-th PathParam object which defines the i-th
	 * path parameter.
	 */
	public PathParam<?> getPathParamDef(int index)
	{
		return getRoute().getPathParam(index);
	}

	
	/**
	 * Sets the value of the first path parameter.
	 * @return this
	 */
	public Url setPathParam(Object value)
	{
		setPathParam(0, value);
		return this;
	}
	
	
	/**
	 * Sets the value of the i-th path parameter.
	 * @see #getPathParamCount()
	 * @return this
	 */
	public Url setPathParam(int index, Object value)
	{
		pathParams_[index] = value;
		return this;
	}
	
	
	/**
	 * Sets the path parameters.
	 * @return this
	 */
	public Url setPathParams(Object... values)
	{
		for (int i=0; i<values.length; i++)
			setPathParam(i, values[i]);
		return this;
	}

	
	/**
	 * Copies the path parameters from the provider to this url.
	 * @return this
	 */
	public Url copyPathParams(PathParamProvider provider)
	{
		Check.notNull(provider, "provider");
		getRoute().extractPathParams(provider, pathParams_);
		return this;
	}

	
	/**
	 * Sets the path parameter who is defined by the PathParam.
	 * @return this
	 */
	public <T> Url setPathParam(PathParam<T> param, T value)
	{
		int index = getRoute().indexOf(param);
		if (index < 0)
			throw new IllegalArgumentException("url does not contain a value for path parameter " + param);
		pathParams_[index] = value;
		return this;
	}

	
	/**
	 * Clears all path parameters.
	 * @return this
	 */
	public Url clearPathParams()
	{
		for (int i=0; i<pathParams_.length; i++)
			setPathParam(i, null);
		return this;
	}
	
	
	//---------------------------------
	// query parameters
	//---------------------------------
	

	/**
	 * Returns a list containing the URL's query parameters.
	 */
	public QueryParamList queryParams()
	{
		if (queryParams_ == null)
			queryParams_ = new QueryParamList();
		return queryParams_;
	}


	/**
	 * Returns if the url has query parameters.
	 */
	public boolean hasQueryParams()
	{
		return queryParams_ != null && queryParams_.size() > 0; 
	}

	
	/**
	 * QueryParam represents a URL query parameter.
	 */
	public class QueryParam
	{
		public QueryParam(String name)
		{
			name_ = Check.notNull(name, "name");
		}
		
		
		/**
		 * Returns the param name.
		 */
		public String getName()
		{
			return name_;
		}
		

		/**
		 * Returns the param value.
		 */
		public String getValue()
		{
			return value_;
		}

		
		/**
		 * Set the value of the Parameter to a string.
		 */
		public void setValue(String value)
		{
			value_ = value;
		}
		
		
		/**
		 * Sets the int value of the Parameter.
		 */
		public void setValue(int value)
		{
			setValue(Integer.valueOf(value));
		}

		
		public void setValue(Integer value)
		{
			setValue(TypeLib.INTEGER, value);
		}
		
		
		/**
		 * Sets the boolean value of the Parameter.
		 */
		public void setValue(boolean value)
		{
			setValue(TypeLib.BOOLEAN, Boolean.valueOf(value));
		}

		
		/**
		 * Sets the boolean value of the Parameter.
		 */
		public void setValue(Boolean value)
		{
			setValue(TypeLib.BOOLEAN, value);
		}

		
		/**
		 * Sets the value of the Parameter.
		 * The Urls TypeSerializer is used to convert the value to a string. 
		 * @see Url#getSerializer()
		 */
		public <T> void setValue(Type<T> type, T value)
		{
			value_ = getSerializer().format(type, value);
		}

		
		void append(StringBuilder s)
		{
			UriEncoder.encode(name_, s);
			s.append('=');
			if (value_ != null)
				UriEncoder.encode(value_, s);
		}
		
		
		private final String name_;
		private String value_;
	}
	
	
	public class QueryParamList extends ArrayList<QueryParam>
	{
		private static final long serialVersionUID = 1L;
		
		
		/**
		 * Adds a new query parameter.
		 * Use the setters on the returned param object to set the parameter value.
		 * @param name the parameter name
		 * @return the new param
		 */
		public QueryParam add(String name)
		{
			QueryParam param = new QueryParam(name);
			add(param);
			return param;
		}
		
		
		/**
		 * Adds a new query parameter.
		 * Use the setters on the returned param object to set the parameter value.
		 * @param name the parameter name
		 * @return the new param
		 */
		public QueryParamList add(String name, String value)
		{
			add(name).setValue(value);
			return this;
		}
		
		
		/**
		 * Adds multiple query parameters with the same name and the given values.
		 * @return this
		 */
		public QueryParamList addQueryParams(String name, String... values)
		{
			for (String value : values)
				add(name, value);
			return this;
		}
		

		@Override public boolean add(QueryParam param)
		{
			Check.notNull(param, "param");
			return super.add(param);
		}
		
		
		/**
		 * Returns the first query parameter with the given name.
		 * @param name the name
		 * @return the first matching parameter or null if none matches
		 */
		public QueryParam get(String name)
		{
			for (int i=0; i<size(); i++)
			{
				QueryParam param = get(i);
				if (param.name_.equals(name))
					return param;
			}
			return null;
		}
		
		
		/**
		 * Returns the first query parameter with the given name.
		 * @param name the name
		 * @return the first matching parameter
		 * @throws IllegalStateException if none matches
		 */
		public QueryParam getRequired(String name)
		{
			QueryParam param = get(name);
			if (param == null)
				throw new IllegalStateException("no query parameter '" + name + "'");
			return param;
		}
		
		
		/**
		 * Returns the first query parameter with the given name or creates one no such param exists.
		 * @param name the name
		 * @return the first matching parameter or the newly created param. Never null.
		 */
		public QueryParam getOrCreate(String name)
		{
			QueryParam param = get(name);
			if (param == null)
				param = add(name);
			return param;
		}
		
		
		/**
		 * Removes all query parameters with the specified name.
		 * @return this
		 */
		public QueryParamList remove(String name)
		{
			for (int i=size()-1; i>=0; i--)
			{
				if (get(i).name_.equals(name))
					remove(i);
			}
			return this;
		}
	}
	

	//---------------------------------
	// fragment
	//---------------------------------

	
	/**
	 * Specifies the fragment which should be added to the end of the Url.
	 * @return this
	 */
	public Url setFragment(String fragment)
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
	// serialize
	//---------------------------------

	
	public String toQueryString()
	{
		return toString(false, true);
	}
	
	
	public String toQuerylessString()
	{
		return toString(true, false);
	}

	
	/**
	 * Converts the Url to a string.
	 */
	@Override public String toString()
	{
		return toString(true, true);
	}
	
	
	/**
	 * Converts the Url to a string.
	 * @param mainPart include the main part (the part before query parameters and fragment)
	 * @param queryString include query string and fragment)
	 */
	public String toString(boolean mainPart, boolean queryString)
	{
		StringBuilder s = new StringBuilder();
		if (mainPart)
		{
			if (prefix_ != null)
				s.append(prefix_);
			if (resource_ != null)
				resource_.getRoute().build(pathParams_, s);
			if (additionalPath_ != null)
				additionalPath_.addTo(s);
		}
		if (queryString)
		{
			if (queryParams_ != null)
			{
				int n = queryParams_.size();
				for (int i=0; i<n; i++)
				{
					s.append(i == 0 ? '?' : '&');
					queryParams_.get(i).append(s);
				}
			}
			if (fragment_ != null)
			{
				s.append('#');
				s.append(fragment_);
			}
		}
		
		return s.toString();
	}

	
	private String fragment_;
	private TypeSerializer serializer_;
	private final Resource resource_;
	private Path additionalPath_;
	private Object[] pathParams_;
	private QueryParamList queryParams_;
	private final String prefix_;
	private final LocaleServiceProvider lsProvider_;
	private static final Object[] EMPTY_PATH_PARAMS = new Object[0];
}
