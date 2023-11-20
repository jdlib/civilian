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


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.JaxbXmlSerializer;
import org.civilian.request.Request;
import org.civilian.request.RequestProvider;
import org.civilian.resource.PathProvider;
import org.civilian.resource.Resource;
import org.civilian.resource.ResourceHandler;
import org.civilian.resource.Url;
import org.civilian.template.Template;
import org.civilian.text.service.LocaleService;
import org.civilian.text.service.LocaleServiceProvider;
import org.civilian.util.Check;
import org.civilian.util.CheckedFunction;


/**
 * Response represents a response to a request.
 * It consists of a {@link Status}, {@link ResponseHeaders headers} and content, produced
 * either by a {@link #getContentWriter() Writer} or a {@link #getContentStream() OutputStream}.<br> 
 * In a Servlet environment Response is functionally equivalent to a HttpServletResponse.
 */
public interface Response extends RequestProvider, ResponseProvider, LocaleServiceProvider
{
	/**
	 * Defines constants for the response status. Their numeric value equals the correspondent HTTP status code.
	 * All status codes are also defined a second time with prefix SC followed by their numerical value.  
	 */
	public interface Status
	{
		public static final int CONTINUE 							= HttpServletResponse.SC_CONTINUE;
		public static final int SWITCHING_PROTOCOLS					= HttpServletResponse.SC_SWITCHING_PROTOCOLS;
		public static final int OK 									= HttpServletResponse.SC_OK;
		public static final int CREATED 							= HttpServletResponse.SC_CREATED;
		public static final int ACCEPTED							= HttpServletResponse.SC_ACCEPTED;
		public static final int NON_AUTHORITATIVE_INFORMATION 		= HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION;
		public static final int NO_CONTENT 							= HttpServletResponse.SC_NO_CONTENT;
		public static final int RESET_CONTENT 						= HttpServletResponse.SC_RESET_CONTENT;
		public static final int PARTIAL_CONTENT 					= HttpServletResponse.SC_PARTIAL_CONTENT;
		public static final int MULTIPLE_CHOICES 					= HttpServletResponse.SC_MULTIPLE_CHOICES;
		public static final int MOVED_PERMANENTLY 					= HttpServletResponse.SC_MOVED_PERMANENTLY;
		public static final int MOVED_TEMPORARILY 					= HttpServletResponse.SC_MOVED_TEMPORARILY;
		public static final int FOUND 								= HttpServletResponse.SC_FOUND;
		public static final int SEE_OTHER 							= HttpServletResponse.SC_SEE_OTHER;
		public static final int NOT_MODIFIED 						= HttpServletResponse.SC_NOT_MODIFIED;
		public static final int USE_PROXY 							= HttpServletResponse.SC_USE_PROXY;
		public static final int TEMPORARY_REDIRECT 					= HttpServletResponse.SC_TEMPORARY_REDIRECT;
		public static final int BAD_REQUEST 						= HttpServletResponse.SC_BAD_REQUEST;
		public static final int UNAUTHORIZED 						= HttpServletResponse.SC_UNAUTHORIZED;
		public static final int PAYMENT_REQUIRED 					= HttpServletResponse.SC_PAYMENT_REQUIRED;
		public static final int FORBIDDEN 							= HttpServletResponse.SC_FORBIDDEN;
		public static final int NOT_FOUND 							= HttpServletResponse.SC_NOT_FOUND;
		public static final int METHOD_NOT_ALLOWED 					= HttpServletResponse.SC_METHOD_NOT_ALLOWED;
		public static final int NOT_ACCEPTABLE 						= HttpServletResponse.SC_NOT_ACCEPTABLE;
		public static final int PROXY_AUTHENTICATION_REQUIRED 		= HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;
		public static final int REQUEST_TIMEOUT 					= HttpServletResponse.SC_REQUEST_TIMEOUT;
		public static final int CONFLICT 							= HttpServletResponse.SC_CONFLICT;
		public static final int GONE 								= HttpServletResponse.SC_GONE;
		public static final int LENGTH_REQUIRED 					= HttpServletResponse.SC_LENGTH_REQUIRED;
		public static final int PRECONDITION_FAILED 				= HttpServletResponse.SC_PRECONDITION_FAILED;
		public static final int REQUEST_ENTITY_TOO_LARGE 			= HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
		public static final int REQUEST_URI_TOO_LONG 				= HttpServletResponse.SC_REQUEST_URI_TOO_LONG;
		public static final int UNSUPPORTED_MEDIA_TYPE 				= HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
		public static final int REQUESTED_RANGE_NOT_SATISFIABLE 	= HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;
		public static final int EXPECTATION_FAILED 					= HttpServletResponse.SC_EXPECTATION_FAILED;
		public static final int INTERNAL_SERVER_ERROR 				= HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		public static final int NOT_IMPLEMENTED 					= HttpServletResponse.SC_NOT_IMPLEMENTED;
		public static final int BAD_GATEWAY 						= HttpServletResponse.SC_BAD_GATEWAY;
		public static final int SERVICE_UNAVAILABLE 				= HttpServletResponse.SC_SERVICE_UNAVAILABLE;
		public static final int GATEWAY_TIMEOUT 					= HttpServletResponse.SC_GATEWAY_TIMEOUT;
		public static final int HTTP_VERSION_NOT_SUPPORTED 			= HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED;

