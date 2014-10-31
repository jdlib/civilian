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


import java.util.GregorianCalendar;
import org.civilian.CivTest;
import org.junit.Test;


public class TimeTest extends CivTest
{
	@Test public void testCreate()
	{
		Time t1 = Time.now();
		Time t2 = new Time();
		Time t3 = new Time(System.currentTimeMillis());
		assertTrue(Math.abs(t1.toInteger() - t2.toInteger()) <= 1);
		assertTrue(Math.abs(t2.toInteger() - t3.toInteger()) <= 1);
		
		@SuppressWarnings("deprecation")
		Time tsql = new Time(new java.sql.Time(t1.getHours(), t1.getMinutes(), t1.getSeconds()));
		assertEquals(t1, tsql);
		
		GregorianCalendar gc = new GregorianCalendar(2000, 1, 1, t1.getHours(), t1.getMinutes(), t1.getSeconds());
		assertEquals(t1, new Time(gc));
		
		assertEquals(t1, new Time(t1));
		
		assertEquals(t1, t2.clone());
		
		assertEquals(t1, new Time(t1.getHours(), t1.getMinutes(), t1.getSeconds()));
		assertEquals(t1, new Time(t1.getHours(), t1.getMinutes(), t1.getSeconds()));
		
		new Time(0, 0, 0);
		new Time(23, 59, 59);
		failCreate(-1, 0, 0);
		failCreate(24, 0, 0);
		failCreate(0, -1, 0);
		failCreate(0, 60, 0);
		failCreate(0, 0, -1);
		failCreate(0, 0, 60);
	}
	
	
	private void failCreate(int hours, int minutes, int seconds)
	{
		try
		{
			assertFalse(Time.isValidTime(hours, minutes, seconds));
			new Time(hours, minutes, seconds);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}


	@Test public void testEquals()
	{
		Time t = new Time();
		assertEquals(t, t);
		assertNotEquals(t, null);
		assertNotEquals(t, "a");
	}
	

	@Test public void compare()
	{
		Time[] times = { new Time(0, 0, 0), new Time(0, 0, 1), new Time(0, 1, 0), new Time(1, 0, 0) };
		
		for (int i=0; i<times.length - 1; i++)
		{
			Time t1 = times[i];
			assertTrue(!t1.isBefore(t1));
			assertTrue(!t1.isAfter(t1));
			assertTrue(t1.equals(t1));
			assertEquals(0, t1.compareTo(t1));

			for (int j=i+1; j<times.length; j++)
			{
				Time t2 = times[j];
				assertTrue(t1.isBefore(t2));
				assertTrue(t2.isAfter(t1));
				assertTrue(t1.compareTo(t2) < 0);
				assertFalse(t1.equals(t2));
			}
		}
	}


	@Test public void conversion()
	{
		Time t = new Time(10, 5, 30);
		assertEquals("10:05:30", t.toString());
		assertEquals(100530, t.toInteger());
		assertEquals(t, Time.fromInteger(100530));
		assertEquals(36330, t.toSeconds());
	}
}
