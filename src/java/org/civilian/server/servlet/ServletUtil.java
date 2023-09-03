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


import java.util.regex.Pattern;


/**
 * Contains servlet related utility methods
 */		
public class ServletUtil
{
	private static final Pattern PROHIBITED_PATTERN	= Pattern.compile("^[\\s\\S]*((WEB|META)-INF.*)[\\s\\S]*", Pattern.CASE_INSENSITIVE);

	
	/**
	 * @param path a path
	 * @return if the given path contains WEB-INF or META-INF. 
	 */
	public static boolean isProhibitedPath(String path)
	{
		return PROHIBITED_PATTERN.matcher(path).matches();
	}
}
