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


import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.util.Check;


/**
 * ControllerConfig stores controller related configurations.
 */
public class ControllerConfig
{
	/**
	 * Creates a new ControllerConfig.
	 * @param rootPackage the root package
	 */
	public ControllerConfig(String rootPackage)
	{
		this(rootPackage, null);
	}
	
	
	/**
	 * Creates a new ControllerConfig.
	 * @param rootPackage the root package
	 * @param pathParams the path params
	 */
	public ControllerConfig(String rootPackage, PathParamMap pathParams)
	{
		this(rootPackage, pathParams, null);
	}

	
	/**
	 * Creates a new ControllerConfig.
	 * @param rootPackage the root package
	 * @param pathParams the path params
	 * @param naming the naming
	 */
	public ControllerConfig(String rootPackage, PathParamMap pathParams, ControllerNaming naming)
	{
		pathParams_  = pathParams != null ? pathParams : PathParamMap.EMPTY;
		rootPackage_ = Check.notNull(rootPackage, "rootPackage");
		naming_ 	 = naming != null ? naming : new ControllerNaming();
	}
	
	
	/**
	 * @return the list of path parameters used by an application.
	 */
	public PathParamMap getPathParams()
	{
		return pathParams_;
	}

	
	/**
	 * @return the root package of the application controller classes.
	 * The root package is specified in the application constructor.
	 */
	public String getRootPackage()
	{
		return rootPackage_;
	}
	
	
	/**
	 * @return the ControllerNaming which provides naming conventions
	 * for controller classes.
	 */
	public ControllerNaming getNaming()
	{
		return naming_;
	}

	
	private final PathParamMap pathParams_;
	private final String rootPackage_;
	private final ControllerNaming naming_;
}
