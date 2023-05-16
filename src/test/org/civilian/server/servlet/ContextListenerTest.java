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


import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.civilian.CivTest;


public class ContextListenerTest extends CivTest
{
	@SuppressWarnings("boxing")
	@Test public void test()
	{
		ServletContextEvent event 	= mock(ServletContextEvent.class);
		ServletContext context 		= mock(ServletContext.class);
		when(event.getServletContext()).thenReturn(context);
		when(context.getMajorVersion()).thenReturn(Integer.valueOf(2), Integer.valueOf(3));
		doAnswer(new Answer<Object>()
		{
			@Override public Object answer(InvocationOnMock invocation) throws Throwable
			{
				attrName 	= (String)invocation.getArguments()[0];
				attrValue 	= invocation.getArguments()[1];
				return null;
			}
			
		}).when(context).setAttribute(anyString(), any());
		
		
		ContextListener listener = new ContextListener();

		// reject servlet version 2
		listener.contextInitialized(event);
		verify(context, times(0)).setAttribute(anyString(), any());
		assertNull(attrName);
		assertNull(attrValue);
		
		// reject servlet version 3
		listener.contextInitialized(event);
		verify(context, times(1)).setAttribute(anyString(), any());
		assertEquals(ServletServer.class.getName(), attrName);
		assertNotNull(attrValue);
		assertEquals(ServletServer.class, attrValue.getClass());
		
		listener.contextDestroyed(null);
	}
	
	
	public String attrName;
	public Object attrValue;
}
