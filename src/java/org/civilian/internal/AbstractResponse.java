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
package org.civilian.internal;


import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import org.civilian.Application;
import org.civilian.Context;
import org.civilian.Controller;
import org.civilian.Request;
import org.civilian.Resource;
import org.civilian.Response;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.internal.intercept.InterceptedOutput;
import org.civilian.internal.intercept.InterceptedOutputStream;
import org.civilian.internal.intercept.InterceptedTemplateWriter;
import org.civilian.internal.intercept.RespStreamInterceptorChain;
import org.civilian.internal.intercept.RespWriterInterceptorChain;
import org.civilian.resource.Url;
import org.civilian.response.ResponseHeaders;
import org.civilian.response.ResponseStreamInterceptor;
import org.civilian.response.ResponseWriterInterceptor;
import org.civilian.response.UriEncoder;
import org.civilian.template.Template;
import org.civilian.template.TemplateWriter;
import org.civilian.text.LocaleService;
import org.civilian.type.lib.LocaleSerializer;
import org.civilian.util.Check;


/**
 * AbstractResponse is a partial Response implementation
 * with useful defaults.
 */
public abstract class AbstractResponse implements Response
{
	/**
	 * Creates a new AbstractResponse.
	 * @param request the associated request.
	 */
	protected AbstractResponse(Request request)
	{
		request_ = Check.notNull(request, "request");
	}


	/**
	 * Creates a new AbstractResponse.
	 * @param request the associated request.
	 */
	protected AbstractResponse(AbstractRequest request)
	{
		request_ = Check.notNull(request, "request");
		request.setResponse(this);
	}

	
	/**
	 * Implements ResponseProvider and returns this.
	 */
	@Override public Response getResponse()
	{
		return this;
	}

	
	@Override public Request getRequest()
	{
		return request_;
	}

	
	@Override public Application getApplication()
	{
		return request_.getApplication();
	}
	

	@Override public Context getContext()
	{
		return getApplication().getContext();
	}

	
	/**
	 * Throws an IllegalStateException if the response is committed.
	 */
	protected void checkNotCommitted()
	{
		if (isCommitted())
			throw new IllegalStateException("response is committed");
	}

 
	@Override public UriEncoder getUriEncoder()
	{
		if (uriEncoder_ == null)
			uriEncoder_ = new UriEncoder(); 
		return uriEncoder_;
	}

	
	@Override public LocaleService getLocaleService()
	{
		return localeService_ != null ? localeService_ : request_.getLocaleService();
	}

	
	@Override public void setLocaleService(LocaleService localeService)
	{
		localeService_ = Check.notNull(localeService, "localeService");
	}

	
	@Override public void setLocaleService(Locale locale)
	{
		localeService_ = getApplication().getLocaleServices().getService(locale);
	}
	

	@Override public LocaleSerializer getLocaleSerializer()
	{
		return getLocaleService().getSerializer();
	}

	
	@Override public void sendError(int statusCode) throws IOException
	{
		sendError(statusCode, null, null);
	}

	
	@Override public void sendError(int statusCode, String message, Throwable error) throws IOException
	{
		checkNotCommitted();
		// do not allow repeated or reentrant calls of sendError
		if (type_ == Type.ERROR)
			throw new IllegalStateException("sendError already called");
		setType(Type.ERROR, false);
		
		try
		{
			getApplication().createErrorResponse().send(this, statusCode, message, error);
		}
		catch(Exception e)
		{
			Logs.RESPONSE.error("error during sendError", e);
		}
		finally
		{
			flushBuffer(); // commits the response
		}
	}
	
	
	/**
	 * Sends a redirect response to the client. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	@Override public void sendRedirect(String url) throws IOException
	{
		checkNotCommitted();
		setType(Type.REDIRECT, false);
		sendRedirectImpl(url);
	}

	
	/**
	 * Sends a redirect response to the client. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	@Override public void sendRedirect(Url url) throws IOException
	{
		sendRedirect(url.toString());
	}

	
	/**
	 * Sends a redirect response to the client. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	@Override public <C extends Controller> void sendRedirect(Class<C> controllerClass) throws IOException
	{
		sendRedirect(new Url(this, controllerClass));
	}

	
	/**
	 * Sends a redirect response to the client. 
	 * After using this method, the response is committed and should not be written to.
	 * @throws IllegalStateException if the response has already been committed 
	 */
	@Override public void sendRedirect(Resource resource) throws IOException
	{
		sendRedirect(new Url(this, resource));
	}
	

	/**
	 * Implements sendRedirect. Should commit the response.
	 */
	protected abstract void sendRedirectImpl(String url) throws IOException;


	/**
	 * Prints a template.
	 */
	@Override public void writeTemplate(Template template) throws Exception
	{
		if (template != null)
			template.print(getContentWriter());	
	}

	
	/**
	 * Prints JSON data to the response content.
	 * @param object a object which is converted to JSON.
	 */
	@Override public void writeJson(Object object) throws Exception
	{
		writeContent(object, ContentType.APPLICATION_JSON);
	}
	
	
	@Override public void writeXml(Object object) throws Exception
	{
		writeContent(object, ContentType.APPLICATION_XML);
	}

	
	@Override public void writeText(String text) throws Exception
	{
		writeContent(text, ContentType.TEXT_PLAIN);
	}
	

