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
package org.civilian.server.servlet;


import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEventListener;
import org.civilian.response.AsyncWriteListener;


class AsyncContextAdapter extends AsyncContext
{
	public AsyncContextAdapter(ServletRequestAdapter request, ServletResponseAdapter response)
	{
		super(request, response);
		servletAsyncContext_ = request.getServletRequest().startAsync(
			request.getServletRequest(),
			response.getServletResponse());
	}
	
	
	@Override public void addEventListener(AsyncEventListener listener)
	{
		servletAsyncContext_.addListener(new AsyncEventListenerAdapter(listener, this));
	}


	@Override public void addWriteListener(AsyncWriteListener listener) 
	{
		throw new Error("not yet implemented");
	}
	

	@Override public void complete()
	{
		getResponse().closeContent();
		servletAsyncContext_.complete();
	}
	

	@Override public void dispatch()
	{
		servletAsyncContext_.dispatch();
	}

	
	@Override public void dispatch(String path)
	{
		servletAsyncContext_.dispatch(path);
	}
	

	@Override public long getTimeout()
	{
		return servletAsyncContext_.getTimeout();
	}

	
	@Override public void setTimeout(long timeout)
	{
		servletAsyncContext_.setTimeout(timeout);
	}


	@Override public void start(Runnable runnable) 
	{
		servletAsyncContext_.start(runnable);
	}

	
	private final javax.servlet.AsyncContext servletAsyncContext_;
}
