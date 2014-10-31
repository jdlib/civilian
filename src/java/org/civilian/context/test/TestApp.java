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


import org.civilian.Application;
import org.civilian.application.AppConfig;
import org.civilian.controller.ControllerNaming;
import org.civilian.resource.PathParamMap;
import org.civilian.util.Settings;


/**
 * TestApp is a {@link Application} implementation which can be used in a test environment.
 * It allows you to create an application instance and configure
 * most of its settings without the need to derive and implement
 * an own application class. 
 */
public class TestApp extends Application
{
	/**
	 * Creates a new TestApp.
	 * It has no path parameters, and its controller package
	 */
	public TestApp()
	{
		super();
	}

	
	public TestApp(PathParamMap pathParams)
	{
		super(pathParams);
	}


	public TestApp(PathParamMap pathParams, String controllerRootPackage)
	{
		super(pathParams, controllerRootPackage);
	}


	public TestApp(PathParamMap pathParams, String controllerRootPackage, ControllerNaming naming)
	{
		super(pathParams, controllerRootPackage, naming);
	}

	
	public void init()
	{
		init("");
	}
	
	
	public void init(String path)
	{
		TestContext context = new TestContext();
		context.addApp(this, "app", path, null);
	}
	
	
	@Override protected void init(AppConfig config) throws Exception
	{
	}
	
	
	public AppConfig getConfig()
	{
		if (appConfig_ == null)
		{
			try
			{
				appConfig_ = new AppConfig(this, null);
			}
			catch (Exception e)
			{
				throw new IllegalStateException("could not create app config", e);
			}
		}
		return appConfig_;
	}
	
	
	@Override protected AppConfig createConfig(Settings settings)
	{
		return appConfig_ != null ? appConfig_ : super.createConfig(settings);
	}
	
	
	@Override protected void close() throws Exception
	{
	}
	
	
	private AppConfig appConfig_;
}
