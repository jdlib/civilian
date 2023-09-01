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
package org.civilian.request;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import org.civilian.application.Application;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;
import org.civilian.resource.Resource;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParamProvider;
import org.civilian.text.service.LocaleService;
import org.civilian.text.service.LocaleServiceProvider;
import org.civilian.text.type.StandardSerializer;
import org.civilian.type.Type;
import org.civilian.util.Check;
import org.civilian.util.Value;


/**
 * Request represents a request for an application resource.<p>
 * A request consists of
 * <ul>
 * <li>a request path, designating a resource,
 * <li>a request method (like GET, POST, etc),
 * <li>a list of headers,
 * <li>preferences for the accepted response content type and locale, 
 * <li>an optional request content,
 * <li>info about remote interface, server and local interface.
 * <li>security info
 * </ul>
 * Each request is associated with a locale, which is either the preferred
 * locale or a locale explicitly set by the application (for example, a locale stored in a user
 * profile).<p> 
 * In a Servlet environment Request is functionally equivalent to a HttpServletRequest.
 * Many methods of HttpServletRequest have direct counterparts in Request.
 * Others methods are bundled to functional groups and available via sub-objects defined
 * in package org.civilian.request:
 * <ul>
 * <li>Header related methods: in {@link RequestHeaders} by {@link #getHeaders()}
 * <li>Security related methods: in {@link RequestSecurity} by {@link #getSecurity()}
 * <li>etc.
 * </ul>
 */
public interface Request extends RequestProvider, PathParamProvider, PathProvider, LocaleServiceProvider
{
	/**
	 * Defines constants for common request methods as defined by the HTTP protocol. 
	 */
	public static interface Method
	{
		public static final String CONNECT	= "CONNECT";
		public static final String DELETE	= "DELETE";
		public static final String GET		= "GET";
		public static final String HEAD		= "HEAD";
		public static final String OPTIONS	= "OPTIONS";
		public static final String PATCH	= "PATCH";
		public static final String POST		= "POST";
		public static final String PUT		= "PUT";
		public static final String TRACE	= "TRACE";
	}
	
	
	/**
	 * An Enum to categorize how the request content was accessed.
	 * @see #getContentAccess()
	 */
	public enum ContentAccess
	{
		/**
		 * Neither a {@link Request#getContentStream() InputStream} nor a 
		 * {@link Request#getContentReader() Reader} were obtained yet.
		 */
		NONE,
		
		/**
		 * {@link Request#getContentStream()} was called.
		 */
		INPUTSTREAM,

		/**
		 * {@link Request#getContentReader()} was called.
		 */
		READER
	}

	
	//-----------------------------
	// general accessors
	//-----------------------------
	
	
	/**
	 * Implements RequestProvider and returns this.
	 */
	@Override public default Request getRequest()
	{
		return this;
	}

	
	/**
	 * @return the owner to which this request belongs.
	 */
	public RequestOwner getOwner();

	
	/**
	 * Returns the name of the method with which this request was made.
	 * Examples are "GET", "PUT", "POST", etc.
	 * @return the method name
	 */
	public String getMethod();
	

	/**
	 * @return if the request has the given method.
	 * @param method the method name
	 * @see #getMethod()
	 */
	public default boolean hasMethod(String method)
	{
		return Objects.equals(getMethod(), method);
	}


