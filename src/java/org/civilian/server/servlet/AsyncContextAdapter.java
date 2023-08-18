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


import org.civilian.request.AsyncContext;
import org.civilian.request.AsyncListener;
import org.civilian.util.Check;


class AsyncContextAdapter extends AsyncContext
{
	public AsyncContextAdapter(ServletRequestAdapter request, ServletResponseAdapter response)
	{
		super(request, response);
		contextImpl_ = request.getServletRequest().startAsync(
			request.getServletRequest(),
			response.getServletResponse());
	}
	
	
	@Override public void addListener(AsyncListener listener)
	{
		Check.notNull(listener, "listener");
		contextImpl_.addListener(new AsyncListenerAdapter(listener, this));
	}
	

	@Override public void complete()
	{
		getResponse().closeContent();
		contextImpl_.complete();
	}
	

	@Override public void dispatch()
	{
		contextImpl_.dispatch();
	}

	
	@Override public void dispatch(String path)
	{
		contextImpl_.dispatch(path);
	}
	

	@Override public long getTimeout()
	{
		return contextImpl_.getTimeout();
	}

	
	@Override public void setTimeout(long timeout)
	{
		contextImpl_.setTimeout(timeout);
	}

	
	private javax.servlet.AsyncContext contextImpl_;
}
