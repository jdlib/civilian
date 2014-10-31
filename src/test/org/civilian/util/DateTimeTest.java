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


public class DateTimeTest extends CivTest
{
	@Test public void testNow()
	{
		long m = System.currentTimeMillis();
		DateTime now1 = new DateTime(); 
		DateTime now2 = new DateTime(m);
		
		java.util.Date judate = new java.util.Date(m);
		DateTime now3 = new DateTime(judate);

		int year = now1.getYear();
		assertEquals(year, now2.getYear());
		assertEquals(year, now3.getYear());

		int month = now1.getMonth();
		assertEquals(month, now2.getMonth());
		assertEquals(month, now3.getMonth());
	}
	
	
	@Test public void testCreate()
	{
		DateTime dt = new DateTime(2014, 12, 31, 11, 12, 13);
		assertEquals(2014, dt.getYear());
		assertEquals(12, dt.getMonth());
		assertEquals(31, dt.getDay());
		assertEquals(11, dt.getHours());
		assertEquals(12, dt.getMinutes());
		assertEquals(13, dt.getSeconds());

		DateTime dt2 = new DateTime(2014, 12, 31, 11, 12, 14);
		assertTrue(dt.isBefore(dt2));
		assertFalse(dt.isAfter(dt2));
		
		DateTime dt3 = DateTime.fromLong(dt.toLong());
		assertEquals(dt, dt3);
		
		assertEquals("20141231 11:12:13", dt.toString());
		
	}
}
