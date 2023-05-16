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
package org.civilian.internal.controller.arg;


import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.internal.controller.arg.conv.SimpleConverter;
import org.civilian.internal.controller.arg.conv.SimpleConverters;
import org.civilian.request.BadRequestException;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.LocaleSerializer;
import org.civilian.type.fn.StandardSerializer;


public class SimpleConverterTest extends CivTest
{
	@Test public void testCreate() throws Exception
	{
		assertConvert(create(String.class),  		"abc", 			"abc",							null);
		assertConvert(create(Integer.class), 		"1.000", 		Integer.valueOf(1000),			null);
		assertConvert(create(int.class), 			"13.000", 		Integer.valueOf(13000),			Integer.valueOf(0));
		assertConvert(create(Double.class),  		"1.234,5", 		Double.valueOf(1234.5),			null);
		assertConvert(create(double.class),  		"1.234,5", 		Double.valueOf(1234.5),			Double.valueOf(0.0));
		assertConvert(create(Boolean.class), 		"true", 		Boolean.TRUE,					null);
		assertConvert(create(boolean.class), 		"false", 		Boolean.FALSE,					Boolean.FALSE);
		assertConvert(create(LocalDate.class), 		"31.12.2012", 	LocalDate.of(2012, 12, 31),		null);
		assertConvert(create(Ctor.class), 			"#", 			new Ctor("#"),					null);
		assertConvert(create(ValueOfString.class), 	"123", 			ValueOfString.valueOf("123"),	null);
		assertConvert(create(FromString.class), 	"456", 			FromString.fromString("456"),	null);
	}
	
	
	@Test public void testFailCreate() throws Exception
	{
		assertNull(create(ArrayList.class));
		assertNull(create(PrivateCtor.class));
		assertNull(create(NoStringCtor.class));
		assertNull(create(NotAccessibleValueOfFrom.class));
		assertNull(create(NotStaticValueOfFrom.class));
		assertNull(create(InvalidReturnValueOfFrom.class));
	}
	
	
	@Test public void testConvert() throws Exception
	{
		SimpleConverter<String> converter = create(String.class);
		TestStringArg testArg = new TestStringArg();
		testArg.value = null;
		
		assertEquals(null, converter.getValue(null, testArg, StandardSerializer.INSTANCE, null));
		assertEquals("d",  converter.getValue(null, testArg, StandardSerializer.INSTANCE, "d"));

		testArg.value = "x";
		assertEquals("x", converter.getValue(null, testArg, StandardSerializer.INSTANCE, "d"));
	}

	
	@Test public void testFailConvert() throws Exception
	{
		SimpleConverter<Integer> converter = create(Integer.class);
		TestStringArg testArg = new TestStringArg();
		testArg.value = "abc";
		try
		{
			converter.getValue(null, testArg, StandardSerializer.INSTANCE, null);
			fail();
		}
		catch(BadRequestException e)
		{
			assertTrue(e.getCause() instanceof ParseException);
			assertTrue(e.getCause().getCause() instanceof NumberFormatException);
			assertEquals("abc", e.getErrorValue());
		}
	}
	
	
	private static <T> SimpleConverter<T> create(Class<?> c)
	{
		return SimpleConverters.create(library_, c);
	}

	
	private <T> void assertConvert(SimpleConverter<?> sc, String s, T value, T nullValue) throws Exception
	{
		assertNotNull(sc);
		assertEquals(value, sc.convert(null, serializer_, s));
		assertEquals(nullValue, sc.nullValue());
	}
	

	public static class Ctor
	{
		public Ctor(String s)
		{
			this.s = s;
		}
		
		@Override public boolean equals(Object other)
		{
			return (other instanceof Ctor) && ((Ctor)other).s.equals(s);
		}

		public final String s;
	}
	
	
	public static class PrivateCtor
	{
		private PrivateCtor(String s)
		{
			// non-public ctor is ignored
		}
	}
	

	public static class NoStringCtor
	{
	}

	
	public static class ValueOfString
	{
		public static ValueOfString valueOf(String s)
		{
			return new ValueOfString(Long.parseLong(s));
		}
		
		public ValueOfString(long n)
		{
			this.n = n;
		}
		
		@Override public boolean equals(Object other)
		{
			return (other instanceof ValueOfString) && ((ValueOfString)other).n == n;
		}

		public final long n;
	}

	
	public static class FromString
	{
		public static FromString fromString(String s)
		{
			return new FromString(Long.parseLong(s));
		}
		
		public FromString(long n)
		{
			this.n = n;
		}
		
		@Override public boolean equals(Object other)
		{
			return (other instanceof FromString) && ((FromString)other).n == n;
		}

		public final long n;
	}


	public static class NotAccessibleValueOfFrom
	{
		protected static NotAccessibleValueOfFrom valueOf(String s)
		{
			// non-public method ignored 
			return null;
		}
		
		@SuppressWarnings("unused")
		private static NotAccessibleValueOfFrom fromString(String s)
		{
			// non-static method ignored 
			return null;
		}
	}
	

	public static class NotStaticValueOfFrom
	{
		public NotStaticValueOfFrom valueOf(String s)
		{
			return null;
		}
		
		public NotStaticValueOfFrom fromString(String s)
		{
			return null;
		}
	}

	
	public static class InvalidReturnValueOfFrom
	{
		public static Integer valueOf(String s)
		{
			// invalid return type 
			return null;
		}
		
		public static String fromString(String s)
		{
			// invalid return type 
			return null;
		}
	}
	

	private static TypeLib library_ = new TypeLib();
	private static LocaleSerializer serializer_ = new LocaleSerializer(Locale.GERMAN);
}
