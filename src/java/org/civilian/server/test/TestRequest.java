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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.civilian.Application;
import org.civilian.Controller;
import org.civilian.Request;
import org.civilian.Resource;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.internal.AbstractRequest;
import org.civilian.internal.ParamList;
import org.civilian.request.AsyncContext;
import org.civilian.request.CookieList;
import org.civilian.request.Upload;
import org.civilian.resource.PathParam;
import org.civilian.resource.Url;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;
import org.civilian.util.Iterators;


/**
 * TestRequest is a {@link Request} implementation to be used in a test environment.
 * Create a TestRequest for an application, set its properties, {@link #run() run}
 * the request, and then evaluate the returned response.
 */
public class TestRequest extends AbstractRequest
{
	/**
	 * Creates a TestRequest.
	 */
	public TestRequest(Application application, String relativePath)
	{
		super(application, relativePath);
		setResponse(testResponse_ = new TestResponse(this));
	}

	
	/**
	 * Creates a TestRequest.
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
		this(createTestApp());
	}
	
	
	private static Application createTestApp()
	{
		TestApp app = new TestApp();
		app.init();
		return app;
	}
	
	
	/**
	 * Creates a TestRequest which copies the settings from another request.
	 */
	public TestRequest(TestRequest request)
	{
		super(request);
		setResponse(testResponse_ = new TestResponse(this, request.getTestResponse()));
		
		method_ 			= request.method_;
		cookies_			= request.cookies_;
		attributes_			= request.attributes_;
		parameters_ 		= request.parameters_;
		uploads_			= request.uploads_;
		uploadError_		= request.uploadError_;
		charEncoding_	= request.charEncoding_;
		contentBytes_ 		= request.contentBytes_;
		contentString_ 		= request.contentString_;
		security_ 			= request.security_;
		serverInfo_ 		= request.serverInfo_;
		remoteInfo_ 		= request.remoteInfo_;
		localInfo_ 			= request.localInfo_;
		headers_ 			= request.headers_;
		session_ 			= request.session_;
		acceptedLocale_ 	= request.acceptedLocale_; 
	}

	
	/**
	 * Returns the TestResponse object associated with the TestRequest. 
	 */
	public TestResponse getTestResponse()
	{
		return testResponse_;
	}

	
	//-----------------------------
	// run
	//-----------------------------

	
	/**
	 * Invokes {@link Application#process(Request)} with this request. 
	 * Before calling process(), the TestResponse associated with this request
	 * is cleared first and the content of this request is reset}.
	 * @return the TestResponse
	 */
	public TestResponse run() throws Exception
	{
		testResponse_.clear();
		resetAsyncContext();
		resetContentInput();
		getApplication().process(this);
		getResponse().flushBuffer();
		return testResponse_;
	}
	
	
	//-----------------------------
	// method
	//-----------------------------

	
	/**
	 * Returns the request method.
	 */
	@Override public String getMethod()
	{
		return method_;
	}
	

