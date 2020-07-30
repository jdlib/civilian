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


import java.util.ArrayList;
import org.civilian.Application;
import org.civilian.Controller;
import org.civilian.Resource;
import org.civilian.Resource.Tree;
import org.civilian.Response;
import org.civilian.controller.ControllerSignature;
import org.civilian.provider.PathParamProvider;
import org.civilian.provider.PathProvider;
import org.civilian.provider.ResponseProvider;
import org.civilian.response.UriEncoder;
import org.civilian.text.LocaleService;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.TypeSerializer;
import org.civilian.util.Check;


/**
 * Url allows to build URLs either to address resources within an {@link Application} 
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
public class Url implements PathParamProvider, ResponseProvider
{
	/**
	 * Creates a Url consisting of the given string value. 
	 * @param rp a Response or a ResponseProvider whose Response is used by the Url
	 * @param url either a relative or absolute path to address a resource on
	 *		the same server or a fully qualified URL.
	 *		In the later case the Url will not add a session id to
	 *		the url string
	 * @see #addSessionId(boolean)
	 */
	public Url(ResponseProvider rp, String url)
	{
		Check.notNull(rp, "response provider");
		Check.notNull(url, "url");
		
		response_ 	= rp.getResponse();
		prefix_ 	= url;
		pathParams_	= EMPTY_PATH_PARAMS;
	}
	
	
	/**
	 * Creates a Url consisting of the path provided
	 * by the PathProvider.
	 * @param rp a Response or a ResponseProvider whose Response is used by the Url
	 * @param pathProvider provides a path. Classes like Context, Application
	 * 		Request, etc. are examples of PathProviders.   
	 */
	public Url(ResponseProvider rp, PathProvider pathProvider)
	{
		this(rp, Check.notNull(pathProvider, "pathProvider").getPath());
	}

	
	/**
	 * Creates a Url consisting of the path.
	 * @param rp a Response or a ResponseProvider whose Response is used by the Url
	 * @param path a path.   
	 */
	public Url(ResponseProvider rp, Path path)
	{
		Check.notNull(rp, "response provider");
		Check.notNull(path, "path");
		
		response_ 		= rp.getResponse();
		pathParams_		= EMPTY_PATH_PARAMS;
		additionalPath_ = path;
	}
	
	
	/**
	 * Creates a Url consisting of the path to the resource whose
	 * controller has the given class.<br>
	 * The path may contain path parameters for which you then must provide
	 * values. Path parameters shared by the current request are automatically initialized. 
	 * If the application defines a default extension for resource urls,
	 * it is also appended to the Url.<br>
	 * Please note that a controller may be mapped to several resources. In this case
	 * it is undefined which resource is chosen to build the url.
	 * @param rp a Response or a ResponseProvider whose Response is used by the Url
	 * @param controllerClass the controller class.   
	 * @see Tree#getDefaultExtension()
	 */
	public <C extends Controller> Url(ResponseProvider rp, Class<C> controllerClass)
	{
		Response response 		= Check.notNull(rp, "response provider").getResponse();
		ControllerSignature sig = new ControllerSignature(controllerClass.getName());
		Resource resource 		= response.getApplication().getRootResource().getTree().getResource(sig);
		
		if (resource == null)
			throw new IllegalArgumentException(controllerClass.getName() + " not mapped to a resource");
		
		init(response, resource);
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
	 * @see Tree#getDefaultExtension()
	 */
	public Url(ResponseProvider rp, Resource resource)
	{
		init(Check.notNull(rp, "response provider").getResponse(),
			Check.notNull(resource, "resource"));
	}
	
	
	private void init(Response response, Resource resource)
	{
		response_ 	= response;
		resource_	= resource;
		prefix_ 	= resource.getTree().getAppPath().toString();

		int ppCount = resource_.getRoute().getPathParamCount();
		if (ppCount > 0)
		{
			pathParams_	= new Object[ppCount];
			copyPathParams(response_.getRequest());
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
	 * Returns the response which was passed to the Url constructor.
	 */
	@Override public Response getResponse()
	{
		return response_;
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
			serializer_ = response_.getLocaleService().getSerializer();
		return serializer_;
	}


	/**
	 * Prepends a url snippet at the beginning of the url string.
	 * @return this.
	 */
	public Url prepend(String url)
	{
		Check.notNull(url, "url");
		if (prefix_ == null)
			prefix_ = url;
		else if (url.endsWith("/") && prefix_.startsWith("/"))
			prefix_ = url + prefix_.substring(1);
		else
			prefix_ = url + prefix_;
		return this;
	}
	
	
	/**
	 * Appends a path snippet to the Url. 
	 * @return this
	 */
	public Url append(String path)
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
	 * Clears all parameters of the Url.
	 * @return this
	 */
	public Url clearQueryParams()
	{
		if (queryParams_ != null)
			queryParams_.clear();
		return this;
	}


	/**
	 * Returns the number of query parameters.
	 */
	public int getQueryParamCount()
	{
		return queryParams_ != null ? queryParams_.size() : 0;
	}
	
	
	/**
	 * Returns the i-th query parameter.
	 */
	public QueryParam getQueryParam(int i)
	{
		return queryParams_.get(i);
	}

	
	/**
	 * Returns the first query parameter with the given name.
	 * @param create if true and the url does not contain such a parameter
	 * 		a new parameter is added.
	 */
	public QueryParam getQueryParam(String name, boolean create)
	{
		for (int i=0; i<getQueryParamCount(); i++)
		{
			if (queryParams_.get(i).name_.equals(name))
				return queryParams_.get(i);
		}
		return create ? addQueryParam(name) : null;
	}

	
	/**
	 * Removes all query parameters with the specified name.
	 * @return this
	 */
	public Url removeQueryParams(String name)
	{
		for (int i=getQueryParamCount()-1; i>=0; i--)
		{
			if (queryParams_.get(i).name_.equals(name))
				queryParams_.remove(i);
		}
		return this;
	}
	
	
	/**
	 * Removes the query parameter from the url.
	 * @return this
	 */
	public Url removeQueryParam(QueryParam param)
	{
		if (queryParams_ != null)
			queryParams_.remove(param);
		return this;
	}
	
	
	/**
	 * Adds a new query parameter to the Url.
	 * Use the setters on the returned param object to set the parameter value.
	 */
	public QueryParam addQueryParam(String name)
	{
		QueryParam param = new QueryParam(name);
		if (queryParams_ == null)
			queryParams_ = new ArrayList<>();
		queryParams_.add(param);
		return param;
	}

	
	/**
	 * Adds a new query parameter to the Url.
	 * @return this
	 */
	public Url addEmptyQueryParam(String name)
	{
		addQueryParam(name);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 * @return this
	 */
	public Url addQueryParam(String name, String value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}
	
	
	/**
	 * Adds multiple query parameter with that name and values.
	 * @return this
	 */
	public Url addQueryParams(String name, String... values)
	{
		for (String value : values)
			addQueryParam(name, value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 * @return this
	 */
	public Url addQueryParam(String name, int value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 * @return this
	 */
	public Url addQueryParam(String name, Integer value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 * @return this
	 */
	public Url addQueryParam(String name, boolean value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 * @return this
	 */
	public Url addQueryParam(String name, Boolean value)
	{
		addQueryParam(name).setValue(value);
		return this;
	}

	
	/**
	 * Adds a query parameter with that name and value.
	 * @return this
	 */
	public <T> Url addQueryParam(String name, Type<T> type, T value)
	{
		addQueryParam(name).setValue(type, value);
		return this;
	}

	
	/**
	 * QueryParam models a linked list of URL parameters.
	 * The {@link Url} class uses Param to store its parameters.
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
	// session
	//---------------------------------

	
	/**
	 * Sets if the server adds a session id to the Url 
	 * string, when {@link #toString()} is called.
	 * By default the session id is not added.
	 * @return this
	 */
	public Url addSessionId(boolean mode)
	{
		addSessionId_ = mode;
		return this;
	}
	
	
	/**
	 * Returns if the server adds a session id to the Url 
	 * string, when {@link #toString()} is called.
	 * By default the session id will not be added.
	 */
	public boolean addSessionId()
	{
		return addSessionId_;
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
			{
				resource_.getRoute().build(pathParams_, response_.getUriEncoder(), s);
				if (additionalPath_ == null)
				{
					String ext = resource_.getTree().getDefaultExtension();
					if (ext != null)
					{
						s.append('.');
						s.append(ext);
					}
				}
			}
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
					queryParams_.get(i).append(response_.getUriEncoder(), s);
				}
			}
			if (fragment_ != null)
			{
				s.append('#');
				s.append(fragment_);
			}
		}
		
		String url = s.toString();
		if (addSessionId_)
			url = response_.addSessionId(url);
		return url;
	}

	
	private boolean addSessionId_;
	private Response response_;
	private String fragment_;
	private TypeSerializer serializer_;
	private Resource resource_;
	private Path additionalPath_;
	private Object[] pathParams_;
	private ArrayList<QueryParam> queryParams_;
	private String prefix_;
	private static final Object[] EMPTY_PATH_PARAMS = new Object[0];
}
