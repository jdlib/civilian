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
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.request.Request;
import org.civilian.request.RequestHeaders;
import org.civilian.request.RequestSecurity;
import org.civilian.request.Upload;
import org.civilian.request.Uploads;
import org.civilian.request.RequestSecurity.SessionIdSource;
import org.civilian.server.test.TestApp;
import org.civilian.request.Session;
import org.civilian.type.TypeLib;
import org.civilian.util.Iterators;
import org.civilian.util.Value;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class ServletRequestTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		app = new TestApp();
		app.setInitializer(config -> config.setSupportedLocales(Locale.ENGLISH));
		app.init("/app");
	}
	
	
	@Before public void before()
	{
		servletReq = mock(HttpServletRequest.class);
		when(servletReq.getLocale()).thenReturn(Locale.ENGLISH);
		servletResp = mock(HttpServletResponse.class);
		request = null;
	}
	
	
	private void init()
	{
		request  = new SpRequestAdapter(app, servletReq, servletResp);
		response = new ServletResponseAdapter(app, request);
	}
	
	
	@Test public void testAccessors() throws Exception
	{
		init();

		when(servletReq.getMethod()).thenReturn("GET");

		assertSame(app,			request.getOwner());
		assertSame(servletReq,	request.getServletRequest());
		assertSame(servletReq,	request.unwrap(ServletRequest.class));
		
		assertEquals("GET", request.getMethod());
		assertTrue(			request.hasMethod(Request.Method.GET));
		assertFalse(		request.hasMethod(Request.Method.POST));

		request.getCookies();
		verify(servletReq).getCookies();
	}
	
	
	@Test public void testPath() throws Exception
	{
		when(servletReq.getPathInfo()).thenReturn("/alpha");
		when(servletReq.getRequestURI()).thenReturn("/app/alpha");
		when(servletReq.getRequestURL()).thenReturn(new StringBuffer("http:test//app/alpha"));
		when(servletReq.getParameterNames()).thenReturn(getEnum("a"));
		when(servletReq.getParameterValues("a")).thenReturn(new String[] { "1", "2" });

		init();

		assertEquals("/app/alpha", 	request.getPath().toString());
		assertEquals("/app/alpha", 	request.getOriginalPath());
		assertEquals("/alpha", 		request.getRelativePath().toString());
		assertEquals("/app/alpha?a=1&a=2",	request.getUrl());
	}		
		
	
	@SuppressWarnings("unchecked")
	@Test public void testAttributes() throws Exception
	{
		init();

		request.setAttribute("x", "y");
		verify(servletReq).setAttribute("x", "y");
		
		when(servletReq.getAttribute("x")).thenReturn("y");
		assertEquals("y", request.getAttribute("x"));
		
		when(servletReq.getAttributeNames()).thenReturn(mock(Enumeration.class));
		request.getAttributeNames();
		verify(servletReq).getAttributeNames();
	}	
	
	
	@Test public void testParameters() throws Exception
	{
		init();

		String[] aParamValues = { "1", "2"};
		when(servletReq.getParameter("id")).thenReturn("15");
		when(servletReq.getParameter("a")).thenReturn("1");
		when(servletReq.getParameterValues("a")).thenReturn(aParamValues);

		assertEquals("15",	request.getParam("id"));
		assertEquals("1",	request.getParam("a"));	
		assertEquals(null,	request.getParam("b"));	
		assertArrayEquals(aParamValues,	request.getParams("a"));	
		assertEquals(0,		request.getParams("b").length);
	}
	
	
	@Test public void testMatrixParams() throws Exception
	{
		// test extension and matrix parameters
		when(servletReq.getPathInfo()).thenReturn("/alpha.html");
		when(servletReq.getRequestURI()).thenReturn("/app;ignored=1/alpha.html;x=y;z;mp=1;mp=2");

		init();
		
		String[] aParamValues = { "1", "2"};
		assertEquals("/app/alpha.html", request.getPath().toString());
		assertEquals("y",				request.getMatrixParam("x"));
		assertEquals("",				request.getMatrixParam("z"));
		assertEquals("1",				request.getMatrixParam("mp"));
		assertNull(						request.getMatrixParam("p"));
		assertArrayEquals(aParamValues,	request.getMatrixParams("mp"));
		assertEquals(1,					request.getMatrixParam("mp", TypeLib.INTEGER).getValue().intValue());
		HashSet<String> mpNames = new HashSet<>();
		request.getMatrixParamNames().forEachRemaining(mpNames::add);
		assertTrue(mpNames.contains("mp"));
		assertTrue(mpNames.contains("x"));
		assertTrue(mpNames.contains("z"));
		assertEquals(3, mpNames.size());
	}
	

	@Test public void testContent() throws Exception
	{
		init();

		when(servletReq.getContentLengthLong()).thenReturn(20L);
		assertEquals(20L, request.getContentLength());

		assertEquals(null, request.getCharEncoding());
		when(servletReq.getCharacterEncoding()).thenReturn("UTF-8");
		assertEquals("UTF-8", request.getCharEncoding());
		request.setCharEncoding("UTF-8");
		verify(servletReq).setCharacterEncoding("UTF-8");

		assertNull(request.getContentType());
		when(servletReq.getContentType()).thenReturn("text/html; charset=UTF-8");
		assertSame(ContentType.TEXT_HTML, request.getContentType());
		when(servletReq.getContentType()).thenReturn("text/css");
		assertSame(ContentType.TEXT_CSS, request.getContentType());
		
		request.getContentStream();
		verify(servletReq).getInputStream();
		
		request.getContentReader();
		verify(servletReq).getReader();
	}
	
	
	@Test public void testHeaders() throws Exception
	{
		init();

		String[] array = new String[] { "1", "2" }; 
		when(servletReq.getHeader("x")).thenReturn("abc");
		when(servletReq.getHeader("i")).thenReturn("123");
		when(servletReq.getHeader("d")).thenReturn("123.45");
		when(servletReq.getDateHeader("l")).thenReturn(3344L);
		when(servletReq.getHeaders("a")).thenReturn(getEnum(array));
		when(servletReq.getHeaderNames()).thenReturn(getEnum("b", "c"));
		
		RequestHeaders headers = request.getHeaders();
		
		assertEquals("abc", headers.get("x"));

		assertTrue (headers.is("x", "abc"));
		assertFalse(headers.is("x", "123"));
		assertTrue (headers.is("y", null));
		assertFalse(headers.is("y", "456"));
		
		assertEquals(123, headers.getInt("i"));
		Value<Double> d = headers.get("d", TypeLib.DOUBLE);
		assertEquals(123.45d, d.getValue().doubleValue(), 0.0);
		assertEquals(123, headers.getInt("i"));
		assertEquals(3344L, headers.getDate("l"));
		assertArrayEquals2(array, headers.getAll("a"));
		
		Iterator<String> names = headers.iterator();
		assertEquals("b", names.next());
		assertEquals("c", names.next());
		assertFalse(names.hasNext());

		when(servletReq.getHeaders("Accept")).thenReturn(getEnum("*/*;q=0.8,text/html,application/xml;q=0.9"));
		ContentTypeList types = request.getAcceptedContentTypes();
		assertSame(types, request.getAcceptedContentTypes());
		assertEquals(3, types.size());
		assertSame(ContentType.TEXT_HTML, types.get(0));
	}

	
	@Test public void testLocale() throws Exception
	{
		init();

		when(servletReq.getPathInfo()).thenReturn("/alpha");

		assertSame(Locale.ENGLISH, request.getAcceptedLocale());
		verify(servletReq).getHeader("Accept-Language");
		
		when(servletReq.getHeader("Accept-Language")).thenReturn("de");
		when(servletReq.getLocale()).thenReturn(Locale.GERMAN);
		assertEquals(Locale.GERMAN, request.getAcceptedLocale());
		verify(servletReq, times(2)).getHeader("Accept-Language");
		verify(servletReq, times(1)).getLocale();
	}
	
	
	@Test public void testSecurity() throws Exception
	{
		init();
		
		RequestSecurity security = request.getSecurity();

		security.isSecure();
		verify(servletReq).isSecure();

		security.authenticate();
		verify(servletReq).authenticate(servletResp);

		security.getAuthType();
		verify(servletReq).getAuthType();

		security.getUserPrincipal();
		verify(servletReq).getUserPrincipal();

		security.isUserInRole("x");
		verify(servletReq).isUserInRole("x");

		security.login("a", "b");
		verify(servletReq).login("a", "b");

		security.logout();
		verify(servletReq).logout();

		security.getRequestedSessionId();
		verify(servletReq).getRequestedSessionId();

		request.getSession(false);
		verify(servletReq).getSession(false);
		
		Session.Optional optional = request.getSessionOptional();
		assertFalse(optional.isPresent());
		assertNull(optional.getAttribute("x"));
		assertFalse(optional.invalidate());
		
		assertEquals(SessionIdSource.NONE, security.getRequestedSessionIdSource());
		when(servletReq.isRequestedSessionIdFromURL()).thenReturn(true);
		assertEquals(SessionIdSource.FROM_URL, security.getRequestedSessionIdSource());
		when(servletReq.isRequestedSessionIdFromCookie()).thenReturn(true);
		assertEquals(SessionIdSource.FROM_COOKIE, security.getRequestedSessionIdSource());

		security.isRequestedSessionIdValid();
		verify(servletReq).isRequestedSessionIdValid();
	}
	
	
	@Test public void testMultiPart() throws Exception
	{
		init();

		when(servletReq.getPathInfo()).thenReturn("/multi");

		// uploads
		Uploads uploads = request.getUploads();
		assertTrue(uploads.isEmpty());
		assertNull(uploads.get("file"));
		assertNull(uploads.error());
		assertFalse(uploads.names().hasNext());
		
		Part part1 = mock(Part.class);
		Part part2 = mock(Part.class);
		when(part1.getHeader("Content-Disposition")).thenReturn("form-data; name=\"email\"");
		when(part1.getInputStream()).thenReturn(new ByteArrayInputStream("John".getBytes()));
		when(part1.getSize()).thenReturn(4L);
		when(part1.getName()).thenReturn("email");
		when(part2.getHeader("Content-Disposition")).thenReturn("form-data; name=\"photo\"; filename=\"/temp/photo.jpg\"");
		when(part2.getInputStream()).thenReturn(new ByteArrayInputStream("jpg".getBytes()));
		when(part2.getSize()).thenReturn(3L);
		when(part2.getName()).thenReturn("photo");
		ArrayList<Part> parts = new ArrayList<>(); 
		parts.add(part1);
		parts.add(part2);
		when(servletReq.getParts()).thenReturn(parts);

		MpRequestAdapter request = new MpRequestAdapter(app, servletReq, servletResp);
		assertEquals(null, request.getParam("dummy"));		
		assertEquals("John", request.getParam("email"));		
		assertEquals("photo.jpg", request.getParam("photo"));		
		
		uploads = request.getUploads();
		assertFalse(uploads.isEmpty());
		assertNull(uploads.get("dummy"));
		assertNull(uploads.error());
		Upload upload = uploads.get("photo");
		assertNotNull(upload);
		assertEquals(3L, upload.length());
	}
	

	@Test public void testRequestInfos()
	{
		init();

		when(servletReq.getProtocol()).thenReturn("HTTP 1.0");
		when(servletReq.getScheme()).thenReturn("http");
		when(servletReq.getProtocol()).thenReturn("http");
		when(servletReq.getServerName()).thenReturn("host");
		when(servletReq.getServerPort()).thenReturn(8080);
		when(servletReq.getRemoteAddr()).thenReturn("1.2.3.4");
		when(servletReq.getRemoteHost()).thenReturn("remote");
		when(servletReq.getRemoteUser()).thenReturn("user");
		when(servletReq.getRemotePort()).thenReturn(2345);
		when(servletReq.getLocalAddr()).thenReturn("5.5.5.5");
		when(servletReq.getLocalName()).thenReturn("local");
		when(servletReq.getLocalPort()).thenReturn(70);

		assertEquals("http://host:8080", request.getServerInfo().toString());
		assertEquals("http", request.getServerInfo().getProtocol());
		
		assertEquals("user@remote:2345", request.getRemoteInfo().toString());
		assertEquals("1.2.3.4", request.getRemoteInfo().getIp());

		assertEquals("local:70", request.getLocalInfo().toString());
		assertEquals("5.5.5.5", request.getLocalInfo().getIp());
	}


	private Enumeration<String> getEnum(String... values)
	{
		Iterator<String> it = Iterators.forValues(values); // temp variable for javac
		return Iterators.asEnumeration(it);
	}
	

	private static TestApp app;
	private HttpServletRequest servletReq;
	private HttpServletResponse servletResp;
	private SpRequestAdapter request;
	@SuppressWarnings("unused")
	private ServletResponseAdapter response;
}
