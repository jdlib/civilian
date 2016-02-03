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
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.civilian.application.AppConfig;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.JaxbXmlSerializer;
import org.civilian.provider.ApplicationProvider;
import org.civilian.provider.ContextProvider;
import org.civilian.provider.LocaleServiceProvider;
import org.civilian.provider.RequestProvider;
import org.civilian.provider.ResponseProvider;
import org.civilian.resource.Url;
import org.civilian.response.ResponseHeaders;
import org.civilian.response.ResponseStreamInterceptor;
import org.civilian.response.ResponseWriterInterceptor;
import org.civilian.response.UriEncoder;
import org.civilian.response.std.ErrorResponse;
import org.civilian.template.Template;
import org.civilian.template.TemplateWriter;
import org.civilian.text.LocaleService;
import org.civilian.type.lib.LocaleSerializer;
import org.civilian.util.Check;


/**
 * Response represents a response to a request.
 * It consists of a {@link Status}, {@link ResponseHeaders headers} and content, produced
 * either by a {@link #getContentWriter() Writer} or a {@link #getContentStream() OutputStream}.<br> 
 * In a Servlet environment Response is functionally equivalent to a HttpServletResponse.<p>
 */
public interface Response extends RequestProvider, ResponseProvider, ApplicationProvider, 
	ContextProvider, LocaleServiceProvider
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
		 * A TemplateWriter is used to produce the response body.
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
	 * Returns the request.
	 */
	@Override public abstract Request getRequest();

	
	/**
	 * Implements ResponseProvider and returns this.
	 */
	@Override default public Response getResponse()
	{
		return this;
	}

	
	/**
	 * Returns the context.
	 */
	@Override default public Context getContext()
	{
		return getApplication().getContext();
	}

	
	/**
	 * Returns the application.
	 */
	@Override default public Application getApplication()
	{
		return getRequest().getApplication();
	}
	

	/**
	 * Returns if the response is committed. A response is committed
	 * if status and headers are already written. 
	 * If not committed then a call to {@link #reset()} or {@link Response#resetBuffer()}
	 * is still possible.
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
	 */
	public abstract Type getType();

	
	/**
	 * Adds a cookie to the response.
	 */
	public abstract void addCookie(Cookie cookie); 


	/**
	 * Adds the session id to the URL and returns the new URL.
	 * The {@link Url} class calls this method automatically if you use it to build a URL string.
	 * In Servlet terms this method corresponds to HttpServletRequest#encodeURL.  
	 * @return the URL with session id included or the original URL if the session id does not 
	 * need to be included in the url.
	 */
	public abstract String addSessionId(String url);
	

	/**
	 * Returns a UriEncoder object which can be used to percent encode all characters of URLs or URIs which
	 * are reserved characters and may not be used in a URL.
	 */
	public abstract UriEncoder getUriEncoder();

	
	//------------------------------
	// locale
	//------------------------------
	
	
	/**
	 * Returns the locale data associated with the response.
	 * The locale data can be set explicitly by {@link #setLocaleService(LocaleService)}.
	 * If not explicitly set it is the same as the LocalData of the request.
	 * @see Request#getLocaleService()
	 */
	@Override public abstract LocaleService getLocaleService();
	

	/**
	 * Sets the locale data associated with the response.
	 */
	public abstract void setLocaleService(LocaleService service);

	
	/**
	 * Sets the locale data associated with the response.
	 */
	default public void setLocaleService(Locale locale)
	{
		setLocaleService(getApplication().getLocaleServices().getService(locale));
	}
	

	/**
	 * Shortcut for {@link #getLocaleService()}.getSerializer().
	 */
	default public LocaleSerializer getLocaleSerializer()
	{
		return getLocaleService().getSerializer();
	}

	
	//------------------------------
	// status
	//------------------------------
	
	
	/**
	 * Returns the status code of the response.
	 */
	public abstract int getStatus();

	
	/**
	 * Sets the status code of the response.
	 * @param statusCode the code. See {@link Status} for a list of common status codes.
	 */
	public abstract void setStatus(int statusCode);
	
	
	//------------------------------
	// error
	//------------------------------

	
	/**
	 * Sends an error response to the client.
	 * This is a shortcut for {@link #sendError(int, String, Throwable) sendError(statusCode, null, null)}.
	 */
	default public void sendError(int statusCode) throws IllegalStateException, IOException
	{
		sendError(statusCode, null, null);
	}

	
	/**
	 * Sends an error response to the client.
	 * After using this method, the response is committed and should not be written to.
	 * Internally the {@link ErrorResponse} implementation provided by {@link Application#createErrorResponse()}
	 * is used to write the response.
	 * @param statusCode see {@link Status} for a list of status codes.
	 * @param message if not null, the message will be included in the error response.
	 * @param error an optional error object.
	 * @throws IllegalStateException if the response is already committed
	 */
	public abstract void sendError(int statusCode, String message, Throwable error) 
		throws IllegalStateException, IOException;

	
	//------------------------------
	// sendRedirect
	//------------------------------
	
	
	/**
	 * Sends a redirect response to the client. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	public abstract void sendRedirect(String url) throws IOException;

	
	/**
	 * Sends a redirect response. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	default public void sendRedirect(Url url) throws IOException
	{
		sendRedirect(url.toString());
	}


	/**
	 * Sends a redirect response to the given Resource. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	default public void sendRedirect(Resource resource) throws IOException
	{
		sendRedirect(new Url(this, resource));
	}


	/**
	 * Sends a redirect response to the Resource whose controller has the given
	 * class. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	default public <C extends Controller> void sendRedirect(Class<C> controllerClass) throws IOException
	{
		sendRedirect(new Url(this, controllerClass));
	}

	
	//------------------------------
	// write
	//------------------------------
	
	
	/**
	 * Writes a template to the response content. 
	 * @param template a template object. If null, the method does nothing. Else
	 * 		it calls {@link Template#print(TemplateWriter)}, passing the 
	 * 		{@link #getContentWriter() content writer} of this response.
	 */
	default public void writeTemplate(Template template) throws Exception
	{
		writeContent(template, null);
	}
	
	
	/**
	 * Writes JSON data to the response content.
	 * @param object a object which is converted to JSON.
	 */
	default public void writeJson(Object object) throws Exception
	{
		writeContent(object, ContentType.APPLICATION_JSON);
	}

	
	/**
	 * Writes XML data to the response content.
	 * In order to use this feature, you need to configure and add
	 * {@link JaxbXmlSerializer} or another suitable ContentSerializer to the applications 
	 * {@link AppConfig#getContentSerializers() serializers}
	 * during application setup.  
	 */
	default public void writeXml(Object object) throws Exception
	{
		writeContent(object, ContentType.APPLICATION_XML);
	}

	
	/**
	 * Writes text to the response content.
	 */
	default public void writeText(String text) throws Exception
	{
		writeContent(text, ContentType.TEXT_PLAIN);
	}

	
	/**
	 * Calls write(object, null);
	 */
	default public void writeContent(Object object) throws Exception
	{
		writeContent(object, null);
	}

	
	/**
	 * Writes data to the response content.
	 * If the object parameter is null, the method does nothing.<br>
	 * If the object parameter is a template, then {@link #writeTemplate(Template)} is called.<br>
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
	 */
	public abstract void writeContent(Object object, ContentType contentType) throws Exception;

	
	//-----------------------------------
	// response content
	//-----------------------------------
	
	
	/**
	 * Returns how response content is produced. 
	 */
	public abstract ContentAccess getContentAccess();
	

	/**
	 * Returns a writer to write textual output. 
	 * If no content character encoding has been set,
	 * the encoding of the application is used.
	 * The method may not be called if {@link #getContentStream()} was called before.
	 * @see Application#getEncoding()
	 */
	public abstract TemplateWriter getContentWriter() throws IOException;

	
	/**
	 * Returns a OutputStream to write a binary response.
	 * The method may not be called if {@link #getContentWriter()} was called before.
	 */
	public abstract OutputStream getContentStream() throws IOException;

	
	/**
	 * Sets the content type of the response content. 
	 * If the content type is null or the response has been committed, it is ignored.
	 */
	public abstract void setContentType(ContentType contentType);
	
	
	/**
	 * Returns the content type used for the response content. If the request
	 * was dispatched to a resource using content negotiation, then
	 * the content type will be initialized to that negotiated content type.
	 * Else it is initialized to null.
	 */
	public abstract ContentType getContentType();

	
	/**
	 * Returns the content type used for the response content as string,
	 * appended by the content encoding.
	 * @see #getContentType()
	 * @see #getContentEncoding()
	 */
	default public String getContentTypeAndEncoding()
	{
		ContentType contentType = getContentType();
		if (contentType != null)
		{
			String result = contentType.getValue();
			String encoding = getContentEncoding();
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
	 */
	public abstract void setContentEncoding(String encoding);
	
	
	/**
	 * Returns the character encoding of the response.
	 * The encoding can be set with a call to {@link #setContentEncoding(String)}.
	 * If getWriter() is called and no encoding is set, then the default encoding of the application 
	 * (see {@link Application#getEncoding()} is used.
	 */
	public abstract String getContentEncoding();
	

	/**
	 * Sets the content length.
	 */
	public abstract void setContentLength(long length);
	
	
	/**
	 * Sets the content language.
	 */
	public abstract void setContentLanguage(Locale locale);


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
	 * or {@link #getContentWriter() TemplateWriter}.
	 * This method is called automatically at the end of request {@link Application#process(Request) processing}.
	 */
	public abstract void closeContent();
	

	/**
	 * Adds a ResponseInterceptor which can wrap the Response OutputStream.
	 * {@link #getContentStream()} or {@link #getContentWriter()} must not have been called yet.
	 */
	public abstract void addInterceptor(ResponseStreamInterceptor interceptor);


	/**
	 * Adds a ResponseWriterInterceptor which can wrap the Response Writer.
	 * {@link #getContentStream()} or {@link #getContentWriter()} must not have been called yet.
	 */
	public abstract void addInterceptor(ResponseWriterInterceptor interceptor);
	
	
	//-----------------------------------
	// buffer
	//-----------------------------------
	
	
	/**
	 * Resets the output buffer, discarding any buffered content. 
	 */
	public void resetBuffer();


	/**
	 * Flushes any buffered content.
	 */
	public void flushBuffer() throws IOException;
	
	
	/**
	 * Sets the buffer size. 
	 */
	public void setBufferSize(int size);
	

	/**
	 * Returns the buffer size. 
	 */
	public int getBufferSize();

	
	//-----------------------------------
	// response headers
	//-----------------------------------
	
	
	/**
	 * Returns the ResponseHeaders object which allows to add or set headers.
	 */
	public abstract ResponseHeaders getHeaders(); 


	//-----------------------------------
	// misc
	//-----------------------------------


	/**
	 * Prints response info to the PrintStream.
	 */
	default public void print(PrintStream out)
	{
		Check.notNull(out, "out");
		print(new PrintWriter(out, true));
	}

	
	/**
	 * Prints response info to the PrintWriter.
	 */
	default public void print(PrintWriter out)
	{
		Check.notNull(out, "out");
		out.println(getStatus());
		ResponseHeaders headers = getHeaders(); 
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
	 * Returns the underlying implementation of the response which has the given class
	 * or null, if the implementation has a different class.
	 * In an ServletEnvironment the underlying implementation is a HttpServletResponse.
	 */
	public abstract <T> T unwrap(Class<T> implClass);
}
