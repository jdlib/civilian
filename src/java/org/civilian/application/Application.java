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
package org.civilian.application;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.civilian.ConfigKeys;
import org.civilian.Logs;
import org.civilian.application.classloader.ClassLoaderFactory;
import org.civilian.application.classloader.ReloadConfig;
import org.civilian.asset.service.AssetConfig;
import org.civilian.asset.service.AssetService;
import org.civilian.asset.service.AssetServices;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.GsonJsonSerializer;
import org.civilian.content.TextSerializer;
import org.civilian.controller.ControllerConfig;
import org.civilian.controller.ControllerNaming;
import org.civilian.controller.ControllerService;
import org.civilian.processor.AssetDispatch;
import org.civilian.processor.ErrorProcessor;
import org.civilian.processor.IpFilter;
import org.civilian.processor.ProcessorConfig;
import org.civilian.processor.ProcessorList;
import org.civilian.processor.ResourceDispatch;
import org.civilian.request.BadRequestException;
import org.civilian.request.Request;
import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;
import org.civilian.resource.Resource;
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.resource.scan.ResourceScan;
import org.civilian.response.Response;
import org.civilian.response.ResponseHandler;
import org.civilian.response.ResponseOwner;
import org.civilian.response.std.ErrorResponseHandler;
import org.civilian.response.std.NotFoundResponseHandler;
import org.civilian.server.Server;
import org.civilian.server.TempServer;
import org.civilian.text.service.LocaleServiceList;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Iterators;
import org.civilian.util.Settings;
import org.slf4j.Logger;


/**
 * Application represents a Civilian application.
 */
public abstract class Application implements ApplicationProvider, PathProvider, ResponseOwner
{
	private static final Logger log = Logs.APPLICATION; 
	
	
	/**
	 * Lifecycle status of the Application. 
	 */
	public enum Status
	{
		/**
		 * The application was created, but not yet initialized.
		 */
		CREATED,
		
		/**
		 * The application was successfully initialized and is now running.
		 */
		RUNNING,
		
		/**
		 * The application threw an error during initialization.
		 */
		ERROR,
		
		/**
		 * The application was closed.
		 */
		CLOSED
	}
	
	
	/**
	 * Creates a new application.
	 * @param pathParams the path parameters used by the application or null
	 * 		if no path parameters are used
	 * @param controllerRootPackage the package of the root controller class. All controller classes
	 * 		are assumed to be in this package or in a subpackage.
	 * 		If it is null, then the package of the application is used.  
	 * 		If the give name starts with "." then the package of the application
	 * 		classes suffixed with that name is used.
	 * @param controllerNaming provides naming conventions for controller classes
	 * 		used during resource scan. If it is null, then the default naming conventions are used.
	 */
	public Application(PathParamMap pathParams, 
		String controllerRootPackage,
		ControllerNaming controllerNaming)
	{
		if (controllerRootPackage == null)
			controllerRootPackage = ClassUtil.getPackageName(getClass());
		else if (controllerRootPackage.startsWith("."))
			controllerRootPackage = ClassUtil.getPackageName(getClass()) + controllerRootPackage;
		
		controllerConfig_ = new ControllerConfig(controllerRootPackage, pathParams, controllerNaming);
	}

	
	/**
	 * Creates a new application.
	 * Shortcut for Application(pathParams, controllerRootPackage, new ControllerNaming())
	 */
	public Application(PathParamMap pathParams, String controllerRootPackage)
	{
		this(pathParams, controllerRootPackage, null);
	}
	
	
	/**
	 * Creates a new application.
	 * Shortcut for Application(pathParams, &lt;application package&gt;, new ControllerNaming())
	 */
	public Application(PathParamMap pathParams)
	{
		this(pathParams, null, null);
	}

	
	/**
	 * Creates a new application.
	 * Shortcut for Application(PathParamMap.EMPTY, &lt;application package&gt;, new ControllerNaming())
	 */
	public Application()
	{
		this(null, null, null);
	}

	
	/**
	 * Called by the server when the application is added to the server.
	 * @param data the init data of the application
	 */
	public final void init(Server.AppInitData data)
	{
		if (getStatus() != Status.CREATED)
			throw new IllegalStateException("already initialized");
		
		server_ 			= data.server;
		id_ 				= data.id;
		path_				= data.path;
		relativePath_		= data.relativePath;

		try
		{
			initApp(data);
			initProcessors(data.settings);
			
			status_ = Status.RUNNING;
			// only log after app has been initialised: an app may change the log configuration
			log.info("initialized app {} at path {}", getId(), getPath());
		}
		catch(Exception e)
		{
			status_ = Status.ERROR;
			log.error("could not initialize " + this, e);
			try
			{
				close();
			}
			catch(Exception e2)
			{
				log.error(toString() + ": error during forced close()", e2);
			}
			
			processors_ = new ProcessorList(new ErrorProcessor(new ErrorResponseHandler(
				data.server.develop(),
				Response.Status.SC503_SERVICE_UNAVAILABLE,
				this + " encountered an error during initialization", e)));
		}
	}


