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


import static org.mockito.Mockito.*;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.application.Application;
import org.civilian.application.DefaultApp;
import org.civilian.resource.Path;


public class AppServletTest extends CivTest
{
	// test creation of AppServlet via xml and lookup of an application
	// via the servletcontext
	@Test public void testLookupApp() throws Exception
	{
		ServletContext context 	= mock(ServletContext.class); 
		ServletConfig config 	= mock(ServletConfig.class);
		ServletServer server 	= mock(ServletServer.class);
		DefaultApp app			= new DefaultApp("");
		when(config.getServletContext()).thenReturn(context);

		AppServlet servlet = new AppServlet();
		
		// we need parameter 'app.id'
		assertInitFail(servlet, config, "no init parameter 'app.id' defined");
		
		// return "test" as app.id 
		when(config.getInitParameter("app.id")).thenReturn("test");

		// ServletContainer is missing
		assertInitFail(servlet, config, "ServletContext is not available:");
		
		when(context.getAttribute(ServletServer.class.getName())).thenReturn(server);

		// app missing
		assertInitFail(servlet, config, "no application with id 'test' defined");

		when(server.getApplication("test", Application.class)).thenReturn(app);
		servlet.init(config);
		assertSame(config, servlet.getServletConfig());
		assertNotNull(servlet.getServletInfo());
		servlet.destroy();
	}
		
		
	private void assertInitFail(AppServlet servlet, ServletConfig config, String message) throws Exception
	{
		try
		{
			// ServletContainer is missing
			servlet.init(config);
			fail();
		}
		catch(UnavailableException e)
		{
			assertTrue(e.getMessage().startsWith(message));
		}
	}


	@Test public void testService() throws Exception
	{
		Application app = mock(Application.class);
		when(app.getPath()).thenReturn(Path.ROOT);
		when(app.getDefaultEncoding()).thenReturn(StandardCharsets.UTF_8);
		AppServlet servlet = new AppServlet(app);
	
		ServletRequest invRequest  		= mock(ServletRequest.class);
		ServletResponse invResponse 	= mock(ServletResponse.class);
		servlet.service(invRequest, invResponse);
		verify(app, times(0)).process(any(ServletRequestAdapter.class), any(ServletResponseAdapter.class));
		
		HttpServletRequest request  	= mock(HttpServletRequest.class);
		HttpServletResponse response 	= mock(HttpServletResponse.class);
		
		servlet.service(request, response);
		verify(app, times(1)).process(any(SpRequestAdapter.class), any(ServletResponseAdapter.class));
		
		when(request.getContentType()).thenReturn("multipart/form-data");
		servlet.service(request, response);
		verify(app, times(1)).process(any(MpRequestAdapter.class), any(ServletResponseAdapter.class));
	}
}
