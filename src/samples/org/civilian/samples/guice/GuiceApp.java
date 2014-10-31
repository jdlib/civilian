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
 package org.civilian.samples.guice;


import com.google.inject.Guice;
import com.google.inject.Injector;
import org.civilian.Application;
import org.civilian.application.AppConfig;
import org.civilian.controller.factory.GuiceControllerFactory;


public class GuiceApp extends Application
{
	@Override protected void init(AppConfig config) throws Exception
	{
		Injector injector = Guice.createInjector(new GuiceModule());
		config.setControllerFactory(new GuiceControllerFactory(injector));
	}
	

	@Override protected void close() throws Exception
	{
	}
}
