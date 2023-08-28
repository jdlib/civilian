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
package org.civilian.response;


import org.civilian.request.Request;


/**
 * AsyncContext provides the execution context for asynchronous request processing.
 */
public abstract class AsyncContext
{
	/**
	 * Creates a new AsyncContext.
	 * @param request the request
	 */
	public AsyncContext(Request request, Response response)
	{
		request_ 	= request;
		response_	= response;
	}
	
	
	/**
	 * Adds a new AsyncListener to the context
	 */
	public abstract void addListener(AsyncListener listener);
	
	
	/**
	 * Completes the asynchronous request and closes the associated response. 
	 */
	public abstract void complete();
	
	
	/**
	 * Dispatches the request and of this AsyncContext to the container, using
	 * the original request path.
	 */
	public abstract void dispatch();
	
	
	/**
	 * Dispatches the request and of this AsyncContext to the container, using
	 * the given request path.
	 */
	public abstract void dispatch(String path);
	
	
	/**
	 * Returns the request.
	 */
	public Request getRequest()
	{
		return request_;
	}


	/**
	 * Returns the response.
	 */
	public Response getResponse()
	{
		return response_;
	}
	
	
	public abstract void start(Runnable runnable);


	public abstract long getTimeout();


	/**
	 * Sets the timeout in milliseconds. If {@link #complete()} is not called within
	 * the timeout duration, the response is stopped and a timeout event is 
	 * sent to any AsyncListener. 
	 * @param milliSeconds the timeout in milliSeconds. If &lt;= 0, no timeout is applied.
	 */
	public abstract void setTimeout(long milliSeconds);

	
	private final Request request_;
	private final Response response_;
}