	private void initApp(Server.AppInitData data) throws Exception
	{
		AppConfig appConfig	= new AppConfig(this, data.settings);
		try
		{
			if (appConfig.getInitException() != null)
				throw appConfig.getInitException();
			init(appConfig);  // customize by implementation
		}
		finally
		{
			// even if init throws an error we complete
			// setup of safe application properties
			// since the error page may rely on them
			data.async				= appConfig.getAsync();
			defaultCharEncoding_	= appConfig.getDefaultCharEncoding();
			version_				= appConfig.getVersion();
			assetService_			= initAssets(appConfig.getAssetConfig());
			uploadConfig_			= appConfig.getUploadConfig();
			contentSerializers_ 	= appConfig.getContentSerializers();
			localeServices_			= new LocaleServiceList( 
				appConfig.getTypeLib(),
				appConfig.getMsgBundleFactory(), 
				appConfig.allowUnsupportedLocales(),
				appConfig.getSupportedLocales());
		}
		
		initDefaultContentSerializers();
		
		// determine the ClassLoader factory used to load controllers
		ClassLoaderFactory clFactory = createClassLoaderFactory(appConfig.getReloadConfig());

		// init the controller service
		controllerService_ = new ControllerService(
			controllerConfig_.getPathParams(), 
			localeServices_.getTypeLib(), 
			appConfig.getControllerFactory(),
			clFactory);
			
		// init the resource tree
		rootResource_ = appConfig.getRootResource();
		if (rootResource_ == null)
		{
			// resource tree not specified: generate on the fly
			// use the request classloader so in case we are doing class reload
			// these touched classes will not stick
			rootResource_ = new ResourceScan(getControllerConfig(), clFactory.getRequestClassLoader(), false)
				.getRootResource();		
		}
		
		Resource.Tree tree = rootResource_.getTree();
		tree.setAppPath(getPath());
		tree.setDefaultExtension(appConfig.getDefaultResExtension());
		tree.setControllerService(controllerService_);
	}
	
	
	/**
	 * Initializes the application.
	 * @param config allows to configure the application. 
	 * @throws Exception if an error during initialization occurs.
	 */
	protected abstract void init(AppConfig config) throws Exception;
	
	
	/**
	 * Adds ContentSerializers for text/plain and application/json
	 * if not done in init(Appconfig). 
	 */
	private void initDefaultContentSerializers()
	{
		if (getContentSerializer(ContentType.TEXT_PLAIN) == null)
			contentSerializers_.put(ContentType.TEXT_PLAIN.getValue(), new TextSerializer());
		if ((getContentSerializer(ContentType.APPLICATION_JSON) == null) && 
			ClassUtil.getPotentialClass("com.google.gson.Gson", Object.class, null) != null)
			contentSerializers_.put(ContentType.APPLICATION_JSON.getValue(), new GsonJsonSerializer());
	}
	
	
	/**
	 * Initializes the AssetService of the application. Called after
	 * {@link #init(AppConfig)} finished.
	 * @return the AssetService
	 */
	protected AssetService initAssets(AssetConfig config)
	{
		AssetService service = AssetServices.combine(Path.ROOT, config.getLocations());
		if (config.getLocationCount() > 0)
			service = AssetServices.makeCaching(service, config.getMaxCachedSize()); 
		service.init(getPath(), getDefaultCharEncoding(), config.getContentTypeLookup());
		return service;
	}
	
	
	private void initProcessors(Settings settings) throws Exception
	{
		ProcessorConfig pconfig = new ProcessorConfig();
		
		// an optional IpFilter as first processor
		String[] ipList = settings.getArray(ConfigKeys.IP);
		if (ipList.length > 0)
			pconfig.addLast(new IpFilter(ipList));
		
		// resource dispatch as next processor
		pconfig.addLast(new ResourceDispatch(rootResource_));
		
		// followed by an optional AssetDispatch
		if (getAssetService().hasAssets())
			pconfig.addLast(new AssetDispatch(getServer()::isProhibitedPath, getAssetService()));
		
		// allow derived implementations to tweak the processor list
		initProcessors(pconfig);
		
		// initialize the processors
		processors_ = new ProcessorList(pconfig.getList());
	}

	
	/**
	 * Allows derived applications to initialize the processor list.
	 * By default the list contains these processors:
	 * <ol>
	 * <li>IpFilter, if the Civilian config specified a list of allowed ips
	 * <li>AssetDispatch, to access CSS, JS and other static resource of the application, if 
	 * 		the asset config is enabled and contains asset locations
	 * <li>ResourceDispatch, to dispatch requests to resources
	 * </ol>
	 * The process list can be rearranged, reduced or enhanced depending
	 * on the needs of your application.  
	 * @throws Exception if an error during initialization occurs.
	 */
	protected void initProcessors(ProcessorConfig config) throws Exception
	{
	}
	
	
	//--------------------------------
	// close
	//--------------------------------
	
	
	/**
	 * Closes the application. Called when the server shuts down
	 * or the application is removed from the server.
	 */
	// TODO must not be public but still accessible from server
	public void runClose() throws Exception
	{
		status_ = Status.CLOSED;
		try
		{
			processors_.close();
		}
		finally
		{
			close();
		}
	}

	
	/**
	 * Closes the application. Called when the server shuts down
	 * or the application is removed from the server.
	 * The method is also called when {@link #init(AppConfig)} threw an exception.
	 * Therefore you need to take into account that your app resource may
	 * not have been fully initialized.
	 */
	protected abstract void close() throws Exception;


