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


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.civilian.application.AppConfig;
import org.civilian.application.ConfigKeys;
import org.civilian.content.ContentTypeLookup;
import org.civilian.internal.DefaultApp;
import org.civilian.internal.Logs;
import org.civilian.internal.TempServer;
import org.civilian.internal.admin.AdminApp;
import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;
import org.civilian.template.TemplateWriter;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.FileType;
import org.civilian.util.Settings;
import org.civilian.util.ResourceLoader;


/**
 * Server represents the environment which hosts and runs Civilian applications.
 * In servlet terms the server corresponds to a ServletContext.
 */
public abstract class Server implements PathProvider
{
	/**
	 * The default name of the Civilian config file.
	 */
	public static final String DEFAULT_CONFIG_FILE 	= "civilian.ini";
	
	private static final String CONNECTOR_NAME = "server.connector";
	
	
	/**
	 * The server log. 
	 */
	private static final Logger log = Logs.SERVER;
	
	
	static
	{
		// in a server environment we want generated responses (html especially) to use a simple "\n"
		// for linebreaks 
		TemplateWriter.setDefaultLineSeparator("\n");
	}
	
	
	//--------------------------
	// civilian.ini
	//--------------------------
	
	
	/**
	 * Reads a config or properties file into a settings object.
	 * @param configName the name of the config file.
	 * @return the settings object 
	 * @throws IOException if the file does not exist or cannot be read.
	 */
	public Settings readSettings(String configName) throws IOException
	{
		Check.notNull(configName, "configName");
		File configFile = getConfigFile(configName, FileType.EXISTENT_FILE);
		
		Settings settings = new Settings();
		settings.read(configFile);
		return settings;
	}

	
	//--------------------------
	// init
	//--------------------------

	
	/**
	 * Initializes the Server from the provided settings.
	 */
	protected void init(ClassLoader appClassLoader, Settings settings) throws Exception
	{
		Check.notNull(settings, "settings");
		boolean logInfo = log.isInfoEnabled();
		long time = logInfo ? System.currentTimeMillis() : 0L;

		AppLoader loader = createAppLoader(appClassLoader); 
		
		// 1. read flags
		develop_ = settings.getBoolean(ConfigKeys.DEVELOP, false);
		
		// 2. create applications: this may fail and throw any exception
		List<AppCreateData> addList = new ArrayList<>();
		createCustomApps(loader, settings, addList);
		createAdminApp(loader, new Settings(settings, ConfigKeys.ADMINAPP_PREFIX), addList);
		
		// 3. add apps to server
		for (AppCreateData add : addList)
			addApp(add.app, add.id, add.relativePath, add.settings);

		if (logInfo)
		{
			// only log after apps have been initialized: an app may change the log configuration
			time = System.currentTimeMillis() - time;
			log.info("version " + Version.VALUE + ", start completed in " + ((time) / 1000.0) + "s"); 
		}
	}


	//--------------------------
	// adding apps
	//--------------------------

	
	/**
	 * Holds the data of an application defined in the Civilian config, 
	 * needed to create and add the application.
	 */
	private static class AppCreateData
	{
		public AppCreateData(Application app, String id, Settings settings, String path)
		{
			this.app		= Check.notNull(app, "app"); 
			this.id			= Check.notNull(id, "id");
			this.relativePath		= path;
			this.settings 	= settings;
		}
		
		
		public final String id;
		public final Application app;
		public final String relativePath;
		public final Settings settings;
	}

	
	private void createAdminApp(AppLoader loader, Settings appSettings, List<AppCreateData> addList) throws Exception
	{
		if (AppConfig.isEnabled(appSettings, develop_, develop_))
		{
			String path 	= appSettings.get(ConfigKeys.PATH, ConfigKeys.ADMIN_PATH_DEFAULT);
			Application app	= loader.createAdminApp();
			addList.add(new AppCreateData(app, "civadmin", appSettings, path));
		}
	}
	
	
	private List<String> findCustomAppIds(Settings settings)
	{
		// test if the app ids are explicitly listed
		if (settings.contains(ConfigKeys.APPLICATIONS, false))
			return settings.getList(ConfigKeys.APPLICATIONS);
		
		// else collect them
		List<String> ids = new ArrayList<>(); 
		for (Iterator<String> keys = settings.keys(); keys.hasNext(); )
		{
			String key = keys.next();
			// test the mandatory keys
			if (key.startsWith(ConfigKeys.APP_PREFIX) && 
				(key.endsWith(ConfigKeys.CLASS) || key.endsWith(ConfigKeys.PACKAGE)))
			{
				int p = key.indexOf('.', ConfigKeys.APP_PREFIX.length());
				if (p != -1)
				{
					String id = key.substring(ConfigKeys.APP_PREFIX.length(), p);
					if (!ids.contains(id))
						ids.add(id);
				}
			}
		}
		Collections.sort(ids);
		return ids;
	}
	