	@Override public void writeContent(Object object) throws Exception
	{
		writeContent(object, null);
	}

	
	@Override public void writeContent(Object object, ContentType contentType) throws Exception
	{
		if (object == null)
			return;
		
		if (object instanceof Template)
		{
			if (contentType != null)
				setContentType(contentType);
			writeTemplate((Template)object);
			return;
		}
		
		if (contentType == null) 
		{
			contentType = getContentType();
			if (contentType == null)
			{
				if (object instanceof String)
					setContentType(contentType = ContentType.TEXT_PLAIN);
				else
					throw new IllegalStateException("no content-type set");
			}
		}
		else
			setContentType(contentType);
		
		ContentSerializer serializer = getApplication().getContentSerializer(contentType);
		if (serializer != null)
			serializer.write(object, getContentWriter());
		else if (object instanceof String)
			getContentWriter().write((String)object);
		else
			throw new IllegalStateException("don't know how to write a " + object.getClass().getName() + " with content type '" + contentType + "'");
	}

	
	@Override public ContentAccess getContentAccess()
	{
		if (contentOutput_ == null)
			return ContentAccess.NONE;
		else if (contentOutput_ instanceof Writer)
			return ContentAccess.WRITER;
		else
			return ContentAccess.OUTPUTSTREAM;
	}
	
	
	@Override public void closeContent()
	{
		if (contentOutput_ != null)
		{
			try
			{
				((Closeable)contentOutput_).close();
			} 
			catch (IOException e)
			{
				Logs.RESPONSE.error("error when closing " + contentOutput_.getClass().getName(), e);
			}
		}
	}


	@Override public TemplateWriter getContentWriter() throws IOException
	{
		if (!(contentOutput_ instanceof Writer))
			initContentOutput(true /*we want a writer*/);
		return (TemplateWriter)contentOutput_;
	}
	
	
	@Override public OutputStream getContentStream() throws IOException
	{
		if (!(contentOutput_ instanceof OutputStream))
			initContentOutput(false /*we want a stream*/);
		return (OutputStream)contentOutput_;
	}
	
	
	private void initContentOutput(boolean createWriter) throws IOException
	{
		// contentOutput_ should be null
		checkNoContentOutput();
		
		// prepare the actual stream interceptors
		ResponseStreamInterceptor streamInterceptor = streamInterceptor_ != null ?
			streamInterceptor_.prepareStreamIntercept(this) :
			null;
			
		if (createWriter)
			initContentWriter(streamInterceptor);
		else
			initContentStream(streamInterceptor);
	}
	
	
	private void initContentStream(ResponseStreamInterceptor streamInterceptor) throws IOException
	{
		OutputStream originalStream = getContentStreamImpl();
		contentOutput_ = originalStream;
		
		if (streamInterceptor != null) 
			contentOutput_ = new InterceptedOutputStream(originalStream, streamInterceptor);
	}
	
		
	private void initContentWriter(ResponseStreamInterceptor streamInterceptor) throws IOException
	{
		// prepare the actual writer interceptors
		ResponseWriterInterceptor writerInterceptor = (writerInterceptor_ != null) ?
			writerInterceptor_.prepareWriterIntercept(this) :
			null;
			
		// make sure that encoding is initialized, fallback to application encoding
		if (contentEncoding_ == null)
			setContentEncoding(getApplication().getEncoding());

		try
		{
			if ((streamInterceptor != null) || !initContentWriterNoStream(writerInterceptor))
				initContentWriterWithStream(streamInterceptor, writerInterceptor);
		}
		finally
		{
			if (contentOutput_ != null)
			{
				if (!(contentOutput_ instanceof TemplateWriter))
					initContentWriterForError();
				TemplateWriter tw = (TemplateWriter)contentOutput_; 
				tw.addContext(this);
			}
		}
	}

	
	/**
	 * Creates a TemplateWriter from a implementation writer.
	 * @param writerInterceptor a writer interceptor, can be null
	 * @return writer successful created? 
	 */
	private boolean initContentWriterNoStream(ResponseWriterInterceptor writerInterceptor) throws IOException
	{
		Writer originalWriter = getContentWriterImpl();
		if (originalWriter == null)
			return false;
		else
		{
			contentOutput_ = originalWriter;
			contentOutput_ = (writerInterceptor_ != null) ?
				new InterceptedTemplateWriter(originalWriter, writerInterceptor) :
				new TemplateWriter(originalWriter);
			return true;
		}
	}
	
		
	/**
	 * Creates a TemplateWriter from a implementation OutputStream.
	 * @param streamInterceptor a stream interceptor, can be null
	 * @param writerInterceptor a writer interceptor, can be null
	 */
	private void initContentWriterWithStream(ResponseStreamInterceptor streamInterceptor,
		ResponseWriterInterceptor writerInterceptor) throws IOException
	{
		OutputStream originalStream = getContentStreamImpl();
		contentOutput_ = originalStream;
		
		if ((streamInterceptor != null) || (writerInterceptor != null))
			contentOutput_ = new InterceptedTemplateWriter(originalStream, streamInterceptor, writerInterceptor, contentEncoding_);
		else
			contentOutput_ = new TemplateWriter(new OutputStreamWriter(originalStream, contentEncoding_));
	}
		

