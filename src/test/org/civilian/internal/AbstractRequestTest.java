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


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import org.civilian.CivTest;
import org.civilian.Request;
import org.civilian.server.test.TestApp;
import org.civilian.server.test.TestRequest;
import org.civilian.server.test.TestResponse;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class AbstractRequestTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		app = new TestApp();
		app.init();
	}

	
	@Before public void before()
	{
		request 	= new TestRequest(app);
		response 	= request.getTestResponse();   
	}
	
	
	@Test public void testBasics()
	{
		assertSame(request, request.getRequest());
		assertSame(app, request.getApplication());
		assertSame(app.getServer(), request.getServer());
		
		request.setResponse(response); // ok
	}
	
	
	@Test public void testReader() throws Exception
	{
		assertEquals("UTF-8", request.getContentEncoding());
		assertSame(Request.ContentAccess.NONE, request.getContentAccess());
		Reader reader = request.getContentReader();
		assertSame(Request.ContentAccess.READER, request.getContentAccess());
		assertSame(reader, request.getContentReader());
		
		try
		{
			request.getContentStream();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Request.getContentReader() has already been called", e.getMessage());
		}
	}
	
	
	@Test public void testStream() throws Exception
	{
		assertSame(Request.ContentAccess.NONE, request.getContentAccess());
		InputStream in = request.getContentStream();
		assertSame(Request.ContentAccess.INPUTSTREAM, request.getContentAccess());
		assertSame(in, request.getContentStream());
		
		try
		{
			request.getContentReader();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Request.getContentStream() has already been called", e.getMessage());
		}
	}
	
	
	@Test public void testIntercept() throws Exception
	{
		request.setContent("123");
		TestRequestStreamInterceptor i1 = new TestRequestStreamInterceptor("abc"); 
		TestRequestStreamInterceptor i2 = new TestRequestStreamInterceptor("def");
		
		request.addInterceptor(i1);
		request.addInterceptor(i2);
		assertEquals("defabc123", request.readContent(String.class));
		
		// can't add interceptors once a input was obtained
		try
		{
			request.addInterceptor(i1);
		}
		catch(IllegalStateException e)
		{
			assertEquals("Request.getContentReader() has already been called", e.getMessage());
		}
		
		request.resetContentInput();
		request.clearInterceptors();

		TestRequestReaderInterceptor i3 = new TestRequestReaderInterceptor("text"); 
		request.addInterceptor(i3);
		assertEquals("text123", request.readContent(String.class));
	}
	
	
	@Test public void testPrint() throws Exception
	{
		request.getHeaders().set("x", "1");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		request.print(new PrintStream(out, true));
		
		assertEquals("GET /\nx 1\n", out.toString().replace("\r", ""));
	}
	
	
	private static TestApp app;
	private static TestRequest request;
	private TestResponse response;
}
