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
package org.civilian.server.servlet;


import java.io.IOException;
import java.util.Iterator;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import org.civilian.application.Application;
import org.civilian.application.UploadConfig;
import org.civilian.content.ContentTypeLookup;
import org.civilian.internal.Logs;
import org.civilian.resource.Path;
import org.civilian.server.Server;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Iterators;
import org.civilian.util.ResourceLoader;
import org.civilian.util.Settings;
import org.civilian.util.StringUtil;


/**
 * A Server implementation which wraps a javax.servlet.ServletContext.
 */
class ServletServer extends Server
{
	/**
	 * The parameter name in web.xml to overwrite the name of
	 * the Civilian config file.
	 */
	public static final String CONFIG_PARAM = "civilian-config";


	/**
	 * Returns the ServletServer instance associated with the ServletContext.
	 */
	public static ServletServer getInstance(ServletContext context)
	{
		return (ServletServer)context.getAttribute(ServletServer.class.getName());
	}
		

	//----------------------
	// init
	//----------------------

	
	/**
	 * Creates a ServletServer.
	 */
	public ServletServer(ServletContext servletContext)
	{
		servletContext_		= servletContext;
		servletContext_.setAttribute(getClass().getName(), this);
		
		path_ 				= new Path(servletContext_.getContextPath());
		resourceLoader_		= ResourceLoader.builder.forSerlvetContext(servletContext);
		contentTypeLookup_	= ContentTypeLookup.forServletContext(servletContext);
		
		try
		{
			// a servlet context param may override the name of the config file
			Settings settings = readSettings();
			init(getClass().getClassLoader(), settings);
		}
		catch (Exception e)
		{
			Logs.SERVER.error("could not initialize", e);
			close();
		}
	}
	
	
	private Settings readSettings() throws IOException
	{
		// a servlet context param may override the name of the config file
		String paramName  = servletContext_.getInitParameter(CONFIG_PARAM);
		String configName = paramName != null ? paramName : DEFAULT_CONFIG_FILE;
		return readSettings(configName);
	}
	
	
	/**
	 * Creates and returns the AppServlet of an application.
	 */
	@Override protected AppServlet connect(Application app, boolean supportAsync)
	{
		AppServlet servlet = new AppServlet(app); 
		ServletRegistration.Dynamic registration = servletContext_.addServlet("civilian.AppServlet-" + app.getId(), servlet);
		registration.addMapping(app.getRelativePath().add("/*").toString());
		registration.setAsyncSupported(supportAsync);

		UploadConfig uc = app.getUploadConfig();
		if (uc.isEnabled())
		{
			registration.setMultipartConfig(new MultipartConfigElement(
				uc.getTempDirectory(), 
				uc.getMaxFileSize(), 
				uc.getMaxRequestSize(), 
				uc.getFileSizeThreshold())
			);
		}
		return servlet;
	}
	
	
	/**
	 * The servlet api does not allow to remove the servlet.
	 * but we stop serving requests.
	 */
	@Override protected void disconnect(Application app, Object connector)
	{
		AppServlet servlet = Check.isA(connector, AppServlet.class, "connector");
		servlet.close(); 
	}
	
	
	//----------------------
	// close
	//----------------------

	
	/**
	 * Called by the servlet context listener when the servlet context is getting destroyed.  
	 */
	void contextDestroyed(ServletContextEvent event)
	{
		close();
		servletContext_ = null;
	}


	//-----------------------------------------------------------------
	// implementation of Server API using the ServletContext
	//-----------------------------------------------------------------

	
	/**
	 * Forwards to the ServletContext.
	 */
	@Override public String getServerVersion()
	{
		return servletContext_.getMajorVersion() + "." + servletContext_.getMinorVersion();
	}


	/**
	 * Returns ServletContext#getServerInfo()
	 */
	@Override public String getServerInfo()
	{
		return servletContext_.getServerInfo();
	}


	/**
	 * Returns the path of the servlet context.
	 */
	@Override public Path getPath()
	{
		return path_;
	}
	

	/**
	 * Returns a ResourceLoader based on ServletContext#getResource().
	 */
	@Override public ResourceLoader getResourceLoader()
	{
		return resourceLoader_;
	}


	/**
	 * Returns a ContentTypeLookup to translate file names into content types. 
	 */
	@Override public ContentTypeLookup getContentTypeLookup()
	{
		return contentTypeLookup_;
	}
	
	
	/**
	 * Logs to ServletContext#log
	 */
	@Override public void log(String msg, Throwable error)
	{
		if ((msg != null) || (error != null))
		{
			if (msg == null)
				msg = "error";
			if (error == null)
				servletContext_.log(msg);
			else
				servletContext_.log(msg, error);
		}
	}
	
	
	/**
	 * Returns ServletContext#getAttribute(String) 
	 */
	@Override public Object getAttribute(String name)
	{
		return servletContext_.getAttribute(name);
	}
	
	
	/**
	 * Returns an iterator of the attribute names stored in the context. 
	 */
	@Override public Iterator<String> getAttributeNames()
	{
		return Iterators.asIterator(servletContext_.getAttributeNames());
	}
	
	
	/**
	 * Calls ServletContext#getAttribute(String) 
	 */
	@Override public void setAttribute(String name, Object object)
	{
		servletContext_.setAttribute(name, object);
	}
	
	
	/**
	 * Returns the servlet context if you pass ServletContext.class
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(servletContext_, implClass);
	}

	
	/**
	 * Returns if the given path contains WEB-INF or META-INF. 
	 */
	@Override public boolean isProhibitedPath(String path)
	{
		return ServletUtil.isProhibitedPath(path);
	}
	
	
	/**
	 * Calls ServletContext#getRealPath(String).
	 */
	@Override public String getRealPath(String path)
	{
		return servletContext_.getRealPath(path);
	}
	
	
	/**
	 * Prefixes the path to start with "/WEB-INF/".
	 */
	@Override public String getConfigPath(String path)
	{
		path = StringUtil.haveLeft(path, "/");
		if (!path.startsWith("/WEB-INF/"))
			path = "/WEB-INF" + path;
		return path;
	}
	
	
	private Path path_;
	private ServletContext servletContext_;
	private ResourceLoader resourceLoader_;
	private ContentTypeLookup contentTypeLookup_;
}
