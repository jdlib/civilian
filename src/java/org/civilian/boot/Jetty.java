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
package org.civilian.boot;


import java.io.File;
import org.civilian.util.Arguments;
import org.civilian.util.FileType;
import org.civilian.util.StringUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


/**
 * Jetty is a command-line interface to start an embedded Jetty server.
 * If you run this class without any arguments, it will print a help message.
 */
public class Jetty
{
	public static void main(String[] s) throws Exception
	{
		Arguments args = new Arguments(s);
		if (!args.hasMore())
		{
			System.out.println("Usage:");
			System.out.println("java " + Jetty.class.getName() + " (options)* <webapp-directory>");
			System.out.println();
			System.out.println("Options:                               default");
			System.out.println("-port <port>         HTTP port         8080");
			System.out.println("-context <context>   ServletContext    /");
			return;
		}
		
		// options
		int port 			= 8080;
		String contextPath 	= "/";
		while(args.startsWith("-"))
		{
			if (args.consume("-port"))
				port = args.nextInt("port");
			else if (args.consume("-context"))
				contextPath = StringUtil.haveLeft(args.next("context"), "/");
		}
		
		// webapp dir
		File webappDir 		= args.nextFile("webapp directory", FileType.EXISTENT_DIR);
		String webappBase 	= webappDir.getAbsolutePath(); 

		// build web context
		WebAppContext context = new WebAppContext();
		context.setDescriptor(webappBase + "/WEB-INF/web.xml");
		context.setResourceBase(webappBase);
		context.setContextPath(contextPath);
		context.setParentLoaderPriority(true);
		
		// start server
		System.out.println("serving webapp " + webappBase + " on http://localhost:" + port + contextPath); 
		
		Server server = new Server(port);
		server.setHandler(context);
		server.start();
		server.join();
	}
}
