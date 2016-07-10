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
package org.civilian.context.test;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.civilian.Application;
import org.civilian.Context;
import org.civilian.application.ConfigKeys;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeLookup;
import org.civilian.context.servlet.ServletUtil;
import org.civilian.internal.Logs;
import org.civilian.resource.Path;
import org.civilian.util.Check;
import org.civilian.util.FileType;
import org.civilian.util.Iterators;
import org.civilian.util.ResourceLoader;
import org.civilian.util.Settings;


/**
 * TestContext is a {@link Context} implementation which can be used in a test environment.
 */
public class TestContext extends Context
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
	 * Creates a TestContext for the current directory.
	 */
	public TestContext()
	{
		this(new File("."));
	}
	
	
	/**
	 * Creates a TestContext for the given directory.
	 * The directory should contain all resources of a servlet application,
	 * including a WEB/-INF subdirectory. 
	 */
	public TestContext(File dir)
	{
		directory_ 			= FileType.EXISTENT_DIR.check(dir);
		contentTypeLookup_ 	= ContentTypeLookup.forMap(EXT_2_CONTENTTYPES);
		resourceLoader_ 	= ResourceLoader.builder.forDirectory(directory_);
	}
	
	
	/**
	 * Returns the root directory of the TestContext.
	 */
	public File getDirectory()
	{
		return directory_;
	}
	
	
	/**
	 * Sets the develop flag.
	 */
	public void setDevelop(boolean develop)
	{
		develop_ = develop;
	}
	
	
	/**
	 * Returns the settings used by the TestContext.
	 * By default the settings are stored in file civilian.ini
	 */
	public Settings getSettings() throws IOException
	{
		if (settings_ == null)
			setSettings(DEFAULT_CONFIG_FILE);
		return settings_;
	}
	

	/**
	 * Sets the settings of the TestContext.
	 */
	public void setSettings(Settings settings)
	{
		settings_ = Check.notNull(settings, "settings");
	}


	/**
	 * Sets the settings of the TestContext.
	 * @param configName a name of a configuration file
	 */
	public void setSettings(String configName) throws IOException
	{
		setSettings(readSettings(configName));
	}
	
	
	/**
	 * Initializes the context from the settings and adds all applications
	 * specified in the settings to the context.
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
		if (urls.size() > 0)
			cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), cl);
		return cl;
	}

	
	@Override protected Object connect(Application app, boolean supportAsync)
	{
		return null;
	}

	
	@Override protected void disconnect(Application app)
	{
	}

	
	/**
	 * Returns the ResourceLoader which servers files from
	 * the context directory.
	 */
	@Override public ResourceLoader getResourceLoader()
	{
		return resourceLoader_;
	}


	/**
	 * Sets the ResourceLoader.
	 */
	public void setResourceLoader(ResourceLoader resourceLoader)
	{
		resourceLoader_ = Check.notNull(resourceLoader, "resourceLoader");
	}

	
	/**
	 * Returns the real path of a file within the context directory.
	 */
	@Override public String getRealPath(String path)
	{
		return new File(directory_, path).getAbsolutePath();
	}


	/**
	 * Returns if the path goes into the WEB-INF or META-INF subdirectory.
	 */
	@Override public boolean isProhibitedPath(String path)
	{
		return ServletUtil.isProhibitedPath(path);
	}
	
	
	/**
	 * Returns a path within the configF subdirectory.
	 */
	@Override public String getConfigPath(String path)
	{
		return configPath_ + path;
	}

	
	/**
	 * Returns the config path below the context directory.
	 */
	public String getConfigPath()
	{
		return configPath_;
	}

	
	/**
	 * Sets the config path (relative to context directory).
	 */
	public void setConfigPath(String path)
	{
		configPath_ = Check.notNull(path, "path"); 
	}

	
	/**
	 * Returns "1.0".
	 */
	@Override public String getServerVersion()
	{
		return "1.0";
	}
	

	/**
	 * Returns "TestContext".
	 */
	@Override public String getServerInfo()
	{
		return "TestContext";
	}

	
	/**
	 * Returns Path.ROOT.
	 */
	@Override public Path getPath()
	{
		return Path.ROOT;
	}
	

	/**
	 * Returns a ContentTypeLookup to translate file names into content types. 
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
	 * Allows to add an application to the context. The application settings
	 * are taken from civilian.ini
	 * @param app the application
     * @param id the application id
     * @param relativePath the relative path of the application within the context
	 */
	public boolean addApp(Application app, String id, String relativePath) throws IOException
	{
		Settings appSettings = new Settings(getSettings(), ConfigKeys.APP_PREFIX + id + ".");
		return addApp(app, id, relativePath, appSettings);
	}
	
	
	/**
	 * Allows to add an application to the context.
	 * @param app the application
     * @param id the application id
     * @param relativePath the relative path of the application within the context
     * @param settings application settings
	 */
	@Override public boolean addApp(Application app, String id, String relativePath, Settings settings)
	{
		return super.addApp(app, id, relativePath, settings);
	}
	

	//--------------------------
	// close
	//--------------------------

	
	/**
	 * Closes all applications of the context.
	 * @see Application#close()
	 */
	@Override public void close()
	{
		// call the protected implementation
		super.close();
	}	
	
	
	/**
	 * Closes an application and removes it from the context.
	 */
	@Override public void close(Application app)
	{
		// call the protected implementation
		super.close(app);
	}	

	
	//--------------------------
	// misc
	//--------------------------

	
	@Override public void log(String msg, Throwable throwable)
	{
		Logs.CONTEXT.error(msg, throwable);
	}
	
	
	/**
	 * Returns null.
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
}