		public static final int SC100_CONTINUE 						= HttpServletResponse.SC_CONTINUE;
		public static final int SC101_SWITCHING_PROTOCOLS			= HttpServletResponse.SC_SWITCHING_PROTOCOLS;
		public static final int SC200_OK 							= HttpServletResponse.SC_OK;
		public static final int SC201_CREATED						= HttpServletResponse.SC_CREATED;
		public static final int SC202_ACCEPTED						= HttpServletResponse.SC_ACCEPTED;
		public static final int SC203_NON_AUTHORITATIVE_INFORMATION = HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION;
		public static final int SC204_NO_CONTENT 					= HttpServletResponse.SC_NO_CONTENT;
		public static final int SC205_RESET_CONTENT 				= HttpServletResponse.SC_RESET_CONTENT;
		public static final int SC206_PARTIAL_CONTENT				= HttpServletResponse.SC_PARTIAL_CONTENT;
		public static final int SC300_MULTIPLE_CHOICES				= HttpServletResponse.SC_MULTIPLE_CHOICES;
		public static final int SC301_MOVED_PERMANENTLY 			= HttpServletResponse.SC_MOVED_PERMANENTLY;
		public static final int SC302_MOVED_TEMPORARILY 			= HttpServletResponse.SC_MOVED_TEMPORARILY;
		public static final int SC302_FOUND 						= HttpServletResponse.SC_FOUND;
		public static final int SC303_SEE_OTHER 					= HttpServletResponse.SC_SEE_OTHER;
		public static final int SC304_NOT_MODIFIED 					= HttpServletResponse.SC_NOT_MODIFIED;
		public static final int SC305_USE_PROXY 					= HttpServletResponse.SC_USE_PROXY;
		public static final int SC307_TEMPORARY_REDIRECT 			= HttpServletResponse.SC_TEMPORARY_REDIRECT;
		public static final int SC400_BAD_REQUEST 					= HttpServletResponse.SC_BAD_REQUEST;
		public static final int SC401_UNAUTHORIZED 					= HttpServletResponse.SC_UNAUTHORIZED;
		public static final int SC402_PAYMENT_REQUIRED 				= HttpServletResponse.SC_PAYMENT_REQUIRED;
		public static final int SC403_FORBIDDEN 					= HttpServletResponse.SC_FORBIDDEN;
		public static final int SC404_NOT_FOUND 					= HttpServletResponse.SC_NOT_FOUND;
		public static final int SC405_METHOD_NOT_ALLOWED 			= HttpServletResponse.SC_METHOD_NOT_ALLOWED;
		public static final int SC406_NOT_ACCEPTABLE 				= HttpServletResponse.SC_NOT_ACCEPTABLE;
		public static final int SC407_PROXY_AUTHENTICATION_REQUIRED = HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED;
		public static final int SC408_REQUEST_TIMEOUT 				= HttpServletResponse.SC_REQUEST_TIMEOUT;
		public static final int SC409_CONFLICT 						= HttpServletResponse.SC_CONFLICT;
		public static final int SC410_GONE 							= HttpServletResponse.SC_GONE;
		public static final int SC411_LENGTH_REQUIRED 				= HttpServletResponse.SC_LENGTH_REQUIRED;
		public static final int SC412_PRECONDITION_FAILED 			= HttpServletResponse.SC_PRECONDITION_FAILED;
		public static final int SC413_REQUEST_ENTITY_TOO_LARGE 		= HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE;
		public static final int SC414_REQUEST_URI_TOO_LONG 			= HttpServletResponse.SC_REQUEST_URI_TOO_LONG;
		public static final int SC415_UNSUPPORTED_MEDIA_TYPE 		= HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
		public static final int SC416_REQUESTED_RANGE_NOT_SATISFIABLE = HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE;
		public static final int SC417_EXPECTATION_FAILED 			= HttpServletResponse.SC_EXPECTATION_FAILED;
		public static final int SC500_INTERNAL_SERVER_ERROR 		= HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		public static final int SC501_NOT_IMPLEMENTED 				= HttpServletResponse.SC_NOT_IMPLEMENTED;
		public static final int SC502_BAD_GATEWAY 					= HttpServletResponse.SC_BAD_GATEWAY;
		public static final int SC503_SERVICE_UNAVAILABLE			= HttpServletResponse.SC_SERVICE_UNAVAILABLE;
		public static final int SC504_GATEWAY_TIMEOUT 				= HttpServletResponse.SC_GATEWAY_TIMEOUT;
		public static final int SC505_HTTP_VERSION_NOT_SUPPORTED 	= HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED;
	}

	
	/**
	 * Type characterizes a response.
	 * @see #getType()
	 */
	public enum Type
	{
		/**
		 * Normal response.
		 */
		NORMAL,
		