	// creates and initializes applications
	private void createCustomApps(AppLoader loader, Settings settings, List<AppCreateData> addList) throws Exception
	{
		for (String id : findCustomAppIds(settings))
		{
			String appPrefix = ConfigKeys.APP_PREFIX + id + '.';
			Settings appSettings = new Settings(settings, appPrefix);
			if (AppConfig.isEnabled(appSettings, true, develop_))
				createCustomApp(loader, appSettings, id, appPrefix, addList);
		}
	}
	
	
	/**
	 * Creates the application for a certain id.
	 * Even if creation fails and we cannot instantiate the application object
	 * we push a DefaultApp object, which - when invoked - will present
	 * an error message.
	 */
	protected void createCustomApp(AppLoader loader, Settings settings, String id, String appPrefix, List<AppCreateData> addList)
		throws Exception
	{
		String path = settings.get(ConfigKeys.PATH, "");
		
		Application app;
		if (settings.contains(ConfigKeys.CLASS))
			app = loader.createApplication(settings.get(ConfigKeys.CLASS));
		else if (settings.contains(ConfigKeys.PACKAGE))
			app = loader.createDefaultApp(settings.get(ConfigKeys.PACKAGE));
		else
		{
			String msg = "application '" + id + "' must either define an application class (key '" + appPrefix + ConfigKeys.CLASS + 
				"') or set the base package for controller classes (key '" + appPrefix + ConfigKeys.PACKAGE + "')";
			throw new IllegalStateException(msg);
		}
		
		addList.add(new AppCreateData(app, id, settings, path));
	}
	
	
	/**
	 * Adds an application to the Server. 
	 * @param app the application. 
	 * @param id the id of the application
	 * @param relPathString the path of the application relative to the server path
	 * @param settings the settings of the application, can be null.
	 * @return was the application successfully initialized?
	 * @throws IllegalArgumentException if the app is contained in another server
	 * 		or its path is already used by another application 
	 */
	protected synchronized boolean addApp(Application app, String id, String relPathString, Settings settings)
	{
		Check.notNull(app, 	"app");
		Check.notNull(id, 	"id");
		Path relPath = new Path(relPathString);
		
		// sanity check 1: app must not be part of a server yet 
		if (app.getServer() != TempServer.INSTANCE)
			throw new IllegalStateException(app + " already added to server " + app.getServer());
		
		// sanity check 2: path and id may not be used by another app
		for (Application prevApp : apps_)
		{
			if (prevApp.getId().equals(id))
				throw new IllegalArgumentException("the app id '" + id + "' is already used by another app");  
			if (prevApp.getRelativePath().equals(relPath))
				throw new IllegalArgumentException("the path '" + relPath + "' is used by app '" + prevApp.getId() + "' and '" + id + "'");  
		}
		
		// add the app to the application list, even if init results in an error
		apps_.add(app);
		
		AppInitData initData = new AppInitData(id, relPath, settings);
		app.init(initData);
		Object connector = connect(app, initData.async);
		if (connector != null)
			app.setAttribute(CONNECTOR_NAME, connector);
		return app.getStatus() == Application.Status.RUNNING;
	}
	

	public class AppInitData
	{
		private AppInitData(String id, Path relativePath, Settings settings)
		{
			this.server = Server.this;
			this.id = id;
			this.relativePath = relativePath;
			this.path = server.getPath().add(relativePath);
			this.settings = settings != null ? settings : new Settings();
		}

		
		public final Server server;
		public final String id;
		public final Path relativePath;
		public final Path path;
		public final Settings settings;
		public boolean async;
	}
	

