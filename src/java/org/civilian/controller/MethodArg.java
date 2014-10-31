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


import org.civilian.Request;
import org.civilian.request.BadRequestException;
import org.civilian.util.StringUtil;


/**
 * MethodArg represents an argument of a controller action method.
 * When the action method is invoked, the MethodArg builds a value
 * which will be injected into the argument variable.
 */
public abstract class MethodArg
{
	/**
	 * Returns the value of the argument for the given request.
	 * @throws Exception thrown if an error occurs when extracting the value.
	 * 		The MethodArg should throw a {@link BadRequestException}
	 * 		to indicate that the request had syntactic errors.
	 */
	public abstract Object getValue(Request request) throws Exception;


	/**
	 * Does any post-processing after the controller action method was called.
	 * @param request the request
	 * @param value the argument value previously created by {@link #getValue(Request)}.
	 */
	public void postProcess(Request request, Object value) throws Exception
	{
	}


	/**
	 * Helper method to build a string representation of the argument plus a name.
	 */
	protected String toString(String arg, String name)
	{
		return arg + " \"" + name + '"';
	}

	
	/**
	 * Returns a string representation of the argument, to be used
	 * in diagnostic messages.
	 */
	@Override public String toString()
	{
		return StringUtil.cutRight(getClass().getSimpleName(), "Arg");
	}
}
