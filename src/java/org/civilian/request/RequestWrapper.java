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
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.civilian.Application;
import org.civilian.Request;
import org.civilian.Resource;
import org.civilian.Response;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.resource.Path;
import org.civilian.resource.PathParam;
import org.civilian.text.service.LocaleService;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * RequestWrapper is a Request implementation which wraps another request.
 * All methods except {@link #getRequest()} forward to the wrapped request.
 */
public class RequestWrapper implements Request
{
	/**
	 * Creates a new RequestWrapper.
	 * @param request the wrapped request
	 */
	public RequestWrapper(Request request)
	{
		request_ = Check.notNull(request, "request");
	}

	
	/**
	 * Implements RequestProvider and returns this.
	 */
	@Override public Request getRequest()
	{
		return this;
	}
	

	@Override public Application getApplication()
	{
		return request_.getApplication();
	}


	@Override public Response getResponse()
	{
		return request_.getResponse();
	}


	@Override public void setResponse(Response response)
	{
		request_.setResponse(response);
	}

	
	@Override public String getMethod()
	{
		return request_.getMethod();
	}


	@Override public Path getPath()
	{
		return request_.getPath();
	}


	@Override public Path getRelativePath()
	{
		return request_.getRelativePath();
	}


	@Override public String getOriginalPath()
	{
		return request_.getOriginalPath();
	}
	
	
	@Override public Resource getResource()
	{
		return request_.getResource();
	}


	@Override public void setResource(Resource resource)
	{
		request_.setResource(resource);
	}
	
	
	@Override public Object getAttribute(String name)
	{
		return request_.getAttribute(name);
	}


	@Override public Iterator<String> getAttributeNames()
	{
		return request_.getAttributeNames();
	}


	@Override public void setAttribute(String name, Object value)
	{
		request_.setAttribute(name, value);
	}


	@Override public ContentTypeList getAcceptedContentTypes()
	{
		return request_.getAcceptedContentTypes();
	}


	@Override public Locale getAcceptedLocale()
	{
		return request_.getAcceptedLocale();
	}
	  
	
	@Override public Iterator<Locale> getAcceptedLocales()
	{
		return request_.getAcceptedLocales();
	}
	
	
	@Override public CookieList getCookies()
	{
		return request_.getCookies();
	}


	@Override public String getParameter(String name)
	{
		return request_.getParameter(name);
	}


	@Override public String[] getParameters(String name)
	{
		return request_.getParameters(name);
	}


	@Override public Iterator<String> getParameterNames()
	{
		return request_.getParameterNames();
	}


	@Override public Map<String, String[]> getParameterMap()
	{
		return request_.getParameterMap();
	}


	@Override public <T> void setPathParam(PathParam<T> format, T value)
	{
		request_.setPathParam(format, value);
	}


	@Override public void setPathParams(Map<PathParam<?>,Object> pathParams)
	{
		request_.setPathParams(pathParams);
	}


	@Override public <T> T getPathParam(PathParam<T> format)
	{
		return request_.getPathParam(format);
	}


	@Override public Iterator<PathParam<?>> getPathParams()
	{
		return request_.getPathParams();
	}


	@Override public String getMatrixParam(String name)
	{
		return request_.getMatrixParam(name);
	}


	@Override public String[] getMatrixParams(String name)
	{
		return request_.getMatrixParams(name);
	}


	@Override public Iterator<String> getMatrixParamNames()
	{
		return request_.getMatrixParamNames();
	}


	@Override public boolean hasUploads()
	{
		return request_.hasUploads();
	}


	@Override public Upload getUpload(String name)
	{
		return request_.getUpload(name);
	}


	@Override public Upload[] getUploads(String name)
	{
		return request_.getUploads(name);
	}


	@Override public Exception getUploadError()
	{
		return request_.getUploadError();
	}


	@Override public Iterator<String> getUploadNames()
	{
		return request_.getUploadNames();
	}


	@Override public LocaleService getLocaleService()
	{
		return request_.getLocaleService();
	}


	@Override public void setLocaleService(LocaleService service)
	{
		request_.setLocaleService(service);
	}


	@Override public ContentAccess getContentAccess()
	{
		return request_.getContentAccess();
	}

	
	@Override public String getCharEncoding()
	{
		return request_.getCharEncoding();
	}


	@Override public long getContentLength()
	{
		return request_.getContentLength();
	}


	@Override public ContentType getContentType()
	{
		return request_.getContentType();
	}

	
	@Override public void setContentType(ContentType contenType)
	{
		request_.setContentType(contenType);
	}
	

	@Override public InputStream getContentStream() throws IOException
	{
		return request_.getContentStream();
	}


	@Override public Reader getContentReader() throws IOException
	{
		return request_.getContentReader();
	}


	@Override public void setCharEncoding(String encoding) throws UnsupportedEncodingException
	{
		request_.setCharEncoding(encoding);
	}


	@Override public void addInterceptor(RequestStreamInterceptor interceptor)
	{
		request_.addInterceptor(interceptor);
	}
	
	
	@Override public void addInterceptor(RequestReaderInterceptor interceptor)
	{
		request_.addInterceptor(interceptor);
	}

	
	@Override public AsyncContext getAsyncContext()
	{
		return request_.getAsyncContext();
	}


	@Override public boolean isAsyncStarted()
	{
		return request_.isAsyncStarted();
	}


	@Override public AsyncContext startAsync()
	{
		return request_.startAsync();
	}

	
	@Override public boolean isAsyncSupported()
	{
		return request_.isAsyncSupported();
	}
	

	@Override public RequestHeaders getHeaders()
	{
		return request_.getHeaders();
	}


	@Override public RequestSecurity getSecurity()
	{
		return request_.getSecurity();
	}


	@Override public ServerInfo getServerInfo()
	{
		return request_.getServerInfo();
	}


	@Override public RemoteInfo getRemoteInfo()
	{
		return request_.getRemoteInfo();
	}


	@Override public LocalInfo getLocalInfo()
	{
		return request_.getLocalInfo();
	}


	@Override public Session getSession(boolean create)
	{
		return request_.getSession(create);
	}


	@Override public <T> T unwrap(Class<T> implClass)
	{
		T unwrapped = ClassUtil.unwrap(request_, implClass);
		if (unwrapped != null)
			return unwrapped;
		else
			return request_.unwrap(implClass);
	}
	
	
	protected Request request_;
}