	//--------------------------------
	// accessors
	//--------------------------------

	
	/**
	 * Implements ApplicationProvider and returns this.
	 */
	@Override public Application getApplication()
	{
		return this;
	}
	
	
	/**
	 * Returns the application id.
	 * The id is used to identify the application within
	 * the {@link Server}. The id was defined within
	 * <code>civilian.ini</code>.
	 * @see Server#getApplication(String) 
	 */
	public String getId()
	{
		return id_;
	}
	
	
	/**
	 * Returns the application version.
	 * The optional version can be defined during setup.
	 * @see AppConfig#setVersion(String)
	 */
	public String getVersion()
	{
		return version_;
	}

	
	/**
	 * Returns the develop flag of the server.
	 * The develop flag is defined in the Civilian config file.
	 * @return if true the application runs in development mode,
	 * 		else in production mode.
	 * @see Server#develop()
	 */
	public boolean develop()
	{
		return server_.develop();
	}


	/**
	 * Returns the server in which the application is running.
	 */
	public Server getServer()
	{
		return server_;
	}
	
	
	/**
	 * Returns the default encoding for textual content of responses.
	 */
	@Override public String getDefaultCharEncoding()
	{
		return defaultCharEncoding_;
	}

	
	/**
	 * Returns the path from the server root to the application.
	 */
	@Override public Path getPath()
	{
		return path_;
	}

	
	/**
	 * Returns the relative path from the server to the application.
	 */
	public Path getRelativePath()
	{ 
		return relativePath_;
	}

	
	/**
	 * Returns the application status.
	 */
	public Status getStatus()
	{
		return status_;
	}

	
	/**
	 * Returns the AssetService used to serve assets.
	 */
	public AssetService getAssetService()
	{
		return assetService_;
	}

	
	/**
	 * Returns the processor list.
	 * The processor list contains the processors which are used to process requests.
	 */
	public ProcessorList getProcessors()
	{
		return processors_;
	}


