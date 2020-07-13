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


import org.civilian.Resource;


/**
 * ControllerSignature is a helper class to build or parse controller signature strings.
 * A signature string is either null, a controller class name, or a controller class name
 * suffixed by a filter string. The last case is used to specify a controller class,
 * and specify that only a subset of its controller methods should be used.  
 * Signature strings are used by {@link Resource#getControllerSignature() resources} to
 * specify which controller is used to handle requests for the resource-
 */
public abstract class ControllerSignature
{
	public static final char SEPARATOR = ':';
	
	
	/**
	 * Builds a signature out of class name and method filter.
	 */
	public static String build(String className, String filter)
	{
		if (className == null)
			return null;
		else if (filter == null)
			return className;
		else
			return className + SEPARATOR + filter;
	}


	/**
	 * Parses a signature into a class name and a method filter.
	 * @return an array with length 2
	 */
	public static String[] parse(String signature)
	{
		String[] s = new String[2];
		if (signature != null)
		{
			int p = signature.indexOf(SEPARATOR);
			if (p < 0)
				s[0] = signature;
			else
			{
				s[0] = signature.substring(0, p);
				s[1] = signature.substring(p + 1);
			}
		}
		return s;
	}


	/**
	 * Extracts the class name from a signature.
	 */
	public static String getClassName(String signature)
	{
		return signature != null ? parse(signature)[0] : null;
	}


	/**
	 * Extracts the method filter from a signature.
	 */
	public static String getMethodFilter(String signature)
	{
		return signature != null ? parse(signature)[1] : null;
	}
}
