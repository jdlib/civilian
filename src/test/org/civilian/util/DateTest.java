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


import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.text.DateFormat;
import org.junit.Test;


/**
 * Test jd.util.Date and jd.util.DateFormat
 */
public class DateTest extends CivTest
{
	@Test public void testInvalidDates() throws Throwable
	{
		// invalid dates
		assertInvalidDate(0,  1, 2001);
		assertInvalidDate(32, 1, 2001);
		assertInvalidDate(30, 2, 2000);
		assertInvalidDate(-1, 1, 2001);
		assertInvalidDate(1,  0, 2001);
		assertInvalidDate(1, 13, 2001);
		assertInvalidDate(1, -1, 2001);
	}
	
	
	@Test public void testCreate() throws Throwable
	{
		Date date = new Date(2001, 2, 1);
		assertDate(date, 2001, 2, 1);
		assertEquals(28, date.getLastDayOfMonth());
		assertEquals(31, Date.getLastDayOfMonth(1, 2001));
		assertEquals(29, Date.getLastDayOfMonth(2, 2000));
	}
	
		
	@Test public void testLeapYear()
	{
		assertTrue(Date.isLeapYear(2000));
		assertTrue(Date.isLeapYear(1996));
		assertTrue(Date.isLeapYear(2004));
		assertTrue(!Date.isLeapYear(2001));
		assertTrue(!Date.isLeapYear(1900));
	}

	
	@Test public void testArithmetic()
	{
		// before, after, arithmetic
		Date d1 = new Date(2001, 1, 1);
		Date d2 = new Date(2001, 2, 1);
		assertTrue(d1.isBefore(d2));
		assertTrue(d2.isAfter(d1));
		
		Date d = new Date(2001, 1, 31);
		d = d.addDays(1);
		assertDate(d, 2001, 2, 1);
		d = d.addMonths(1);
		assertDate(d, 2001, 3, 1);
		d = d.addDays(-1);
		assertDate(d, 2001, 2, 28);
		d = d.addYears(-1);
		assertDate(d, 2000, 2, 28);
		d = d.addDays(1);
		assertDate(d, 2000, 2, 29);
		d = d.addYears(1);
		assertDate(d, 2001, 2, 28);
		
		// test addDays with leap-year situations
	    Date date1 	= Date.fromInteger(20040101);
	    Date date2a	= date1.addDays(366);
	    Date date2b = date1.addDays(350).addDays(16);
	    assertEquals(date2a, date2b);
		
		// difference
		d = new Date();
		for (int i=0; i<10; i++)
		{
			int diff = i*10;
			d2 = d.addDays(diff);
			assertEquals(diff, d2.difference(d));
		}
	}
	
	
	@Test public void testJulianDayNumber() throws Throwable
	{
		// julian day number
		assertJdNumber(-4713, 11, 24, 0);
		assertJdNumber(-4712,  1,  1, 38);
		assertJdNumber(    0,  1,  1, 1721060);
		assertJdNumber( 1970,  1,  1, 2440588);
		assertJdNumber( 1999, 12, 31, 2451544);
	}
	

	@Test public void testDayOfWeek() throws Throwable
	{
		// day of week
		assertDow(2001, 06, 11, Date.WEEKDAY_MONDAY);
		assertDow(2001, 06, 12, Date.WEEKDAY_TUESDAY);
		assertDow(2001, 06, 13, Date.WEEKDAY_WEDNESDAY);
		assertDow(2001, 06, 14, Date.WEEKDAY_THURSDAY);
		assertDow(2001, 06, 15, Date.WEEKDAY_FRIDAY);
		assertDow(2001, 06, 16, Date.WEEKDAY_SATURDAY);
		assertDow(2001, 06, 17, Date.WEEKDAY_SUNDAY);
		assertDow(2001, 06, 18, Date.WEEKDAY_MONDAY);
		assertDow(1966, 11, 27, Date.WEEKDAY_SUNDAY);

		dateFormat_ = new DateFormat(Locale.GERMAN);
		Date d = new Date(1966, 11, 27);
		assertEquals("Sonntag", dateFormat_.getWeekdayName(d.getDayOfWeek()));
		d = d.addDays(-1);
		assertEquals("Samstag", dateFormat_.getWeekdayName(d.getDayOfWeek()));
	}
	
	
	private void assertInvalidDate(int year, int month, int day)
	{
		try
		{
			new Date(day, month, year);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
		if (Date.isValidDate(year, month, day))
			fail();
	}
	
	
	private void assertDate(Date d, int year, int month, int day)
	{
		assertEquals("day",		day,	d.getDay());
		assertEquals("month",	month,	d.getMonth());
		assertEquals("year",	year,	d.getYear());
	}
	
	
	private void assertJdNumber(int year, int month, int day, int number) throws Throwable
	{
		Date d = new Date(year, month, day);
		assertEquals(number, d.toJulianDayNumber());
	}

	
	private void assertDow(int year, int month, int day, int dayOfWeek) throws Throwable
	{
		Date d = new Date(year, month, day);
		assertEquals(dayOfWeek, d.getDayOfWeek());
	}

	
	private DateFormat dateFormat_;
}