		/**
		 * An error has been sent.
		 */
		ERROR,
		
		/**
		 * A redirect was sent.
		 */
		REDIRECT,
	}
	
	
	/**
	 * An Enum to categorize how response content is produced.
	 */
	public enum ContentAccess
	{
		/**
		 * Neither #getContentWriter() nor #getContentStream() was called.
		 */
		NONE,
		
		/**
		 * A PrintWriter is used to produce the response body.
		 * @see #getContentWriter()
		 */
		WRITER,

		/**
		 * A OutputStream is used to produce the response body.
		 * @see #getContentStream()
		 */
		OUTPUTSTREAM
	}

	
	/**
	 * @return the request.
	 */
	@Override public abstract Request getRequest();

	
	/**
	 * @return Implements ResponseProvider and returns this.
	 */
	@Override public default Response getResponse()
	{
		return this;
	}

	
	/**
	 * @return the response owner.
	 */
	public ResponseOwner getOwner();
	

	/**
	 * Returns if the response is committed. A response is committed
	 * if status and headers are already written. 
	 * If not committed then a call to {@link #reset()} or {@link Response#resetBuffer()}
	 * is still possible.
	 * @return is committed?
	 */
	public abstract boolean isCommitted();

	
	/**
	 * Clears the response buffer, the status code, and headers.
	 * @throws IllegalStateException if the response has already been committed
	 * @see #isCommitted()
	 */
	public abstract void reset();

	
	/**
	 * Returns the response type. The initial type is Type.NORMAL.
	 * When you send an error or forward, the type changes
	 * accordingly.
	 * @return the type 
	 */
	public abstract Type getType();

	
	/**
	 * Adds a cookie to the response.
	 * @param cookie a cookie
	 * @return this response
	 */
	public abstract Response addCookie(Cookie cookie); 


