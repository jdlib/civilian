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
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.civilian.util.Arguments;
import org.civilian.util.FileType;
import org.civilian.util.StringUtil;


/**
 * Tomcat is a command-line interface to start an embedded Tomcat server.
 * If you run this class without any arguments, it will print a help message.
 * Code based on http://www.copperykeenclaws.com/embedding-tomcat-7/
 * If your are experiencing a java.lang.SecurityException: access denied (javax.management.MBeanTrustPermission register)
 * you will need to add
 * <pre> 
 * grant { permission javax.management.MBeanTrustPermission "register"; };
 * </pre> 
 * to your java.policy file unter <JRE_HOME>/lib/security.
 */
public class Tomcat
{
	public static void main(String[] s) throws Exception
	{
		Arguments args = new Arguments(s);
		if (!args.hasMore())
		{
			System.out.println("Usage:");
			System.out.println("java " + Tomcat.class.getName() + " (options)* <webapp-directory>");
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
		
		org.apache.catalina.startup.Tomcat tomcat = new org.apache.catalina.startup.Tomcat();
		tomcat.setPort(port);

		tomcat.setBaseDir(".");
		//tomcat.getHost().setAppBase(webappBase);

		StandardServer server = (StandardServer)tomcat.getServer();
		AprLifecycleListener listener = new AprLifecycleListener();
		server.addLifecycleListener(listener);

		tomcat.addWebapp(contextPath, webappBase);
		tomcat.start();
		tomcat.getServer().await();
	}
}
