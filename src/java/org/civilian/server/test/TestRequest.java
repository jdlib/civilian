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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import jakarta.servlet.http.Cookie;
import org.civilian.application.Application;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.request.AbstractRequest;
import org.civilian.request.CookieList;
import org.civilian.request.Request;
import org.civilian.request.RequestHeaders;
import org.civilian.request.Uploads;
import org.civilian.resource.Resource;
import org.civilian.resource.Url;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.response.Response;
import org.civilian.util.Check;
import org.civilian.util.Iterators;
import org.civilian.util.http.HeaderNames;
import org.civilian.util.http.HeaderMap;


/**
 * TestRequest is a {@link Request} implementation to be used in a test environment.
 */
public class TestRequest extends AbstractRequest
{
	/**
	 * Creates a TestRequest.
	 * @param application an application
	 * @param relativePath the relative path of the request
	 */
	public TestRequest(Application application, String relativePath)
	{
		super(application, relativePath);
	}

	
	/**
	 * Creates a TestRequest.
	 * @param application an application
	 */
	public TestRequest(Application application)
	{
		this(application, (String)null);
	}

	
	/**
	 * Creates a TestRequest.
	 */
	public TestRequest()
	{
		this(new TestApp().init());
	}

	
	//-----------------------------
	// method
	//-----------------------------

	
	/**
	 * @return the request method.
	 */
	@Override public String getMethod()
	{
		return method_;
	}
	

	/**
	 * Sets the request method.
	 * @param method the method
	 * @return this
	 */
	public TestRequest setMethod(String method)
	{
		method_ = Check.notNull(method, "method");
		return this;
	}

	
	//-----------------------------
	// path
	//-----------------------------
	
	
	/**
	 * Sets the path of the request relative to the application path.
	 * @param path the relative request path
	 * @return this
	 */
	public TestRequest setPath(String path)
	{
		setRelativePath(path);
		return this;
	}
	
	
	/**
	 * Sets the path of the request relative to the application path.
	 * @param response the response
	 * @param resource the resource which defines the path
	 * @param pathParams the path parameters of the resource  
	 * @return this
	 */
	public TestRequest setPath(Response response, Resource resource, Object... pathParams)
	{
		Url url = response.url().to(resource);
		url.setPathParams(pathParams);
		return setPath(url);
	}

	
	/**
	 * Sets the relative path of the request.
	 * @param url a Url. Resource, path parameters and query parameters are copied
	 * 		from the resource to this request
	 * @return this
	 */
	public TestRequest setPath(Url url)
	{
		setRelativePath(url.toQuerylessString());
		
		setResource(url.getResource());

		clearPathParams();
		for (int i=0; i<url.getPathParamCount(); i++)
			setPathParam(url, i);
		
		clearParameters();
		Url.QueryParamList queryParams = url.queryParams();
		for (int i=0; i<queryParams.size(); i++)
		{
			Url.QueryParam qp = queryParams.get(i);
			getParameterList().add(qp.getName(), qp.getValue());
		}
		
		return this;
	}

	
	@SuppressWarnings("unchecked")
	private <T> void setPathParam(Url url, int i)
	{
		setPathParam((PathParam<T>)url.getPathParamDef(i), (T)url.getPathParam(i));
	}
	
	
	/**
	 * @return the path string.
	 */
	@Override public String getOriginalPath()
	{
		return getPath().print();
	}
	

	//----------------------------
	// attributes
	//----------------------------
	
	
	/**
	 * @return an attribute.
	 */
	@Override public Object getAttribute(String name)
	{
		return attributes_ != null ? attributes_.get(name) : null;
	}


	/**
	 * @return the attribute names.
	 */
	@Override public Iterator<String> getAttributeNames()
	{
		return attributes_ != null ? attributes_.keySet().iterator() : Iterators.<String>empty();
	}

	
	/**
	 * Allows to set an attribute.
	 */
	@Override public void setAttribute(String name, Object value)
	{
		if (attributes_ == null)
			attributes_ = new HashMap<>();
		attributes_.put(name, value);
	}
	

	//----------------------------
	// path parameters
	//----------------------------

	
	/**
	 * Clears the path parameters.
	 */
	@Override public void clearPathParams()
	{
		// call protected method
		super.clearPathParams();
	}

	
	//----------------------------
	// cookies
	//----------------------------
	
	
	/**
	 * @return the CookieList.
	 */
	@Override public CookieList getCookies()
	{
		if (cookies_ == null)
			cookies_ = new CookieList();
		return cookies_;
	}

	
	/**
	 * Allows to set the cookies.
	 * @param cookies the cookies
	 * @return this
	 */
	public TestRequest setCookies(CookieList cookies)
	{
		cookies_ = cookies;
		return this;
	}

	
	/**
	 * Allows to set the cookies.
	 * @param cookies the cookies
	 * @return this
	 */
	public TestRequest setCookies(Cookie... cookies)
	{
		return setCookies(new CookieList(cookies));
	}

	
	//----------------------------
	// parameters
	//----------------------------
	
	
	/**
	 * @return a request parameter.
	 */
	@Override public String getParam(String name)
	{
		return parameters_.get(name);
	}
	

