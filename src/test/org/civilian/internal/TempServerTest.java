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
package org.civilian.internal;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.Test;
import org.civilian.CivTest;


public class TempServerTest extends CivTest
{
	@Test public void test() throws Throwable
	{
		TempServer server = new TempServer(); 
		for (Method method : server.getClass().getDeclaredMethods())
		{
			if (!Modifier.isPrivate(method.getModifiers()))
			{
				try
				{
					method.invoke(server, getDummyMethodParams(method));
				}
				catch(InvocationTargetException e)
				{
					Throwable cause = e.getCause();
					if (cause == null)
						throw e;
					if ((cause.getMessage() == null) || !cause.getMessage().equals("Server method may not be called before Application#init was called"))
						throw new IllegalStateException("error in method '" + method.getName() + "'", e);
				}
			}	
		}
	}
}