	/**
	 * Returns the root resource of the application.
	 */
	public Resource getRootResource()
	{
		return rootResource_;
	}
	

	/**
	 * Returns the controller service which provides
	 * access to controller types. 
	 */
	public ControllerService getControllerService()
	{
		return controllerService_;
	}
	

	/**
	 * Returns the controller config. 
	 */
	public ControllerConfig getControllerConfig()
	{
		return controllerConfig_;
	}

	
	/**
	 * Returns the LocaleServiceList.
	 */
	@Override
	public LocaleServiceList getLocaleServices()
	{
		return localeServices_;
	}
	

	/**
	 * Returns a ContentSerializer for the content type.
	 * @return the ContentSerializer or null if no suitable serializer is available
	 * By default the application possesses ContentSerializers for text/plain and
	 * application/json (based on GSON).
	 * @see AppConfig#registerContentSerializer(ContentType, ContentSerializer)
	 */
	public ContentSerializer getContentSerializer(ContentType contentType)
	{
		return contentSerializers_.get(contentType != null ? contentType.getValue() : null);
	}
	
	
	/**
	 * Returns a ContentSerializer for the content type.
	 * @return the ContentSerializer or null if no suitable serializer is available
	 * By default the application possesses ContentSerializers for text/plain and
	 * application/json (based on GSON).
	 * @see AppConfig#registerContentSerializer(String, ContentSerializer)
	 */
	@Override
	public ContentSerializer getContentSerializer(String contentType)
	{
		return contentSerializers_.get(contentType);
	}

	
	/**
	 * Returns an iterator for all ContentTypes with a registered ContentSerializer.
	 */
	public Iterator<String> getContentSerializerTypes()
	{
		return Iterators.unmodifiable(contentSerializers_.keySet().iterator());
	}

	
	/**
	 * Returns the UploadConfig which defines upload limits and location. 
	 */
	public UploadConfig getUploadConfig()
	{
		return uploadConfig_;
	}


	/**
	 * Stores an attribute under the given name in the application. 
	 */
	public void setAttribute(String name, Object value)
	{
		attributes_.put(name, value);
	}
	
	
	/**
	 * Returns an attribute which was previously associated with
	 * the application.
	 * @param name the attribute name
	 * @see #setAttribute(String, Object)
	 */
	public Object getAttribute(String name)
	{
		return attributes_.get(name);
	}


	/**
	 * Returns an iterator of the attribute names stored in the application. 
	 */
	public Iterator<String> getAttributeNames()
	{
		return attributes_.keySet().iterator();
	}


	private ClassLoaderFactory createClassLoaderFactory(ReloadConfig reloadConfig) 
	{
		ClassLoader appClassLoader = getClass().getClassLoader();
		boolean reloading = reloadConfig != null && reloadConfig.isValid();
		return reloading ?
			new ClassLoaderFactory.Dev(appClassLoader, reloadConfig) :
			new ClassLoaderFactory.Production(appClassLoader);
	}
			

