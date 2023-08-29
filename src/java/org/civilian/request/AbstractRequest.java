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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.civilian.application.Application;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.resource.Path;
import org.civilian.resource.Resource;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.text.service.LocaleService;
import org.civilian.util.Check;
import org.civilian.util.http.HeaderMap;


/**
 * AbstractRequest is a partial request implementation
 * with useful defaults.
 */
public abstract class AbstractRequest implements Request
{
	/**
	 * Creates a new AbstractRequest.
	 * @param owner the associated owner
	 * @param relativePath the relative path of the request with respect to the application path.
	 */
	public AbstractRequest(RequestOwner owner, String relativePath)
	{
		owner_		 	= Check.notNull(owner, "owner");
		relativePath_	= new Path(relativePath);
	}
	
	
	//-----------------------------
	// general accessors
	//-----------------------------
	
	
	@Override public RequestOwner getOwner()
	{
		return owner_;
	}

	
	//-----------------------------
	// path related methods
	//-----------------------------

	
	protected void setRelativePath(String relativePath)
	{
		relativePath_	= new Path(relativePath);
		path_ 			= null; // create lazy
		if (extension_ != null)
			extension_.matrixParams = null;
	}
	
	
	@Override public Path getPath()
	{
		if (path_ == null)
			path_ = owner_.getPath().add(relativePath_);
		return path_;
	}

	
	@Override public Path getRelativePath()
	{
		return relativePath_;
	}

	
	//----------------------------
	// resource
	//----------------------------

	
	@Override public Resource getResource()
	{
		return resource_;
	}


	@Override public void setResource(Resource resource)
	{
		resource_ = resource;
	}
	
	
	//----------------------------
	// path params
	//----------------------------


