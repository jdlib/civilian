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
package org.civilian.type.fn;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.text.NumberStyle;
import org.civilian.text.TimeStyle;
import org.civilian.type.DiscreteType;
import org.civilian.type.EnumType;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;


public class SerializerTest extends CivTest
{
	@SuppressWarnings("deprecation")
	@Test public void testFormat() throws Exception
	{
		// mysteriously the french locale uses \u00a0 = 160 as grouping separator
		assertFormat(TypeLib.BIGDECIMAL, 	new BigDecimal(10.5), 					"10.5", "10.50", "10,50", "10,50");
		assertFormat(TypeLib.BIGINTEGER, 	new BigInteger("1234"), 				"1234", "1,234", "1.234", "1\u00a0234");
		assertFormat(TypeLib.BIGINTEGER, 	new BigInteger("12345678901234567890"), "12345678901234567890", "12,345,678,901,234,567,890", "12.345.678.901.234.567.890", "12\u00a0345\u00a0678\u00a0901\u00a0234\u00a0567\u00a0890");
		assertFormat(TypeLib.BOOLEAN, 		Boolean.TRUE, 							"true");
		assertFormat(TypeLib.BYTE, 	 		new Byte(Byte.MIN_VALUE),				"-128");
		assertFormat(TypeLib.CHARACTER, 	new Character('a'),						"a");
		assertFormat(TypeLib.DATE_CALENDAR, new GregorianCalendar(2012, 0, 31),		"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DATE_CIVILIAN, new org.civilian.util.Date(2012, 1, 31),"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DATE_LOCAL,    LocalDate.of(2012, 1, 31),              "20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DATE_JAVA_SQL, new java.sql.Date(112, 0, 31), 			"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DATE_JAVA_UTIL,new java.util.Date(112, 0, 31), 		"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DATETIME_LOCAL,LocalDateTime.of(2012,1,31,12,13,14), 	"20120131121314", "01/31/2012 12:13", "31.01.2012 12:13", "31/01/2012 12:13");
		assertFormat(TypeLib.DOUBLE, 		new Double(2345.6),						"2345.6", "2,345.60","2.345,60","2\u00a0345,60");
		assertFormat(TypeLib.DOUBLE, 		new Double(2345),						"2345.0", "2,345.00","2.345,00","2\u00a0345,00");
		assertFormat(TypeLib.FLOAT, 		new Float(2345.6),						"2345.6", "2,345.60","2.345,60","2\u00a0345,60");
		assertFormat(TypeLib.INTEGER, 		new Integer(2345),						"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertFormat(TypeLib.LONG, 			new Long(2345),							"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertFormat(TypeLib.LONG, 			new Long(-5432),						"-5432",  "-5,432",  "-5.432", "-5\u00a0432");
		assertFormat(TypeLib.SHORT, 		new Short((short)2345),					"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertFormat(TypeLib.TIME_LOCAL,	LocalTime.of(12, 13, 14),				"121314", "12:13",   "12:13",   "12:13");
		assertFormat(TypeLib.STRING,		"abc",									"abc");
	}
	
	
	private <T> void assertFormat(Type<T> type, T value, String string) throws Exception
	{
		assertFormat(type, value, string, string, string, string);
	}
	
	
	private <T> void assertFormat(Type<T> type, T value, String defaultString, String usString, String deString, String frString) throws Exception
	{
		assertEquals("format default null",		"",				STANDARD.format(type, null));
		assertEquals("format default value",	defaultString,	STANDARD.format(type, value));
		assertEquals("format locale-us null",	"",				LOCALE_US.format(type, null));
		assertEquals("format locale-us value",	usString,		LOCALE_US.format(type, value));
		assertEquals("format locale-de value",	deString,		LOCALE_DE.format(type, value));
		assertEquals("format locale-fr value",	frString,		LOCALE_FR.format(type, value));
	}
	
	
	@SuppressWarnings("deprecation")
	@Test public void testParse() throws Exception
	{
		// mysteriously the french locale uses \u00a0 = 160 as grouping separator
		assertParse(TypeLib.BIGDECIMAL, 	new BigDecimal(10.5), 					"10.5", "10.5", "10,5", "10,5");
		assertParse(TypeLib.BIGINTEGER, 	new BigInteger("1234"), 				"1234", "1,234", "1.234", "1\u00a0234");
		assertParse(TypeLib.BIGINTEGER, 	new BigInteger("12345678901234567890"), "12345678901234567890", "12,345,678,901,234,567,890", "12.345.678.901.234.567.890", "12\u00a0345\u00a0678\u00a0901\u00a0234\u00a0567\u00a0890");
		assertParse(TypeLib.BOOLEAN, 		Boolean.TRUE, 							"true");
		assertParse(TypeLib.BYTE, 	 		new Byte(Byte.MIN_VALUE),				"-128");
		assertParse(TypeLib.CHARACTER, 		new Character('a'),						"a");
		assertParse(TypeLib.DATE_CALENDAR, 	new GregorianCalendar(2012, 0, 31),		"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertParse(TypeLib.DATE_CIVILIAN, 	new org.civilian.util.Date(2012, 1, 31),"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertParse(TypeLib.DATE_LOCAL, 	LocalDate.of(2012, 1, 31),				"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertParse(TypeLib.DATE_JAVA_SQL, 	new java.sql.Date(112, 0, 31), 			"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertParse(TypeLib.DATE_JAVA_UTIL, new java.util.Date(112, 0, 31), 		"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertParse(TypeLib.DOUBLE, 		new Double(2345.6),						"2345.6", "2,345.6", "2.345,6", "2\u00a0345,6");
		assertParse(TypeLib.DOUBLE, 		new Double(2345),						"2345.0", "2,345",   "2.345",   "2\u00a0345");
		assertParse(TypeLib.FLOAT, 			new Float(2345.6),						"2345.6", "2,345.6", "2.345,6", "2\u00a0345,6");
		assertParse(TypeLib.FLOAT, 			new Float(2345),						"2345.0", "2,345",   "2.345",   "2\u00a0345");
		assertParse(TypeLib.INTEGER, 		new Integer(2345),						"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertParse(TypeLib.LONG, 			new Long(2345),							"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertParse(TypeLib.LONG, 			new Long(-5432),						"-5432",  "-5,432",   "-5.432", "-5\u00a0432");
		assertParse(TypeLib.SHORT, 			new Short((short)2345),					"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertParse(TypeLib.STRING,			"abc",									"abc");
	}

	
	private <T> void assertParse(Type<T> type, T value, String string) throws Exception
	{
		assertParse(type, value, string, string, string, string);
	}
	
	
	private <T> void assertParse(Type<T> type, T value, String defaultString, String usString, String deString, String frString) throws Exception
	{
		assertEquals("parse default empty",	type == TypeLib.STRING ? "" : null,	STANDARD.parse(type, ""));
		assertNull  ("parse default null",			STANDARD.parse(type, null));
		assertEquals("parse default",		value,	STANDARD.parse(type, defaultString));
		assertNull  ("parse locale null",			LOCALE_US.parse(type, null));
		assertEquals("parse locale us",		value,	LOCALE_US.parse(type, usString));
		assertEquals("parse locale de",		value,	LOCALE_DE.parse(type, deString));
		assertEquals("parse locale fr",		value,	LOCALE_FR.parse(type, frString));

		if (type == TypeLib.STRING)
			assertEquals("parse locale empty ", "",	LOCALE_US.parse(type, ""));
		else
			assertEquals("parse locale empty ", null, LOCALE_US.parse(type, ""));
	}
	
	
	@Test public void testParseDefaultDate() throws Exception
	{
		try
		{
			STANDARD.parse(TypeLib.DATE_CIVILIAN, "123");
			fail();
		}
		catch(ParseException e)
		{
			assertEquals("123", e.getMessage());
		}

		try
		{
			STANDARD.parse(TypeLib.DATE_CIVILIAN, "20131401");
			fail();
		}
		catch(ParseException e)
		{
			assertEquals("20131401", e.getMessage());
		}
	}
	
	
	/**
	 * Test specific locale serializer methods, not tested in all branches, by testFormatParse()
	 * @throws Exception
	 */
	@Test public void testLocaleSerializer() throws Exception
	{
		assertNotNull(LOCALE_US.getDateFormat());
		assertNotNull(LOCALE_US.getNumberFormat());

		String big = "1234567890123456789012345678901234567890";
		assertEquals(new BigDecimal(big), LOCALE_US.parse(TypeLib.BIGDECIMAL, big));
		assertEquals(new BigInteger(big), LOCALE_US.parse(TypeLib.BIGINTEGER, big));
		
		assertEquals(123L, LOCALE_US.parse(TypeLib.LONG, "+123").longValue());
		assertEquals(123456L, LOCALE_FR.parse(TypeLib.LONG, "+123 456").longValue());
	}
	
	
	@Test public void testStyles() throws Exception
	{
		LocalTime time = LocalTime.of(12, 13, 14);
		assertEquals("12:13", LOCALE_US.format(TypeLib.TIME_LOCAL, time));
		assertEquals("12:13", LOCALE_US.format(TypeLib.TIME_LOCAL, time, TimeStyle.HM));
		assertEquals("12:13:14", LOCALE_US.format(TypeLib.TIME_LOCAL, time, TimeStyle.HMS));
		
		LocalDateTime dateTime = LocalDateTime.of(2012, 1, 31, 12, 13, 14);
		assertEquals("31.01.2012 12:13", LOCALE_DE.format(TypeLib.DATETIME_LOCAL, dateTime));
		assertEquals("31.01.2012 12:13", LOCALE_DE.format(TypeLib.DATETIME_LOCAL, dateTime, TimeStyle.HM));
		assertEquals("31.01.2012 12:13:14", LOCALE_DE.format(TypeLib.DATETIME_LOCAL, dateTime, TimeStyle.HMS));
		
		Double d = Double.valueOf(1234.56);
		assertEquals("1.234,56", LOCALE_DE.format(TypeLib.DOUBLE, d));
		assertEquals("1234,56", LOCALE_DE.format(TypeLib.DOUBLE, d, NumberStyle.RAW));
		assertEquals("1234,5", LOCALE_DE.format(TypeLib.DOUBLE, d, NumberStyle.RAW.decimals(1)));
	}
	
	
	@Test public void testDiscreteType() throws Exception
	{
		Integer one = Integer.valueOf(1);
		Integer thousand = Integer.valueOf(1000);
		
		DiscreteType<Integer> type = new DiscreteType<>(TypeLib.INTEGER, one, thousand);
		
		assertFormat(type, one, "1", "1", "1", "1");
		assertParse(type, one, "1", "1", "1", "1");
		
		assertFormat(type, thousand, "1000", "1,000", "1.000", "1 000");
		assertParse(type, thousand, "1000", "1,000", "1.000", "1 000");
		
		try
		{
			// if not contained in the discrete list, a value is rejected
			STANDARD.parse(type, "0");
			fail();
		}
		catch(ParseException e)
		{
		}
	}
	
	
	private enum TestEnum
	{
		alpha,
		beta
	}
	

	@Test public void testEnumType() throws Exception
	{
		EnumType<TestEnum> type = new EnumType<>(TestEnum.class);
		
		assertFormat(type, TestEnum.alpha, "alpha", "alpha", "alpha", "alpha");
		assertParse (type, TestEnum.alpha, "alpha", "alpha", "alpha", "alpha");

		try
		{
			STANDARD.parse(type, "gamma");
			fail();
		}
		catch(ParseException e)
		{
		}
	}

	
	private static final StandardSerializer STANDARD = StandardSerializer.INSTANCE;
	private static final LocaleSerializer LOCALE_US = new LocaleSerializer(Locale.US);
	private static final LocaleSerializer LOCALE_DE = new LocaleSerializer(Locale.GERMAN);
	private static final LocaleSerializer LOCALE_FR = new LocaleSerializer(Locale.FRENCH);
}
