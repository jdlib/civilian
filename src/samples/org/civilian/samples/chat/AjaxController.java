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


import java.io.PrintWriter;
import org.civilian.annotation.Get;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.controller.Controller;
import org.civilian.response.Response;
import org.civilian.annotation.Parameter;


public class AjaxController extends Controller
{
	private static final String JUNK = "<!-- ============= filler comment for first request ============= -->\n";
	
	
	@Override public ChatApp getApplication()
	{
		return (ChatApp)super.getApplication();
	}
	
	
	@Get @Produces("text/html") public void get() throws Exception
	{
		// some browser require some content
		PrintWriter out = getResponse().getContentWriter();
		for (int i=0; i<20; i++)
			out.write(JUNK);
		out.flush();

		// register the async-context
		getApplication().addClient(getResponse().startAsync());
	}
	
	
	@Post @Produces("text/plain") public void post(
		@Parameter("action") String action,
		@Parameter("name") String name,
		@Parameter("message") String message) throws Exception
	{
		String broadcastMsg = null;
		
		if ("login".equals(action))
		{
			if (name != null)
				broadcastMsg = createMessageScript("System Message", name + " has joined.");
		}
		else if ("post".equals(action))
		{
			if ((name != null) && (message != null))
				broadcastMsg = createMessageScript(name, message);
		}
		
		Response response = getResponse();
		if (broadcastMsg != null)
		{
			getApplication().broadcastMessage(broadcastMsg);
			response.getContentWriter().println("success");
		}
		else
			response.sendError(Response.Status.SC400_BAD_REQUEST);
	}


	private String createMessageScript(String name, String message)
	{
		return "<script>\n" + 
			"window.parent.chat.update({ name: \"" + escape(name) + "\", message: \"" + escape(message) + "\" });\n" + 
			"</script>\n";
	}


	private String escape(String s)
	{
		StringBuilder buffer = new StringBuilder(s.length());
		for (int i=0; i<s.length(); i++)
		{
			char c = s.charAt(i);
			switch(c)
			{
				case '\b': buffer.append("\\b"); break;
				case '\f': buffer.append("\\f"); break;
				case '\n': buffer.append("<br />"); break;
				case '\r': break;
				case '\t': buffer.append("\\t"); break;
				case '\'': buffer.append("\\'"); break;
				case '\"': buffer.append("\\\""); break;
				case '\\': buffer.append("\\\\"); break;
				case '<':  buffer.append("&lt;"); break;
				case '>':  buffer.append("&gt;"); break;
				case '&':  buffer.append("&amp;"); break;
				default:   buffer.append(c); break;
			}
		}
		return buffer.toString();
	}
}


