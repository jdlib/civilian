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
package org.civilian.internal;


import org.civilian.Application;
import org.civilian.application.AppConfig;


/**
 * DefaultApp is a simple application implementation used 
 * if an application does not implement an own application
 * class.
 */
public class DefaultApp extends Application
{
	/**
	 * Creates the DefaultApp.
	 * @param resourceBasePackage the base package
	 * 		of the application resources
	 */
	public DefaultApp(String resourceRootPackage)
	{
		super(null, resourceRootPackage);
	}
	

	/**
	 * Does nothing.
	 */
	@Override protected void init(AppConfig config) throws Exception
	{
	}


	/**
	 * Does nothing.
	 */
	@Override protected void close() throws Exception
	{
	}
}
