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
package org.civilian.server;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.civilian.ConfigKeys;
import org.civilian.Logs;
import org.civilian.Version;
import org.civilian.application.factory.AppFactory;
import org.civilian.content.ContentTypeLookup;
import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;
import org.civilian.template.TemplateWriter;
import org.civilian.util.Check;
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

		// 1. read flags
		develop_ = settings.getBoolean(ConfigKeys.DEVELOP, false);
		
		// 2. create applications: this may fail and throw any exception
		List<AppFactory.Data> dataList = AppFactory.load(appClassLoader, settings, develop_);
		
		// 3. add apps to server
		for (AppFactory.Data data : dataList)
			addApp(data.app, data.id, data.relativePath, data.settings);

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
	 * Adds an application to the Server. 
	 * @param app the application. 
	 * @param id the id of the application
	 * @param relPathString the path of the application relative to the server path
	 * @param settings the settings of the application, can be null.
	 * @return was the application successfully initialized?
	 * @throws IllegalArgumentException if the app is contained in another server
	 * 		or its path is already used by another application 
	 */
	protected synchronized boolean addApp(ServerApp app, String id, String relPathString, Settings settings)
	{
		Check.notNull(app, 	"app");
		Check.notNull(id, 	"id");
		Path relPath = new Path(relPathString);
		
		// sanity check 1: app must not be part of a server yet 
		if (app.getServer() != TempServer.INSTANCE)
			throw new IllegalStateException(app + " already added to server " + app.getServer());
		
		// sanity check 2: path and id may not be used by another app
		for (ServerApp prevApp : apps_)
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
		return app.getStatus() == ServerApp.Status.RUNNING;
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
	 * @return an connector object.
	 * 		In an servlet environment, the connector is a servlet
	 * 		which receives requests and directs them to the application.
	 */
	protected abstract Object connect(ServerApp app, boolean supportAsync);
	
	
	/**
	 * Disconnect the application from the server.
	 * Called when the application is closed.
	 */
	protected abstract void disconnect(ServerApp app, Object connector);

	
	//--------------------------
	// close
	//--------------------------

	
	/**
	 * Closes all applications of the Server.
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
	protected synchronized void close(ServerApp app)
	{
		if (app == null)
			return;
		if (app.getServer() != this)
			throw new IllegalArgumentException("not my application: " + app);
		
		try
		{
			if (app.getStatus() == ServerApp.Status.RUNNING)
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
	public synchronized List<ServerApp> getApplications()
	{
		return new ArrayList<>(apps_);
	}
	
	
	/**
	 * Returns the first application with the given class.
	 * @return the application or null
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T extends ServerApp> T getApplication(Class<T> appClass)
	{
		for (ServerApp app : apps_)
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
	public synchronized ServerApp getApplication(String id)
	{
		for (ServerApp app : apps_)
		{
			if (app.getId().equals(id))
				return app;
		}
		return null;
	}
	
	
	public <T extends ServerApp> T getApplication(String id, Class<T> appClass)
	{
		ServerApp app = getApplication(id);
		return app != null ? Check.isA(app, appClass) : null;
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
	 * Returns the ServerFiles associated with the server.
	 */
	public abstract ServerFiles getServerFiles();

	
	
	

	
	/**
	 * Returns if the given path is not valid for a file resource request.
	 * In a servlet environment this is any path pointing into the WEB-INF directory. 
	 */
	public abstract boolean isProhibitedPath(String path);
	
	
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

	
	/**
	 * The develop flag of the Server.
	 */
	protected boolean develop_;
	private final List<ServerApp> apps_ = new ArrayList<>();
}
