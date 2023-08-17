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
package org.civilian.controller.method.arg.reqparam;


import org.civilian.controller.method.arg.MethodArg;
import org.civilian.request.Request;
import org.civilian.resource.PathParam;


/**
 * An MethodArg for controller action parameters annotated
 * with a {@link PathParam} annotation.
 */
public class PathParamArg<T> extends MethodArg 
{
	/**
	 * Creates a new PathParamArg object.
	 */
	public PathParamArg(PathParam<T> pathParam, T defaultValue)
	{
		pathParam_ 		= pathParam;
		defaultValue_	= defaultValue;
	}
	
	
	/**
	 * Extracts the parameter or matrix parameter from the request and
	 * converts into the target value which will be injected into
	 * argument variable. 
	 */
	@Override public T getValue(Request request) throws Exception
	{
		T value = request.getPathParam(pathParam_);
		return value != null ? value : defaultValue_;
	}

	
	@Override public String toString()
	{
		return toString("PathParam", pathParam_.getName());
	}
	
	
	private final PathParam<T> pathParam_;
	private final T defaultValue_;
}