	/**
	 * Adds the session id to the URL string and returns the new URL string.
	 * In Servlet terms this method corresponds to HttpServletResponse#encodeURL.
	 * @param url a url  
	 * @return the URL with session id included or the original URL if the session id does not 
	 * need to be included in the URL.
	 */
	public abstract String addSessionId(String url);

	
	//------------------------------
	// locale
	//------------------------------
	
	
	/**
	 * Returns the LocaleService associated with the response.
	 * The LocaleService can be set explicitly by {@link #setLocaleService(LocaleService)}.
	 * If not explicitly set it is the same as the LocaleService of the request.
	 * @see Request#getLocaleService()
	 */
	@Override public abstract LocaleService getLocaleService();
	

	/**
	 * Sets the LocaleService associated with the response.
	 * @param service a LocaleService
	 * @return this
	 */
	public abstract Response setLocaleService(LocaleService service);

	
	/**
	 * Sets the LocaleService associated with the response
	 * to the application LocaleService for the given locale.
	 * @param locale a locale
	 * @return this
	 */
	public default Response setLocaleService(Locale locale)
	{
		return setLocaleService(getOwner().getLocaleServices().getService(locale));
	}

	
	//------------------------------
	// status
	//------------------------------
	
	
	/**
	 * @return the status code of the response.
	 */
	public abstract int getStatus();

	
	/**
	 * Sets the status code of the response.
	 * @param statusCode the code. See {@link Status} for a list of common status codes.
	 * @return this response
	 */
	public abstract Response setStatus(int statusCode);
	
	
	//------------------------------
	// url
	//------------------------------

	
	/**
	 * A builder for Urls.
	 * @param <T> the result type of the build process.
	 */
	public static class UrlBuilder<T,E extends Exception>
	{
		protected UrlBuilder(Response response, CheckedFunction<Url,T,E> end)
		{
			response_ 	= response;
			end_ 		= end;
		}
		
		
		public T to(String urlString) throws E
		{
			return to(new Url(response_, urlString));
		}
		
		
		public T to(PathProvider pathProvider) throws E
		{
			return to(new Url(response_, pathProvider));
		}


		/**
		 * Builds a Url to the given Resource.
		 * The path may contain path parameters for which you then must provide
		 * values. Path parameters shared by the current request are automatically initialized.
		 * @param resource a resource 
		 * @return the result 
		 * @throws E in case of an error 
		 */
		public T to(Resource resource) throws E
		{
			Check.notNull(resource, "resource");
			Url url = new Url(response_, response_.getOwner());
			url.setResource(resource).copyPathParams(response_.getRequest());
			return to(url);
		}
		
		
		/**
		 * Builds a Url to to the Resource whose controller has the given class.
		 * Please note that a controller may be mapped to several resources. In this case
	     * it is undefined which resource is chosen to build the url.
	     * @param handlerClass a handler class
		 * @return the result
		 * @throws E in case of an error 
		 */
		public T to(Class<? extends ResourceHandler> handlerClass) throws E
		{
			Resource resource = response_.getOwner().getResource(handlerClass);
			return to(resource);
		}

		
		public T to(Url url) throws E
		{
			return end_.apply(Check.notNull(url, "url"));
		}
		
		
		protected final Response response_;
		protected final CheckedFunction<Url,T,E> end_;
	}
	
	
	/**
	 * @return a UrlBuilder which allows you to build a url backed by this response
	 */
	public default UrlBuilder<Url,RuntimeException> url()
	{
		return new UrlBuilder<>(this, url -> url);
	}
	
	
	//------------------------------
	// error
	//------------------------------

	
	/**
	 * Sends an error response to the client.
	 * This is a shortcut for {@link #sendError(int, String, Throwable) sendError(statusCode, null, null)}.
	 * @param statusCode a status code
	 * @throws IOException if an IO error occurs
	 */
	public default void sendError(int statusCode) throws IllegalStateException, IOException
	{
		sendError(statusCode, null, null);
	}

	
	/**
	 * Sends an error response to the client.
	 * After using this method, the response is committed and should not be written to.
	 * Internally the {@link ResponseHandler} implementation provided by {@link Application#createErrorHandler(int, String, Throwable)}
	 * is used to write the response.
	 * @param statusCode see {@link Status} for a list of status codes.
	 * @param message if not null, the message will be included in the error response.
	 * @param error an optional error object.
	 * @throws IllegalStateException if the response is already committed
	 * @throws IOException if an IO error occurs
	 */
	public abstract void sendError(int statusCode, String message, Throwable error) 
		throws IllegalStateException, IOException;

	
	//------------------------------
	// redirect
	//------------------------------
	
	
	/**
	 * Sends a redirect response to the client. 
	 * After using this method, the response is committed and should not be written to.
	 * @param url a URL
	 * @throws IllegalStateException if the response has already been committed 
	 * @throws IOException if an IO error occurs
	 */
	public abstract void redirect(String url) throws IOException;


