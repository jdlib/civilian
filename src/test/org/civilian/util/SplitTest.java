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


import org.civilian.CivTest;
import org.junit.Test;


public class SplitTest extends CivTest
{
	@Test public void test()
	{
		Split split = new Split(3, "\\s*=\\s*");
		
		assertEquals(3, split.apply("a=b=c"));
		assertEquals("a", split.s[0]);
		assertEquals("b", split.s[1]);
		assertEquals("c", split.s[2]);

		assertEquals(2, split.apply(" a = b "));
		assertEquals(" a", split.s[0]);
		assertEquals("b ", split.s[1]);
		assertEquals(null, split.s[2]);

		assertEquals(2, split.apply("a="));
		assertEquals("a",  split.s[0]);
		assertEquals("",   split.s[1]);
		assertEquals(null, split.s[2]);

		assertEquals(1, split.apply("a"));
		assertEquals("a",  split.s[0]);
		assertEquals(null, split.s[1]);
		assertEquals(null, split.s[2]);

		Split split2 = new Split(2, "=");
		split2.trim();
		assertEquals(2, split2.apply(" a = b "));
		assertEquals("a", split2.s[0]);
		assertEquals("b", split2.s[1]);
	}
}