	//-----------------------------------
	// process
	//-----------------------------------

	
	/**
	 * Processes a request. 
	 * The default implementation forwards the request to the processor pipeline.
	 * If no processor handled the request, it is forwarded to the {@link #createNotFoundHandler() NotFoundResponse}.
	 * If an exception occurs during request processing {@link #onError(Request, Throwable)} is called  
	 */
	public void process(Request request)
	{
		Check.notNull(request, "request");
		if (request.getApplication() != this)
			throw new IllegalArgumentException("not my request: " + request.getApplication());
		
		try
		{
			if (log.isDebugEnabled())
				log.debug("{} {}", request.getMethod(), request.getPath());
			
			try
			{
				boolean processed = processors_.process(request);
				if (!processed)
					createNotFoundHandler().send(request.getResponse());
			}
			finally
			{
				if (!request.isAsyncStarted())
					request.getResponse().closeContent();
			}
		}
		catch (Throwable t)
		{
			try
			{
				onError(request, t);
			}
			catch(Exception e)
			{
				log.error("exception during exception processing", e); 
			}
		}
	}
	
	
	/**
	 * Called when an error during request processing occurs.
	 * The default implementation
	 * <ul>
	 * <li>recognizes a {@link BadRequestException} and calls {@link Response#sendError(int, String, Throwable)}
	 * 		using the status code, the message and cause of the BadRequestException.
	 * <li>otherwise calls {@link #ignoreError(Throwable)} if the 
	 * 		error should be ignored. 
	 * 		If it should not be ignored, it logs the error and - if the response is not yet committed -
	 * 		sends the error via {@link Response#sendError(int, String, Throwable)}
	 * </ul> 
	 */
	protected void onError(Request request, Throwable error) throws Exception
	{
		Check.notNull(request, "request");
		Check.notNull(error, "error");
		Response response = request.getResponse();
		
		if (error instanceof BadRequestException)
		{
			if (log.isDebugEnabled())
				log.debug("bad request", error.getCause());
			
			BadRequestException e = (BadRequestException)error;
			if (!response.isCommitted())
				response.sendError(e.getStatusCode(), e.getMessage(), e.getCause());
		}
		else if (!ignoreError(error))
		{
			log.error("onError", error);
			if (!response.isCommitted())
				response.sendError(Response.Status.INTERNAL_SERVER_ERROR, null, error);
		}
	}
	
	
	/**
	 * Called by onError to decide if a thrown error should be ignored.
	 * A good example would be to ignore socket errors when the
	 * socket connection was aborted by the client.
	 * The default implementation returns false (not to ignore the error).
	 * Derived application classes should override the method and implement
	 * their own strategy. 
	 */
	public boolean ignoreError(Throwable error)
	{
		// String s = error.toString();
		// return s.contains("connection abort") || s.contains("ClientAbortException");
		return false;
	}

	
	/**
	 * Returns an ResponseHandler which is used by the response
	 * to send an error to the client. Applications can 
	 * return a different implementation to tweak the error response. 
	 * @see Response#sendError(int)  
	 * @see Response#sendError(int, String, Throwable)  
	 */
	public ResponseHandler createErrorHandler(int statusCode, String message, Throwable error)
	{
		return new ErrorResponseHandler(develop(), statusCode, message, error);
	}
	
	
	/**
	 * Returns a ResponseHandler object which is used 
	 * to send a response to the client, if no processor handled the request.
	 * Applications can return a different implementation to tweak the not-found-response.
	 */
	public ResponseHandler createNotFoundHandler()
	{
		return new NotFoundResponseHandler(develop());
	}
	
	
	/**
	 * Returns a string representation of the application.
	 */
	@Override public String toString()
	{
		return "app '" + id_ + "'";  
	}
	
	
	// properties all have reasonable defaults, initialized again when added to the server
	private String id_ = "?";
	private String defaultCharEncoding_ = ConfigKeys.ENCODING_DEFAULT;
	private Path relativePath_ = Path.ROOT;
	private Path path_ = Path.ROOT;
	private Server server_ = TempServer.INSTANCE;
	
	// properties set after init(AppConfig)
	private ControllerService controllerService_;
	private final ControllerConfig controllerConfig_;
	private LocaleServiceList localeServices_;
	private Resource rootResource_;
	private AssetService assetService_;
	private UploadConfig uploadConfig_;
	private String version_;
	private ProcessorList processors_ = ProcessorList.EMPTY;
	private final HashMap<String, Object> attributes_ = new HashMap<>();
	private Map<String,ContentSerializer> contentSerializers_ = Collections.<String,ContentSerializer>emptyMap();

	// lifecycle property 
	private Status status_ = Status.CREATED;
}
