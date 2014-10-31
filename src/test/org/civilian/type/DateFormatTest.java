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
package org.civilian.type;


import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.text.DateFormat;
import org.civilian.util.Date;


public class DateFormatTest extends CivTest
{
	@Test public void testSerialize() throws Throwable
	{
		// format, parse
		dateFormat_ = DateFormat.getInstance(Locale.GERMAN);
		assertFormat("1.1.2001", 2001, 1, 1, "01.01.2001");
		assertFormat("27.11.1766", 1766, 11, 27);
		
		dateFormat_ = DateFormat.getInstance(Locale.US);
		assertFormat("12/31/2001", 2001, 12, 31);

		dateFormat_ = DateFormat.getInstance(Locale.ENGLISH);
		assertFormat("12/31/2001", 2001, 12, 31);

		dateFormat_ = DateFormat.getInstance(Locale.FRENCH);
		assertFormat("31/12/2001", 2001, 12, 31);
		
		// names
		dateFormat_ = DateFormat.getInstance(Locale.GERMAN);
		assertEquals("Januar", dateFormat_.getMonthName(1));
		assertEquals("Feb", dateFormat_.getShortMonthName(2));
		
		assertEquals("Sonntag", dateFormat_.getWeekdayName(Date.WEEKDAY_SUNDAY));
		assertEquals("So", dateFormat_.getShortWeekdayName(Date.WEEKDAY_SUNDAY));
	}
	
	
	private void assertFormat(String text, int year, int month, int day) throws Throwable
	{
		assertFormat(text, year, month, day, text);
	}
	

	private void assertFormat(String text, int year, int month, int day, String textOut) throws Throwable
	{
		Date d = dateFormat_.parse(text, TypeLib.DATE_CIVILIAN);
		assertDate(d, year, month, day);
		assertEquals(textOut, dateFormat_.format(d));
	}
	
	
	private void assertDate(Date d, int year, int month, int day)
	{
		assertEquals("day",		day,	d.getDay());
		assertEquals("month",	month,	d.getMonth());
		assertEquals("year",	year,	d.getYear());
	}
	
	
	private DateFormat dateFormat_;
}
