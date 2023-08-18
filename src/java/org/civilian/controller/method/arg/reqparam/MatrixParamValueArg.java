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


import org.civilian.request.Request;
import org.civilian.response.Response;


/**
 * An MethodArg which returns the value of a matrix parameter.
 */
public class MatrixParamValueArg extends ReqParamValueArg
{
	public MatrixParamValueArg(String name)
	{
		super(name);
	}
	
	
	@Override public String getValue(Request request, Response response) throws Exception
	{
		return request.getMatrixParam(name_);
	}
	

	@Override public String[] getValues(Request request, Response response) throws Exception
	{
		return request.getMatrixParams(name_);
	}
}
