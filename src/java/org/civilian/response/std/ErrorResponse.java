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
package org.civilian.response.std;


import org.civilian.Application;
import org.civilian.content.ContentType;
import org.civilian.internal.Logs;
import org.civilian.response.Response;
import org.civilian.util.Check;


/**
 * ErrorResponse is used to write an error response.
 * It is called by {@link Response#sendError(int)} or {@link Response#sendError(int, String, Throwable)}.
 * The actual ErrorResponse object used is created by {@link Application#createErrorResponse()}.
 */
public class ErrorResponse
{
	/**
	 * Sends the error response.
	 * If the response is not yet committed, it resets the response buffer,
	 * sets the error status, initializes helper variables and then calls {@link #sendImpl(Response, int, String, Throwable)}.
	 * @param response the response  
	 * @param statusCode the error status code
	 * @param message an optional error message
	 * @param error an optional error
	 */
	public synchronized Exception send(Response response, int statusCode, String message, Throwable error)
	{
		Check.notNull(response, "response");
		try
		{
			if (!response.isCommitted())
			{
				response.resetBuffer();
				response.setStatus(statusCode);
				sendImpl(response, statusCode, message, error);
			}
			return null;
		}
		catch(Exception e)
		{
			Logs.RESPONSE.error("unexpected", e);
			return e;
		}
	}
	

	/**
	 * Sends the error response. 
	 * The default implementation does the following:
	 * <ol>
	 * <li>If in {@link Application#develop() development mode} and a text/html response is accepted 
	 * 		then {@link ErrorTemplate} is used to print a debug response, including message and exception stacktrace. 
	 * <li>Else if the message is not null, the message is printed as text/plain content.
	 * 		If in development mode and the message is null and the error is not null, then the message of the error
	 * 		is used as message.  
	 * <li>Else no response content is printed.
	 * </ol>
	 * @param response the response  
	 * @param statusCode the error status code. It has already been set on the response
	 * @param message an optional error message
	 * @param error an optional error
	 */
	protected void sendImpl(Response response, int statusCode, String message, Throwable error) throws Exception
	{
		boolean develop = response.getApplication().develop(); 
		if (develop && 
			response.getRequest().getAcceptedContentTypes().contains(ContentType.TEXT_HTML))
		{
			response.setContentType(ContentType.TEXT_HTML);
			ErrorTemplate t = new ErrorTemplate(response.getRequest(), statusCode, message, error);
			response.writeTemplate(t);
		}
		else
		{
			if ((message == null) && develop && (error != null))
				message = error.getMessage();
			if (message != null)
			{
				response.setContentType(ContentType.TEXT_PLAIN);
				response.writeText(message);
			}
		}
	}
}
