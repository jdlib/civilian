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
import org.civilian.response.Response;


/**
 * An MethodArgs for a parameter whose type is Cookie.
 */
public class CookieParamObjectArg extends MethodArg
{
	public CookieParamObjectArg(String name)
	{
		name_ = name;
	}
	
	
	@Override public Object getValue(Request request, Response response) throws Exception
	{
		return request.getCookies().get(name_);
	}

	
	@Override public String toString()
	{
		return toString("CookieParam", name_);
	}
	
	
	private final String name_;
}
