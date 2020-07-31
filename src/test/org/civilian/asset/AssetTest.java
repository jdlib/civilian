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
package org.civilian.asset;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.content.ContentType;
import org.civilian.internal.ParamList;
import org.civilian.internal.asset.MemoryAsset;
import org.civilian.internal.asset.UrlAsset;
import org.civilian.server.test.TestResponse;


public class AssetTest extends CivTest
{
	@Test public void testMemoryAsset() throws Exception
	{
		MemoryAsset asset = new MemoryAsset("UTF-8", "bytes");
		assertTrue(asset.isValid());
		assertNotNull(asset.getContent());
		assertTrue(asset.toString().startsWith("MemAsset@"));
		
		try
		{
			new MemoryAsset("XYZ", "bytes");
		}
		catch(IllegalStateException e)
		{
			assertTrue(e.getCause() instanceof UnsupportedEncodingException);
		}
	}


	@Test public void testUrlAsset() throws Exception
	{
		URL url = getClass().getResource("/java/lang/String.class");
		UrlAsset asset = new UrlAsset(url);
		assertTrue(asset.isValid());
		assertEquals(url.toString(), asset.toString());
		try(InputStream in = asset.getInputStream())
		{
			assertNotNull(in);
		}
	}
	
	
	@Test public void testLastModified() throws Exception
	{
		TestAsset asset = new TestAsset("content");
		asset.setLastModified(10000L);
		assertEquals(10000L, asset.lastModified());

		asset.setLastModified(-50L);
		assertEquals(-1L, asset.lastModified());
	}
	
	
	@Test public void testWrite() throws Exception
	{
		TestAsset asset = new TestAsset("content");
		asset.setEncoding("ISO-8859-1");
		asset.setContentType(ContentType.TEXT_CSS);
		
		Request request 			= mock(Request.class);
		ParamList reqHeaders 		= new ParamList(true);
		TestResponse response 		= new TestResponse(request);
		when(request.getHeaders()).thenReturn(reqHeaders);
		
		asset.setLastModified(10000);
		reqHeaders.setDate("If-Modified-Since", 9500);
		
		asset.write(response, true);
		assertEquals("Thu, 01 Jan 1970 00:00:10 GMT", response.getHeaders().get("Last-Modified"));
		assertEquals(2592000, response.getHeaders().getInt("max-age"));
		assertEquals(Response.Status.SC304_NOT_MODIFIED, response.getStatus());
		
		response.reset();
		reqHeaders.setDate("If-Modified-Since", 8000);
		asset.write(response, true);

		assertEquals("Thu, 01 Jan 1970 00:00:10 GMT", response.getHeaders().get("Last-Modified"));
		assertEquals(2592000, response.getHeaders().getInt("max-age"));
		assertEquals(ContentType.TEXT_CSS.getValue(), response.getContentType());
		assertEquals("ISO-8859-1", response.getContentEncoding());
		assertEquals(Response.Status.SC200_OK, response.getStatus());
		assertEquals("content", response.getContentText(true));
	}
}
