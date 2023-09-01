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


import java.io.IOException;

import org.civilian.request.Request;
import org.civilian.util.Check;
import org.civilian.util.CheckedConsumer;
import org.civilian.util.CheckedRunnable;


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
	 * Adds a new AsyncEventListener to the context
	 * @param listener the listener
	 */
	public abstract void addEventListener(AsyncEventListener listener);
	
	
	/**
	 * Return the AsyncInput of the context. 
	 * @throws IOException if an IO error occurs
	 */
	public abstract AsyncInput getAsyncInput() throws IOException;
	
	
	/**
	 * Return the AsyncOutput of the context. 
	 * @throws IOException if an IO error occurs
	 */
	public abstract AsyncOutput getAsyncOutput() throws IOException;
	
	
	/**
	 * Completes the asynchronous request and closes the associated response. 
	 */
	public void complete()
	{
		if (!completed_)
		{
			completed_ = true;
			completeImpl();
		}
	}
	
	
	protected abstract void completeImpl();
	
	
	public boolean isCompleted()
	{
		return completed_;
	}
	
	
	
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
	
	
	public abstract void start(CheckedRunnable<?> runnable);


	public void start(CheckedConsumer<AsyncContext, ?> consumer)
	{
		Check.notNull(consumer, "consumer");
		start(() -> consumer.accept(this));
	}

	
	public abstract long getTimeout();


	/**
	 * Sets the timeout in milliseconds. If {@link #complete()} is not called within
	 * the timeout duration, the response is stopped and a timeout event is 
	 * sent to any AsyncEventListener. 
	 * @param milliSeconds the timeout in milliSeconds. If &lt;= 0, no timeout is applied.
	 */
	public abstract void setTimeout(long milliSeconds);

	
	private final Request request_;
	private final Response response_;
	private boolean completed_;
}
