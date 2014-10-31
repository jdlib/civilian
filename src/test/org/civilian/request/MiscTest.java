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
package org.civilian.request;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import javax.servlet.http.Cookie;
import org.civilian.CivTest;
import org.civilian.context.test.TestLocalInfo;
import org.civilian.context.test.TestRemoteInfo;
import org.junit.Test;


public class MiscTest extends CivTest 
{
	@Test public void testLocalInfo()
	{
		TestLocalInfo info = new TestLocalInfo();  
		assertEquals("localhost:80", info.toString());
		
		info.setHost(null);
		info.setIp("1.2.3.4");
		info.setPort(10);
		assertEquals("1.2.3.4:10", info.toString());
		
		info.setHost("test.com");
		info.setPort(-1);
		assertEquals("test.com", info.toString());
	}


	@Test public void testRemoteInfo()
	{
		TestRemoteInfo info = new TestRemoteInfo();  
		assertEquals("localhost:80", info.toString());
		
		info.setHost(null);
		info.setIp("1.2.3.4");
		info.setPort(10);
		assertEquals("1.2.3.4:10", info.toString());
		
		info.setHost("test.com");
		info.setPort(0);
		assertEquals("test.com", info.toString());
		
		info.user_ = "user";
		assertEquals("user@test.com", info.toString());
	}


	@Test public void testCookieList()
	{
		CookieList list = new CookieList();
		assertEquals(0, list.size());
		assertNull(list.get("a"));
		assertNull(list.get(null));
		
		list = new CookieList((Cookie[])null);
		assertEquals(0, list.size());

		list = new CookieList(new Cookie[0]);
		assertEquals(0, list.size());
		
		list = new CookieList((Cookie)null);
		assertEquals(1, list.size());
		assertNull(list.get("a"));

		Cookie cookie = mock(Cookie.class);
		when(cookie.getName()).thenReturn("a");
		list = new CookieList(cookie);
		assertSame(cookie, list.get("a"));
	}
}