	/**
	 * @return all values of a request parameter.
	 */
	@Override public String[] getParams(String name)
	{
		return parameters_.getAll(name);
	}

	
	/**
	 * @return the names of all request parameters.
	 */
	@Override public Iterator<String> getParamNames()
	{
		return parameters_.iterator();
	}

	
	/**
	 * Re@returnurns a map of all request parameters.
	 */
	@Override public Map<String, String[]> getParamMap()
	{
		return parameters_.getMap();
	}
	
	
	/**
	 * @return a ParamList containing the request parameters.
	 * The ParamList allows you to add parameters. 
	 */
	public HeaderMap getParameterList()
	{
		return parameters_;
	}
	

	/**
	 * Sets a request parameter.
	 * @param name the param name
	 * @param value the param value
	 * @return this
	 */
	public TestRequest setParameter(String name, String value)
	{
		parameters_.set(name, value);
		return this;
	}
	
	
	/**
	 * Clears the parameters.
	 */
	public void clearParameters()
	{
		parameters_.clear();
	}
	

	//----------------------------
	// uploads
	//----------------------------
	
	
	/**
	 * @return the uploads.
	 */
	@Override public Uploads getUploads()
	{
		return uploads_ != null ? uploads_ : Uploads.EMPTY;
	}


	public void setUploads(Uploads uploads)
	{
		uploads_ = uploads;
	}

	
	//----------------------------
	// content
	//----------------------------

	
	/**
	 * @return the content encoding.
	 */
	@Override public String getCharEncoding()
	{
		return charEncoding_;
	}


	/**
	 * Sets the encoding.
	 */
	@Override public void setCharEncoding(String encoding)
	{
		charEncoding_ = encoding;
	}

	
	/**
	 * @return the content length.
	 */
	@Override public long getContentLength()
	{
		return getContentBytes().length;
	}


	/**
	 * @return the content-type.
	 */
	@Override protected ContentType getContentTypeImpl()
	{
		return null;
	}


	/**
	 * @return an input stream for the content.
	 */
	@Override protected InputStream getContentStreamImpl() throws IOException
	{
		return new ByteArrayInputStream(getContentBytes());
	}
	
	
	private byte[] getContentBytes()
	{
		if (contentBytes_ == null)
		{
			if (contentString_ == null)
				contentBytes_ = EMPTY_BYTES;
			else
			{
				String encoding = charEncoding_ != null ? charEncoding_ : getOwner().getDefaultEncoding().name();
				try
				{
					contentBytes_ = contentString_.getBytes(encoding); 
				}
				catch (Exception e)
				{
					throw new IllegalStateException("unexpected", e);
				}
			}
		}
		
		return contentBytes_;
	}
	


	/**
	 * @return a reader for the content.
	 */
	@Override protected Reader getContentReaderImpl() throws IOException
	{
		if (contentString_ != null)
			return new StringReader(contentString_);
		else
		{
			String encoding = charEncoding_ != null ? charEncoding_ : getOwner().getDefaultEncoding().name();
			return new InputStreamReader(new ByteArrayInputStream(getContentBytes()), encoding);
		}
	}
	

	/**
	 * Allows you to set the content.
	 * @param content the content
	 * @return this
	 */
	public TestRequest setContent(byte[] content)
	{
		contentBytes_ = content != null ? content : new byte[0];
		resetContentInput();
		return this;
	}
	
	
	/**
	 * Allows you to set the content.
	 * @param content the content. If it is null, the content will be set empty.
	 * 		If it is a byte array or a string the content will be set to that value.
	 * 		For any other object, the object will be serialized using the current content-type.
	 * @return this
	 * @throws Exception if an error occurs
	 */
	public TestRequest setContent(Object content) throws Exception
	{
		resetContentInput();
		
		if (content != null)
		{
			if (EMPTY_BYTES.getClass() == content.getClass())
				contentBytes_ = (byte[])content;
			else if (content instanceof String)
				contentString_ = (String)content;
			else
			{
				ContentType contentType = getContentType();
				if (contentType == null)
					throw new IllegalStateException("no content-type set");
				ContentSerializer serializer = getOwner().getContentSerializers().get(contentType);
				if (serializer == null)
					throw new IllegalStateException("don't know how to write content with content type '" + contentType + "'");
				contentString_ = serializer.write(content);
			}
		}
		
		return this;
	}
	
	
	/**
	 * Resets content Reader or InputStream.
	 * The request content can now be read again.
	 */
	@Override public void resetContentInput()
	{
		super.resetContentInput();
	}
	
	
	//----------------------------
	// headers
	//----------------------------

	
	/**
	 * @return the request headers.
	 */
	@Override public Headers getHeaders()
	{
		return headers_;
	}
	

