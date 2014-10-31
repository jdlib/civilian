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
package org.civilian.text.msg.resbundle;


import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleFactories;
import org.civilian.text.msg.MsgBundleFactory;


public class ResMsgBundleTest extends CivTest
{
	@Test public void test() throws Exception
	{
		String def = "resbundle:org.civilian.text.msg.resbundle.msg";
		MsgBundleFactory factory = MsgBundleFactories.createFactory(def);
		assertEquals("resbundle:org.civilian.text.msg.resbundle.msg", factory.toString());
		
		MsgBundle en = factory.getMsgBundle(Locale.ENGLISH);	// exists
		MsgBundle de = factory.getMsgBundle(Locale.GERMAN);		// exists
		
		assertEquals(Locale.ENGLISH, en.getLocale()); 
		assertEquals("Hello", en.msg("hello"));
		assertEquals("World", en.msg("world"));
		assertEquals("?some", en.msg("some"));
		assertEquals("1 plus 1 equals 2", en.msg("pluseq", "1", "2"));
		assertTrue(en.contains("world"));
		assertFalse(en.contains("some"));
		assertNotNull(en.unwrap(ResourceBundle.class));

		assertEquals(Locale.GERMAN, de.getLocale()); 
		assertEquals("Hallo", de.msg("hello"));
		assertEquals("Welt",  de.msg("world"));
		assertEquals("?some", de.msg("some"));
		assertEquals("1 plus 1 ist 2", de.msg("pluseq", "1", "2"));
	}
}
