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
package org.civilian;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.provider.*;
import org.civilian.request.*;
import org.civilian.resource.Path;
import org.civilian.resource.PathParam;
import org.civilian.resource.PathParamProvider;
import org.civilian.resource.PathProvider;
import org.civilian.resource.Url;
import org.civilian.text.LocaleService;
import org.civilian.text.LocaleServiceProvider;
import org.civilian.type.Type;
import org.civilian.type.fn.StandardSerializer;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
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
public interface Request extends RequestProvider, ResponseProvider, ApplicationProvider, 
	ServerProvider, PathParamProvider, PathProvider, LocaleServiceProvider
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
	 * Returns the associated response.
	 */
	@Override public Response getResponse();

	
	/**
	 * Allows to set the response.
	 * The {@link Response#getRequest() request} of the response must equal this.
	 * Use this method if you need to wrap the original response to intercept
	 * invocation of response methods.
	 * @param response the Response 
	 */
	public void setResponse(Response response);

	
	/**
	 * Returns the server to which this request belongs.
	 */
	@Override public default Server getServer()
	{
		return getApplication().getServer();
	}

	
	/**
	 * Returns the application to which this request belongs.
	 */
	@Override public Application getApplication();

	
	/**
	 * Returns the name of the method with which this request was made.
	 * Examples are "GET", "PUT", "POST", etc.
	 * @return the method name
	 */
	public String getMethod();
	

	/**
	 * Returns if the request has the given method.
	 * @param method the method name
	 * @see #getMethod()
	 */
	public default boolean hasMethod(String method)
	{
		return ClassUtil.equals(getMethod(), method);
	}


	//-----------------------------
	// path
	//-----------------------------
	
	
	/**
	 * Returns the absolute path of this request.  
	 */
	@Override public Path getPath();

	
	/**
	 * Returns the path of this request relative to the application path.
	 * @see Application#getPath()
	 */
	public Path getRelativePath();

	
	/**
	 * Returns the real path on the server which corresponds to the request path
	 * @return the path or null, if there is no correspondence.
	 * @see Server#getRealPath(String)
	 */
	public default String getRealPath()
	{
		Server server = getServer();
		Path subContextPath = getPath().cutStart(server.getPath()); 
		return subContextPath != null ? server.getRealPath(subContextPath) : null;
	}


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
	 * @param addServer should protocol, host and port be included in the URL? 
	 * @param addParams should parameters added as query parameter?
	 * @return the URL 
	 */
	public default Url getUrl(boolean addServer, boolean addParams)
	{
		Url url;
		
		if (getResource() != null)
			url = new Url(this, getResource()); // also copies the path params
		else
			url = new Url(this, getPath());
		
		if (addServer)
			url.prepend(getServerInfo().toString());
		
		if (addParams)
		{
			for (Iterator<String> pnames = getParameterNames(); pnames.hasNext(); )
			{
				String pname = pnames.next();
				url.addQueryParams(pname, getParameters(pname));
			}
		}
		
		return url;
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
	public String getParameter(String name);

	
	/**
	 * Returns the value of a parameter wrapped in a Value object.
	 * If the parameter does not exist, the Value is {@link Value#hasValue() empty}. 
	 * If the conversion of the parameter to the requested type fails, the 
	 * Value contains the {@link Value#getError() conversion error}.
	 * @param<T> the type of the parameter value
	 * @param name the parameter name
	 * @param type the parameter type
	 * @return the parameter value
	 */
	public default <T> Value<T> getParameter(String name, Type<T> type)
	{
		return new Value<>(type, getParameter(name), StandardSerializer.INSTANCE);
	}

	
	/**
	 * Returns a String array containing all values of a request parameter. 
	 * @return the values or an empty array with length 0 if the parameter does not exist.
	 */
	public String[] getParameters(String name);
	

	/**
	 * Returns an Iterator for the names of the parameters contained in this request.
	 */
	public Iterator<String> getParameterNames();

	
	/**
	 * Returns a map of all request parameter names and values.
	 */
	public Map<String,String[]> getParameterMap();


	//----------------------------
	// path params
	//----------------------------


	/**
	 * Sets the value of a path parameter.
	 * This is usually automatically done during resource dispatch, when 
	 * the request path is parsed, and path segments are recognized to match 
	 * defined PathParams. 
	 * @param<T> the type of the parameter value
	 */
	public <T> void setPathParam(PathParam<T> pathParam, T value);


	/**
	 * Sets the path parameters.
	 * This is usually automatically done during resource dispatch, when 
	 * the request path is parsed, and path segments are recognized to match 
	 * defined PathParams. 
	 * All previous path parameters are cleared.
	 */
	public void setPathParams(Map<PathParam<?>,Object> pathParams);
	
	
	/**
	 * Returns the value of a path parameter, or null if the path parameter is not contained
	 * in the request.
	 */
	@Override public <T> T getPathParam(PathParam<T> pathParam);
	
	
	/**
	 * Returns an Iterator for all path parameters recognized in this request. 
	 */
	public Iterator<PathParam<?>> getPathParams();
	
	
	//----------------------------
	// matrix params
	//----------------------------


	/**
	 * Returns the value of a matrix parameter, or null if such a matrix parameter does not exist.
	 * Matrix parameters are only recognized in the last segment of the request path.
	 */
	public String getMatrixParam(String name);
	
	
	/**
	 * Returns the values of a matrix parameter as a string array.
	 * @return the array. The array is empty if the matrix parameter is not contained
	 * 		in this request. 
	 */
	public String[] getMatrixParams(String name);
	
	
	/**
	 * Returns the value of a matrix parameter wrapped in a Value object.
	 * If the parameter does not exist, the Value is empty.
	 * If the conversion of the parameter to the requested type fails, the 
	 * Value contains the error.
	 */
	public default <T> Value<T> getMatrixParam(String name, Type<T> type)
	{
		return new Value<>(type, getMatrixParam(name), StandardSerializer.INSTANCE);
	}

	
	/**
	 * Returns an Iterator of matrix parameter names contained in this request.
	 */
	public Iterator<String> getMatrixParamNames();

	
	//----------------------------
	// uploads
	//----------------------------
	
	
	/**
	 * Returns if the request contains uploads.
	 */
	public boolean hasUploads();

	
	/**
	 * Returns the first Upload object with the given name.
	 * In Servlet terms the Upload object corresponds to a javax.servlet.http.Part object 
	 * in a multipart/form-data request whose content disposition contains
	 * a filename parameter and whose name equals the given name.
	 * @return the upload
	 */
	public Upload getUpload(String name);

	
	/**
	 * Returns all Upload objects with the given name.
	 * In Servlet terms the Upload object corresponds to a javax.servlet.http.Part object 
	 * in a multipart/form-data request whose content disposition contains
	 * a filename parameter and whose name equals the given name.
	 * @return the uploads
	 */
	public Upload[] getUploads(String name);

	
	/**
	 * Returns an exception if the request contains uploaded files and
	 * one ore more files violated constraints defined by the {@link Application#getUploadConfig() UploadConfig}.
	 * In this case the request parameters may not be properly initialized.
	 * Therefore for upload requests you should check upload errors first, before you evaluate request parameters.
	 * @return the upload error  
	 */
	public Exception getUploadError();

	
	/**
	 * Returns an iterator for all Upload names.
	 * @see #getUpload(String)
	 */
	public Iterator<String> getUploadNames();

	
	//------------------------------
	// locale
	//------------------------------
	
	
	/**
	 * Returns the locale data associated with the request.
	 * The locale data can be set explicitly by {@link #setLocaleService(LocaleService)}.
	 * If not explicitly set, it is derived from the preferred locale ({@link Request#getAcceptedLocale()}).
	 * If the preferred locale is not contained in the list of supported locales (see {@link Application#getLocaleServices()})
	 * then the default application locale will be used.  
	 */
	@Override public LocaleService getLocaleService();
	

	/**
	 * Sets the locale data associated with the request.
	 * This overrides the default locale data, as defined by the preferred locale.
	 * A use case is to set the locale according to the preferences stored in a user session.
	 */
	public void setLocaleService(LocaleService service);

	
	/**
	 * Sets the locale data associated with the request to the locale data with
	 * the given locale
	 */
	public default void setLocaleService(Locale locale)
	{
		Check.notNull(locale, "locale");
		setLocaleService(getApplication().getLocaleServices().getService(locale));
	}

	
	/**
	 * Returns a list of qualified response content types accepted by the client. 
	 * This list is defined by the Accept header.
	 * If the request does not specify an explicit list of accepted content-types
	 * and {@link ExtensionMapping extension mappings} are configured, then
	 * the extension of the path is used to determine the accepted content type. 
	 * Else a list with the single content-type &#42;/&#42; is returned.
	 * @return the ContentTypeList 
	 */
	public ContentTypeList getAcceptedContentTypes();


	/**
	 * Returns the preferred Locale for response content.<br>
	 * This Locale is defined by the Accept-Language header.<br> 
	 * If the request does not contain a preferred locale and {@link ExtensionMapping extension locales} 
	 * are configured, then the extension of the path is used to determine the accepted locale. 
	 * Else the default locale of the application is returned. (Note: This differs from 
	 * a servlet container which would return the default system locale).
	 * @return the locale 
	 */
	public abstract Locale getAcceptedLocale();
	

	/**
	 * Returns the preferred Locales for returned content, in decreasing order.
	 * This list is defined by the Accept-Language header.<br> 
	 */
	public abstract Iterator<Locale> getAcceptedLocales(); 

	
	//--------------------------------------
	// content
	//--------------------------------------

	
	/**
	 * Returns the character encoding used for the request content, or
	 * null if not specified.
	 * @return encoding
	 */
	public String getCharEncoding();


	/**
	 * Explicitly sets the character encoding of the content.
	 */
	public void setCharEncoding(String encoding) throws UnsupportedEncodingException;


	/**
	 * Returns the length of the content in bytes, or -1 if not known. 
	 * @return the length
	 */
	public long getContentLength();
	
	
	/**
	 * Returns the content type of the request content or null if not known.
	 * @return the ContentType
	 */
	public ContentType getContentType();
	
	
	/**
	 * Overrides the content type of the request.
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
	 * @return the transformed content
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
	 * @throws Exception if reading throws a runtime exception or no suitable
	 * 		content reader is available.
	 * 		The exception is a {@link BadRequestException} if the ContentSerializer
	 * 		recognized bad syntax in the content.  
	 * @see Application#getContentSerializer(ContentType)
	 * @see ContentSerializer#read(Class, java.lang.reflect.Type, Reader)
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
			
		ContentSerializer reader = getApplication().getContentSerializer(contentType);
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
	 * Adds a RequestStreamInterceptor which can wrap the request {@link #getContentStream() InputStream}.
	 * {@link #getContentStream()} or {@link #getContentReader()} must not have been called yet.
	 * @param interceptor an interceptor
	 */
	public abstract void addInterceptor(RequestStreamInterceptor interceptor);
	
	
	/**
	 * Adds a RequestReaderInterceptor which can wrap the request {@link #getContentReader() Reader}.
	 * {@link #getContentStream()} or {@link #getContentReader()} must not have been called yet.
	 * @param interceptor an interceptor
	 */
	public abstract void addInterceptor(RequestReaderInterceptor interceptor);

	
	//--------------------------------------
	// async operations
	//--------------------------------------

	
	/**
	 * Returns the AsyncContext that was created by the most recent call to {@link #startAsync()}
	 * @return the AsnycContext
	 * @throws IllegalStateException if startAsync() has not been called.
	 */
	public AsyncContext getAsyncContext();
	
	
	/**
	 * Returns if this request has been put into asynchronous mode by a call to {@link #startAsync()}.
	 * @return the flag 
	 */
	public boolean isAsyncStarted();
	
	
	/**
	 * Returns if this request supports asynchronous mode. 
	 * @return the flag 
	 */
	public boolean isAsyncSupported();
	
	
	/**
	 * Puts this request into asynchronous mode, and initializes its AsyncContext.
	 * @return the AsyncContext 
	 * @throws IllegalStateException if this request does not support asynchronous operations or if called again
	 * 		in a state where the AsyncContext intervenes, or when the response has been closed.
	 */
	public AsyncContext startAsync();

	
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
	 */
	public default void print(PrintStream out)
	{
		Check.notNull(out, "out");
		print(new PrintWriter(out, true));
	}

	
	/**
	 * Prints request info to the PrintWriter.
	 */
	public default void print(PrintWriter out)
	{
		Check.notNull(out, "out");
		out.print(getMethod());
		out.print(" ");
		out.println(getUrl(false /*server*/, true /*params*/));
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
	 * @return the implementation
	 */
	public <T> T unwrap(Class<T> implClass);
}
