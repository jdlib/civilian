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


import org.civilian.Logs;
import org.civilian.application.Application;
import org.civilian.content.ContentType;
import org.civilian.response.Response;
import org.civilian.response.ResponseHandler;
import org.civilian.util.Check;


/**
 * ErrorResponseHandler is used to write an error response.
 * It is called by {@link Response#sendError(int)} or {@link Response#sendError(int, String, Throwable)}.
 * The actual ErrorResponse object used is created by {@link Application#createErrorHandler(int, String, Throwable)}.
 */
public class ErrorResponseHandler implements ResponseHandler
{
	/**
	 * Creates a new ErrorResponseHandler.
	 * @param develop the develop flag of the server
	 * @param statusCode the error status code
	 * @param message an optional error message
	 * @param error an optional error
	 */
	public ErrorResponseHandler(boolean develop, int statusCode, String message, Throwable error)
	{
		develop_ 	= develop;
		statusCode_ = statusCode;
		message_	= message;
		error_		= error;
	}
	

	/**
	 * Sends the error response.
	 * If the response is not yet committed, it resets the response buffer,
	 * sets the error status, initializes helper variables and then calls {@link #sendContent(Response)}.
	 * @param response the response  
	 * @return an Exception or null 
	 */
	@Override public synchronized Exception send(Response response)
	{
		Check.notNull(response, "response");
		try
		{
			if (!response.isCommitted())
			{
				response.resetBuffer();
				response.setStatus(statusCode_);
				sendContent(response);
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
	 * @throws Exception in case of an error
	 */
	protected void sendContent(Response response) throws Exception
	{
		if (develop_ && 
			response.getRequest().getAcceptedContentTypes().contains(ContentType.TEXT_HTML))
		{
			ErrorTemplate t = new ErrorTemplate(response.getRequest(), statusCode_, message_, error_);
			response.writeContent(t, ContentType.TEXT_HTML);
		}
		else
		{
			String message = message_;
			if ((message == null) && develop_ && (error_ != null))
				message = error_.getMessage();
			if (message != null)
				response.writeText(message);
		}
	}
	
	
	@Override public String toString() 
	{
		StringBuilder s = new StringBuilder();
		s.append(statusCode_);
		if (error_ != null)
			s.append(' ').append(error_.getMessage());
		else if (message_ != null)
			s.append(' ').append(message_);
		return s.toString();
	}

	
	protected final boolean develop_;
	protected final int statusCode_;
	protected final String message_;
	protected final Throwable error_;
}
