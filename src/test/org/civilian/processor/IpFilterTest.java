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
package org.civilian.processor;


import org.junit.Test;
import org.civilian.CivTest;


public class IpFilterTest extends CivTest
{
	@Test public void testLiteral() throws Exception
	{
		IpFilter filter = new IpFilter("100.99.98.97", "100.99.98.96");
		assertTrue(filter.isAllowedIp("100.99.98.97"));
		assertTrue(filter.isAllowedIp("100.99.98.96"));
		assertFalse(filter.isAllowedIp("100.99.98.95"));
		
		assertEquals("Allowed IPs: 100.99.98.97, 100.99.98.96", filter.getInfo());
	}


	@Test public void testLocalhost() throws Exception
	{
		IpFilter filter = new IpFilter("localhost");
		assertTrue(filter.isAllowedIp("127.0.0.1"));
		assertTrue(filter.isAllowedIp("0:0:0:0:0:0:0:1"));
		assertFalse(filter.isAllowedIp("100.99.98.95"));

		assertEquals("Allowed IPs: 127.0.0.1, 0:0:0:0:0:0:0:1", filter.getInfo());
	}


	@Test public void testAll() throws Exception
	{
		IpFilter filter = new IpFilter("100.99.98.97", "all");
		assertTrue(filter.isAllowedIp("100.99.98.97"));
		assertTrue(filter.isAllowedIp("100.99.98.95"));

		assertEquals("Allowed IPs: all", filter.getInfo());
	}


	@Test public void testWildcard() throws Exception
	{
		IpFilter filter = new IpFilter("100.99.98.*");
		assertTrue(filter.isAllowedIp("100.99.98.97"));
		assertTrue(filter.isAllowedIp("100.99.98.95"));
		assertTrue(filter.isAllowedIp("100.99.98.0"));
		assertTrue(filter.isAllowedIp("100.99.98.999"));
		assertFalse(filter.isAllowedIp("100.99.99.0"));
		assertEquals("Allowed IPs: 100.99.98.*", filter.getInfo());

		filter = new IpFilter("100.*.*.*");
		assertTrue(filter.isAllowedIp("100.0.0.0"));
		assertTrue(filter.isAllowedIp("100.1.1.1"));
		assertFalse(filter.isAllowedIp("101.1.1.1"));

		assertEquals("Allowed IPs: 100.*.*.*", filter.getInfo());
	}
}
