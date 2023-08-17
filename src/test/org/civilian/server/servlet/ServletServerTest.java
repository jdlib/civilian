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


import java.io.File;
import javax.servlet.ServletRegistration;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.civilian.CivTest;
import org.civilian.application.DefaultApp;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeLookup;
import org.civilian.internal.admin.AdminApp;
import org.civilian.testcase1.Test1App;


public class ServletServerTest extends CivTest
{
	@Test public void test()
	{
		// prepare servlet mock
		ServletRegistration.Dynamic dynamic = mock(ServletRegistration.Dynamic.class); 
		MockServletContext context = mock(MockServletContext.class);
		doCallRealMethod().when(context).log(anyString());
		doCallRealMethod().when(context).log(anyString(), any(Throwable.class));
		when(context.getContextPath()).thenReturn("/test");
		when(context.getRealPath("/WEB-INF/civilian.ini")).then(new Answer<String>()
		{
			@Override public String answer(InvocationOnMock invocation) throws Throwable
			{
				return new File(getClass().getResource("/org/civilian/testcase1/civilian.ini").toURI()).getAbsolutePath();
			}
		});
		
		when(context.addServlet(anyString(), any(AppServlet.class))).thenReturn(dynamic);

		ServletServer server = new ServletServer(context);
		
		Test1App test1App 	= server.getApplication(Test1App.class);
		AdminApp adminApp 	= server.getApplication(AdminApp.class);
		assertNotNull(test1App);
		assertNotNull(adminApp);
		assertNull(server.getApplication(DefaultApp.class));
		
		assertEquals(test1App, server.getApplication("test"));
		assertEquals(adminApp, server.getApplication("civadmin"));
		
		server.log("test", null);

		when(context.getMimeType("x.css")).thenReturn("text/css");
		ContentTypeLookup lookup = server.getContentTypeLookup();
		assertNull(lookup.forExtension(null));
		assertSame(ContentType.TEXT_CSS, lookup.forExtension("css"));
		assertSame(ContentType.TEXT_CSS, lookup.forExtension(".css"));
		
		server.contextDestroyed(null);
	}
}