	//--------------------------
	// connect
	//--------------------------

	
	/**
	 * Connect the application to the server. This should enable the application 
	 * to receive requests.
	 * @param app the app 
	 * @param supportAsync should async operations be supported?
	 * @return an connector object which is {@link Application#getConnector() available} in the application.
	 * 		In an servlet environment, the connector is a servlet
	 * 		which receives requests and directs them to the application.
	 */
	protected abstract Object connect(Application app, boolean supportAsync);
	
	
	/**
	 * Disconnect the application from the server.
	 * Called when the application is closed.
	 */
	protected abstract void disconnect(Application app, Object connector);

	
	//--------------------------
	// close
	//--------------------------

	
	/**
	 * Closes all applications of the Server.
	 * @see Application#close()
	 */
	protected synchronized void close()
	{
		// close in reverse initialization order
		for (int i=apps_.size() - 1; i>=0; i--)
			close(apps_.get(i));
	}

	
	/**
	 * Closes an application and removes it from the Server.
	 */
	protected synchronized void close(Application app)
	{
		if (app == null)
			return;
		if (app.getServer() != this)
			throw new IllegalArgumentException("not my application: " + app);
		
		try
		{
			if (app.getStatus() == Application.Status.RUNNING)
			{
				try
				{
					app.runClose();
				}
				catch(Exception e)
				{
					Logs.SERVER.error("error when closing application " + app, e);
				}
				
				Object connector = app.getAttribute(CONNECTOR_NAME);
				if (connector != null)
					disconnect(app, connector);
			}
		}
		finally
		{
			apps_.remove(app);
		}
	}
	
	
	//--------------------------
	// accessors
	//--------------------------
	
	
	/**
	 * Returns an list of all applications of the Server.
	 * @return the list
	 */
	public synchronized List<Application> getApplications()
	{
		return new ArrayList<>(apps_);
	}
	
	
	/**
	 * Returns the first application with the given class.
	 * @return the application or null
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends Application> T getApplication(Class<T> appClass)
	{
		for (Application app : apps_)
		{
			if (appClass.isAssignableFrom(app.getClass()))
				return (T)app;
		}
		return null;
	}
	
	
	/**
	 * Returns the application with the given id.
	 * @return the application or null
	 */
	public synchronized Application getApplication(String id)
	{
		for (Application app : apps_)
		{
			if (app.getId().equals(id))
				return app;
		}
		return null;
	}
	
	
	/**
	 * Returns the develop flag of the Server. 
	 */
	public boolean develop()
	{
		return develop_;
	}
	
	
	/**
	 * Returns a ResourceLoader to access Server resources. 
	 */
	public abstract ResourceLoader getResourceLoader();

	
	/**
	 * Returns the real path corresponding to the given relative request path.
	 * @param path a path relative to the Server path.
	 * @return the real path or null if it does not map to a real path. 
	 */
	public abstract String getRealPath(String path);
	
	
	/**
	 * Returns the real path corresponding to the given request path.
	 * @param path a path relative to the server path
	 * @return the real path or null if it does not map to a real path. 
	 */
	public String getRealPath(Path path)
	{
		return getRealPath(path.toString()); 
	}

	
	/**
	 * Returns the file corresponding to the given virtual path.
	 * @param path the path
	 * @param fileType the expected fileType or null  
	 * @throws IllegalArgumentException thrown if the path cannot be mapped to a real path
	 */
	public File getRealFile(String path, FileType fileType) throws IllegalArgumentException
	{
		String realPath = getRealPath(path);
		File file = realPath != null ? new File(realPath) : null;
		if (fileType != null)
			fileType.check(file, "getRealFile " + path);
		return file;
	}

	
	/**
	 * Returns the file corresponding to the servers's root directory in the local file system.  
	 */
	public File getRootDir()
	{
		return getRealFile("", FileType.EXISTENT_DIR);
	}
	
	
	/**
	 * Returns if the given path is not valid for a file resource request.
	 * In a servlet environment this is any path pointing into the WEB-INF directory. 
	 */
	public abstract boolean isProhibitedPath(String path);


