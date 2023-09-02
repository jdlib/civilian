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
package org.civilian.controller.method.arg.misc;


import java.lang.reflect.Modifier;
import org.civilian.controller.method.arg.MethodArg;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.response.ResponseContent;


/**
 * An MethodArg for controller arguments with {@link ResponseContent} type
 * or annotated with {@link org.civilian.annotation.ResponseContent}.
 * A new object is created and passed to the controller method.
 * After the method was invoked, the object is written to the response,
 * if nothing else was written to the response yet.
 */
public class ResponseContentArg extends MethodArg
{
	/**
	 * Creates a new ResponseContentArg.
	 * @param paramClass the class of the content 
	 */
	public ResponseContentArg(Class<?> paramClass)
	{
		if (Modifier.isAbstract(paramClass.getModifiers()))
			throw new IllegalArgumentException(paramClass + " is abstract and cannot be used as controller ResponseContent parameter"); 
		paramClass_ = paramClass;
		hasRcClass_	= ResponseContent.class.isAssignableFrom(paramClass);
	}
	
	
	/**
	 * @return a new parameter instance.
	 */
	@SuppressWarnings("deprecation")
	@Override public Object getValue(Request request, Response response)
	{
		try
		{
			return paramClass_.newInstance();
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("can't instantiate " + paramClass_, e);
		}
	}


	/**
	 * Writes the parameter object to the response.
	 * @param request the request
	 * @param response the response
	 * @param param a parameter
	 * @throws Exception if something fails
	 */
	@Override public void postProcess(Request request, Response response, Object param) throws Exception
	{
		if (!response.isCommitted() && (response.getContentAccess() == Response.ContentAccess.NONE))
		{
			if (hasRcClass_)
				((ResponseContent)param).writeTo(response);
			else
				response.writeContent(param);
		}
	}
	
	
	private final Class<?> paramClass_;
	private final boolean hasRcClass_;
}
