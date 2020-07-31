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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.Reader;
import org.civilian.Application;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.internal.asset.CivResourceLocation;
import org.civilian.resource.Path;
import org.civilian.server.test.TestServer;
import org.civilian.util.IoUtil;
import org.junit.BeforeClass;
import org.junit.Test;


public class AssetLocationTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		TestServer server = new TestServer(); 
		
		app_ = mock(Application.class);
		when(app_.getServer()).thenReturn(server);
	}
	
	
	@Test public void testClassPathLocation() throws Exception
	{
		AssetLocation location = AssetServices.getJavaResourceLocation("/", "javax/servlet");
		assertNull(location.getAsset("/xyz"));
		
		Asset asset = (location.getAsset("/ServletRequest.class"));
		assertNotNull(asset);
	}
	

	@Test public void testCivAssetLocation() throws Exception
	{
		AssetLocation location = AssetServices.getCivResourceLocation(null, null, false);
		assertEquals("/civilian", location.getPath().toString());
		
		assertNull(location.getAsset("/angular"));

		Asset asset = location.getAsset("/civilian/angular/civ-auth.js");
		assertNotNull(asset);
		assertTrue(asset.isValid());
		assertEquals("UTF-8", asset.getEncoding());
		assertEquals(ContentType.APPLICATION_OCTET_STREAM, asset.getContentType());
		
		Path initScriptPath = new Path("/civilian").add(CivResourceLocation.ANGULAR_INIT_SCRIPT); 
		assertNull(location.getAsset(initScriptPath));
		
		location = new CivResourceLocation("/civilian", "/app", true);
		asset = location.getAsset(initScriptPath);
		assertNotNull(asset);
		try(Reader reader = asset.getReader())
		{
			String[] lines = IoUtil.readLines(reader, false);
			assertEquals(3, lines.length);
			assertEquals("civilian.appPath = new civilian.Path('/app');", lines[0]);
			assertEquals("civilian.basePath = civilian.Path.createBasePath();", lines[1]);
			assertEquals("civilian.develop = true;", lines[2]);
		}
	}
	
	
	@Test public void testDirectoryLocation() throws Exception
	{
		File file = File.createTempFile("test", ".css");
		write(file, "UTF-8", "body");
		try
		{
			File dir = file.getParentFile();
			AssetLocation location = AssetServices.getDirectoryLocation("files", dir);
			assertEquals(dir.getPath(), location.getInfoParam());
			
			Asset asset;
			
			// directories are not server
			asset = location.getAsset(new Path("files").add(dir.getName()));
			assertNull(asset);

			// find existing file
			asset = location.getAsset(new Path("files").add(file.getName()));
			assertNotNull(asset);
			assertEquals(file.getAbsolutePath(), asset.toString());
			assertEquals(ContentType.APPLICATION_OCTET_STREAM, asset.getContentType()); // content-type set in asset root
			assertEquals(4, asset.length());
			assertEquals(file.lastModified(), asset.lastModified());
			assertTrue(asset.isValid());
			assertNull(asset.getContent());
			
			asset.readContent();
			assertArrayEquals("body".getBytes(), asset.getContent());
			
			byte[] oldContent = asset.getContent();
			asset.readContent();
			assertArrayEquals(oldContent, asset.getContent());
			
			write(file, "UTF-8", "body{}");
			assertFalse(asset.isValid());
			
			// non existing file not found
			assertNull(location.getAsset(new Path("nonexistent")));
		}
		finally
		{
			assertTrue(file.getAbsolutePath(), file.delete());
		}
	}

	
	private static Application app_;
}
