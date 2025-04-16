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


import java.io.IOException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEventListener;
import org.civilian.response.AsyncInput;
import org.civilian.response.AsyncOutput;
import org.civilian.util.Check;
import org.civilian.util.CheckedRunnable;


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


	@Override public AsyncInput getAsyncInput() throws IOException
	{
		if (asyncInput_ == null)
		{
			ServletInputStream in = Check.isA(getRequest().getContentStream(), ServletInputStream.class);
			asyncInput_ = new AsyncInputAdapter(in);
		}
		return asyncInput_;
	}

	
	@Override public AsyncOutput getAsyncOutput() throws IOException
	{
		if (asyncOutput_ == null)
		{
			ServletOutputStream out = Check.isA(getResponse().getContentStream(), ServletOutputStream.class);
			asyncOutput_ = new AsyncOutputAdapter(out);
		}
		return asyncOutput_;
	}
	

	@Override protected void completeImpl()
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


	@Override public void start(CheckedRunnable<?> runnable) 
	{
		Check.notNull(runnable, "runnable");
		servletAsyncContext_.start(runnable.unchecked());
	}

	
	private final jakarta.servlet.AsyncContext servletAsyncContext_;
	private AsyncInputAdapter asyncInput_;
	private AsyncOutputAdapter asyncOutput_;
}
