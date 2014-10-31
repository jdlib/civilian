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
package org.civilian.internal.controller.arg.reqcontent;


import java.lang.reflect.Type;
import org.civilian.Request;
import org.civilian.content.ContentType;


/**
 * ReqContentGenericArg reads the content of the request and converts it into
 * the target type using a Type object.
 */
public class ReqContentGenericArg extends RequestContentArg 
{
	public ReqContentGenericArg(Class<?> type, Type genericType)
	{
		type_ 			= type;
		genericType_	= genericType;
	}
	
	
	/**
	 * Uses a content reader from the application to read and return the request content.   
	 * @see Request#readContent(ContentType)
	 */
	@Override public Object getValue(Request request) throws Exception
	{
		return request.readContent(type_, genericType_);
	}

	
	private Class<?> type_;
	private Type genericType_;
}
