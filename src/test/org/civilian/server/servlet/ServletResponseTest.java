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
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.response.ResponseHeaders;
import org.civilian.server.servlet.ServletResponseAdapter;
import org.civilian.server.servlet.SpRequestAdapter;
import org.civilian.server.test.TestApp;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ServletResponseTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		app = new TestApp();
		app.getConfig().setSupportedLocales(Locale.GERMAN);
		app.init("/app");
	}
	
	
	@Before public void before()
	{
		servletReq  = mock(HttpServletRequest.class);
		servletResp = mock(HttpServletResponse.class);
		when(servletReq.getLocale()).thenReturn(Locale.GERMAN);
		request = null;
		response = null;
	}

	
	private void init()
	{
		request  = new SpRequestAdapter(app, servletReq);
		response = new ServletResponseAdapter(request, servletResp);
	}
	
	
	@Test public void testSimpleMethods()
	{
		init();
		
		response.addCookie(null);
		verify(servletResp).addCookie(null);

		response.addSessionId(null);
		verify(servletResp).encodeURL(null);

		assertSame(servletResp, response.getServletResponse());
		
		response.getStatus();
		verify(servletResp).getStatus();
		response.setStatus(11);
		verify(servletResp).setStatus(11);
		
		response.reset();
		verify(servletResp).reset();

		response.isCommitted();
		verify(servletResp).isCommitted();
	}
	
	
	@Test public void testSendError() throws Exception
	{
		init();
		
		assertFalse(response.isCommitted());
		response.sendError(123, "msg", null);
		
		verify(servletResp).setStatus(123);
		verify(servletResp).flushBuffer();
	}
	
	
	@Test public void testSendRedirect() throws Exception
	{
		when(servletResp.encodeRedirectURL("x")).thenReturn("x!id");
		init();
		
		assertFalse(response.isCommitted());
		response.sendRedirect("x");
		
		verify(servletResp).sendRedirect("x!id");
	}
	
	
	@Test public void testWriter() throws Exception
	{
		when(servletResp.getWriter()).thenReturn(mock(PrintWriter.class));
		init();
		
		response.getContentWriter();
		verify(servletResp).getWriter();
	}
	
	
	@Test public void testOutputstream() throws Exception
	{
		init();
		
		response.getContentStream();
		verify(servletResp).getOutputStream();
	}
	
	
	@Test public void testContent() throws Exception
	{
		init();
		
		assertNull(response.getContentType());
		response.setContentType(ContentType.APPLICATION_EXCEL);
		assertSame(ContentType.APPLICATION_EXCEL.getValue(), response.getContentType());
		
		response.setContentLength(2L);
		verify(servletResp).setContentLength(2);
	}
	

	@Test public void testBuffer() throws Exception
	{
		init();

		response.flushBuffer();
		verify(servletResp).flushBuffer();

		response.resetBuffer();
		verify(servletResp).resetBuffer();

		response.getBufferSize();
		verify(servletResp).getBufferSize();

		response.setBufferSize(2);
		verify(servletResp).setBufferSize(2);
	}
	
	
	@Test public void testHeaders() throws Exception
	{
		init();
		
		ResponseHeaders headers = response.getHeaders();
		
		headers.add("x", "y");
		verify(servletResp).addHeader("x", "y");

		headers.addDate("d", 123);
		verify(servletResp).addDateHeader("d", 123);
		
		headers.addInt("i", 456);
		verify(servletResp).addIntHeader("i", 456);
		
		headers.contains("x");
		verify(servletResp).containsHeader("x");
		
		headers.iterator();
		verify(servletResp).getHeaderNames();

		headers.getAll("y");
		verify(servletResp).getHeaders("y");

		headers.get("z");
		verify(servletResp).getHeader("z");

		headers.set("x", "y");
		verify(servletResp).setHeader("x", "y");
	}
	
	
	private static TestApp app;
	private HttpServletRequest servletReq;
	private HttpServletResponse servletResp;
	private SpRequestAdapter request;
	private ServletResponseAdapter response;
}	