	/**
	 * Returns the real path of a resource located in a server specific config
	 * directory. For a servlet environment, the config directory is the WEB-INF directory
	 * of the web application. For that a call getConfigPath("myconfig.ini"), would return
	 * a file path whose name ends with "/WEB-INF/myconfig.ini".   
	 */
	public abstract String getConfigPath(String name);

	
	/**
	 * Returns a File for a config path.
	 * @param name a name below the {@link #getConfigPath(String) config path}.
	 * @param fileType the expected fileType or null  
	 */
	public File getConfigFile(String name, FileType fileType) throws IllegalArgumentException
	{
		return getRealFile(getConfigPath(name), fileType);
	}
	
	
	/**
	 * Returns the implementation dependent server version. 
	 */
	public abstract String getServerVersion();


	/**
	 * Returns the implementation dependent server info. 
	 */
	public abstract String getServerInfo();
	
	
	/**
	 * Returns the absolute path of the Server.
	 * It is the root path for all applications deployed in
	 * the server.
	 * Example: If the server is a webserver and listens at http://&lt;host&gt;:&lt;port&gt;/
	 * then the applications are located below
	 * http://&lt;host&gt;:&lt;port&gt;/&lt;server-path&gt;/
	 * In that example the server path corresponds to the path 
	 * of the associated ServletContext. 
	 */
	@Override public abstract Path getPath();

	
	/**
	 * Returns a ContentTypeLookup to translate file names into content types. 
	 */
	public abstract ContentTypeLookup getContentTypeLookup();
	
	
	/**
	 * Returns an attribute which was previously associated with
	 * the server.
	 * @param name the attribute name
	 * @see #setAttribute(String, Object)
	 */
	public abstract Object getAttribute(String name); 
	
	
	/**
	 * Returns an iterator of the attribute names stored in the server. 
	 */
	public abstract Iterator<String> getAttributeNames();
	
	
	/**
	 * Stores an attribute under the given name in the server. 
	 */
	public abstract void setAttribute(String name, Object object); 

	
	/**
	 * Logs a message into the server log file.
	 * In a servlet environment this goes to ServletContext.log()
	 * Note: The Server itself use slf4j for logging.
	 */
	public void log(String msg)
	{
		log(msg, null);
	}


	/**
	 * Logs a message into the server log file.
	 * In a servlet environment this goes to ServletContext.log()
	 * Note: The Server itself use slf4j for logging.
	 */
	public abstract void log(String msg, Throwable throwable);
	

	/**
	 * Returns the underlying implementation of the Server which has the given class
	 * or null, if the implementation has a different class.
	 * In a servlet environment you can access the ServletContext in this way. 
	 */
	public abstract <T> T unwrap(Class<T> implClass);

	
	private AppLoader createAppLoader(ClassLoader cl) throws Exception
	{
		if (cl == null)
			cl = getClass().getClassLoader();
		Class<? extends AppLoader> c = ClassUtil.getClass(AppLoader.class.getName() + "Impl", AppLoader.class, cl);
		Constructor<? extends AppLoader> ctor = c.getConstructor();
		ctor.setAccessible(true);
		return ctor.newInstance();
	}
	
	
	private static interface AppLoader 
	{
		public Application createAdminApp();


		public Application createDefaultApp(String packageName);

	
		public Application createApplication(String className) throws Exception;
	}
	
	
	// instantiated with Class.forName
	@SuppressWarnings("unused")
	private static class AppLoaderImpl implements AppLoader
	{
		public AppLoaderImpl()
		{
		}
		
		
		@Override public Application createAdminApp()
		{
			return new AdminApp();
		}


		@Override public Application createDefaultApp(String packageName)
		{
			return new DefaultApp(packageName);
		}

	
		@Override public Application createApplication(String className) throws Exception
		{
			return ClassUtil.createObject(className, Application.class, getClass().getClassLoader());
		}
	}

	
	/**
	 * The develop flag of the Server.
	 */
	protected boolean develop_;
	private final List<Application> apps_ = new ArrayList<>();
}
