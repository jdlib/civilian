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


import org.civilian.content.ContentType;
import org.civilian.controller.method.ControllerMethod;
import org.civilian.util.Check;


/**
 * NegotiatedMethod represents the result of content negotiation,
 * Content negotiation means to select a {@link ControllerMethod}
 * of a {@link Controller} which is most suitable to handle a request.
 * NegotiatedMethod either holds the ControllerMethod and the preferred
 * content type, or an error code if no suitable method was found.
 * @see ControllerType#getMethod(org.civilian.request.Request)
 */
public class NegotiatedMethod
{
	/**
	 * Creates a positive NegotiatedMethod.
	 * @param method the selected action method of a Controller
	 * @param contentType the negotiated content type of the response. Can be null.
	 */
	public NegotiatedMethod(ControllerMethod method, ContentType contentType)
	{
		method_			= Check.notNull(method, "method"); 
		contentType_	= contentType;
		error_			= -1;
	}

	
	/**
	 * Creates a negative NegotiatedMethod.
	 * @param error the error code.
	 */ 
	public NegotiatedMethod(int error)
	{
		method_			= null; 
		contentType_	= null;
		error_ 			= error;
	}
	
	
	/**
	 * @return if this NegotiatedMethod holds a ControllerMethod.
	 */ 
	public boolean success()
	{
		return method_ != null;
	}
	
	
	/**
	 * @return the error code for a negative NegotiatedMethod.
	 */ 
	public int getError()
	{
		return error_;
	}

	
	/**
	 * @return the ControllerMethod for a positive NegotiatedMethod.
	 */ 
	public ControllerMethod getMethod()
	{
		return method_;
	}
	

	/**
	 * @return the expected content type of the response for 
	 * a positive NegotiationResult. Can be null.
	 */ 
	public ContentType getContentType()
	{
		return contentType_;
	}

	
	private final ControllerMethod method_;
	private final ContentType contentType_;
	private final int error_;
}