	/**
	 * Sets the request method.
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
	 * @param resource the resource which defines the path
	 * @param pathParams the path parameters of the resource  
	 * @return this
	 */
	public TestRequest setPath(Resource resource, Object... pathParams)
	{
		Url url = new Url(this, resource);
		url.setPathParams(pathParams);
		return setPath(url);
	}

	
	/**
	 * Sets the path of the request relative to the application path.
	 * @param controllerClass a controller class. The resource associated with the controller
	 * 		is used to define the path
	 * @param pathParams the path parameters of the resource  
	 * @return this
	 */
	public TestRequest setPath(Class<? extends Controller> controllerClass, Object... pathParams)
	{
		Url url = new Url(this, controllerClass);
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
		for (int i=0; i<url.getQueryParamCount(); i++)
		{
			Url.QueryParam qp = url.getQueryParam(i);
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
	 * Returns the path string.
	 */
	@Override public String getOriginalPath()
	{
		return getPath().print();
	}
	

	//----------------------------
	// attributes
	//----------------------------
	
	
	/**
	 * Returns an attribute.
	 */
	@Override public Object getAttribute(String name)
	{
		return attributes_ != null ? attributes_.get(name) : null;
	}


	/**
	 * Returns the attribute names.
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
	 * Returns the CookieList.
	 */
	@Override public CookieList getCookies()
	{
		if (cookies_ == null)
			cookies_ = new CookieList();
		return cookies_;
	}

	
	/**
	 * Allows to set the cookies.
	 * @return this
	 */
	public TestRequest setCookies(CookieList cookies)
	{
		cookies_ = cookies;
		return this;
	}

	
	/**
	 * Allows to set the cookies.
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
	 * Returns a request parameter.
	 */
	@Override public String getParameter(String name)
	{
		return parameters_.get(name);
	}
	

	/**
	 * Returns all values of a request parameter.
	 */
	@Override public String[] getParameters(String name)
	{
		return parameters_.getAll(name);
	}

	
	/**
	 * Returns the names of all request parameters.
	 */
	@Override public Iterator<String> getParameterNames()
	{
		return parameters_.iterator();
	}

	
	/**
	 * Returns a map of all request parameters.
	 */
	@Override public Map<String, String[]> getParameterMap()
	{
		return parameters_.getMap();
	}
	
	
	/**
	 * Returns a ParamList containing the request parameters.
	 * The ParamList allows you to add parameters. 
	 */
	public ParamList getParameterList()
	{
		return parameters_;
	}
	

	/**
	 * Sets a request parameter.
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
		parameters_.clear();;
	}
	

	//----------------------------
	// uploads
	//----------------------------
	
	
	/**
	 * Returns if the request has uploads.
	 */
	@Override public boolean hasUploads()
	{
		return uploads_ != null && uploads_.size() > 0;
	}
	

	/**
	 * Returns a upload.
	 */
	@Override public Upload getUpload(String name)
	{
		return uploads_ != null ? uploads_.get(name) : null;
	}

	
	/**
	 * Returns all uploads for the given name.
	 */
	@Override public Upload[] getUploads(String name)
	{
		Upload upload = getUpload(name);
		return upload != null ? new Upload[] { upload } : new Upload[0];
	}

	
	/**
	 * Returns the names of all uploads.
	 */
	@Override public Iterator<String> getUploadNames()
	{
		return uploads_ != null ? uploads_.keySet().iterator() : Iterators.<String>empty();
	}
	
	
	/**
	 * Allows you to add an upload.
	 * @return this
	 */
	public TestRequest addUpload(Upload upload)
	{
		if (uploads_ == null)
			uploads_ = new HashMap<>();
		uploads_.put(upload.getName(), upload);
		return this;
	}

	
	/**
	 * Adds and returns a TestUpload object. Use setters on
	 * the returned object to set further details.
	 */
	public TestUpload addUpload(String name, String fileName)
	{
		TestUpload upload = new TestUpload(name, fileName);
		addUpload(upload);
		return upload;
	}

	
	/**
	 * Allows you to clear the uploads.
	 * @return this
	 */
	public TestRequest clearUploads()
	{
		uploads_ = null;
		return this;
	}
	
	
	/**
	 * Returns any upload error.
	 */
	@Override public Exception getUploadError()
	{
		return uploadError_;
	}
	

	/**
	 * Allows you to set the upload error.
	 * @return this
	 */
	public TestRequest setUploadError(Exception error)
	{
		uploadError_ = error;
		return this;
	}

	
	//----------------------------
	// content
	//----------------------------

	
	/**
	 * Returns the content encoding.
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
	 * Returns the content length.
	 */
	@Override public long getContentLength()
	{
		return getContentBytes().length;
	}


	/**
	 * Returns the content-type.
	 */
	@Override protected ContentType getContentTypeImpl()
	{
		return null;
	}


	/**
	 * Returns an input stream for the content.
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
				String encoding = charEncoding_ != null ? charEncoding_ : getApplication().getDefaultCharEncoding();
				ByteArrayOutputStream out = new ByteArrayOutputStream(); 
				try
				{
					OutputStreamWriter writer = new OutputStreamWriter(out, encoding);
					IoUtil.copy(new StringReader(contentString_), writer);
					writer.flush();
				}
				catch (Exception e)
				{
					throw new IllegalStateException("unexpected", e);
				}
				contentBytes_ = out.toByteArray();
			}
		}
		
		return contentBytes_;
	}
	


	/**
	 * Returns a reader for the content.
	 */
	@Override protected Reader getContentReaderImpl() throws IOException
	{
		if (contentString_ != null)
			return new StringReader(contentString_);
		else
		{
			String encoding = charEncoding_ != null ? charEncoding_ : getApplication().getDefaultCharEncoding();
			return new InputStreamReader(new ByteArrayInputStream(getContentBytes()), encoding);
		}
	}
	

	/**
	 * Allows you to set the content.
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
				ContentSerializer serializer = getApplication().getContentSerializer(contentType);
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
	 * Returns the request headers.
	 */
	@Override public ParamList getHeaders()
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
			acceptedLocale_ = getApplication().getLocaleServices().getDefaultLocale();
		return acceptedLocale_;
	}


	@Override public Iterator<Locale> getAcceptedLocales()
	{
		return Iterators.forValue(getAcceptedLocale());
	}

	
	/**
	 * Allows you to set the preferred content types.
	 * @return this
	 */
	public TestRequest setAcceptedContentTypes(ContentTypeList contentTypes)
	{
		acceptedContentTypes_ = Check.notNull(contentTypes, "contentTypes");
		getHeaders().set("Accept", acceptedContentTypes_.toString());
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
	// async
	//----------------------------

	
	@Override public boolean isAsyncSupported()
	{
		return false;
	}


	@Override protected AsyncContext createAsyncContext()
	{
		return new TestAsyncContext(this);
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
	 * Returns null.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}

	
	private String method_ = Request.Method.GET;
	private CookieList cookies_;
	private HashMap<String, Object> attributes_;
	private ParamList parameters_ = new ParamList();
	private HashMap<String, Upload> uploads_;
	private Exception uploadError_;
	private String charEncoding_ = "UTF-8";
	private byte[] contentBytes_;
	private String contentString_;
	private TestSecurity security_;
	private TestServerInfo serverInfo_;
	private TestRemoteInfo remoteInfo_;
	private TestLocalInfo localInfo_;
	private ParamList headers_ = new ParamList(true);
	private TestSession session_;
	private Locale acceptedLocale_ = Locale.getDefault();
	private TestResponse testResponse_;
	private static final byte[] EMPTY_BYTES = new byte[0];
}
