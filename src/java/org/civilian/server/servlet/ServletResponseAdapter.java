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
package org.civilian.server.servlet;


import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.civilian.Logs;
import org.civilian.response.AbstractResponse;
import org.civilian.response.AsyncContext;
import org.civilian.response.Response;
import org.civilian.response.ResponseHeaders;
import org.civilian.util.ClassUtil;


/**
 * A Response implementation based on a HttpServletResponse.
 */
class ServletResponseAdapter extends AbstractResponse
{
	/**
	 * Creates a new ServletResponseAdapter.
	 */
	public ServletResponseAdapter(ServletRequestAdapter request)
	{
		super(request);
		servletResponse_ = request.getServletResponse();
	}
	

	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public boolean isCommitted()
	{
		return servletResponse_.isCommitted();
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public void reset()
	{
		servletResponse_.reset();
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public Response addCookie(Cookie cookie)
	{
		servletResponse_.addCookie(cookie);
		return this;
	}
	  
	
	/**
	 * Forwards to the HttpServletResponse#encodeURL(String)
	 */
	@Override public String addSessionId(String url)
	{
		return servletResponse_.encodeURL(url);
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override protected void sendRedirectImpl(String url) throws IOException
	{
		if (Logs.RESPONSE.isDebugEnabled())
			Logs.RESPONSE.debug("redirect to " + url);
		// will fail if response was already committed
		url = servletResponse_.encodeRedirectURL(url);
		servletResponse_.sendRedirect(url);
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public int getStatus()
	{
		return servletResponse_.getStatus();
	}

	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public Response setStatus(int statusCode)
	{
		servletResponse_.setStatus(statusCode);
		return this;
	}
	

	//-----------------------------------
	// response body
	//-----------------------------------
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override protected Writer getContentWriterImpl() throws IOException
	{
		return servletResponse_.getWriter();
	}


	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override protected OutputStream getContentStreamImpl() throws IOException
	{
		return servletResponse_.getOutputStream();
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public Response setContentType(String type)
	{
		servletResponse_.setContentType(type);
		contentType_ = type;
		return this;
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public String getContentType()
	{
		return contentType_;
	}

	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override protected void setCharEncodingImpl(String encoding)
	{
		servletResponse_.setCharacterEncoding(encoding);
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public Response setContentLength(long length)
	{
		if (SETCONTENT_LENGTH_LONG_METHOD != null)
		{
			try
			{
				SETCONTENT_LENGTH_LONG_METHOD.invoke(servletResponse_, Long.valueOf(length));
			}
			catch(Exception e)
			{
				Logs.REQUEST.error("unexpected", e);
			}
		}
		servletResponse_.setContentLength((int)length);
		return this;
	}


	/**
	 * Sets the content language and forwards to the HttpServletResponse.
	 */
	@Override public Response setContentLanguage(Locale locale)
	{
		super.setContentLanguage(locale);
		servletResponse_.setLocale(locale);
		return this;
	}

	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override protected void resetBufferImpl()
	{
		servletResponse_.resetBuffer();
	}
	

	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override protected void flushBuffer(Flushable flushable) throws IOException
	{
		if (flushable != null)
			flushable.flush();
		else
			servletResponse_.flushBuffer();
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public Response setBufferSize(int size)
	{
		servletResponse_.setBufferSize(size);
		return this;
	}
	
	
	/**
	 * Forwards to the HttpServletResponse.
	 */
	@Override public int getBufferSize()
	{
		return servletResponse_.getBufferSize();
	}
	
	
	//-----------------------------------
	// async
	//-----------------------------------
	
	
	@Override public boolean isAsyncSupported()
	{
		return getServletRequest().isAsyncSupported();
	}


	@Override protected AsyncContext createAsyncContext()
	{
		return new AsyncContextAdapter((ServletRequestAdapter)getRequest(), this);
	}


	//----------------------------
	// ResponseHeaders interface
	//----------------------------
	
	
	/**
	 * Returns a ResponseHeaders object.
	 */
	@Override public ResponseHeaders getHeaders()
	{
		if (headers_ == null)
			headers_ = new Headers();
		return headers_;
	}
	
	
	private class Headers implements ResponseHeaders
	{
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public void add(String name, String value)
		{
			servletResponse_.addHeader(name, value);
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public void addDate(String name, long value)
		{
			servletResponse_.addDateHeader(name, value);
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public void addInt(String name, int value)
		{
			servletResponse_.addIntHeader(name, value);
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public boolean contains(String name)
		{
			return servletResponse_.containsHeader(name);
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public Iterator<String> iterator()
		{
			return servletResponse_.getHeaderNames().iterator();
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public String[] getAll(String name)
		{
			Collection<String> values = servletResponse_.getHeaders(name);
			return values.toArray(new String[values.size()]);
		}
	
	
		@Override public String get(String name)
		{
			return servletResponse_.getHeader(name);
		}

		
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public void set(String name, String value)
		{
			servletResponse_.setHeader(name, value);
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public void setDate(String name, long value)
		{
			servletResponse_.setDateHeader(name, value);
		}
	
	
		/**
		 * Forwards to the HttpServletResponse.
		 */
		@Override public void setInt(String name, int value)
		{
			servletResponse_.setIntHeader(name, value);
		}
	}

	
	/**
	 * Returns the HttpServletResponse.
	 */
	public HttpServletResponse getServletResponse()
	{
		return servletResponse_;
	}
	

	/**
	 * Returns the HttpServletRequest.
	 */
	public HttpServletRequest getServletRequest()
	{
		return ((ServletRequestAdapter)getRequest()).getServletRequest();
	}

	
	/**
	 * Returns the HttpServletResponse, if implClass is ServletResponse.class or
	 * HttpServletResponse.class 
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(servletResponse_, implClass);
	}

	
	private HttpServletResponse servletResponse_;
	private Headers headers_;
	private String contentType_;
	private static final java.lang.reflect.Method SETCONTENT_LENGTH_LONG_METHOD;
	static 
	{
		java.lang.reflect.Method set = null;
		try
		{
			set = ServletResponse.class.getMethod("setContentLengthLong", Long.class);
		}
		catch(Exception e)
		{
		}
		SETCONTENT_LENGTH_LONG_METHOD = set;
	}
}
