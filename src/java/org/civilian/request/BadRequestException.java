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
package org.civilian.request;


import org.civilian.response.Response;


/**
 * An BadRequestException signals that a request cannot be processed
 * and must be changed by the client.
 * It contains a response status code, an optional diagnostic message and an optional Throwable
 * which caused the error in the first place.<br>
 * The default error handling in the application recognizes BadRequestExceptions
 * and treats them like a call to {@link Response#sendError(int, String, Throwable)}.
 * <p>
 * You can throw BadRequestException when you process requests. Several methods
 * of the request like {@link Request#readContent(Class)} and injection of arguments
 * into controller action methods also throw such an exception.    
 */
public class BadRequestException extends Exception
{
	private static final long serialVersionUID = -8751237246837868869L;

	
	/**
	 * Creates a new BadRequestException.
	 * @param message a diagnostic message which can be shown to the client.
	 * @param cause an exception which caused the error in the first place
	 * @param errorValue an error value
	 */
	public BadRequestException(String message, Throwable cause, String errorValue)
	{
		super(message, cause);
		errorValue_ = errorValue;
	}
	
	
	/**
	 * @return the value which caused the exception.
	 */
	public String getErrorValue()
	{
		return errorValue_;
	}
	

	private final String errorValue_;
}
