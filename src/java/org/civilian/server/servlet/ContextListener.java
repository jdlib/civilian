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


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;


/**
 * ContextListener is a ServletContextListener which needs to
 * be defined in your web.xml in order to run Civilian applications.
 * ContextListener bootstraps the initialization of a Civilian context
 * and the applications configured in your Civilian config file.
 * <p>
 * To define the context listener add these lines to your web.xml:
 * ContextListener 
 * {@code<listener>
 *    <listener-class>org.civilian.server.servlet.ContextListener</listener-class>
 * </listener>}
 */
public class ContextListener implements ServletContextListener
{
	/**
	 * Creates a ServletContentAdapter.
	 */
	@Override public void contextInitialized(ServletContextEvent event)
	{
		ServletContext servletContext = event.getServletContext();
		if (servletContext.getMajorVersion() < 3)
			servletContext.log("civilian needs a servlet engine >= 3.0");
		else
			server_ = new ServletServer(servletContext);
	}
	
	
	/**
	 * Removes the ServletContentAdapter.
	 */
	@Override public void contextDestroyed(ServletContextEvent event)
	{
		if (server_ != null)
			server_.contextDestroyed(event);
	}

	
	private ServletServer server_;
}