	/**
	 * @return a UrlBuilder which calls {@link #redirect(String)} using the created URL. 
	 */
	public default UrlBuilder<Void,IOException> redirect()
	{
		return new UrlBuilder<>(this, url -> { redirect(url.toString()); return null; }); 
	}

	
	//------------------------------
	// write
	//------------------------------
	
	
	/**
	 * Writes JSON data to the response content.
	 * @param object a object which is converted to JSON.
	 * @throws Exception if an error occurs
	 */
	public default void writeJson(Object object) throws Exception
	{
		writeContent(object, ContentType.APPLICATION_JSON);
	}

	
	/**
	 * Writes XML data to the response content.
	 * In order to use this feature, you need to configure and add
	 * {@link JaxbXmlSerializer} or another suitable ContentSerializer to the applications 
	 * {@link AppConfig#getContentSerializers() serializers}
	 * during application setup.  
	 * @param object a object containing the data.
	 * @throws Exception if an error occurs
	 */
	public default void writeXml(Object object) throws Exception
	{
		writeContent(object, ContentType.APPLICATION_XML);
	}

	
	/**
	 * Writes text to the response content.
	 * @param text the text
	 * @throws Exception if an error occurs
	 */
	public default void writeText(String text) throws Exception
	{
		writeContent(text, ContentType.TEXT_PLAIN);
	}

	
	/**
	 * Calls write(object, null);
	 * @param object a object containing the data.
	 * @return this
	 * @throws Exception if an error occurs
	 */
	public default Response writeContent(Object object) throws Exception
	{
		return writeContent(object, (String)null);
	}

	
	/**
	 * Writes data to the response content.
	 * If the object parameter is null, the method does nothing.<br>
	 * If the object parameter is a template, then {@link Template#print(PrintWriter,Object...)} is called.<br>
	 * If the provided content-type is null, then the current content-type of the response
	 * is used. (The response content-type may have been set during content negotiation).<br>
	 * If the current content-type is null and the object is a string the content-type 
	 * is set to text/plain. Else an exception is raised.<br>
	 * The content-type of the response is then set to the calculated content-type.
	 * and the object is written to the response using a {@link ContentSerializer} for
	 * that content-type.
	 * @param object a object containing the data. If null the method does nothing.
	 * @param contentType a content type. Can be null, if the content-type was already set on
	 * 		the response
	 * @return this
	 * @throws Exception if an error occurs
	 */
	public abstract Response writeContent(Object object, String contentType) throws Exception;

	
	/**
	 * Calls write(object, contentType.getValue());
	 * @param object the content
	 * @param contentType a content type
	 * @return this
	 * @throws Exception if an error occurs
	 */
	public default Response writeContent(Object object, ContentType contentType) throws Exception
	{
		return writeContent(object, contentType != null ? contentType.getValue() : null); 
	}

	
	//-----------------------------------
	// response content
	//-----------------------------------
	
	
	/**
	 * @return how response content is produced. 
	 */
	public abstract ContentAccess getContentAccess();
	

