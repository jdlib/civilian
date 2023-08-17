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
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.civilian.Version;
import org.civilian.application.Application;
import org.civilian.internal.Logs;
import org.civilian.response.Response;
import org.civilian.server.Server;
import org.civilian.util.Check;


/**
 * AppServlet is a servlet which is mapped to the path of an {@link Application}.
 * When it receives a request, it constructs a Request and Response object
 * wrapping HttpServletRequest and HttpServletResponse and then invokes
 * the application.<p>
 * For every Civilian application within a {@link Server} exists an own AppServlet instance.
 * Usually the servlets are dynamically created and registered, using the
 * information of the application config.
 */
public class AppServlet implements Servlet
{
	private static final String APP_ID_PARAMETER = "app.id";
	
	
	/**
	 * Constructor used when the servlet is defined in web.xml and
	 * created by the servlet container.
	 */
	public AppServlet()
	{
	}
	
	
	/**
	 * Constructor used when the servlet is created by ServletContainer and
	 * dynamically registered.
	 */
	public AppServlet(Application app)
	{
		app_ = Check.notNull(app, "app");	
	}
	
	
	/**
	 * Called by the servlet container.
	 */
	@Override public void init(ServletConfig config) throws ServletException
	{
		servletConfig_		= config; 
		servletContext_		= config.getServletContext();
		
		if (app_ == null)
			initApplication();
	}
	

	/**
	 * Obtain a pointer to the application, when we were created via web.xml definition.
	 */
	private void initApplication() throws ServletException
	{
		String appId = servletConfig_.getInitParameter(APP_ID_PARAMETER);
		if (appId == null)
			throw new UnavailableException("no init parameter '" + APP_ID_PARAMETER + "' defined");
		ServletServer adapter = ServletServer.getInstance(servletContext_);
		if (adapter == null)
			throw new UnavailableException("ServletContext is not available: did you register " + ContextListener.class.getName() + " as listener in your web.xml?");
		app_ = adapter.getApplication(appId);
		if (app_ == null)
			throw new UnavailableException("no application with id '" + appId + "' defined");
	}

	
	/**
	 * Returns the servlet config.
	 */
	@Override public ServletConfig getServletConfig()
	{
		return servletConfig_;
	}


	/**
	 * Returns a servlet info string.
	 */
	@Override public String getServletInfo()
	{
		return getClass().getSimpleName() + " v" + Version.VALUE;
	}


	/**
	 * Processes the servlet request.
	 */
	@Override public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException,
		ServletException
	{
		HttpServletRequest httpRequest;
		HttpServletResponse httpResponse;
		try
		{
			httpRequest		= (HttpServletRequest)servletRequest;
			httpResponse	= (HttpServletResponse)servletResponse;
		}
		catch(ClassCastException e)
		{
			Logs.SERVER.error("invalid servlet request and response: " + 
				servletRequest.getClass().getName() + ", " + 
				servletResponse.getClass().getName(), e);
			return;
		}
		
		// don't serve anything if we are closed
		if (app_ == null)
		{
			httpResponse.sendError(Response.Status.NOT_FOUND);
			return;
		}
		
		ServletRequestAdapter request;
		try
		{
			request = isMultiPartRequest(httpRequest) ? 
				new MpRequestAdapter(app_, httpRequest) :
				new SpRequestAdapter(app_, httpRequest);
			// will register with the request	
			new ServletResponseAdapter(request, httpResponse);
		}
		catch(Exception e)
		{
			Logs.SERVER.error(null, e);
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		app_.process(request);
	}
	
	
	private boolean isMultiPartRequest(HttpServletRequest httpRequest)
	{
		String contentType = httpRequest.getContentType();
		return (contentType != null) && contentType.toLowerCase().startsWith("multipart/form-data");
	}
	
	
	/**
	 * Called by the servlet context when the context shuts down.
	 * Calls close().
	 */
	@Override public void destroy()
	{
		close();
	}
	
	
	/**
	 * Clears the reference to the app.
	 * Called by the servlet container when the context is closed.
	 */
	public void close()
	{
		app_ = null;
	}

	
	private Application app_;
	private ServletConfig servletConfig_;
	private ServletContext servletContext_;
}