	//-----------------------------
	// path
	//-----------------------------
	
	
	/**
	 * @return the absolute path of this request.  
	 */
	@Override public Path getPath();

	
	/**
	 * @return the path of this request relative to the application path.
	 * @see Application#getPath()
	 */
	public Path getRelativePath();

	
	/**
	 * Returns the request path in its original form, as specified by the client. 
	 * For a HTTP request this equals the part of the request URL from the protocol 
	 * name up to the query string in the first line of the HTTP request.
	 * @return the original path 
	 */
	public String getOriginalPath();
	
	
	/**
	 * Returns a Url object for the request. 
	 * If the request was completely matched against a {@link Resource}, it is constructed 
	 * from that Resource, and all path params of the request are set in the URL.
	 * Else it is constructed from the request path.
	 * @return the URL 
	 */
	public default String getUrl()
	{
		StringBuilder s = new StringBuilder();
		s.append(getOriginalPath());

		boolean hasParam = false;
		for (Iterator<String> pnames = getParamNames(); pnames.hasNext(); )
		{
			String pname = pnames.next();
			for (String pvalue : getParams(pname)) 
			{
				s.append(hasParam ? '&' : '?').append(pname).append('=').append(pvalue);
				hasParam = true;
			}
		}
		return s.toString();
	}

	
	//----------------------------
	// resource
	//----------------------------
	
	
	/**
	 * Returns the resource associated with request. The resource
	 * is determined during resource dispatch. It returns null,
	 * if resource dispatch has not run yet, or no resource corresponds
	 * to the request path.
	 * @return the Resource
	 */
	public Resource getResource();

	
	/**
	 * Sets the resource associated with request. The resource
	 * is automatically set when detected during resource dispatch.
	 * @param resource the associated Resource
	 */
	public void setResource(Resource resource);
	
	
	//----------------------------
	// attributes
	//----------------------------
	
	
	/**
	 * Returns an attribute which was previously associated with the request.
	 * @param name the attribute name
	 * @return the attribute 
	 * @see #setAttribute(String, Object)
	 */
	public Object getAttribute(String name);

	
	/**
	 * Returns an iterator for the attribute names stored in the request.
	 * @return the iterator 
	 */
	public Iterator<String> getAttributeNames();

	
	/**
	 * Stores an attribute under the given name in the request. 
	 * @param name the name
	 * @param value the value
	 */
	public void setAttribute(String name, Object value);

	
	//----------------------------
	// cookies
	//----------------------------
	
	
	/**
	 * Returns the CookieList containing all the Cookies sent with this request. 
	 * The method returns an empty list if no cookies were sent.
	 * @return the CookieList 
	 */
	public CookieList getCookies();

	
	//----------------------------
	// parameters
	//----------------------------
	
	
	/**
	 * Returns the first value of a request parameter, or null if the parameter does not exist.
	 * In HTTP terms this includes query parameters contained in the request URL
	 * and also post parameters (i.e. parameters contained in the request content with
	 * content type application/x-www-form-urlencoded).
	 * @param name the parameter name
	 * @return the parameter value 
	 */
	public String getParam(String name);

	
	/**
	 * Returns a String array containing all values of a request parameter.
	 * @param name the name 
	 * @return the values or an empty array with length 0 if the parameter does not exist.
	 */
	public String[] getParams(String name);
	

	/**
	 * @return an Iterator for the names of the parameters contained in this request.
	 */
	public Iterator<String> getParamNames();

	
	/**
	 * @return a map of all request parameter names and values.
	 */
	public Map<String,String[]> getParamMap();


	//----------------------------
	// path params
	//----------------------------


	/**
	 * Sets the value of a path parameter.
	 * This is usually automatically done during resource dispatch, when 
	 * the request path is parsed, and path segments are recognized to match 
	 * defined PathParams.
	 * @param pathParam the path param
	 * @param value the param value 
	 * @param <T> the type of the parameter value
	 */
	public <T> void setPathParam(PathParam<T> pathParam, T value);


	/**
	 * Sets the path parameters.
	 * This is usually automatically done during resource dispatch, when 
	 * the request path is parsed, and path segments are recognized to match 
	 * defined PathParams. 
	 * All previous path parameters are cleared.
	 * @param pathParams the path params
	 */
	public void setPathParams(Map<PathParam<?>,Object> pathParams);
	
	
	/**
	 * Returns the value of a path parameter, or null if the path parameter is not contained
	 * in the request.
	 */
	@Override public <T> T getPathParam(PathParam<T> pathParam);
	
	
	/**
	 * @return an Iterator for all path parameters recognized in this request. 
	 */
	public Iterator<PathParam<?>> getPathParams();
	
	
	//----------------------------
	// matrix params
	//----------------------------


	/**
	 * Returns the value of a matrix parameter, or null if such a matrix parameter does not exist.
	 * Matrix parameters are only recognized in the last segment of the request path.
	 * @param name the matrix param name
	 * @return the param value
	 */
	public String getMatrixParam(String name);
	
	
	/**
	 * Returns the values of a matrix parameter as a string array.
	 * @param name the matrix param name
	 * @return the array. The array is empty if the matrix parameter is not contained
	 * 		in this request. 
	 */
	public String[] getMatrixParams(String name);
	
	
	/**
	 * Returns the value of a matrix parameter wrapped in a Value object.
	 * If the parameter does not exist, the Value is empty.
	 * If the conversion of the parameter to the requested type fails, the 
	 * Value contains the error.
	 * @param name the matrix param name
	 * @param type the matrix param type
	 * @param <T> the type class
	 * @return the value
	 */
	public default <T> Value<T> getMatrixParam(String name, Type<T> type)
	{
		return StandardSerializer.INSTANCE.parseValue(type, getMatrixParam(name));
	}

	
	/**
	 * @return an Iterator of matrix parameter names contained in this request.
	 */
	public Iterator<String> getMatrixParamNames();

	
	//----------------------------
	// uploads
	//----------------------------
	
	
	/**
	 * @return the upload of the request.
	 */
	public Uploads getUploads();

	
	//------------------------------
	// locale
	//------------------------------
	
	
	/**
	 * Returns the locale data associated with the request.
	 * The locale data can be set explicitly by {@link #setLocaleService(LocaleService)}.
	 * If not explicitly set, it is derived from the preferred locale ({@link Request#getAcceptedLocale()}).
	 * If the preferred locale is not contained in the list of supported locales (see {@link Application#getLocaleServices()})
	 * then the default application locale will be used.
	 * @return the service  
	 */
	@Override public LocaleService getLocaleService();
	

