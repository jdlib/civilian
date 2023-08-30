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


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.mockito.Mockito.*;
import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.request.Request;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParams;
import org.civilian.response.Response;
import org.civilian.response.ResponseOwner;
import org.civilian.response.Url;
import org.civilian.text.service.LocaleService;
import org.civilian.text.type.StandardSerializer;
import org.civilian.type.TypeLib;


public class UrlTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		request_ = mock(Request.class);
		when(request_.getPath()).thenReturn(new Path("/some/path"));
		
		ResponseOwner respOwner = mock(ResponseOwner.class);
		when(respOwner.getPath()).thenReturn(new Path("/app"));
		response_ = mock(Response.class);
		when(response_.getResponse()).thenReturn(response_);
		when(response_.getRequest()).thenReturn(request_);
		when(response_.getOwner()).thenReturn(respOwner);
		when(response_.getLocaleService()).thenReturn(new LocaleService(Locale.ENGLISH));
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
		assertEquals("index.html", url.toString());

		url = new Url(response_, "http://test.com");
		assertEquals("http://test.com", url.toString());
	}
	

	@Test public void testResourceUrls()
	{
		PathParam<String> pp 	= PathParams.forSegment("id");
		PathParam<String> pp2 	= PathParams.forSegment("id2");
		Resource root 			= new Resource();
		Resource ppChild   		= new Resource(root, pp);
		
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
	

	@Test public void testQueryParams()
	{
		Url url = new Url(response_, "index.html");
		
		Url.QueryParamList queryParams = url.queryParams();
		assertEquals(0, queryParams.size());
		
		// clear, remove when empty
		queryParams.clear();
		queryParams.remove((Url.QueryParam)null);
		assertEquals(0, queryParams.size());

		// add simple and get
		queryParams.add("a");
		queryParams.add("b", "2");
		assertEquals(2, queryParams.size());
		assertEquals("index.html?a=&b=2", url.toString());
		assertEquals("a",  queryParams.get(0).getName());
		assertEquals(null, queryParams.get(0).getValue());
		assertEquals(null, queryParams.get("c"));
		assertEquals("a",  queryParams.getRequired("a").getName());

		// escaping
		queryParams.add("a", "#");
		assertEquals("index.html?a=&b=2&a=%23", url.toString());
		
		// remove
		queryParams.remove("a");
		assertEquals("index.html?b=2", url.toString());
		queryParams.get(0).setValue(true);
		assertEquals("index.html?b=true", url.toString());
		
		// clear when not empty
		queryParams.clear();
		assertEquals("index.html", url.toString());
		
		// value format
		Url.QueryParam p = queryParams.getOrCreate("d");
		p.setValue(TypeLib.DOUBLE, Double.valueOf(2.0));
		assertEquals("index.html?d=2.00", url.toString());
		p.setValue(2);
		assertEquals("index.html?d=2", url.toString());
		queryParams.remove(p);
		assertEquals(0, queryParams.size());

		// value integers
		queryParams.add("i").setValue(1234);
		queryParams.add("i").setValue(Integer.valueOf(78));
		assertEquals("index.html?i=1%2C234&i=78", url.toString());
		queryParams.clear();
		
		queryParams.add("b").setValue(true);
		queryParams.add("b").setValue(Boolean.FALSE);
		queryParams.add("b").setValue(TypeLib.BOOLEAN, Boolean.TRUE);
		assertEquals("index.html?b=true&b=false&b=true", url.toString());
		queryParams.clear();

		
		// fragments
		queryParams.add("x");
		url.setFragment("frag");
		assertEquals("frag", url.getFragment());
		assertEquals("index.html?x=#frag", url.toString());
	}
	

	private static Request request_;
	private static Response response_;
	private static String sessionId_;
}
