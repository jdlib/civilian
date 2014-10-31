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


import org.civilian.Request;
import org.civilian.Response;
import org.civilian.Response.Status;


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
	 * The default response status code used by BadRequestExceptions.
	 * It is set to {@link Status#SC400_BAD_REQUEST} by default.
	 */
	public static int defaultStatusCode = Response.Status.SC400_BAD_REQUEST;
	
	
	/**
	 * Creates a new BadRequestException with status code {@link #defaultStatusCode}.
	 * @param message a diagnostic message which can be shown to the client.
	 * @param cause an exception which caused the error in the first place
	 */
	public BadRequestException(String message, Throwable cause)
	{
		this(defaultStatusCode, message, cause);
	}

	
	/**
	 * Creates a new BadRequestException.
	 * @param statusCode the response status code
	 * @param message a diagnostic message which can be shown to the client.
	 * @param cause an exception which caused the error in the first place
	 */
	public BadRequestException(int statusCode, String message, Throwable cause)
	{
		super(message, cause);
		statusCode_ = statusCode;
	}
	
	
	/**
	 * Returns the status code which should be used when an error response is 
	 * sent to the client.
	 */
	public int getStatusCode()
	{
		return statusCode_;
	}
	
	
	/**
	 * Sets the status code.
	 * @return this
	 */
	public BadRequestException setStatusCode(int statusCode)
	{
		statusCode_ = statusCode;
		return this;
	}

	
	/**
	 * Returns the value which caused the exception.
	 */
	public String getErrorValue()
	{
		return errorValue_;
	}
	

	/**
	 * Sets the value which caused the exception.
	 * @return this
	 */
	public BadRequestException setErrorValue(String value)
	{
		errorValue_ = value;
		return this;
	}

	
	private int statusCode_;
	private String errorValue_;
}