	/**
	 * Reinit the response writer if an exception was thrown during regular init.  
	 */
	private void initContentWriterForError() throws IOException
	{
		if (contentOutput_ instanceof OutputStream)
			contentOutput_ = new OutputStreamWriter((OutputStream)contentOutput_, getContentEncoding());
		contentOutput_ = new TemplateWriter((Writer)contentOutput_);
	}
	
	
	private void checkNoContentOutput()
	{
		if (contentOutput_ instanceof OutputStream)
			throw new IllegalStateException("Response.getContentStream() has already been called");
		if (contentOutput_ instanceof Writer)
			throw new IllegalStateException("Response.getContentWriter() has already been called");
	}


	/**
	 * Provides an OutputStream to write binary Response content.
	 * In a servlet environment this returns HttpServletResponse#getOutputStream().
	 */
	protected abstract OutputStream getContentStreamImpl() throws IOException;
	
	
	/**
	 * Provides a Writer to write text Response content.
	 * In a servlet environment this returns HttpServletResponse#getWriter().
	 * If an implementation does not have an own writer implementation
	 * (but only OutputStreams, it should return null).
	 */
	protected abstract Writer getContentWriterImpl() throws IOException;
	
	
	@Override public void setContentLanguage(Locale locale)
	{
		contentLanguage_ = Check.notNull(locale, "locale");
	}


	@Override public Locale getContentLanguage()
	{
		return contentLanguage_;
	}
	
	
	@Override public String getContentTypeAndEncoding()
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
	

	@Override public void setContentEncoding(String encoding)
	{
		if ((contentOutput_ == null) && !isCommitted()) 
		{
			contentEncoding_ = encoding;
			setContentEncodingImpl(encoding);
		}
	}
	
	
	/**
	 * Implements setContentEncoding().
	 */
	protected abstract void setContentEncodingImpl(String encoding);

	
	@Override public String getContentEncoding()
	{
		return contentEncoding_;
	}

	
	@Override public void addInterceptor(ResponseStreamInterceptor interceptor)
	{
		Check.notNull(interceptor, "interceptor");
		checkNoContentOutput();
		
		streamInterceptor_ = streamInterceptor_ != null ?
			new RespStreamInterceptorChain(streamInterceptor_, interceptor) :
			interceptor;
	}
	
	
	@Override public void addInterceptor(ResponseWriterInterceptor interceptor)
	{
		Check.notNull(interceptor, "interceptor");
		checkNoContentOutput();
		
		writerInterceptor_ = writerInterceptor_ != null ?
			new RespWriterInterceptorChain(writerInterceptor_, interceptor) :
			interceptor;
	}

	
	
	
	
	//--------------------
	// type
	//--------------------
	
	
	/**
	 * Allow implementations to set the response type.
	 */
	private void setType(Type type, boolean force)
	{
		if ((type_ == Type.NORMAL) || force)
			type_ = type;
		else if (type_ != type) 
			throw new IllegalStateException("response type already set to " + type_);
	}
	
	
	@Override public Type getType()
	{
		return type_;
	}
	
	
	//--------------------
	// buffer
	//--------------------
	
	
	@Override public void flushBuffer() throws IOException
	{
		flushBuffer(contentOutput_);
	}

	
	protected abstract void flushBuffer(Flushable flushable) throws IOException;

	
	@Override public void resetBuffer()
	{
		resetBufferImpl();
		if (contentOutput_ instanceof InterceptedOutput)
			((InterceptedOutput)contentOutput_).reset();
	}
	
	
	protected abstract void resetBufferImpl();

	
	/**
	 * Sets all fields of the AbstractRequest to its initial state.
	 */
	protected void clear()
	{
		type_ 				= Type.NORMAL;
		localeService_ 		= null;
		contentLanguage_	= null;
		contentEncoding_	= null;
		contentOutput_		= null;
		streamInterceptor_	= null;
		writerInterceptor_	= null;
	}


	//--------------------
	// print
	//--------------------
	
	
	@Override public void print(PrintStream out)
	{
		Check.notNull(out, "out");
		print(new PrintWriter(out, true));
	}

	
	@Override public void print(PrintWriter out)
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


	private Request request_;
	private UriEncoder uriEncoder_;
	private LocaleService localeService_;
	private Flushable contentOutput_;
	// we duplicate the encoding since we want to know if an encoding was explicitly set
	// (the servlet response returns ISO-8859-1 if no encoding was set).
	private String contentEncoding_;
	private Locale contentLanguage_;
	private Type type_ = Type.NORMAL;
	private ResponseStreamInterceptor streamInterceptor_;
	private ResponseWriterInterceptor writerInterceptor_;
}
