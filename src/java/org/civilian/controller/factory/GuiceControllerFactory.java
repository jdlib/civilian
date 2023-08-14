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
package org.civilian.controller.factory;


import org.civilian.application.AppConfig;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerFactory;
import org.civilian.util.Check;
import com.google.inject.Injector;


/**
 * GuiceControllerFactory is a ControllerFactory which uses a Guice injector
 * to create controller objects.
 * Use it to inject dependencies from the guice container
 * into Controllers.
 * The GuiceControllerFactory can be installed during application setup.
 * @see AppConfig#setControllerFactory(ControllerFactory)
 */
public class GuiceControllerFactory implements ControllerFactory
{
	/**
	 * Creates a new GuiceControllerFactory.
	 * @param injector the guice injector
	 */
	public GuiceControllerFactory(Injector injector)
	{
		injector_ = Check.notNull(injector, "injector");
	}
	
	
	/**
	 * Returns a Controller instance created by the guice injector.
	 */
	@Override public Controller createController(Class<? extends Controller> controllerClass) throws Exception
	{
		return injector_.getInstance(controllerClass);
	}
	
	
	private Injector injector_;
}
