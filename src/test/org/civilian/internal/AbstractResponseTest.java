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
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.Resource;
import org.civilian.Response;
import org.civilian.content.ContentType;
import org.civilian.resource.Url;
import org.civilian.server.test.TestApp;
import org.civilian.server.test.TestRequest;
import org.civilian.server.test.TestResponse;
import org.civilian.template.TemplateWriter;
import org.civilian.template.TextTemplate;
import org.civilian.text.LocaleService;
import org.civilian.text.keys.KeyList;
import org.civilian.text.keys.KeyLists;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class AbstractResponseTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		app = new TestApp();
		app.init();
	}

	
	@Before public void before()
	{
		init();
	}
	
	
	private void init()
	{
		request 	= new TestRequest(app);
		response 	= request.getTestResponse();   
	}
	
			
	@Test public void testAccessors() throws Exception
	{
		assertSame(request, 			response.getRequest());
		assertSame(app, 				response.getApplication());
		assertSame(response, 			response.getResponse());
		assertSame(app.getServer(), 	response.getServer());
		
		assertNotNull(response.getUriEncoder());

		LocaleService lsEn = new LocaleService(Locale.ENGLISH);
		LocaleService lsDe = new LocaleService(Locale.GERMAN);
		request.setLocaleService(lsEn);
		assertSame(lsEn, response.getLocaleService());
		response.setLocaleService(lsDe);
		assertSame(lsDe, response.getLocaleService());
		
		assertNull(response.getCharEncoding());
		response.setCharEncoding("UTF-8");
		assertEquals("UTF-8", response.getCharEncoding());

		assertNull(response.getContentType());
		response.setContentType(ContentType.TEXT_PLAIN);
		assertSame(ContentType.TEXT_PLAIN.getValue(), response.getContentType());
		
		assertEquals("text/plain; charset=UTF-8", response.getContentTypeAndEncoding());
	}
	
	
	@Test public void testSendError() throws Exception
	{
		response.sendError(400, "fail", null);
		
		assertSame(Response.Type.ERROR, response.getType());
		assertEquals(400, response.getStatus());
		assertTrue(response.isCommitted());
		
		try
		{
			response.sendError(500);
		}
		catch(IllegalStateException e)
		{
			assertEquals("response is committed", e.getMessage());
		}
	}


	@Test public void testRedirect() throws Exception
	{
		response.sendRedirect(new Url(response, "x"));
		assertSame(Response.Type.REDIRECT, response.getType());
		assertEquals("x", response.getHeaders().get("location"));
		assertTrue(response.isCommitted());
		
		try
		{
			response.sendRedirect(new Resource());
		}
		catch(IllegalStateException e)
		{
			assertEquals("response is committed", e.getMessage());
		}
	}


	@Test public void testOutputStream() throws Exception
	{
		assertSame(Response.ContentAccess.NONE, response.getContentAccess());
		
		OutputStream out = response.getContentStream();
		assertSame(Response.ContentAccess.OUTPUTSTREAM, response.getContentAccess());
		assertSame(out, response.getContentStream());
		
		try
		{
			response.getContentWriter();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Response.getContentStream() has already been called", e.getMessage());
		}
	}
	
	
	@Test public void testWriter() throws Exception
	{
		assertNull(response.getCharEncoding());
		assertSame(Response.ContentAccess.NONE, response.getContentAccess());

		TemplateWriter out = response.getContentWriter();
		
		assertSame(app.getDefaultCharEncoding(), response.getCharEncoding());
		response.setCharEncoding("x");
		assertSame(app.getDefaultCharEncoding(), response.getCharEncoding());
		assertSame(Response.ContentAccess.WRITER, response.getContentAccess());
		assertSame(out, response.getContentWriter());
		assertSame(response, out.getAttribute(Response.class));
		
		try
		{
			response.getContentStream();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Response.getContentWriter() has already been called", e.getMessage());
		}
		
		assertEquals("", response.getContentText(false));
		assertEquals("", response.getContentText(true));
		out.write("hello");
		assertEquals("", response.getContentText(false));
		assertEquals("hello", response.getContentText(true));
	}
	
	
	@Test public void testWriteTemplate() throws Exception
	{
		TextTemplate t = new TextTemplate("abc");
		
		response.writeTemplate(null); // ignored
		response.writeTemplate(t);
		response.writeContent(t);
		
		assertEquals("abcabc", response.getContentText(true));
	}

	
	@Test public void testWriteJson() throws Exception
	{
		response.writeJson(new int[] { 1, 2 });
		assertEquals("[1,2]", response.getContentText(true));
		
		response.clearCommitted();
		KeyList<String> kl = KeyLists.forTexts("a", "b");
		response.writeJson(kl);
		assertEquals("[{\"text\":\"a\",\"value\":\"a\"},{\"text\":\"b\",\"value\":\"b\"}]", response.getContentText(true));
	}

	
	@Test public void testWriteString() throws Exception
	{
		response.writeContent(null); // ignored
		response.writeContent("hello");
		assertEquals("hello", response.getContentText(true));
	}
	

	@Test public void testWriteUnsupportedObject() throws Exception
	{
		try
		{
			response.writeContent(new Object(), ContentType.APPLICATION_X_MPEG);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("no ContentSerializer for content type 'application/x-mpeg' to write a 'java.lang.Object'. Are third party libraries missing?", e.getMessage());
		}
	}
	
	
	@Test public void testPrint() throws Exception
	{
		response.getHeaders().set("x", "1");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		response.print(new PrintStream(out, true));
		
		assertEquals("200\nx 1\n", out.toString().replace("\r", ""));
	}
	
	
	@Test public void testStreamIntercept() throws Exception
	{
		TestResponseStreamInterceptor interceptor1 = new TestResponseStreamInterceptor("abc");
		TestResponseStreamInterceptor interceptor2 = new TestResponseStreamInterceptor("def");
		
		// can't set an interceptor when a stream or writer was already obtained
		response.getContentWriter();
		try
		{
			response.addInterceptor(interceptor1);
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("Response.getContentWriter() has already been called", e.getMessage());
		}
		
		// add two interceptors and test that they are both executed in the correct order
		response.clear();
		response.addInterceptor(interceptor1);
		response.addInterceptor(interceptor2);
		
		OutputStream out = response.getContentStream();
		out.write('0');
		assertEquals("abcdef0", response.getContentText(true));
		
		
		// add two interceptors but one will not contribute
		interceptor1.intercept = false;
		response.clear();
		response.addInterceptor(interceptor1);
		response.addInterceptor(interceptor2);
		TemplateWriter writer = response.getContentWriter();
		writer.print('0');
		assertEquals("def0", response.getContentText(true));

		// add two interceptors but no one will contribute
		interceptor1.intercept = false;
		interceptor2.intercept = false;
		response.clear();
		response.addInterceptor(interceptor1);
		response.addInterceptor(interceptor2);
		writer = response.getContentWriter();
		writer.print('0');
		assertEquals("0", response.getContentText(true));
		
		
		// use an interceptor which writes on flush to test if flushBuffer works
		interceptor1.lazy = true;
		interceptor1.intercept = true;
		response.clear();
		response.addInterceptor(interceptor1);
		out = response.getContentStream();
		out.write('0');
		assertEquals("0", response.getBufferText());
		out.flush();
		assertEquals("", response.getBufferText());
		assertEquals("0abc", response.getContentText(false));
		assertEquals("0abcabc", response.getContentText(true));

		
		// use an interceptor which writes to the outputstream,
		// add own content via an writer and then reset the buffer
		// <- writer buffer (in OutputStreamWriter) should be cleared
		// <- interceptor output should be regenerated
		interceptor1.lazy = false;
		interceptor1.intercept = true;
		response.clear();
		response.addInterceptor(interceptor1);
		assertEquals("", response.getBufferText());
		writer = response.getContentWriter();
		assertEquals("abc", response.getBufferText());
		
		writer.print("1");
		// "1" is in the buffer of the OutputStreamWriter
		assertEquals("abc", response.getBufferText());
		
		response.resetBuffer();
		assertEquals("abc", response.getBufferText());
		writer.print("2");
		writer.flush();
		assertEquals("", response.getBufferText());
		assertEquals("abc2", response.getContentText(false));
	}
	
	
	@Test public void testWriterIntercept() throws Exception
	{
		TestResponseStreamInterceptor is = new TestResponseStreamInterceptor("abc");
		TestResponseWriterInterceptor ir1 = new TestResponseWriterInterceptor("123");
		TestResponseWriterInterceptor ir2 = new TestResponseWriterInterceptor("456");
		
		// test two writer interceptors
		response.addInterceptor(ir1);
		response.addInterceptor(ir2);
		Writer out = response.getContentWriter();
		out.write("z");
		assertEquals("123456z", response.getContentText(true));
		
		
		// test a writer interceptor + stream interceptor
		ir2.intercept = false;
		response.clear();
		response.addInterceptor(ir1);
		response.addInterceptor(ir2);
		response.addInterceptor(is);
		out = response.getContentWriter();
		out.write("z");
		assertEquals("abc123z", response.getContentText(true));
	}
	
		
	private static TestApp app;
	private static TestRequest request;
	private TestResponse response;
}
