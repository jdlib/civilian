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
package org.civilian.text;


import java.text.ParseException;
import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.type.TypeLib;
import org.civilian.util.Date;


public class DateFormatTest extends CivTest
{
	@Test public void testGet()
	{
		DateFormat format = new DateFormat(Locale.getDefault());
		assertSame(Locale.getDefault(), format.getLocale());
	}
	
	
	@Test public void testSymbols()
	{
		DateFormat german = new DateFormat(Locale.GERMAN);
		assertEquals('.', 		german.getSeparatorSymbol());
		assertEquals(0, 		german.getDayPosition());
		assertEquals(1, 		german.getMonthPosition());
		assertEquals(2, 		german.getYearPosition());
		assertEquals("Januar", 	german.getMonthName(1));
		assertEquals("Dez", 	german.getShortMonthName(12));
		assertEquals("Freitag", german.getWeekdayName(Date.WEEKDAY_FRIDAY));
		assertEquals("Sa", 		german.getShortWeekdayName(Date.WEEKDAY_SATURDAY));
		
		try
		{
			german.getMonthName(0);
			fail();
		}
		catch (IllegalArgumentException e)
		{
		}
	}
	
	
	@Test public void testParse() throws Exception
	{
		DateFormat uk = new DateFormat(Locale.UK);
		Date date;
		
		date = uk.parse("31/10/2014", TypeLib.DATE_CIVILIAN);
		assertDate(2014, 10, 31, date);

		date = uk.parse("31/10/14", TypeLib.DATE_CIVILIAN);
		assertDate(2014, 10, 31, date);
		
		date = uk.parse("31/10/  14", TypeLib.DATE_CIVILIAN);
		assertDate(2014, 10, 31, date);
		
		
		assertParseError(uk, "31/xx/2014");
		assertParseError(uk, "31/14/2014");
	}
	
	
	@Test public void testFormat() throws Exception
	{
		DateFormat german = new DateFormat(Locale.GERMAN);
		Date date = new Date(2014, 12, 31);
		assertEquals("31.12.2014", german.format(date));
		
		StringBuilder s = new StringBuilder();
		german.format(date, s);
		assertEquals("31.12.2014", s.toString());
		
		s.setLength(0);
		german.format(date, s, DateFormat.SYMBOL_DAY);
		assertEquals("12.2014", s.toString());
	}

	
	private void assertDate(int year, int month, int day, Date date)
	{
		assertEquals(year, 	date.getYear());
		assertEquals(month, date.getMonth());
		assertEquals(day, 	date.getDay());
	}
	
	
	private void assertParseError(DateFormat format, String s)
	{
		try
		{
			format.parse(s, TypeLib.DATE_CIVILIAN);
			fail();
		}
		catch(ParseException e)
		{
		}
	}
}
