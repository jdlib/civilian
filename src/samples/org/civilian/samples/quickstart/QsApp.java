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
package org.civilian.samples.quickstart;


import org.civilian.application.AppConfig;
import org.civilian.application.Application;


/**
 * The application class.
 */ 
public class QsApp extends Application
{
	/**
	 * Required default constructor.
	 */
	public QsApp()
	{
		super(QsPathParams.PARAMS, null /* default controller package */);
	}
	
	
	/**
	 * Initializes the application.
	 */
	@Override protected void init(AppConfig config) throws Exception
	{
		// register the generated resource root
		config.setResourceRoot(QsResources.root);
	}	
	
	
	/**
	 * Closes all application resources.
	 */
	@Override protected void close() throws Exception
	{
	}	
}