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
package org.civilian;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.civilian.application.AppConfig;
import org.civilian.application.ConfigKeys;
import org.civilian.application.UploadConfig;
import org.civilian.asset.AssetConfig;
import org.civilian.asset.AssetService;
import org.civilian.asset.AssetServices;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.GsonJsonSerializer;
import org.civilian.content.TextSerializer;
import org.civilian.controller.ControllerConfig;
import org.civilian.controller.ControllerNaming;
import org.civilian.controller.ControllerService;
import org.civilian.controller.classloader.ReloadConfig;
import org.civilian.internal.Logs;
import org.civilian.internal.TempContext;
import org.civilian.processor.AssetDispatch;
import org.civilian.processor.ErrorProcessor;
import org.civilian.processor.IpFilter;
import org.civilian.processor.ProcessorConfig;
import org.civilian.processor.ProcessorList;
import org.civilian.processor.ResourceDispatch;
import org.civilian.provider.ApplicationProvider;
import org.civilian.provider.ContextProvider;
import org.civilian.provider.PathProvider;
import org.civilian.request.BadRequestException;
import org.civilian.resource.Path;
import org.civilian.resource.PathParamMap;
import org.civilian.resource.ResourceConfig;
import org.civilian.resource.scan.ResourceScan;
import org.civilian.response.std.ErrorResponse;
import org.civilian.response.std.NotFoundResponse;
import org.civilian.text.LocaleService;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Iterators;
import org.civilian.util.Settings;
import org.slf4j.Logger;


/**
 * Application represents a Civilian application.
 */
