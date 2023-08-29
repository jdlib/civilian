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
package org.civilian.samples.chat;


import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEvent;
import org.civilian.response.AsyncListener;
import org.civilian.util.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatApp extends Application
{
	private static final Logger log = LoggerFactory.getLogger(ChatApp.class);

	
	@Override protected void init(AppConfig config) throws Exception
	{
		updateThread_ = new UpdateThread(messages_, asyncContexts_);
		updateThread_.start();
	}
	
	
	@Override protected void close() throws Exception
	{
		if (updateThread_ != null)
			updateThread_.interrupt();
		for (AsyncContext context : asyncContexts_)
			context.complete();
	}
	
	
	public void broadcastMessage(String message) 
	{
		Check.notNull(message, "message");
		try
		{
			messages_.put(message);
		}
		catch (Exception e)
		{
			log.error("error during broadcast", e);
		}
	}


	public void addClient(AsyncContext context)
	{
		context.setTimeout(10*60*1000); // 10 minutes
		context.addListener(new Listener());
		asyncContexts_.add(context);
	}
	
	
	private class Listener implements AsyncListener
	{
		@Override public void onEvent(AsyncEvent event)
		{
			if (event.getType() != AsyncEvent.Type.START)
				asyncContexts_.remove(event.getContext());
		}
	}
	
	
	private UpdateThread updateThread_;
	private Queue<AsyncContext> asyncContexts_ = new ConcurrentLinkedQueue<>();
	private BlockingQueue<String> messages_ = new LinkedBlockingQueue<>();
}
