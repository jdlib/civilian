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
package org.civilian.controller;


/**
 * ControllerFactory is used to create controller objects
 * from a Controller class.
 * By default the ControllerFactory just uses Class.newInstance()
 * to this. But if you want to use Dependency Injection to
 * setup controller objects, use a derived factory suitable
 * for your needs.
 */
public interface ControllerFactory
{
	public Controller createController(Class<? extends Controller> controllerClass) throws Exception;
}
