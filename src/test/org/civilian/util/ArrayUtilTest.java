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


import java.util.Vector;
import org.junit.Test;
import org.civilian.CivTest;


public class ArrayUtilTest extends CivTest
{
	@Test public void testAdd()
	{
		String[] s0   = { };
		String[] s1a  = { "a" };
		String[] s1b  = { "b" };
		String[] s2ab = { "a", "b" };
		String[] s2aN = { "a", null };
		
		assertArrayEquals(s1a, 	ArrayUtil.addLast(s0, "a"));
		assertArrayEquals(s2ab,	ArrayUtil.addLast(s1a, "b"));
		assertArrayEquals(s2aN,	ArrayUtil.addLast(s1a, null));
		
		assertArrayEquals(s2ab, ArrayUtil.addFirst(s1b, "a"));
	}


	@Test public void testRemoveFirst()
	{
		String[] s0 = { };
		String[] s1 = { "b" };
		String[] s2 = { "a", "b" };
		
		assertSame(s2, ArrayUtil.removeFirst(s2, 0));
		assertArrayEquals(s1, ArrayUtil.removeFirst(s2, 1));
		assertArrayEquals(s0, ArrayUtil.removeFirst(s2, 2));
		
		try
		{
			ArrayUtil.removeFirst(s1, 2);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}


	@Test public void testConcat()
	{
		String[] array  = { "a" };
		
		assertSame(array, ArrayUtil.concat(array, null));
		assertSame(array, ArrayUtil.concat(null, array));
		assertArrayEquals(new String[] { "a", "b", "c" }, ArrayUtil.concat(array, new String[] { "b", "c" }));
	}
	
	
	@Test public void testContains()
	{
		assertFalse(ArrayUtil.contains(null, "x"));

		String[] array  = { "a", null };
		assertTrue(ArrayUtil.contains(array, "a"));
		assertTrue(ArrayUtil.contains(array, null));
		assertFalse(ArrayUtil.contains(array, "b"));
	}


	@Test public void testToArray()
	{
		Vector<String> list = new Vector<>();
		list.add("a");
		
		String[] result = ArrayUtil.toArray(list.elements(), String.class);
		assertArrayEquals(new String[] { "a" }, result);
	}
}
