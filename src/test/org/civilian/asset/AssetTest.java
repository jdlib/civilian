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


import java.io.File;
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
import org.civilian.internal.asset.BytesAsset;
import org.civilian.internal.asset.CachedAsset;
import org.civilian.internal.asset.FileAsset;
import org.civilian.internal.asset.UrlAsset;
import org.civilian.server.test.TestResponse;
import org.civilian.util.HttpHeaders;


public class AssetTest extends CivTest
{
	@Test public void testBytesAsset() throws Exception
	{
		BytesAsset asset = new BytesAsset("UTF-8", "bytes");
		assertTrue(asset.isValid());
		assertTrue(asset.toString().startsWith("BytesAsset@"));
		assertSame(asset, asset.cache());
		
		try
		{
			new BytesAsset("XYZ", "bytes");
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
	
	
	@Test public void testCachedAsset() throws Exception
	{
		File file = File.createTempFile("test", ".css");
		write(file, "UTF-8", "body");
		try
		{
			FileAsset fileAsset = new FileAsset(file);
			Asset asset = fileAsset.cache();
			assertTrue(asset instanceof CachedAsset);
			CachedAsset cached = (CachedAsset)asset;
			
			assertArrayEquals("body".getBytes(), cached.getContent());
			assertSame(cached, cached.cache());
			assertEquals(4, asset.length());
			assertEquals(file.lastModified(), asset.getLastModified());
			assertTrue(asset.isValid());
			
			write(file, "UTF-8", "body{}");
			assertFalse(asset.isValid());
		}
		finally
		{
			assertTrue(file.getAbsolutePath(), file.delete());
		}
	}

	
	@Test public void testLastModified() throws Exception
	{
		TestAsset asset = new TestAsset("content");
		asset.setLastModified(10000L);
		assertEquals(10000L, asset.getLastModified());

		asset.setLastModified(-50L);
		assertEquals(-1L, asset.getLastModified());
	}
	
	
	@Test public void testWrite() throws Exception
	{
		TestAsset asset = new TestAsset("content");
		asset.setCharEncoding("ISO-8859-1");
		asset.setContentType(ContentType.TEXT_CSS);
		asset.setCacheControl(AssetCacheControl.DEFAULT);
		
		Request request 			= mock(Request.class);
		ParamList reqHeaders 		= new ParamList(true);
		TestResponse response 		= new TestResponse(request);
		when(request.getHeaders()).thenReturn(reqHeaders);
		
		asset.setLastModified(10000);
		reqHeaders.setDate(HttpHeaders.IF_MODIFIED_SINCE, 9500);
		
		asset.write(response, true);
		assertEquals("Thu, 01 Jan 1970 00:00:10 GMT", response.getHeaders().get(HttpHeaders.LAST_MODIFIED));
		assertEquals("max-age=2592000", response.getHeaders().get(HttpHeaders.CACHE_CONTROL));
		assertEquals(Response.Status.SC304_NOT_MODIFIED, response.getStatus());
		
		response.reset();
		reqHeaders.setDate(HttpHeaders.IF_MODIFIED_SINCE, 8000);
		asset.write(response, true);

		assertEquals("Thu, 01 Jan 1970 00:00:10 GMT", response.getHeaders().get(HttpHeaders.LAST_MODIFIED));
		assertEquals("max-age=2592000", response.getHeaders().get(HttpHeaders.CACHE_CONTROL));
		assertEquals(ContentType.TEXT_CSS.getValue(), response.getContentType());
		assertEquals("ISO-8859-1", response.getCharEncoding());
		assertEquals(Response.Status.SC200_OK, response.getStatus());
		assertEquals("content", response.getContentText(true));
	}
}