public abstract class Application implements ApplicationProvider, ContextProvider, PathProvider
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
		
		resourceConfig_ 	= new ResourceConfig(pathParams);
		controllerConfig_ 	= new ControllerConfig(controllerRootPackage, controllerNaming);
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
	 * Shortcut for Application(pathParams, <application package>, new ControllerNaming())
	 */
	public Application(PathParamMap pathParams)
	{
		this(pathParams, null, null);
	}

	
	/**
	 * Creates a new application.
	 * Shortcut for Application(PathParamMap.EMPTY, <application package>, new ControllerNaming())
	 */
	public Application()
	{
		this(null, null, null);
	}

	
	/** 
	 * Called by the Context.
	 */
	void setConnector(Object connector)
	{
		connector_ = connector;  
	}
	

	/**
	 * Returns the connector of the application. A connector is a service which allows
	 * the application to receive requests. In case of an servlet container, the connector
	 * is a servlet.
	 */
	public Object getConnector()
	{
		return connector_;
	}

	
	/**
	 * Returns the connector of the application which has the given class
	 * or null, if the connector has a different class.
	 */
	public <T> T getConnector(Class<T> connectorClass)
	{
		return ClassUtil.unwrap(connector_, connectorClass);
	}

	
	/**
	 * Called by the context when the application is added to the context.
	 * @param context the context
	 * @param id the application id
	 * @param relativePath the relative path of the application within the context
	 * @param settings the application settings
	 */
	final InitResult init(Context context, String id, Path relativePath, Settings settings)
	{
		context_ 			= context;
		id_ 				= id;
		relativePath_		= relativePath;
		path_				= context.getPath().add(relativePath);
		InitResult result	= new InitResult();

		if (getStatus() != Status.CREATED)
			throw new IllegalStateException("already initialized");
		if (settings == null)
			settings = new Settings();
		
		try
		{
			initApp(settings, result);
			initProcessors(settings);
			
			status_ = Application.Status.RUNNING;
			log.info("init {} at {}", getId(), getPath());
			
			result.success = true;
		}
		catch(Exception e)
		{
			status_ = Application.Status.ERROR;
			log.error("could not initialize " + this, e);
			try
			{
				close();
			}
			catch(Exception e2)
			{
				log.error(toString() + ": error during forced close()", e2);
			}
			
			processors_ = new ProcessorList(new ErrorProcessor(
				Response.Status.SC503_SERVICE_UNAVAILABLE,
				this + " encountered an error during initialization", e));
		}
		
		return result;
	}


	private void initApp(Settings settings, InitResult initResult) throws Exception
	{
		AppConfig appConfig	= createConfig(settings);
		try
		{
			appConfig.init(); // inits unsafe-config settings
			init(appConfig);
		}
		finally
		{
			// even if init throws an error we complete
			// setup of safe application properties
			// since the error page may rely on them
			initResult.connect	= appConfig.getConnect();
			initResult.async	= appConfig.getAsync();
			encoding_			= appConfig.getEncoding();
			version_			= appConfig.getVersion();
			typeLib_ 			= appConfig.getTypeLibrary();
			assetService_		= initAssets(appConfig.getAssetConfig());
			uploadConfig_		= appConfig.getUploadConfig();
			contentSerializers_ = appConfig.getContentSerializers();
			localeService_		= new LocaleService( 
				appConfig.getMsgBundleFactory(), 
				appConfig.allowUnsupportedLocales(),
				appConfig.getSupportedLocales());
		}
		
		initDefaultContentSerializers();

		// init the controller service
		controllerService_ = new ControllerService(
			resourceConfig_.getPathParams(), 
			getTypeLib(), 
			appConfig.getControllerFactory(),
			appConfig.getReloadConfig());
			
		// init the resource tree
		rootResource_ = appConfig.getRootResource();
		if (rootResource_ == null)
			rootResource_ = generateResourceTree(appConfig.getReloadConfig());
		
		Resource.Tree tree = rootResource_.getTree();
		tree.setAppPath(getPath());
		tree.setDefaultExtension(appConfig.getDefaultResExtension());
		tree.setControllerService(controllerService_);
	}
	
	
	static class InitResult
	{
		boolean success;
		boolean connect;
		boolean async;
	}
	
	
	/**
	 * Initializes the application.
	 * @param config allows to configure the application. 
	 * @throws Exception if an error during initialization occurs.
	 */
	protected abstract void init(AppConfig config) throws Exception;
	
	
	/**
	 * Creates the AppConfig during initialization. 
	 * Derived implementations may overwrite this method
	 * if they intend to initialize the AppConfig in a different way.
	 */
	protected AppConfig createConfig(Settings settings)
	{
		return new AppConfig(this, settings);
	}


	/**
	 * Adds ContentSerializers for text/plain and application/json
	 * if not done in init(Appconfig). 
	 */
	private void initDefaultContentSerializers()
	{
		if (getContentSerializer(ContentType.TEXT_PLAIN) == null)
			contentSerializers_.put(ContentType.TEXT_PLAIN, new TextSerializer());
		if ((getContentSerializer(ContentType.APPLICATION_JSON) == null) && 
			ClassUtil.getPotentialClass("com.google.gson.Gson", Object.class, null) != null)
			contentSerializers_.put(ContentType.APPLICATION_JSON, new GsonJsonSerializer());
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
		service.init(getPath(), getEncoding(), config.getContentTypeLookup());
		return service;
	}
	
	
	private void initProcessors(Settings settings) throws Exception
	{
		ProcessorConfig pconfig = new ProcessorConfig();
		
		// an optional IpFilter as first processor
		String[] ipList = settings.getList(ConfigKeys.IP);
		if (ipList.length > 0)
			pconfig.addLast(new IpFilter(ipList));
		
		// resource dispatch as next processor
		pconfig.addLast(new ResourceDispatch(rootResource_));
		
		// followed by an optional AssetDispatch
		if (getAssetService().hasAssets())
			pconfig.addLast(new AssetDispatch(getAssetService()));
		
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
	// resource/path-setup
	//--------------------------------
	
	
	/**
	 * Generates the resource tree at application startup.
	 * Called during application initialization when no 
	 * resource tree was configured.
	 * @see AppConfig#setResourceRoot(Resource)
	 */
	public Resource generateResourceTree(ReloadConfig reloadConfig) throws Exception
	{
		ClassLoader loader = getClass().getClassLoader();
		if (reloadConfig != null)
			loader = reloadConfig.createClassLoader();

		ResourceScan scan = new ResourceScan(
			getControllerConfig().getRootPackage(),
			getControllerConfig().getNaming(),
			getResourceConfig().getPathParams(), 
			loader);
		return scan.run();
	}

	
	//--------------------------------
	// close
	//--------------------------------
	
	
	/**
	 * Closes the application. Called when the context shuts down
	 * or the application is removed from the context.
	 */
	void runClose() throws Exception
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
	 * Closes the application. Called when the context shuts down
	 * or the application is removed from the context.
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
	 * the {@link Context}. The id was defined within
	 * <code<civilian.ini</code.
	 * @see Context#getApplication(String). 
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
	 * Returns the develop flag of the context.
	 * The develop flag is defined in the Civilian config file.
	 * @return if true the application runs in development mode,
	 * 		else in production mode.
	 * @see Context#develop()
	 */
	public boolean develop()
	{
		return context_.develop();
	}


	/**
	 * Returns the context in which the application is running.
	 */
	@Override public Context getContext()
	{
		return context_;
	}
	
	
	/**
	 * Returns the default encoding for textual content of responses.
	 */
	public String getEncoding()
	{
		return encoding_;
	}

	
	/**
	 * Returns the path from the server root to the application.
	 */
	@Override public Path getPath()
	{
		return path_;
	}

	
	/**
	 * Returns the relative path from the context to the application.
	 */
	@Override public Path getRelativePath()
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
	 * Returns the resource config which stores resource related settings.
	 */
	public ResourceConfig getResourceConfig()
	{
		return resourceConfig_;
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
	 * Returns the the type library used by the application.
	 * The library contains type implementations which are used
	 * to serialize (format and parse) values of these types.
	 */
	public TypeLib getTypeLib()
	{
		return typeLib_;
	}
	

	/**
	 * Returns the locale service which is used for localization.
	 */
	public LocaleService getLocaleService()
	{
		return localeService_;
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
		return contentSerializers_.get(contentType);
	}
	
	
	/**
	 * Returns an iterator for all ContentTypes with a registered ContentSerializer.
	 */
	public Iterator<ContentType> getContentSerializerTypes()
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


	//-----------------------------------
	// process
	//-----------------------------------

	
	/**
	 * Processes a request. 
	 * The default implementation forwards the request to the processor pipeline.
	 * If no processor handled the request, it is forwarded to the {@link #createNotFoundResponse() NotFoundResponse}.
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
					createNotFoundResponse().send(request.getResponse());
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
	 * Returns an ErrorResponseobject which is used by the response
	 * to send an error to the client. Applications can 
	 * return a different implementation to tweak the error response. 
	 * @see Response#sendError(int)  
	 * @see Response#sendError(int, String, Throwable)  
	 */
	public ErrorResponse createErrorResponse()
	{
		return new ErrorResponse();
	}
	
	
	/**
	 * Returns a NotFoundResponse object which is used by 
	 * to send a response to the client, if not processor handled the request.
	 * Applications can return a different implementation to tweak the not-found-response.
	 */
	public NotFoundResponse createNotFoundResponse()
	{
		return new NotFoundResponse();
	}
	
	
	/**
	 * Returns a string representation of the application.
	 */
	@Override public String toString()
	{
		return "app '" + id_ + "'";  
	}
	
	
	// properties all have reasonable defaults, initialized again when added to the context
	private String id_ = "?";
	private String encoding_ = ConfigKeys.ENCODING_DEFAULT;
	private Path relativePath_ = Path.ROOT;
	private Path path_ = Path.ROOT;
	private Context context_ = TempContext.INSTANCE;
	
	// properties set after init(AppConfig)
	private ControllerService controllerService_;
	private ControllerConfig controllerConfig_;
	private LocaleService localeService_;
	private Resource rootResource_;
	private ResourceConfig resourceConfig_;
	private TypeLib typeLib_;
	private AssetService assetService_;
	private UploadConfig uploadConfig_;
	private String version_;
	private Object connector_;
	private ProcessorList processors_ = ProcessorList.EMPTY;
	private final HashMap<String, Object> attributes_ = new HashMap<>();
	private Map<ContentType,ContentSerializer> contentSerializers_ = Collections.<ContentType,ContentSerializer>emptyMap();

	// lifecycle property 
	private Status status_ = Status.CREATED;
}