	/**
	 * Sets the locale data associated with the request.
	 * This overrides the default locale data, as defined by the preferred locale.
	 * A use case is to set the locale according to the preferences stored in a user session.
	 * @param service the service
	 */
	public void setLocaleService(LocaleService service);

	
	/**
	 * Sets the locale data associated with the request to the locale data with
	 * the given locale
	 * @param locale the locale
	 */
	public default void setLocaleService(Locale locale)
	{
		Check.notNull(locale, "locale");
		setLocaleService(getOwner().getLocaleServices().getService(locale));
	}

	
	/**
	 * Returns a list of qualified response content types accepted by the client. 
	 * This list is defined by the Accept header.
	 * If the request does not specify an explicit list of accepted content-types
	 * a list with the single content-type &#42;/&#42; is returned.
	 * @return the ContentTypeList 
	 */
	public ContentTypeList getAcceptedContentTypes();


	/**
	 * Returns the preferred Locale for response content.<br>
	 * This Locale is defined by the Accept-Language header.<br> 
	 * If the request does not contain a preferred the default locale of the application is returned. 
	 * (Note: This differs from a servlet container which would return the default system locale).
	 * @return the locale 
	 */
	public abstract Locale getAcceptedLocale();
	

	/**
	 * @return the preferred Locales for returned content, in decreasing order.
	 * This list is defined by the Accept-Language header.<br> 
	 */
	public abstract Iterator<Locale> getAcceptedLocales(); 

	
	//--------------------------------------
	// content
	//--------------------------------------

	
	/**
	 * Returns the character encoding used for the request content, or
	 * null if not specified.
	 * @return the encoding
	 */
	public String getCharEncoding();


	/**
	 * Explicitly sets the character encoding of the content.
	 * @param encoding the encoding
	 * @throws UnsupportedEncodingException if the encoding is not supported
	 */
	public void setCharEncoding(String encoding) throws UnsupportedEncodingException;


	/**
	 * @return the length of the content in bytes, or -1 if not known. 
	 */
	public long getContentLength();
	
	
	/**
	 * @return the content type of the request content or null if not known.
	 */
	public ContentType getContentType();
	
	
	/**
	 * Overrides the content type of the request.
	 * @param contentType the content type
	 */
	public void setContentType(ContentType contentType);
	
	
	/**
	 * Returns how the request content was accessed.
	 * @return the ContentAccess
	 */
	public ContentAccess getContentAccess();
	
	
	/**
	 * Returns an InputStream to read the request content.
	 * @return the InputStream
	 * @throws IOException if {@link #getContentReader()} has already been called. 
	 */
	public abstract InputStream getContentStream() throws IOException;
	
	  
	/**
	 * Returns a Reader for the request content. If the {@link #getCharEncoding() content encoding}
	 * is not set, it uses the default {@link Application#getDefaultCharEncoding() application encoding}.
	 * @return  the Reader
	 * @throws IOException if {@link #getContentStream()} has already been called. 
	 */
	public Reader getContentReader() throws IOException; 
	

