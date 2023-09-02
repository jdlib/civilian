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
package org.civilian.util.classpath;


import java.util.Set;
import org.civilian.CivTest;
import org.junit.Test;


public class ScanTest extends CivTest
{
	@Test public void testFileScan() throws Exception
	{
		ClassPathScan scan = new ClassPathScan("org.civilian.server.servlet");
		Set<String> adapters = scan.collect(className -> className.endsWith("Adapter"));
		
		assertEquals(9, adapters.size());
		assertTrue(adapters.contains("org.civilian.server.servlet.ServletResponseAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.ServletRequestAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.MpRequestAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.SpRequestAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.SessionAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.AsyncContextAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.AsyncEventListenerAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.AsyncInputAdapter"));
		assertTrue(adapters.contains("org.civilian.server.servlet.AsyncOutputAdapter"));
	}


	@Test public void testJarScan() throws Exception
	{
		ClassPathScan scan = new ClassPathScan("javax.servlet");
		Set<String> adapters = scan.collect(className -> className.endsWith(".HttpSession"));
		
		assertEquals(1, adapters.size());
		assertTrue(adapters.contains("javax.servlet.http.HttpSession"));
	}
}
