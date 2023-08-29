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


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import org.civilian.response.AsyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UpdateThread extends Thread
{
	private static final Logger log = LoggerFactory.getLogger(UpdateThread.class);
	
		
	public UpdateThread(BlockingQueue<String> messages, Queue<AsyncContext> asyncContexts)
	{
		messages_ 		= messages;
		asyncContexts_	= asyncContexts;
	}
	
	
	@Override public void run() 
	{
		try 
		{
			while (true) 
			{
				String message = messages_.take();
				for (AsyncContext context : asyncContexts_)
				{
					try
					{
						PrintWriter out = context.getResponse().getContentWriter();
						out.println(message);
						out.flush();
					}
					catch (IOException e)
					{
						log.error("error while sending message", e);
						asyncContexts_.remove(context);
					}
				}
			}
		}
		catch (InterruptedException e) 
		{
			log.info("interrupted, closing");
	    }
	}


	private final BlockingQueue<String> messages_;
	private final Queue<AsyncContext> asyncContexts_;
}