	/**
	 * Reads the content and transforms it into an Object of the given type.
	 * This method is a shortcut for readContent(type, type).
	 * @param type the class
	 * @param <T> the class type
	 * @return the transformed content
	 * @throws Exception thrown during read
	 */
	public default <T> T readContent(Class<T> type) throws Exception
	{
		return readContent(type, type); 
	}

	
	/**
	 * Reads the content and transforms it into an Object of the given type.
	 * Internally a ContentSerializer defined by the application is used
	 * to read the content.
	 * @param type the type of the expected object
	 * @param genericType the generic type of the expected object or null not known
	 * @param <T> the class type
	 * @throws BadRequestException if the ContentSerializer
	 * 		recognizes bad syntax in the content.
	 * @throws Exception if reading throws a runtime exception or no suitable
	 * 		content reader is available.
	 * @see Application#getContentSerializers()
	 * @see ContentSerializer#read(Class, java.lang.reflect.Type, Reader)
	 * @return the content object
	 */
	public default <T> T readContent(Class<T> type, java.lang.reflect.Type genericType) 
		throws BadRequestException, Exception
	{
		Check.notNull(type, "type");
		if (genericType == null)
			genericType = type;

		ContentType contentType	= getContentType();
		if ((contentType == null) && (type == String.class))
			contentType = ContentType.TEXT_PLAIN;
			
		ContentSerializer reader = getOwner().getContentSerializers().get(contentType);
		if (reader == null)
			throw new IllegalStateException("don't know how to read content with content type '" + contentType + "'");
		
		try
		{
			return reader.read(type, genericType, getContentReader());
		}
		catch(Exception e)
		{
			String message = reader.describeReadError(e);
			if (message != null)
				throw new BadRequestException("RequestContent: " + message, e);
			else
				throw e;
		}
	}

	
	/**
	 * Returns a InterceptorBuilder which allows to add a interceptor.
	 * @return the builder
	 */
	public abstract InterceptorBuilder addInterceptor();

	
	public interface InterceptorBuilder
	{
		/**
		 * Adds a RequestInterceptor which can wrap the request {@link #getContentStream() InputStream}.
		 * {@link #getContentStream()} or {@link #getContentReader()} must not have been called yet.
		 * @param interceptor an interceptor
		 */
		public void forStream(RequestInterceptor<InputStream> interceptor);

	
		/**
		 * Adds a RequestInterceptor which can wrap the request {@link #getContentReader() Reader}.
		 * {@link #getContentStream()} or {@link #getContentReader()} must not have been called yet.
		 * @param interceptor an interceptor
		 */
		public void forReader(RequestInterceptor<Reader> interceptor);
	}
	

	
	//--------------------------------------
	// detail objects
	//--------------------------------------

	
	/**
	 * Returns the request headers. 
	 * @return the headers
	 */
	public RequestHeaders getHeaders();
	
	
	/**
	 * Returns a RequestSecurity object which provides security related
	 * information.
	 * @return the RequestSecurity
	 */
	public RequestSecurity getSecurity();

	
	/**
	 * Returns a ServerInfo object which provides server related
	 * information.
	 * @return the ServerInfo
	 */
	public ServerInfo getServerInfo();
	
	
	/**
	 * Returns a RemoteInfo object which provides information about 
	 * the remote site of the request.
	 * @return the RemoteInfo
	 */
	public RemoteInfo getRemoteInfo();
	
	
	/**
	 * Returns a LocalInfo object which provides information about 
	 * the local interface which received the request.
	 * @return the LocalInfo
	 */
	public LocalInfo getLocalInfo();


	/**
	 * Returns the session of the request.
	 * @param create if true and the request does not have a session, a session is created
	 * @return the session or null, if the request does not have a session and create is false. 
	 */
	public Session getSession(boolean create);

	
	/**
	 * Returns a Session.Optional for the current session.
	 * @return the optional
	 */
	public default Session.Optional getSessionOptional()
	{
		return new Session.Optional(getSession(false));
	}

	
	//--------------------------------------
	// misc
	//--------------------------------------

	
	/**
	 * Prints request info to the PrintStream.
	 * @param out a PrintStream
	 */
	public default void print(PrintStream out)
	{
		Check.notNull(out, "out");
		print(new PrintWriter(out, true));
	}

	
	/**
	 * Prints request info to the PrintWriter.
	 * @param out a PrintWriter
	 */
	public default void print(PrintWriter out)
	{
		Check.notNull(out, "out");
		out.print(getMethod());
		out.print(" ");
		out.println(getUrl());
		RequestHeaders headers = getHeaders(); 
		for (String name : headers)
		{
			String[] values = headers.getAll(name);
			for (String value : values)
			{
				out.print(name);
				out.print(' ');
				out.println(value);
			}
		}
	}

	
	/**
	 * Returns the underlying implementation of the request which has the given class
	 * or null, if the implementation has a different class.
	 * In a servlet environment Request wraps a HttpServletRequest.
	 * @param implClass a class
	 * @param <T> a class type
	 * @return the implementation
	 */
	public <T> T unwrap(Class<T> implClass);
}
