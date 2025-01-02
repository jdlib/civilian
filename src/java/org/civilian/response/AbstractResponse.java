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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.civilian.Logs;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.request.Request;
import org.civilian.template.Template;
import org.civilian.text.service.LocaleService;
import org.civilian.util.Check;
import org.civilian.util.Iterators;


/**
 * AbstractResponse is a partial Response implementation
 * with useful defaults.
 */
public abstract class AbstractResponse implements Response
{
	/**
	 * Creates a new AbstractResponse.
	 * @param owner the owner
	 * @param request the associated request.
	 */
	protected AbstractResponse(ResponseOwner owner, Request request)
	{
		owner_	 = Check.notNull(owner, "owner");
		request_ = Check.notNull(request, "request");
	}

	
	@Override public Request getRequest()
	{
		return request_;
	}

	
	@Override public ResponseOwner getOwner()
	{
		return owner_;
	}

	
	/**
	 * Throws an IllegalStateException if the response is committed.
	 */
	protected void checkNotCommitted()
	{
		if (isCommitted())
			throw new IllegalStateException("response is committed");
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
	 * @throws IOException if an IO error occurs
	 */
	@Override public void redirect(String url) throws IOException
	{
		checkNotCommitted();
		setType(Type.REDIRECT, false);
		redirectImpl(url);
	}

	
	/**
	 * Implements redirect. Should commit the response.
	 * @param url an url
	 * @throws IOException if an IO error occurs
	 */
	protected abstract void redirectImpl(String url) throws IOException;


	@SuppressWarnings("resource")
	@Override public Response writeContent(Object object, String contentType) throws Exception
	{
		if (object == null)
			return this;
		if (object instanceof Template)
		{
			writeTemplate((Template)object, contentType);
			return this;
		}
		
		contentType = initWriteContentType(object, contentType);
		
		ContentSerializer serializer = getOwner().getContentSerializers().get(contentType);
		if (serializer != null)
			serializer.write(object, getContentWriter());
		else if (object instanceof String)
			getContentWriter().write((String)object);
		else
			throw new IllegalStateException("no ContentSerializer for content type '" + contentType + "' to write a '" + object.getClass().getName() + "'. Are third party libraries missing?");
		
		return this;
	}
	
	
	private void writeTemplate(Template template, String contentType) throws Exception
	{
		if (contentType != null)
			setContentType(contentType);
		template.print(getContentWriter(), this); // we provide the response as context data
	}
	
	
	private String initWriteContentType(Object object, String contentType)
	{
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
		return contentType;
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


	@Override public PrintWriter getContentWriter() throws IOException
	{
		if (!(contentOutput_ instanceof Writer))
			initContentOutput(true /*we want a writer*/);
		return (PrintWriter)contentOutput_;
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
		ResponseInterceptor<OutputStream> interceptor = readExt().streamInterceptor; 
		if (interceptor != null)
			interceptor = interceptor.prepareIntercept(this);
			
		if (createWriter)
			initContentWriter(interceptor);
		else
			initContentStream(interceptor);
	}
	
	
	private void initContentStream(ResponseInterceptor<OutputStream> interceptor) throws IOException
	{
		OutputStream originalStream = getContentStreamImpl();
		contentOutput_ = originalStream;
		
		if (interceptor != null) 
			contentOutput_ = new InterceptedOutputStream(originalStream, interceptor);
	}
	
		
	private void initContentWriter(ResponseInterceptor<OutputStream> streamInterceptor) throws IOException
	{
		// prepare the actual writer interceptors
		ResponseInterceptor<Writer> writerInterceptor = readExt().writerInterceptor; 
		if (writerInterceptor != null)
			writerInterceptor = writerInterceptor.prepareIntercept(this);
			
		// make sure that encoding is initialized, fallback to application encoding
		if (charEncoding_ == null)
			setCharEncoding(getOwner().getDefaultEncoding().name());

		try
		{
			if ((streamInterceptor != null) || !initContentWriterNoStream(writerInterceptor))
				initContentWriterWithStream(streamInterceptor, writerInterceptor);
		}
		finally
		{
			if (contentOutput_ != null)
			{
				if (!(contentOutput_ instanceof PrintWriter))
					initContentWriterForError();
			}
		}
	}

	
	/**
	 * Creates a PrintWriter from a implementation writer.
	 * @param writerInterceptor a writer interceptor, can be null
	 * @return writer successful created? 
	 * @throws IOException if an IO error occurs
	 */
	private boolean initContentWriterNoStream(ResponseInterceptor<Writer> writerInterceptor) throws IOException
	{
		PrintWriter writer = getContentWriterImpl();
		if (writer == null)
			return false;
		else
		{
			if (writerInterceptor != null)
				writer = new InterceptedPrintWriter(writer, writerInterceptor);
			contentOutput_ = writer;
			return true;
		}
	}
	
		
	/**
	 * Creates a PrintWriter from a implementation OutputStream.
	 * @param streamInterceptor a stream interceptor, can be null
	 * @param writerInterceptor a writer interceptor, can be null
	 * @throws IOException if an IO error occurs
	 */
	private void initContentWriterWithStream(ResponseInterceptor<OutputStream> streamInterceptor,
		ResponseInterceptor<Writer> writerInterceptor) throws IOException
	{
		OutputStream originalStream = getContentStreamImpl();
		contentOutput_ = originalStream;
		
		if ((streamInterceptor != null) || (writerInterceptor != null))
			contentOutput_ = new InterceptedPrintWriter(originalStream, streamInterceptor, writerInterceptor, charEncoding_);
		else
			contentOutput_ = new PrintWriter(new OutputStreamWriter(originalStream, charEncoding_));
	}
		

	/**
	 * Reinit the response writer if an exception was thrown during regular init.  
	 */
	private void initContentWriterForError() throws IOException
	{
		if (contentOutput_ instanceof OutputStream)
			contentOutput_ = new OutputStreamWriter((OutputStream)contentOutput_, getCharEncoding());
		contentOutput_ = new PrintWriter((Writer)contentOutput_);
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
	 * @return the output stream
	 * @throws IOException if an IO error occurs
	 */
	protected abstract OutputStream getContentStreamImpl() throws IOException;
	
	
	/**
	 * Provides a PrintWriter to write text Response content.
	 * In a servlet environment this returns HttpServletResponse#getWriter().
	 * If an implementation does not have an own writer implementation
	 * (but only OutputStreams, it should return null).
	 * @return the writer
	 * @throws IOException if an IO error occurs
	 */
	protected abstract PrintWriter getContentWriterImpl() throws IOException;
	
	
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
	 * @param encoding the encoding
	 */
	protected abstract void setCharEncodingImpl(String encoding);

	
	@Override public String getCharEncoding()
	{
		return charEncoding_;
	}

	
	@Override public InterceptorBuilder addInterceptor()
	{
		return new InterceptorBuilderImpl();
	}

	
	private class InterceptorBuilderImpl implements InterceptorBuilder
	{
		@Override public void forStream(ResponseInterceptor<OutputStream> interceptor)
		{
			Check.notNull(interceptor, "interceptor");
			checkNoContentOutput();
			Extension ext = writeExt();
			ext.streamInterceptor = ResponseInterceptorChain.of(ext.streamInterceptor, interceptor);
		}

	
		@Override public void forWriter(ResponseInterceptor<Writer> interceptor)
		{
			Check.notNull(interceptor, "interceptor");
			checkNoContentOutput();
			Extension ext = writeExt();
			ext.writerInterceptor = ResponseInterceptorChain.of(ext.writerInterceptor, interceptor);
		}
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

	
	//----------------------------
	// attributes
	//----------------------------
	
	
	/**
	 * Returns an attribute.
	 */
	@Override public Object getAttribute(String name)
	{
		HashMap<String, Object> attributes = readExt().attributes;
		return attributes != null ? attributes.get(name) : null;
	}


	/**
	 * Returns the attribute names.
	 */
	@Override public Iterator<String> getAttributeNames()
	{
		HashMap<String, Object> attributes = readExt().attributes;
		return attributes != null ? attributes.keySet().iterator() : Iterators.<String>empty();
	}

	
	/**
	 * Allows to set an attribute.
	 */
	@Override public void setAttribute(String name, Object value)
	{
		Extension ext = writeExt();
		if (ext.attributes == null)
			ext.attributes = new HashMap<>();
		ext.attributes.put(name, value);
	}

	
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
		extension_			= null;
		resetAsyncContext();
	}


	//------------------------------
	// async
	//------------------------------

	
	/**
	 * Returns the AsyncContext that was created by the most recent call to {@link #startAsync()}
	 * @return the AsnycContext
	 * @throws IllegalStateException if startAsync() has not been called.
	 */
	@Override public AsyncContext getAsyncContext()
	{
		AsyncContext content = readExt().asyncContext; 
		if (content == null)
			throw new IllegalStateException("async not started");
		return content;
	}
	
	
	/**
	 * Returns if this request has been put into asynchronous mode by a call to {@link #startAsync()}. 
	 */
	@Override public boolean isAsyncStarted()
	{
		return readExt().asyncContext != null;
	}
	
	
	/**
	 * Returns if this request supports asynchronous processing. 
	 */
	@Override public abstract boolean isAsyncSupported();
	
	
	/**
	 * Puts this request into asynchronous mode and initializes its AsyncContext.
	 * @throws IllegalStateException if this request does not support asynchronous operations or if called again
	 * 		in a state where the AsyncContext intervenes, or when the response has been closed.
	 */
	@Override public AsyncContext startAsync()
	{
		try
		{
			return writeExt().asyncContext = createAsyncContext();
		}
		catch(Exception e)
		{
			String message = "can't start async mode";
			if (!isAsyncSupported())
				message += ", not supported or enabled";
			throw new IllegalArgumentException(message, e);
		}
	}
	
	
	protected abstract AsyncContext createAsyncContext() throws Exception;
	
	
	protected void resetAsyncContext()
	{
		if (extension_ != null)
			extension_.asyncContext = null;
	}
	
	
	//------------------------------
	// ext
	//------------------------------

	
	/**
	 * Bundles properties which are null most of the times
	 */
	private static class Extension
	{
		public Extension()
		{
		}
		
		
//		public Extension(Extension other)
//		{
//			streamInterceptor	= other.streamInterceptor;
//			writerInterceptor	= other.writerInterceptor;
//			attributes			= other.attributes;
//		}

		public ResponseInterceptor<OutputStream> streamInterceptor;
		public ResponseInterceptor<Writer> writerInterceptor;
		public HashMap<String, Object> attributes;
		public AsyncContext asyncContext;
	}
	
	
	private Extension readExt()
	{
		return extension_ != null ? extension_ : READ_EXTENSION;
	}
	
	
	private Extension writeExt()
	{
		if (extension_ == null)
			extension_ = new Extension();
		return extension_;
	}

	
	private final Request request_;
	private final ResponseOwner owner_;
	private LocaleService localeService_;
	private Flushable contentOutput_;
	// we duplicate the encoding since we want to know if an encoding was explicitly set
	// (the servlet response returns ISO-8859-1 if no encoding was set).
	private String charEncoding_;
	private Locale contentLanguage_;
	private Type type_ = Type.NORMAL;
	private Extension extension_;
	private static final Extension READ_EXTENSION = new Extension();
}
