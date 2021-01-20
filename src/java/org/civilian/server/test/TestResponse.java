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
package org.civilian.server.test;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.http.Cookie;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.internal.AbstractResponse;
import org.civilian.internal.ParamList;
import org.civilian.request.CookieList;
import org.civilian.request.Session;
import org.civilian.util.Check;


/**
 * TestResponse is a {@link Response} implementation to be used in a test environment.
 */
public class TestResponse extends AbstractResponse
{
	public TestResponse(Request request)
	{
		super(request);
	}
	
	
	public TestResponse(TestRequest request, TestResponse response)
	{
		this(request);
		
		headers_ 		= response.headers_;
		bufferSize_ 	= response.bufferSize_;
	}
	
	
	@Override public boolean isCommitted()
	{
		return isCommitted_;
	}
	
	
	@Override public void reset()
	{
		checkNotCommitted();
		statusCode_ = Status.OK;
		headers_.clear();
	}

	
	@Override public Response addCookie(Cookie cookie)
	{
		getCookies().add(Check.notNull(cookie, "cookie"));
		return this;
	}
	
	
	public CookieList getCookies()
	{
		if (cookies_ == null)
			cookies_ = new CookieList();
		return cookies_;
	}
	
	
	/**
	 * Appends the session id to the url, if a session id was set.
	 */
	@Override public String addSessionId(String url)
	{
		Session session = getRequest().getSession(false);
		return session != null ? url + ";session=" + session.getId() : url;
	}

	
	@Override protected void sendRedirectImpl(String url) throws IOException
	{
		resetBuffer();
		getHeaders().set("Location", url);
		setStatus(Status.SC302_FOUND);
		flushBuffer();
	}

	
	@Override public int getStatus()
	{
		return statusCode_;
	}

	
	@Override public Response setStatus(int statusCode)
	{
		statusCode_ = statusCode;
		return this;
	}
	

	//-----------------------------------
	// response content
	//-----------------------------------
	
	
	@Override public Response setContentType(String contentType)
	{
		contentType_ = contentType;
		return this;
	}


	@Override public String getContentType()
	{
		return contentType_;
	}


	@SuppressWarnings("resource")
	@Override protected OutputStream getContentStreamImpl() throws IOException
	{
		OutputStream out = outputStream_ = new ResponseStream();
		if (bufferSize_ > 0)
			out = bufferedStream_ = new BufferedStream(out, bufferSize_);
		return out;
	}

	
	@Override protected Writer getContentWriterImpl() throws IOException
	{
		return null;
	}

	
	@Override protected void setContentEncodingImpl(String encoding)
	{
		// encoding already stored in AbstractResponse
	}

	
	@Override public Response setContentLength(long length)
	{
		contentLength_ = length;
		getHeaders().set("Content-Length", length < 0 ? null : String.valueOf(length));
		return this;
	}
	
	
	/**
	 * Returns the content-length.
	 */
	public long getContentLength()
	{
		return contentLength_;
	}

	
	/**
	 * Returns the byte currently residing in the buffer
	 * converted to a string. This does not use any encoding,
	 * hence it only makes sense if the buffer contains 7-bit data. 
	 */
	public String getBufferText() throws IOException
	{
		return new String(getBufferBytes()); 
	}

	
	/**
	 * Returns the byte currently residing in the buffer. 
	 */
	public byte[] getBufferBytes() throws IOException
	{
		return bufferedStream_ != null ? bufferedStream_.getBufferBytes() : new byte[0]; 
	}

	
	/**
	 * Returns the current response content as byte array. 
	 */
	public byte[] getContentBytes(boolean flushBuffer) throws IOException
	{
		if (flushBuffer)
			flushBuffer();
		return outputStream_ != null ? outputStream_.toByteArray() : new byte[0]; 
	}

	
	/**
	 * Returns the current response content as text string. 
	 */
	public String getContentText(boolean flushBuffer) throws IOException
	{
		if (flushBuffer)
			flushBuffer();
		if (outputStream_ != null)
		{
			String encoding = getContentEncoding();
			if (encoding == null)
				encoding = getApplication().getDefaultCharEncoding();
			// does not help to call setContentEncoding() since
			// it is ignored since we already obtaine an outputstream or writer
			return outputStream_.toString(encoding);
		}
		else
			return ""; 
	}
	
	
	//----------------------------
	// response buffer
	//----------------------------

	
	@Override protected void resetBufferImpl()
	{
		checkNotCommitted();
		if (bufferedStream_ != null)
			bufferedStream_.reset();
		if (outputStream_ != null)
			outputStream_.reset();
	}


	@Override protected void flushBuffer(Flushable flushable) throws IOException
	{
		isCommitted_ = true;
		if (flushable != null)
			flushable.flush();
	}


	/**
	 * Clears the committed flag and resets the buffer.
	 */
	public void clearCommitted()
	{
		isCommitted_ = false;
		resetBuffer();
	}

	
	/**
	 * Set the response into an initial state.
	 * A cleared response can be reused again.
	 */
	@Override public void clear()
	{
		clearCommitted();
		super.clear();
		reset();
		outputStream_ = null;
		bufferedStream_ = null;
	}
	
	
	@Override public Response setBufferSize(int size)
	{
		if (getContentAccess() != ContentAccess.NONE)
			throw new IllegalStateException("content already written");
		bufferSize_ = Math.max(0, size);
		return this;
	}


	@Override public int getBufferSize()
	{
		return bufferSize_;
	}


	@Override public ParamList getHeaders()
	{
		return headers_;
	}

	
	/**
	 * Returns null.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}

	
	private static class BufferedStream extends BufferedOutputStream
	{
	    public BufferedStream(OutputStream out, int size)
	    {
	    	super(out, size);
	    }

	    
	    public byte[] getBufferBytes()
	    {
	    	byte[] bytes = new byte[count];
	    	System.arraycopy(buf, 0, bytes, 0, count);
	    	return bytes;
	    }
	    
	    
	    public void reset()
	    {
	    	count = 0;
	    }
	}
	
	
	private class ResponseStream extends ByteArrayOutputStream
	{
		@Override public void flush() throws IOException
		{
			super.flush();
			isCommitted_ = true;
		}
	}
	
	

	private int statusCode_ = Status.OK;
	private boolean isCommitted_;
	private ParamList headers_ = new ParamList(true);
	private String contentType_;
	private long contentLength_ = -1;
	private int bufferSize_ = 4096;
	private BufferedStream bufferedStream_;
	private ResponseStream outputStream_;
	private CookieList cookies_;
}
