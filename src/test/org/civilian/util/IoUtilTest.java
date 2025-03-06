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


import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;


public class IoUtilTest extends CivTest
{
	@Test public void testClosed() throws Exception
	{
		IoUtil.close(null);
		
		IOException e = new IOException();
		Closeable c = mock(Closeable.class);
		doThrow(e).when(c).close();
		assertSame(e, IoUtil.close(c));
	}


	@Test public void testCopy() throws Exception
	{
		File file1 = createTempFile("txt", "UTF-8", "abc");
		File file2 = File.createTempFile("test", "txt");
		try
		{
			IoUtil.copyFile(file1, file2);
			assertEquals("abc", read(file2));
		}
		finally
		{
			file1.delete();
			file2.delete();
		}
	}
	
	
	@Test public void testReadLines() throws Exception
	{
		String[] s = IoUtil.readLines(new StringReader("abc \n xyz "));
		assertArrayEquals2(new String[] { "abc ", " xyz " }, s); 
	}


	@Test public void testExtension() throws Exception
	{
		File f = new File("test.css");
		assertEquals("test", IoUtil.cutExtension(f));
		
		assertNull(null, IoUtil.getExtension("test"));
		
		assertNull(IoUtil.normExtension(""));
		assertNull(IoUtil.normExtension(null));
		assertEquals("css", IoUtil.normExtension(".css"));
		assertEquals("css", IoUtil.normExtension("css"));
	}
}