	/**
	 * Returns a writer to write textual output. 
	 * If no content character encoding has been set,
	 * the default character encoding of the application is used.
	 * The method may not be called if {@link #getContentStream()} was called before.
	 * @see Application#getDefaultEncoding()
	 * @throws IOException if an IO error occurs
	 * @return the writer
	 */
	public abstract PrintWriter getContentWriter() throws IOException;

	
	/**
	 * Returns a OutputStream to write a binary response.
	 * The method may not be called if {@link #getContentWriter()} was called before.
	 * @return the OutputStream
	 * @throws IOException if an IO error occurs
	 */
	public abstract OutputStream getContentStream() throws IOException;

	
	/**
	 * Sets the content type of the response content. 
	 * If the content type is null or the response has been committed, it is ignored.
	 * Forwards to {@link #setContentType(String)}.
	 * @param contentType the content type
	 * @return this Response
	 */
	public default Response setContentType(ContentType contentType)
	{
		String s = contentType != null ? contentType.getValue() : null;
		return setContentType(s);
	}

	
	/**
	 * Sets the content type of the response content. 
	 * If the content type is null or the response has been committed, it is ignored.
	 * @param contentType the content type
	 * @return this response
	 */
	public abstract Response setContentType(String contentType);
	
	
	/**
	 * Returns the content type used for the response content. If the request
	 * was dispatched to a resource using content negotiation, then
	 * the content type will be initialized to that negotiated content type.
	 * Else it is initialized to null.
	 * @return the content type
	 */
	public abstract String getContentType();

	
	/**
	 * Returns the content type used for the response content as string,
	 * appended by the content encoding.
	 * @return the content type + encoding
	 * @see #getContentType()
	 * @see #getCharEncoding()
	 */
	public default String getContentTypeAndEncoding()
	{
		String contentType = getContentType();
		if (contentType != null)
		{
			String result = contentType;
			String encoding = getCharEncoding();
			if (encoding != null)
				result += "; charset=" + encoding;
			return result;
		}
		else
			return null;
	}

	
	/**
	 * Sets the character encoding.
	 * If the response has been committed or {@link #getContentWriter()} has been called, it has no effect
	 * @param encoding the encoding
	 * @return this response
	 */
	public abstract Response setCharEncoding(String encoding);
	
	
	/**
	 * Returns the character encoding of the response.
	 * The encoding can be set with a call to {@link #setCharEncoding(String)}.
	 * If getWriter() is called and no encoding is set, then the default encoding of the application 
	 * (see {@link Application#getDefaultEncoding()} is used.
	 * @return the encoding
	 */
	public abstract String getCharEncoding();
	

	/**
	 * Sets the content length.
	 * @param length the length
	 * @return this response
	 */
	public abstract Response setContentLength(long length);
	
	
	/**
	 * Sets the content language.
	 * @param locale the locale of the language
	 * @return this response
	 */
	public abstract Response setContentLanguage(Locale locale);


	/**
	 * Returns the content language.
	 * @return the locale defining the content language, or null if not 
	 * 		set. When a Writer is requested, and the content language is null,
	 * 		then it is set to locale of the response's lcaleData.
	 * @see #getLocaleService()
	 */
	public abstract Locale getContentLanguage();

	
	/**
	 * Close the content output, either the {@link #getContentStream() OutputStream}
	 * or {@link #getContentWriter() PrintWriter}.
	 * This method is called automatically at the end of request {@link Application#process(Request,Response) processing}.
	 */
	public abstract void closeContent();
	

