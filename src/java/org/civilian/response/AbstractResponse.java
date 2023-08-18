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


import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;

import org.civilian.Logs;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.request.AbstractRequest;
import org.civilian.request.Request;
import org.civilian.template.Template;
import org.civilian.template.TemplateWriter;
import org.civilian.text.service.LocaleService;
import org.civilian.util.Check;
import org.civilian.util.UriEncoder;


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

	
	@Override public Request getRequest()
	{
		return request_;
	}

	
	@Override public ResponseOwner getOwner()
	{
		return Check.isA(request_.getOwner(), ResponseOwner.class); // TODO
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

	
	@Override public Response setLocaleService(LocaleService localeService)
	{
		localeService_ = Check.notNull(localeService, "localeService");
		return this;
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
			getOwner().createErrorHandler(statusCode, message, error).send(this);
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
	 * Implements sendRedirect. Should commit the response.
	 */
	protected abstract void sendRedirectImpl(String url) throws IOException;


	@Override public Response writeContent(Object object, String contentType) throws Exception
	{
		if (object == null)
			return this;
		
		if (object instanceof Template)
		{
			if (contentType != null)
				setContentType(contentType);
			((Template)object).print(getContentWriter());	
			return this;
		}
		
		if (contentType == null) 
		{
			contentType = getContentType();
			if (contentType == null)
			{
				if (object instanceof String)
					setContentType(contentType = ContentType.TEXT_PLAIN.getValue());
				else
					throw new IllegalStateException("no content-type set");
			}
		}
		else
			setContentType(contentType);
		
		ContentSerializer serializer = getOwner().getContentSerializer(contentType);
		if (serializer != null)
			serializer.write(object, getContentWriter());
		else if (object instanceof String)
			getContentWriter().write((String)object);
		else
			throw new IllegalStateException("no ContentSerializer for content type '" + contentType + "' to write a '" + object.getClass().getName() + "'. Are third party libraries missing?");
		
		return this;
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
		if (charEncoding_ == null)
			setCharEncoding(getOwner().getDefaultCharEncoding());

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
				tw.addAttribute(this);
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
			contentOutput_ = new InterceptedTemplateWriter(originalStream, streamInterceptor, writerInterceptor, charEncoding_);
		else
			contentOutput_ = new TemplateWriter(new OutputStreamWriter(originalStream, charEncoding_));
	}
		

	/**
	 * Reinit the response writer if an exception was thrown during regular init.  
	 */
	private void initContentWriterForError() throws IOException
	{
		if (contentOutput_ instanceof OutputStream)
			contentOutput_ = new OutputStreamWriter((OutputStream)contentOutput_, getCharEncoding());
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
	
	
	@Override public Response setContentLanguage(Locale locale)
	{
		contentLanguage_ = Check.notNull(locale, "locale");
		return this;
	}


	@Override public Locale getContentLanguage()
	{
		return contentLanguage_;
	}
	
	
	@Override public Response setCharEncoding(String encoding)
	{
		if ((contentOutput_ == null) && !isCommitted()) 
		{
			charEncoding_ = encoding;
			setCharEncodingImpl(encoding);
		}
		return this;
	}
	
	
	/**
	 * Implements setCharEncoding().
	 */
	protected abstract void setCharEncodingImpl(String encoding);

	
	@Override public String getCharEncoding()
	{
		return charEncoding_;
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
	
	
	@Override public Response flushBuffer() throws IOException
	{
		flushBuffer(contentOutput_);
		return this;
	}

	
	protected abstract void flushBuffer(Flushable flushable) throws IOException;

	
	@Override public Response resetBuffer()
	{
		resetBufferImpl();
		if (contentOutput_ instanceof InterceptedOutput)
			((InterceptedOutput)contentOutput_).reset();
		return this;
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
		charEncoding_		= null;
		contentOutput_		= null;
		streamInterceptor_	= null;
		writerInterceptor_	= null;
	}


	private Request request_;
	private UriEncoder uriEncoder_;
	private LocaleService localeService_;
	private Flushable contentOutput_;
	// we duplicate the encoding since we want to know if an encoding was explicitly set
	// (the servlet response returns ISO-8859-1 if no encoding was set).
	private String charEncoding_;
	private Locale contentLanguage_;
	private Type type_ = Type.NORMAL;
	private ResponseStreamInterceptor streamInterceptor_;
	private ResponseWriterInterceptor writerInterceptor_;
}