	@Override public <T> void setPathParam(PathParam<T> pathParam, T value)
	{
		Check.notNull(pathParam, "pathParam");
		Check.notNull(value, "value");
		if (pathParams_ == null)
			pathParams_ = new HashMap<>();
		pathParams_.put(pathParam, value);
	}

	
	@Override public void setPathParams(Map<PathParam<?>,Object> pathParams)
	{
		pathParams_ = pathParams;
	}

	
	@SuppressWarnings("unchecked")
	@Override public <T> T getPathParam(PathParam<T> pathParam)
	{
		return pathParams_ != null ? (T)pathParams_.get(pathParam) : null;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override public Iterator<PathParam<?>> getPathParams()
	{
		return pathParams_ != null ? pathParams_.keySet().iterator() : Collections.EMPTY_LIST.iterator();
	}
	
	
	protected void clearPathParams()
	{
		pathParams_ = null;
	}
	

	//----------------------------
	// matrix params
	//----------------------------


	@Override public String getMatrixParam(String name)
	{
		return getMatrixParams().get(name);
	}
	
	
	@Override public String[] getMatrixParams(String name)
	{
		return getMatrixParams().getAll(name);
	}
	
	
	@Override public Iterator<String> getMatrixParamNames()
	{
		return getMatrixParams().iterator();
	}

	
	private HeaderMap getMatrixParams()
	{
		if (readExt().matrixParams == null)
			parseMatrixParams();
		return readExt().matrixParams;
	}

	
	private void parseMatrixParams()
	{
		String uri = getOriginalPath();
		
		int slash = uri.lastIndexOf('/');
		int colon = uri.indexOf(';', slash + 1);
		if (colon < 0)
			writeExt().matrixParams = HeaderMap.EMPTY;
		else
		{
			HeaderMap matrixParams = writeExt().matrixParams = new HeaderMap();
			
			int start = colon + 1;
			do
			{
				int end = uri.indexOf(';', start);
				if (end < 0)
					end = uri.length();
				int eq  = uri.indexOf('=', start);
				if ((eq < 0) || (eq > end))
					matrixParams.add(uri.substring(start, end), "");
				else if (eq > start)
					matrixParams.add(uri.substring(start, eq), uri.substring(eq + 1, end));
				start = end + 1;
			}
			while(start < uri.length());
		}
	}
	

	//----------------------------
	// preferences
	//----------------------------

	
	@Override public ContentTypeList getAcceptedContentTypes()
	{
		if (acceptedContentTypes_ == null)
			initAcceptedContentTypes();
		return acceptedContentTypes_;
	}
	
	
	// lazy construct the list of accepted content types
	// we sort by quality, higher quality coming first, to speed up content negotiation
	// if there are no accept headers, we want a list containing */*
	private void initAcceptedContentTypes()
	{
		acceptedContentTypes_ = ContentTypeList.parse(ContentType.Compare.BY_QUALITY, getHeaders().getAll("Accept"));
		if (acceptedContentTypes_.size() == 0)
			acceptedContentTypes_ = ContentTypeList.ANY;
	}


	//------------------------------
	// locale
	//------------------------------
	
	
	/**
	 * Returns the locale data associated with the request.
	 * The locale data can be set explicitly by {@link #setLocaleService(LocaleService)}.
	 * If not explicitly set it is derived from the requested preferred locale ({@link #getAcceptedLocale()}).
	 * If the preferred locale is not contained in the list of supported locales (see {@link Application#getLocaleServices()})
	 * then the default application locale will be used.  
	 */
	@Override public LocaleService getLocaleService()
	{
		// lazy init of LocaleService
		// if during processing the locale-item is initialized by a call
		// to #setLocaleItem (e.g. from a user profile), then we avoid to call getPreferences().getLocale())
		if (localeService_ == null)
			localeService_ = getOwner().getLocaleServices().getService(getAcceptedLocale());
		return localeService_;
	}
	

	/**
	 * Sets the localeService associated with the request.
	 * This overrides the default locale data, as defined by the requested preferred language.
	 */
	@Override public void setLocaleService(LocaleService localeService)
	{
		localeService_ = Check.notNull(localeService, "localeService");
	}

	
	//------------------------------
	// content
	//------------------------------
	

	/**
	 * Initializes the encoding to the application encoding
	 * if the encoding is not set and content-type is
	 * application/x-www-form-urlencoded.
	 * Derived implementation should call this method once
	 * they are initialized and allow access to their
	 * getContentType() implementation.
	 * Since the request implementation (e.g. HttpServletRequest)
	 * will read the from-urlencoded content to parse parameters)
	 * this initialization should be done before a parameter is accessed.
	 */
	protected void initEncoding()
	{
		if ((getCharEncoding() == null) && 
			ContentType.APPLICATION_X_WWW_FORM_URLENCODED.equals(getContentType()))
		{
			setDefaultCharEncoding();
		}
	}
	
	
	private void setDefaultCharEncoding()
	{
		String encoding = getOwner().getDefaultCharEncoding();
		try
		{
			setCharEncoding(encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalStateException("application encoding '" + encoding + "' not supported", e); 
		}
	}

	
	@Override public ContentAccess getContentAccess()
	{
		if (contentInput_ == null)
			return ContentAccess.NONE;
		else if (contentInput_ instanceof Reader)
			return ContentAccess.READER;
		else
			return ContentAccess.INPUTSTREAM;
	}

	
	@Override public ContentType getContentType()
	{
		ContentType contentType = readExt().contentType;
		return contentType != null ? contentType : getContentTypeImpl(); 
	}
	
	
	protected abstract ContentType getContentTypeImpl();

	
	@Override public void setContentType(ContentType contentType)
	{
		writeExt().contentType = contentType;
		initEncoding();
	}
	

	@Override public InputStream getContentStream() throws IOException
	{
		if (!(contentInput_ instanceof InputStream))
			initContentStream();
		return (InputStream)contentInput_;
	}
	
	  
	private void initContentStream() throws IOException
	{
		checkNoContentReader();
		InputStream in = getContentStreamImpl();
		
		// always save in contentInput_ before we try to apply the interceptors
		// if an interceptor fails, we still want to have an assigned imputstream 
		contentInput_ = in; 
		
		RequestInterceptor<InputStream> interceptor = readExt().streamInterceptor;
		if (interceptor != null) 
			contentInput_ = RequestInterceptorChain.intercept(this, in, interceptor);
	}
	
	
	protected abstract InputStream getContentStreamImpl() throws IOException;
	
	
	/**
	 * Returns a Reader for the request content, using the character
	 * encoding of the content.
	 */
	@Override public Reader getContentReader() throws IOException
	{
		if (!(contentInput_ instanceof Reader))
			initContentReader();
		return (Reader)contentInput_;
	}
	
	
	private void initContentReader() throws IOException
	{
		checkNoContentStream();
		
		if (getCharEncoding() == null)
			setDefaultCharEncoding();
		
		Reader reader;
		
		Extension ext = readExt();
		if (ext.streamInterceptor != null)   
		{
			initContentStream();
			reader = getContentReaderImpl((InputStream)contentInput_);  
		}
		else
			reader = getContentReaderImpl();
		
		// always save in contentInput_ before we try to apply the interceptors
		// if an interceptor fails, we still want to have an assigned reader 
		contentInput_ = reader;
		if (ext.readerInterceptor != null)
			contentInput_ = RequestInterceptorChain.intercept(this, reader, ext.readerInterceptor);
	}
	
	
	protected abstract Reader getContentReaderImpl() throws IOException; 

	
	protected Reader getContentReaderImpl(InputStream out) throws IOException
	{
		return new InputStreamReader(out, getCharEncoding()); 
	}

	
	private void checkNoContentStream()
	{
		if (contentInput_ instanceof InputStream)
			throw new IllegalStateException("Request.getContentStream() has already been called"); 
	}
	
	
	private void checkNoContentReader()
	{
		if (contentInput_ instanceof Reader)
			throw new IllegalStateException("Request.getContentReader() has already been called"); 
	}
	
	
	protected void resetContentInput()
	{
		contentInput_ = null;
	}
	
	
	@Override public InterceptorBuilder addInterceptor()
	{
		return new InterceptorBuilderImpl();
	}
	
	
	private class InterceptorBuilderImpl implements InterceptorBuilder
	{
		@Override
		public void forStream(RequestInterceptor<InputStream> interceptor)
		{
			checkAddInterceptor(interceptor);
			Extension ext 			= writeExt();
			ext.streamInterceptor 	= RequestInterceptorChain.of(ext.streamInterceptor, interceptor); 
		}

	
		@Override
		public void forReader(RequestInterceptor<Reader> interceptor)
		{
			checkAddInterceptor(interceptor);
			Extension ext 			= writeExt();
			ext.readerInterceptor 	= RequestInterceptorChain.of(ext.readerInterceptor, interceptor); 
		}
	}
	
	
	protected void clearInterceptors()
	{
		Extension ext = readExt();
		if (ext.streamInterceptor != null)
			ext.streamInterceptor = null;
		if (ext.readerInterceptor != null)
			ext.readerInterceptor = null;
	}
	

	private void checkAddInterceptor(Object interceptor)
	{
		Check.notNull(interceptor, "interceptor");
		
		// getContentStream() or getContentWriter() may not be called yet
		if (contentInput_ != null)
		{
			checkNoContentStream(); // fails or 	
			checkNoContentReader(); // fails
		}
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
		
		
		public Extension(Extension other)
		{
			matrixParams		= other.matrixParams;
			contentType			= other.contentType;
			streamInterceptor	= other.streamInterceptor;
			readerInterceptor	= other.readerInterceptor;
		}

		public HeaderMap matrixParams;
		public ContentType contentType;
		public RequestInterceptor<InputStream> streamInterceptor;
		public RequestInterceptor<Reader> readerInterceptor;
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

	
	@Override public String toString()
	{
		return "request:" + getPath(); 
	}
	

	private Path path_;
	private Path relativePath_;
	private Map<PathParam<?>, Object> pathParams_;
	private LocaleService localeService_;
	private RequestOwner owner_;
	private Object contentInput_;
	private Resource resource_;
	private Extension extension_;
	protected ContentTypeList acceptedContentTypes_;
	private static final Extension READ_EXTENSION = new Extension();
}
