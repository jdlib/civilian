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
package org.civilian.text.msg;


import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;


public class MsgBundleTest extends CivTest
{
	@Test public void testEmptyBundle()
	{
		MsgBundle b = MsgBundle.empty(Locale.US);
		assertSame(Locale.US, b.getLocale());
		assertFalse(b.contains("x"));
		assertNull(b.unwrap(null));
		assertEquals("?x", b.msg("x"));
	}
	

	@Test public void testMsgId()
	{
		String x = "x";
		
		MsgId id = new MsgId(x);
		assertEquals(1, id.length());
		assertEquals('x', id.charAt(0));
		assertEquals("x", id.subSequence(0, 1));
		assertSame(x, id.toString());
	}
	
	
	@Test public void testFactories() throws Exception
	{
		MsgBundleFactory f = MsgBundleFactories.createFactory(MsgBundleFactories.EMPTY);
		assertTrue(f.getMsgBundle(Locale.CHINA) instanceof EmptyMsgBundle);

		f = MsgBundleFactories.createFactory(MyMsgBundleFactory.class.getName());
		assertTrue(f instanceof MyMsgBundleFactory);
	}
}
