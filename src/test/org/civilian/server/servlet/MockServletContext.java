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


import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.ServletContext;


public abstract class MockServletContext implements ServletContext
{
	@Override public void log(String s)
	{
		log(s, null);
	}


	@Override public void log(String s, Throwable e)
	{
		if (s != null)
		{
			if (out == null)
				out = new PrintWriter(new StringWriter()); 
			out.println(s);
		}
		if (e != null)
			throw new IllegalStateException(e);
	}
	
	
	public PrintWriter out = new PrintWriter(new StringWriter()); 
}
