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
package org.civilian.util;


import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import javax.servlet.ServletContext;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.civilian.CivTest;


public class ResourceLoaderTest extends CivTest
{
	@Test public void testClass() throws Exception
	{
		ResourceLoader loader = ResourceLoaders.forClass(getClass());
		URL url = loader.getResourceUrl(getClass().getSimpleName() + ".class");
		assertTrue(url.toString().endsWith("org/civilian/util/ResourceLoaderTest.class"));
	}


	@Test public void testEmpty() throws Exception
	{
		ResourceLoader loader = ResourceLoaders.empty();
		assertNull(loader.getResourceUrl(null));
		assertNull(loader.getResourceAsStream(null));
		assertNull(loader.getResourceAsReader(null, null));
	}


	@Test public void testChained() throws Exception
	{
		ResourceLoader empty  = ResourceLoaders.empty();
		ResourceLoader loader = ResourceLoaders.chain(empty, empty);
		assertNull(loader.getResourceUrl(null));
		assertNull(loader.getResourceAsStream(null));
		assertNull(loader.getResourceAsReader(null, null));
	}


	@Test public void testFileLoaders() throws Exception
	{
		File temp = File.createTempFile("loader", ".txt");
		
		ResourceLoader dirLoader = ResourceLoaders.forDirectory(temp.getParentFile()); 
		assertEquals(temp.toURI().toURL(), dirLoader.getResourceUrl(temp.getName()));
		assertNull(dirLoader.getResourceUrl("invalidname"));
		assertNull(dirLoader.getResourceAsStream("invalidname"));

		ResourceLoader dirLoader2 = ResourceLoaders.forDirectory(temp); 
		assertEquals(temp.toURI().toURL(), dirLoader2.getResourceUrl(temp.getName()));
	}


	@Test public void testServletContextLoader() throws Exception
	{
		ServletContext context = mock(ServletContext.class); 
		ResourceLoader loader = ResourceLoaders.forSerlvetContext(context);
		loader.getResourceAsStream("x");
		verify(context).getResourceAsStream("x");
		
		loader.getResourceUrl("x");
		verify(context).getResource("x");
		
		when(context.getResource("y")).thenThrow(new MalformedURLException("y"));
		try
		{
			loader.getResourceUrl("y");
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("cannot create resource URL for 'y'", e.getMessage());
		}
		
		HashSet<String> paths = new HashSet<>();
		paths.add("z");
		when(context.getResourcePaths("z")).thenReturn(paths);
		when(context.getResource("z")).thenReturn(new URL("http://z.com"));
	}


	@Test public void testClassLoaderLoader() throws Exception
	{
		ResourceLoader loader = ResourceLoaders.forSystemClassLoader();
		URL url = loader.getResourceUrl("java/lang/String.class");
		assertTrue(url.toString().endsWith("/java/lang/String.class"));

		try (InputStream in = loader.getResourceAsStream("java/lang/String.class"))
		{
			assertNotNull(in);
		}
	}
}
