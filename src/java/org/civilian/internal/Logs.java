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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Defines the Civilian loggers.
 */
public abstract class Logs
{
	public static final Logger APPLICATION	= getLog("application"); 
	public static final Logger ASSET 		= getLog("asset");
	public static final Logger CLASSLOADER 	= getLog("classloader");
	public static final Logger CONTEXT		= getLog("context");
	public static final Logger CONTROLLER	= getLog("controller");
	public static final Logger PROCESSOR	= getLog("processor");
	public static final Logger REQUEST 		= getLog("request");
	public static final Logger RESPONSE 	= getLog("response");
	public static final Logger SERVLET 		= getLog("servlet");
	
	
	private static Logger getLog(String suffix)
	{
		return LoggerFactory.getLogger("org.civilian." + suffix);
	}
}
