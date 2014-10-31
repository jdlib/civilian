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
package org.civilian.internal;


import org.junit.Test;
import org.civilian.CivTest;


public class ParamListTest extends CivTest
{
	@Test public void test()
	{
		ParamList list = new ParamList();
		list.add("a", "alpha");
		assertEquals("alpha", list.get("a"));
		assertArrayEquals2(list.getAll("a"), "alpha"); 
		assertEquals(null, list.get("b"));
		
		list.add("a", "beta");
		assertEquals("alpha", list.get("a"));
		assertArrayEquals2(list.getAll("a"), "alpha", "beta");

		list.set("a", "gamma");
		assertEquals("gamma", list.get("a"));
		assertArrayEquals2(list.getAll("a"), "gamma"); 
		
		list.setInt("b", 5);
		assertEquals(5, list.getInt("b"));
		assertEquals("5", list.get("b"));

		list.setDate("c", 10L);
		assertEquals(10L, list.getDate("c"));
		assertEquals("10", list.get("c"));

	
		list = new ParamList(true);
		list.add("a", "alpha");
		assertEquals("alpha", list.get("A"));
	}
}