	public TestRequest setAcceptedLocale(Locale locale)
	{
		acceptedLocale_ = Check.notNull(locale, "locale");
		return this;
	}


	@Override public Locale getAcceptedLocale()
	{
		if (acceptedLocale_ == null)
			acceptedLocale_ = getOwner().getLocaleServices().getDefaultLocale();
		return acceptedLocale_;
	}


	@Override public Iterator<Locale> getAcceptedLocales()
	{
		return Iterators.forValue(getAcceptedLocale());
	}

	
	/**
	 * Allows you to set the preferred content types.
	 * @param contentTypes the content types
	 * @return this
	 */
	public TestRequest setAcceptedContentTypes(ContentTypeList contentTypes)
	{
		acceptedContentTypes_ = Check.notNull(contentTypes, "contentTypes");
		getHeaders().set(HeaderNames.ACCEPT, acceptedContentTypes_.toString());
		return this;
	}


	public TestRequest setAcceptedContentTypes(ContentType... contentTypes)
	{
		setAcceptedContentTypes(new ContentTypeList(contentTypes));
		return this;
	}

	
	public AcceptCtBuilder setAcceptedContentTypes()
	{
		return new AcceptCtBuilder();
	}

	
	public class AcceptCtBuilder
	{
		public AcceptCtBuilder()
		{
			setAcceptedContentTypes(ContentTypeList.EMPTY);
		}
		
		
		public AcceptCtBuilder add(ContentType... types)
		{
			return add(1.0, types);
		}

	
		public AcceptCtBuilder add(double quality, ContentType... types)
		{
			for (ContentType type : types)
			{
				type = type.withQuality(quality);
				types_.add(type);
			}
			setAcceptedContentTypes(new ContentTypeList(types_));
			return this;
		}
		
		
		private ArrayList<ContentType> types_ = new ArrayList<>(); 
	}
	
	
	//----------------------------
	// security
	//----------------------------

	
	@Override public TestSession getSession(boolean create)
	{
		if ((session_ == null) && create)
			session_ = new TestSession();
		return session_;
	}


	public TestRequest setSession(TestSession session)
	{
		session_ = session;
		return this;
	}

	
	@Override public TestSecurity getSecurity()
	{
		if (security_ == null)
			security_ = new TestSecurity(); 
		return security_;
	}
	
			
	public TestRequest setSecurity(TestSecurity security)
	{
		security_ = security;
		return this;
	}

	
	//----------------------------
	// server info
	//----------------------------

	
	@Override public TestServerInfo getServerInfo()
	{
		if (serverInfo_ == null)
			serverInfo_ = new TestServerInfo();
		return serverInfo_;
	}

	
	public TestRequest setServerInfo(TestServerInfo info)
	{
		serverInfo_ = info;
		return this;
	}

	
	//----------------------------
	// remote info
	//----------------------------

	
	@Override public TestRemoteInfo getRemoteInfo()
	{
		if (remoteInfo_ == null)
			remoteInfo_ = new TestRemoteInfo();
		return remoteInfo_;
	}

	
	public TestRequest setRemoteInfo(TestRemoteInfo info)
	{
		remoteInfo_ = info;
		return this;
	}

	
	//----------------------------
	// local info
	//----------------------------

	
	@Override public TestLocalInfo getLocalInfo()
	{
		if (localInfo_ == null)
			localInfo_ = new TestLocalInfo();
		return localInfo_;
	}


	public TestRequest setLocalInfo(TestLocalInfo info)
	{
		localInfo_ = info;
		return this;
	}

	
	/**
	 * @return null.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}
	
	
	public static class Headers extends HeaderMap implements RequestHeaders
	{
		public Headers()
		{
			super(true);
		}
	}

	
	private String method_ = Request.Method.GET;
	private CookieList cookies_;
	private HashMap<String, Object> attributes_;
	private HeaderMap parameters_ = new HeaderMap();
	private Uploads uploads_;
	private String charEncoding_ = "UTF-8";
	private byte[] contentBytes_;
	private String contentString_;
	private TestSecurity security_;
	private TestServerInfo serverInfo_;
	private TestRemoteInfo remoteInfo_;
	private TestLocalInfo localInfo_;
	private Headers headers_ = new Headers();
	private TestSession session_;
	private Locale acceptedLocale_ = Locale.getDefault();
	private static final byte[] EMPTY_BYTES = new byte[0];
}
