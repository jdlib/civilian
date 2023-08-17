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


import java.util.ArrayList;
import javax.servlet.http.Cookie;

import org.civilian.request.CookieList;
import org.civilian.request.Request;


/**
 * An MethodArg which returns the value of a cookie.
 */
public class CookieParamValueArg extends ReqParamValueArg
{
	public CookieParamValueArg(String name)
	{
		super(name);
	}
	
	
	@Override public String getValue(Request request) throws Exception
	{
		Cookie cookie = request.getCookies().get(name_);
		return cookie != null ? cookie.getValue() : null; 
	}
	

	@Override public String[] getValues(Request request) throws Exception
	{
		ArrayList<String> values = new ArrayList<>(); 
		CookieList cookies = request.getCookies();
		for (Cookie cookie : cookies)
		{
			if (name_.equals(cookie.getName()))
				values.add(cookie.getValue());
		}
		return values.toArray(new String[values.size()]);
	}
}
