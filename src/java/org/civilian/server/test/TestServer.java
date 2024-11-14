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


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.civilian.ConfigKeys;
import org.civilian.Logs;
import org.civilian.application.Application;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeLookup;
import org.civilian.resource.Path;
import org.civilian.server.Server;
import org.civilian.server.ServerApp;
import org.civilian.server.ServerFiles;
import org.civilian.server.servlet.ServletUtil;
import org.civilian.util.Check;
import org.civilian.util.FileType;
import org.civilian.util.Iterators;
import org.civilian.util.ResourceLoader;
import org.civilian.util.ResourceLoaders;
import org.civilian.util.Settings;


/**
 * TestServer is a {@link Server} implementation which can be used in a test environment.
 */
public class TestServer extends Server
{
	public static final HashMap<String,ContentType> EXT_2_CONTENTTYPES = new HashMap<>();
	static
	{
		EXT_2_CONTENTTYPES.put("html",  ContentType.TEXT_HTML);
		EXT_2_CONTENTTYPES.put("css", 	ContentType.TEXT_CSS);
		EXT_2_CONTENTTYPES.put("js",  	ContentType.APPLICATION_JAVASCRIPT);
		EXT_2_CONTENTTYPES.put("json",  ContentType.APPLICATION_JSON);
		EXT_2_CONTENTTYPES.put("xml",	ContentType.APPLICATION_XML);
		EXT_2_CONTENTTYPES.put("txt",	ContentType.TEXT_PLAIN);
	}

	
	/**
	 * Creates a TestServer for the current directory.
	 */
	public TestServer()
	{
		this(new File("."));
	}
	
	
	/**
	 * Creates a TestServer for the given directory.
	 * The directory should contain all resources of a servlet application,
	 * including a WEB/-INF subdirectory. 
	 * @param dir the test server directory
	 */
	public TestServer(File dir)
	{
		directory_ 			= FileType.EXISTENT_DIR.check(dir);
		contentTypeLookup_ 	= ContentTypeLookup.forMap(EXT_2_CONTENTTYPES);
		resourceLoader_ 	= ResourceLoaders.forDirectory(directory_);
	}
	
	
	/**
	 * @return the root directory of the TestServer.
	 */
	public File getDirectory()
	{
		return directory_;
	}
	
	
	/**
	 * Sets the develop flag.
	 * @param develop the flag
	 */
	public void setDevelop(boolean develop)
	{
		develop_ = develop;
	}
	
	
	/**
	 * Returns the settings used by the TestServer.
	 * By default the settings are stored in file civilian.ini
	 * @throws IOException if an IO error occurs 
	 * @return the settings
	 */
	public Settings getSettings() throws IOException
	{
		if (settings_ == null)
			setSettings(DEFAULT_CONFIG_FILE);
		return settings_;
	}
	

	/**
	 * Sets the settings of the TestServer.
	 * @param settings the settings
	 */
	public void setSettings(Settings settings)
	{
		settings_ = Check.notNull(settings, "settings");
	}


