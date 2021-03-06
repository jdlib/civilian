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
package org.civilian.resource;


import org.civilian.Application;
import org.civilian.application.AppConfig;


/**
 * ResourceConfig stores resource related configurations of an {@link Application}.
 * @see Application#getResourceConfig() 
 */
public class ResourceConfig
{
	public ResourceConfig(PathParamMap pathParams) 
	{
		pathParams_ = pathParams != null ? pathParams : PathParamMap.EMPTY;
	}
	
	
	/**
	 * Returns the list of path parameters used by the application.
	 * @see Application#Application(PathParamMap, String)
	 */
	public PathParamMap getPathParams()
	{
		return pathParams_;
	}

	
	/**
	 * Returns the extension mapping defined by the resource setup.
	 * @see AppConfig#getExtensionMapping()
	 */
	public ExtensionMapping getExtensionMapping()
	{
		return extensionMapping_;
	}

	
	private PathParamMap pathParams_;
	private ExtensionMapping extensionMapping_ = new ExtensionMapping();
}
