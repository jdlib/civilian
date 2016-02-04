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
package org.civilian.resource;


import java.util.Locale;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.Request;
import org.civilian.Resource;
import org.civilian.Response;
import org.civilian.response.UriEncoder;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.LocaleSerializer;
import org.civilian.type.fn.StandardSerializer;


public class UrlTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		request_ = mock(Request.class);
		when(request_.getPath()).thenReturn(new Path("/some/path"));
		
		response_ = mock(Response.class);
		when(response_.getResponse()).thenReturn(response_);
		when(response_.getRequest()).thenReturn(request_);
		when(response_.getUriEncoder()).thenReturn(new UriEncoder());
		when(response_.getLocaleSerializer()).thenReturn(new LocaleSerializer(Locale.ENGLISH));
		when(response_.addSessionId(anyString())).then(new Answer<String>()
		{
			@Override public String answer(InvocationOnMock invocation) throws Throwable
			{
				return (String)invocation.getArguments()[0] + sessionId_;
			}
		});
	}
	
	
	@Before public void beforeTest()
	{
		sessionId_ = "";
	}
	
	

	@Test public void testCreateReqPath()
	{
		Url url = new Url(response_, request_.getPath());
		assertEquals("/some/path", url.toString());
		
		assertSame(response_, url.getResponse());
		url.setSerializer(StandardSerializer.INSTANCE);
		assertSame(StandardSerializer.INSTANCE, url.getSerializer());
	}
	

	@Test public void testCreateString()
	{
		Url url;
		
		url = new Url(response_, "/");
		assertEquals("/", url.toString());
		
		url = new Url(response_, "/index.html");
		assertEquals("/index.html", url.toString());
		
		url = new Url(response_, "index.html");
		assertFalse(url.addSessionId());
		assertEquals("index.html", url.toString());

		url = new Url(response_, "index.html");
		url.addSessionId(false);
		assertEquals("index.html", url.toString());
		
		url = new Url(response_, "http://test.com");
		assertFalse(url.addSessionId());
		assertEquals("http://test.com", url.toString());
	}
	

	@Test public void testResourceUrls()
	{
		PathParam<String> pp 	= PathParams.forSegment("id");
		PathParam<String> pp2 	= PathParams.forSegment("id2");
		Resource root 			= new Resource();
		Resource ppChild   		= new Resource(root, pp);
		root.getTree().setAppPath(new Path("/app"));
		
		when(request_.getPathParam(pp)).thenReturn("abc");
		
		Url url = new Url(response_, ppChild);
		assertEquals(1, 		url.getPathParamCount());
		assertEquals("abc", 	url.getPathParam(0));
		assertSame  (pp, 		url.getPathParamDef(0));
		assertEquals("abc",		url.getPathParam(pp));
		assertNull(url.getPathParam(null));
		assertEquals("/app/abc",url.toString());
		
		url.setPathParam("xyz");
		assertEquals("xyz", 	url.getPathParam(0));

		url.setPathParam(pp, "mno");
		assertEquals("mno", 	url.getPathParam(0));
		
		url.setPathParams("rst");
		assertEquals("rst", 	url.getPathParam(0));

		url.clearPathParams();
		assertEquals(null, 		url.getPathParam(0));
		
		try
		{
			url.setPathParam(pp2, "x");
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	
	@Test public void testAddPath()
	{
		Url url = new Url(response_, "/");
		
		url.append("a");
		assertEquals("/a", url.toString());
		
		url.append("/test.xml");
		assertEquals("/a/test.xml", url.toString());
	}
	

	@Test public void testSessionId()
	{
		Url url;
		
		url = new Url(response_, "/");
		assertEquals("/", url.toString());
		
		assertFalse(url.addSessionId());
		url.addSessionId(true);
		assertTrue(url.addSessionId());
		sessionId_ = "!x";
		assertEquals("/!x", url.toString());

		url.addSessionId(false);
		assertEquals("/", url.toString());
	}
	
	
	@Test public void testQueryParams()
	{
		Url url = new Url(response_, "index.html");
		assertEquals(0, url.getQueryParamCount());
		
		// clear, remove when empty
		url.clearQueryParams();
		url.removeQueryParam((Url.QueryParam)null);
		assertEquals(0, url.getQueryParamCount());

		// add simple and get
		url.addEmptyQueryParam("a");
		url.addQueryParam("b", "2");
		assertEquals(2, url.getQueryParamCount());
		assertEquals("index.html?a=&b=2", url.toString());
		assertEquals("a",  url.getQueryParam(0).getName());
		assertEquals(null, url.getQueryParam(0).getValue());
		assertEquals(null, url.getQueryParam("c", false));
		assertEquals("a",  url.getQueryParam("a", false).getName());

		// escaping
		url.addQueryParam("a", "#");
		assertEquals("index.html?a=&b=2&a=%23", url.toString());
		
		// remove
		url.removeQueryParams("a");
		assertEquals("index.html?b=2", url.toString());
		url.getQueryParam(0).setValue(true);
		assertEquals("index.html?b=true", url.toString());
		
		// clear when not empty
		url.clearQueryParams();
		assertEquals("index.html", url.toString());
		
		// value format
		Url.QueryParam p = url.getQueryParam("d", true);
		p.setValue(TypeLib.DOUBLE, new Double(2.0));
		assertEquals("index.html?d=2.00", url.toString());
		p.setValue(2);
		assertEquals("index.html?d=2", url.toString());
		url.removeQueryParam(p);
		assertEquals(0, url.getQueryParamCount());

		// value integers
		url.addQueryParam("i", 1234);
		url.addQueryParam("i", new Integer(78));
		assertEquals("index.html?i=1%2C234&i=78", url.toString());
		url.clearQueryParams();
		
		url.addQueryParam("b", true);
		url.addQueryParam("b", Boolean.FALSE);
		url.addQueryParam("b", TypeLib.BOOLEAN, Boolean.TRUE);
		assertEquals("index.html?b=true&b=false&b=true", url.toString());
		url.clearQueryParams();

		
		// fragments
		url.addQueryParam("x");
		url.setFragment("frag");
		assertEquals("frag", url.getFragment());
		assertEquals("index.html?x=#frag", url.toString());
	}
	

	private static Request request_;
	private static Response response_;
	private static String sessionId_;
}
