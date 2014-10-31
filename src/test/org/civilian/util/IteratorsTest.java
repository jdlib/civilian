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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import org.junit.Test;
import org.civilian.CivTest;


public class IteratorsTest extends CivTest
{
	@Test public void testArray()
	{
		String[] array = { "a", "b", "c" };
		
		Iterator<String> it = Iterators.forValues(array);
		assertNext(it, true, "a");
		assertNext(it, true, "b");
		assertNext(it, true, "c");
		assertNext(it, false, null);
		failRemove(it);
	}
	
	
	@Test public void testEnumeration()
	{
		Vector<String> list = new Vector<>();
		list.add("a");
		list.add("b");
		list.add("c");
		
		Iterator<String> it = Iterators.asIterator(list.elements());
		failRemove(it);
		assertNext(it, true, "a");
		assertNext(it, true, "b");
		assertNext(it, true, "c");
		assertNext(it, false, null);
	}
	
	
	@Test public void testSingleton()
	{
		Iterator<String> it = Iterators.forValue("a");
		failRemove(it);
		assertNext(it, true, "a");
		assertNext(it, false, null);

		it = Iterators.forValue(null);
		failRemove(it);
		assertNext(it, false, null);
	}
	
	
	@Test public void testUnmodifiable()
	{
		List<String> list = new ArrayList<>();
		list.add("a");
		Iterator<String> it = Iterators.unmodifiable(list);
		failRemove(it);
		assertNext(it, true, "a");
	}
	
	
	@Test public void testAddAll()
	{
		Iterator<String> it = Iterators.forValue("a");
		List<String> list = Iterators.addAll(new ArrayList<String>(), it);
		assertEquals(1, list.size());
		assertEquals("a", list.get(0));
	}
	
	
	
	@Test public void testJoin()
	{
		Iterator<String> it1 = Iterators.forValue("1");
		Iterator<String> it2 = Iterators.forValues(new String[] { "2a", "2b" });
		Iterator<String> it  = Iterators.join(it1, it2);
		assertIterator(it, "1", "2a", "2b");
	}
	
	
	@Test public void testUnique()
	{
		Iterator<String> it1 = Iterators.forValue("1");
		Iterator<String> it2 = Iterators.forValue("1");
		Iterator<String> itJ = Iterators.join(it1, it2);
		Iterator<String> it  = Iterators.unique(itJ);
		assertIterator(it, "1");
	}

	
	private <T> void assertNext(Iterator<T> it, boolean hasNext, T value)
	{
		assertEquals(hasNext, it.hasNext());
		if (hasNext)
			assertEquals(value, it.next());
		else
		{
			try
			{
				it.next();
				fail();
			}
			catch(NoSuchElementException e)
			{
			}
		}
	}


	private <T> void failRemove(Iterator<T> it)
	{
		try
		{
			it.remove();
			fail();
		}
		catch(Exception e)
		{
		}
	}
}
