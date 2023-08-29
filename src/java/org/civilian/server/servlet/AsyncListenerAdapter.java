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
import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEvent;
import org.civilian.response.AsyncListener;
import org.civilian.response.AsyncEvent.Type;


class AsyncListenerAdapter implements javax.servlet.AsyncListener
{
	public AsyncListenerAdapter(org.civilian.response.AsyncListener listener, AsyncContext context)
	{
		context_	= context;
		listener_ 	= listener;
	}
	
	
	@Override public void onComplete(javax.servlet.AsyncEvent servletEvent) throws IOException
	{
		onEvent(Type.COMPLETE, servletEvent);
	}

	
	@Override public void onError(javax.servlet.AsyncEvent servletEvent) throws IOException
	{
		onEvent(Type.ERROR, servletEvent);
	}

	
	@Override public void onStartAsync(javax.servlet.AsyncEvent servletEvent) throws IOException
	{
		onEvent(Type.START, servletEvent);
	}


	@Override public void onTimeout(javax.servlet.AsyncEvent servletEvent) throws IOException
	{
		onEvent(Type.TIMEOUT, servletEvent);
	}

	
	private void onEvent(Type type, javax.servlet.AsyncEvent servletEvent)
	{
		AsyncEvent civEvent = new AsyncEvent(type, context_, servletEvent.getThrowable());
		listener_.onEvent(civEvent);
	}
	
	
	private AsyncListener listener_;
	private AsyncContext context_;
}