	/**
	 * Returns a InterceptorBuilder to adds a ResponseInterceptor
	 * @return the builder
	 */
	public abstract InterceptorBuilder addInterceptor();
	
	
	public interface InterceptorBuilder
	{
		/**
		 * Adds a ResponseInterceptor which can wrap the response {@link Response#getContentStream() OutputStream}.
		 * {@link Response#getContentStream()} or {@link Response#getContentWriter()} must not have been called yet.
		 * @param interceptor an interceptor
		 */
		public void forStream(ResponseInterceptor<OutputStream> interceptor);

	
		/**
		 * Adds a ResponseInterceptor which can wrap the request {@link Response#getContentWriter() Writer}.
		 * {@link Response#getContentStream()} or {@link Response#getContentWriter()} must not have been called yet.
		 * @param interceptor an interceptor
		 */
		public void forWriter(ResponseInterceptor<Writer> interceptor);
	}

	
	//-----------------------------------
	// buffer
	//-----------------------------------
	
	
	/**
	 * Resets the output buffer, discarding any buffered content.
	 * @return this 
	 */
	public Response resetBuffer();


	/**
	 * Flushes any buffered content.
	 * @return this
	 * @throws IOException if an IO error occurs
	 */
	public Response flushBuffer() throws IOException;
	
	
	/**
	 * Sets the buffer size.
	 * @param size the size 
	 * @return this response
	 */
	public Response setBufferSize(int size);
	

	/**
	 * @return the buffer size.
	 */
	public int getBufferSize();

	
	//-----------------------------------
	// response headers
	//-----------------------------------
	
	
	/**
	 * Returns the ResponseHeaders object which allows to add or set headers.
	 * @return the headers
	 */
	public abstract ResponseHeaders getHeaders(); 


	//----------------------------
	// attributes
	//----------------------------
	
	
	/**
	 * Returns an attribute which was previously associated with the response.
	 * @param name the attribute name
	 * @return the attribute 
	 * @see #setAttribute(String, Object)
	 */
	public Object getAttribute(String name);

	
	/**
	 * @return an iterator for the attribute names stored in the response.
	 */
	public Iterator<String> getAttributeNames();

	
	/**
	 * Stores an attribute under the given name in the response. 
	 * @param name the name
	 * @param value the value
	 */
	public void setAttribute(String name, Object value);

	
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
	 * @return if this request has been put into asynchronous mode by a call to {@link #startAsync()}.
	 */
	public boolean isAsyncStarted();
	
	
	/**
 	 * @return if this resonse supports asynchronous mode. 
	 */
	public boolean isAsyncSupported();
	
	
	/**
	 * Puts this request into asynchronous mode, and initializes its AsyncContext.
	 * @return the AsyncContext 
	 * @throws IllegalStateException if this request does not support asynchronous operations or if called again
	 * 		in a state where the AsyncContext intervenes, or when the response has been closed.
	 */
	public AsyncContext startAsync();

	
	//-----------------------------------
	// misc
	//-----------------------------------


	/**
	 * Prints response info to the PrintStream.
	 * @param out a PrintStream
	 */
	public default void print(PrintStream out)
	{
		Check.notNull(out, "out");
		print(new PrintWriter(out, true));
	}

	
	/**
	 * Prints response info to the PrintWriter.
	 * @param out a PrintWriter
	 */
	public default void print(PrintWriter out)
	{
		Check.notNull(out, "out");
		out.println(getStatus());
		ResponseHeaders headers = getHeaders(); 
		for (String name : headers)
		{
			for (String value : headers.getAll(name))
			{
				out.print(name);
				out.print(' ');
				out.println(value);
			}
		}
	}
	
	
	/**
	 * Returns the underlying implementation of the response which has the given class
	 * or null, if the implementation has a different class.
	 * In an ServletEnvironment the underlying implementation is a HttpServletResponse.
	 * @param implClass the implementation class
	 * @param <T> the class type
	 * @return the result object
	 */
	public abstract <T> T unwrap(Class<T> implClass);
}
