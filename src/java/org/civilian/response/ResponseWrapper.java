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
import java.util.Locale;
import javax.servlet.http.Cookie;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.content.ContentType;
import org.civilian.template.TemplateWriter;
import org.civilian.text.LocaleService;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * ResponseWrapper is a Response implementation which wraps another response.
 * All methods forward to the wrapped response.
 */
public class ResponseWrapper implements Response
{
	/**
	 * Creates a new ResponseWrapper.
	 */
	public ResponseWrapper(Response response)
	{
		response_ = Check.notNull(response, "response");
	}


	@Override public Request getRequest()
	{
		return response_.getRequest();
	}


	@Override public boolean isCommitted()
	{
		return response_.isCommitted();
	}


	@Override public void reset()
	{
		response_.reset();
	}


	@Override public void addCookie(Cookie cookie)
	{
		response_.addCookie(cookie);
	}


	@Override public String addSessionId(String url)
	{
		return response_.addSessionId(url);
	}


	@Override public UriEncoder getUriEncoder()
	{
		return response_.getUriEncoder();
	}


	@Override public LocaleService getLocaleService()
	{
		return response_.getLocaleService();
	}


	@Override public void setLocaleService(LocaleService service)
	{
		response_.setLocaleService(service);
	}


	@Override public void sendError(int errorCode, String message, Throwable error) throws IOException
	{
		response_.sendError(errorCode, message, error);
	}


	@Override public void sendRedirect(String url) throws IOException
	{
		response_.sendRedirect(url);
	}


	@Override public void writeContent(Object object, ContentType contentType) throws Exception
	{
		response_.writeContent(object, contentType);
	}


	@Override public int getStatus()
	{
		return response_.getStatus();
	}


	@Override public Type getType()
	{
		return response_.getType();
	}

	
	@Override public void setStatus(int statusCode)
	{
		response_.setStatus(statusCode);
	}


	@Override public ContentAccess getContentAccess()
	{
		return response_.getContentAccess();
	}


	@Override public TemplateWriter getContentWriter() throws IOException
	{
		return response_.getContentWriter();
	}


	@Override public OutputStream getContentStream() throws IOException
	{
		return response_.getContentStream();
	}


	@Override public void setContentType(ContentType contentType)
	{
		response_.setContentType(contentType);
	}


	@Override public ContentType getContentType()
	{
		return response_.getContentType();
	}


	@Override public void setContentEncoding(String encoding)
	{
		response_.setContentEncoding(encoding);
	}


	@Override public String getContentEncoding()
	{
		return response_.getContentEncoding();
	}


	@Override public void setContentLength(long length)
	{
		response_.setContentLength(length);
	}


	@Override public void setContentLanguage(Locale locale)
	{
		response_.setContentLanguage(locale);
	}


	@Override public Locale getContentLanguage()
	{
		return response_.getContentLanguage();
	}


	@Override public void closeContent()
	{
		response_.closeContent();
	}


	@Override public void addInterceptor(ResponseStreamInterceptor interceptor)
	{
		response_.addInterceptor(interceptor);
	}


	@Override public void addInterceptor(ResponseWriterInterceptor interceptor)
	{
		response_.addInterceptor(interceptor);
	}

	
	@Override public void resetBuffer()
	{
		response_.resetBuffer();
	}


	@Override public void flushBuffer() throws IOException
	{
		response_.flushBuffer();
	}
	
	
	@Override public void setBufferSize(int size)
	{
		response_.setBufferSize(size);
	}
	

	@Override public int getBufferSize()
	{
		return response_.getBufferSize();
	}

	
	@Override public ResponseHeaders getHeaders()
	{
		return response_.getHeaders();
	}


	@Override public <T> T unwrap(Class<T> implClass)
	{
		T unwrapped = ClassUtil.unwrap(response_, implClass);
		if (unwrapped != null)
			return unwrapped;
		else
			return response_.unwrap(implClass);
	}


	protected Response response_;
}
