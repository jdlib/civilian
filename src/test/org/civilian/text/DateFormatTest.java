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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.type.TypeLib;


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
		assertEquals("Freitag", german.getWeekdayName(DayOfWeek.FRIDAY));
		assertEquals("Sa", 		german.getShortWeekdayName(DayOfWeek.SATURDAY));
		
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
		LocalDate date;
		
		date = uk.parse(TypeLib.DATE_LOCAL, "31/10/2014");
		assertDate(2014, 10, 31, date);

		date = uk.parse(TypeLib.DATE_LOCAL, "31/10/14");
		assertDate(2014, 10, 31, date);
		
		date = uk.parse(TypeLib.DATE_LOCAL, "31/10/  14");
		assertDate(2014, 10, 31, date);
		
		
		assertParseError(uk, "31/xx/2014");
		assertParseError(uk, "31/14/2014");
	}
	
	
	@Test public void testFormat() throws Exception
	{
		DateFormat german = new DateFormat(Locale.GERMAN);
		assertEquals("31.12.2014", german.format(2014, 12, 31));
		
		StringBuilder s = new StringBuilder();
		german.format(2014, 12, 31, s);
		assertEquals("31.12.2014", s.toString());
		
		s.setLength(0);
		german.format(2014, 12, 31, s, DateFormat.SYMBOL_DAY);
		assertEquals("12.2014", s.toString());
	}

	
	private void assertDate(int year, int month, int day, LocalDate date)
	{
		assertEquals(year, 	date.getYear());
		assertEquals(month, date.getMonthValue());
		assertEquals(day, 	date.getDayOfMonth());
	}
	
	
	private void assertParseError(DateFormat format, String s)
	{
		try
		{
			format.parse(TypeLib.DATE_LOCAL, s);
			fail();
		}
		catch(ParseException e)
		{
		}
	}
}
