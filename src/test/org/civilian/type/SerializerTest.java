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


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.type.lib.StandardSerializer;
import org.civilian.type.lib.LocaleSerializer;


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
		assertFormat(TypeLib.DATE_JAVA_SQL, new java.sql.Date(112, 0, 31), 			"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DATE_JAVA_UTIL,new java.util.Date(112, 0, 31), 		"20120131", "01/31/2012", "31.01.2012", "31/01/2012");
		assertFormat(TypeLib.DOUBLE, 		new Double(2345.6),						"2345.6", "2,345.60","2.345,60","2\u00a0345,60");
		assertFormat(TypeLib.DOUBLE, 		new Double(2345),						"2345.0", "2,345.00","2.345,00","2\u00a0345,00");
		assertFormat(TypeLib.FLOAT, 		new Float(2345.6),						"2345.6", "2,345.60","2.345,60","2\u00a0345,60");
		assertFormat(TypeLib.INTEGER, 		new Integer(2345),						"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertFormat(TypeLib.LONG, 			new Long(2345),							"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertFormat(TypeLib.LONG, 			new Long(-5432),						"-5432",  "-5,432",   "-5.432", "-5\u00a0432");
		assertFormat(TypeLib.SHORT, 		new Short((short)2345),					"2345",   "2,345",   "2.345",   "2\u00a0345");
		assertFormat(TypeLib.STRING,		"abc",									"abc");
	}
	
	
	private <T> void assertFormat(Type<T> type, T value, String string) throws Exception
	{
		assertFormat(type, value, string, string, string, string);
	}
	
	
	private <T> void assertFormat(Type<T> type, T value, String defaultString, String usString, String deString, String frString) throws Exception
	{
		assertEquals("format default null",		"",				type.format(DEFAULT, null));
		assertEquals("format default value",	defaultString,	type.format(DEFAULT, value));
		assertEquals("format locale-us null",	"",				type.format(LOCALE_US, null));
		assertEquals("format locale-us value",	usString,		type.format(LOCALE_US, value));
		assertEquals("format locale-de value",	deString,		type.format(LOCALE_DE, value));
		assertEquals("format locale-fr value",	frString,		type.format(LOCALE_FR, value));
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
		assertEquals("parse default empty",	type == TypeLib.STRING ? "" : null,	type.parse(DEFAULT, ""));
		assertNull  ("parse default null",						type.parse(DEFAULT, null));
		assertEquals("parse default",			value,			type.parse(DEFAULT, defaultString));
		assertNull  ("parse locale null",						type.parse(LOCALE_US, null));
		assertEquals("parse locale us",			value,			type.parse(LOCALE_US, usString));
		assertEquals("parse locale de",			value,			type.parse(LOCALE_DE, deString));
		assertEquals("parse locale fr",			value,			type.parse(LOCALE_FR, frString));

		if (type == TypeLib.STRING)
			assertEquals("parse lcoale empty ", "",	type.parse(LOCALE_US, ""));
		else if (type == TypeLib.BOOLEAN)
			assertEquals("parse lcoale empty ", Boolean.FALSE, type.parse(LOCALE_US, ""));
		else
			assertEquals("parse lcoale empty ", null, type.parse(LOCALE_US, ""));
	}
	
	
	@Test public void testParseDefaultDate() throws Exception
	{
		try
		{
			StandardSerializer.INSTANCE.parseDate("123", TypeLib.DATE_CIVILIAN);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("date strings must be 8 characters long", e.getMessage());
		}

		try
		{
			StandardSerializer.INSTANCE.parseDate("20131401", TypeLib.DATE_CIVILIAN);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not a valid date (yyyymmdd): 20131401", e.getMessage());
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

		// 
		String big = "1234567890123456789012345678901234567890";
		assertEquals(new BigDecimal(big), LOCALE_US.parseBigDecimal(big));
		assertEquals(new BigInteger(big), LOCALE_US.parseBigInteger(big));
		
		assertEquals(123L, LOCALE_US.parseLong("+123").longValue());
		assertEquals(123456L, LOCALE_FR.parseLong("+123 456").longValue());
	}
	
	
	private static final StandardSerializer DEFAULT = StandardSerializer.INSTANCE;
	private static final LocaleSerializer LOCALE_US = new LocaleSerializer(Locale.US);
	private static final LocaleSerializer LOCALE_DE = new LocaleSerializer(Locale.GERMAN);
	private static final LocaleSerializer LOCALE_FR = new LocaleSerializer(Locale.FRENCH);
}