	/**
	 * Sets the settings of the TestServer.
	 * @param configName a name of a configuration file
	 * @throws IOException if an IO error occurs 
	 */
	public void setSettings(String configName) throws IOException
	{
		setSettings(getServerFiles().readConfigSettings(configName));
	}
	
	
	/**
	 * Initializes the server from the settings and adds all applications
	 * specified in the settings to the server.
	 * @throws IOException if an error occurs 
	 */
	public void init() throws Exception
	{
		// call the protected implementation
		init(getAppClassLoader(), getSettings());
	}
	
	
	private ClassLoader getAppClassLoader() throws Exception
	{
		if (appClassLoader_ == null)
			appClassLoader_ = createAppClassLoader();
		return appClassLoader_;
	}
	
	
	private ClassLoader createAppClassLoader() throws Exception
	{
		ArrayList<URL> urls = new ArrayList<>(); 
		File classesDir = new File(directory_, "WEB-INF/classes");
		if (classesDir.exists() && classesDir.isDirectory())
			urls.add(classesDir.toURI().toURL());
		
		File libDir = new File(directory_, "WEB-INF/lib");
		if (libDir.exists() && libDir.isDirectory())
		{
			File[] files = libDir.listFiles();
			if (files != null)
			{
				for (File f : files)
				{
					if (!f.isDirectory() && f.getName().endsWith(".jar"))
						urls.add(f.toURI().toURL());
				}
			}
		}
		
		ClassLoader cl = getClass().getClassLoader();
		if (!urls.isEmpty())
			cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), cl);
		return cl;
	}

	
	@Override protected Object connect(ServerApp app, boolean supportAsync)
	{
		return null;
	}

	
	@Override protected void disconnect(ServerApp app, Object connector)
	{
	}

	
	/**
	 * @return the ResourceLoader which servers files from
	 * the server directory.
	 */
	@Override public ResourceLoader getResourceLoader()
	{
		return resourceLoader_;
	}


	/**
	 * Sets the ResourceLoader.
	 * @param loader the loader
	 */
	public void setResourceLoader(ResourceLoader loader)
	{
		resourceLoader_ = Check.notNull(loader, "loader");
	}

	
	/**
	 * @return if the path goes into the WEB-INF or META-INF subdirectory.
	 */
	@Override public boolean isProhibitedPath(String path)
	{
		return ServletUtil.isProhibitedPath(path);
	}

	
	/**
	 * @return the config path below the server directory.
	 */
	public String getConfigPath()
	{
		return configPath_;
	}

	
	/**
	 * Sets the config path (relative to server directory).
	 * @param path a path
	 */
	public void setConfigPath(String path)
	{
		configPath_ = Check.notNull(path, "path"); 
	}

	
	/**
	 * @return "1.0".
	 */
	@Override public String getServerVersion()
	{
		return "1.0";
	}
	

	/**
	 * @return "TestServer".
	 */
	@Override public String getServerInfo()
	{
		return getClass().getSimpleName();
	}

	
	/**
	 * @return Path.ROOT.
	 */
	@Override public Path getPath()
	{
		return Path.ROOT;
	}
	

	/**
	 * @return a ContentTypeLookup to translate file names into content types. 
	 */
	@Override public ContentTypeLookup getContentTypeLookup()
	{
		return contentTypeLookup_;
	}
	
	
	//--------------------------
	// attributes
	//--------------------------

	
	@Override public Object getAttribute(String name)
	{
		return attributes_ != null ? attributes_.get(name) : null;
	}


	@Override public Iterator<String> getAttributeNames()
	{
		return attributes_ != null ? attributes_.keySet().iterator() : Iterators.<String>empty();
	}

	
	@Override public void setAttribute(String name, Object object)
	{
		if (attributes_ == null)
			attributes_ = new HashMap<>();
		attributes_.put(name, object);
	}
	

	//--------------------------
	// applications
	//--------------------------
	

	/**
	 * Allows to add an application to the server. The application settings
	 * are taken from civilian.ini
	 * @param app the application
     * @param id the application id
     * @param relativePath the relative path of the application within the server
	 * @throws IOException if an IO error occurs
	 * @return was the application successfully initialized?
	 */
	public boolean addApp(Application app, String id, String relativePath) throws IOException
	{
		Settings appSettings = new Settings(getSettings(), ConfigKeys.APP_PREFIX + id + ".");
		return addApp(app, id, relativePath, appSettings);
	}
	
	
	/**
	 * Allows to add an application to the server.
	 * @param app the application
     * @param id the application id
     * @param relativePath the relative path of the application within the server
     * @param settings application settings
	 * @return was the application successfully initialized?
	 */
	@Override public boolean addApp(ServerApp app, String id, String relativePath, Settings settings)
	{
		return super.addApp(app, id, relativePath, settings);
	}
	

	//--------------------------
	// close
	//--------------------------

	
	/**
	 * Closes all applications of the server.
	 * @see Application#close()
	 */
	@Override public void close()
	{
		// call the protected implementation
		super.close();
	}	
	
	
	/**
	 * Closes an application and removes it from the server.
	 */
	@Override public void close(ServerApp app)
	{
		// call the protected implementation
		super.close(app);
	}	

	
	@Override public ServerFiles getServerFiles() 
	{
		return files_;
	}
	
	
	private class Files extends ServerFiles
	{
		/**
		 * @return the real path of a file within the server directory.
		 */
		@Override public String getRealPath(String path)
		{
			return new File(directory_, path).getAbsolutePath();
		}


		/**
		 * @return a path within the configF subdirectory.
		 */
		@Override public String getConfigPath(String path)
		{
			return configPath_ + path;
		}
	}

	
	//--------------------------
	// misc
	//--------------------------

	
	@Override public void log(String msg, Throwable throwable)
	{
		Logs.SERVER.error(msg, throwable);
	}
	
	
	/**
	 * @return null
	 */
	@Override public <T> T unwrap(Class<T> implClass) 
	{
		return null;
	}

	
	private final File directory_;
	private String configPath_ = "WEB-INF/";
	private HashMap<String,Object> attributes_;
	private ContentTypeLookup contentTypeLookup_;
	private ResourceLoader resourceLoader_;
	private ClassLoader appClassLoader_;
	private Settings settings_;
	private final Files files_ = new Files();
}
