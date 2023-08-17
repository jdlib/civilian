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


/**
 * NotFoundResponse is used the application to print 
 * a response if no processor handled the request. 
 * If you want a different response, derive from NotFoundResponse,
 * override {@link #sendImpl(Response)} and return this implementation
 * in {@link Application#createNotFoundResponse()}.  
 * A NotFoundResponse object should be created for a single response and not be reused.
 */
public class NotFoundResponse
{
	public NotFoundResponse(boolean develop)
	{
		develop_ = develop;
	}
	

	/**
	 * If the response is not yet committed it calls {@link #sendImpl(Response)}
	 * Any exception is catched and logged. 
	 * @return the exception if an exception occurred else null
	 */
	public synchronized Exception send(Response response)
	{
		try
		{
			if (!response.isCommitted())
				sendImpl(response);
			return null;
		}
		catch(Exception e)
		{
			Logs.RESPONSE.error("unexpected", e);
			return e;
		}
	}
	
	
	/**
	 * If in development mode and a text/html response is accepted, 
	 * it uses {@link NotFoundTemplate} to print a response page with useful context information, 
	 * else it just sends a 404 not found status-code.
	 */
	protected void sendImpl(Response response) throws Exception
	{
		if (develop_ &&
			response.getRequest().getAcceptedContentTypes().contains(ContentType.TEXT_HTML))
		{
			response.setContentType(ContentType.TEXT_HTML);
			response.writeTemplate(new NotFoundTemplate(response.getRequest()));
		}
		else
			response.setStatus(Response.Status.NOT_FOUND);
	}
	
	
	private final boolean develop_;
}
