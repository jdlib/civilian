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


import org.junit.Test;
import org.civilian.CivTest;


public class ScannerTest extends CivTest
{
	@Test public void testNextQuotedString()
	{
		Scanner s = new Scanner("'abc' 'abc' \"\" 'ab\"c'#");
		assertEquals('\'', s.current());
		assertEquals("abc", s.consumeQuotedString(false));
		s.skipWhitespace();
		assertEquals("'abc'", s.consumeQuotedString(true));
		s.skipWhitespace();
		assertEquals("", s.consumeQuotedString(false));
		s.skipWhitespace();
		assertEquals("'ab\"c'", s.consumeQuotedString(true));
		assertEquals('#', s.current());
	}


	@Test public void testNextKeyword()
	{
		Scanner s = new Scanner("encoding12");
		assertFalse(s.nextKeyword("encoding"));

		s = new Scanner("encoding");
		assertTrue(s.nextKeyword("encoding"));
		assertFalse(s.hasMoreChars());

		s = new Scanner("encoding ISO-8859-1");
		assertTrue(s.nextKeyword("encoding"));
		assertEquals("ISO-8859-1", s.consumeRest());
	}
	

	@Test public void testMultLines()
	{
		Scanner s = new Scanner("a", "b", "c");
		assertEquals(3, s.getLines().length);
		for (int i=0; i<3; i++)
		{
			assertEquals(i, s.getLineIndex());
			assertEquals(i < 2, s.nextLine());
		}
		
		s = new Scanner(new String[0]);
		assertEquals(1, s.getLines().length);

		s = new Scanner((String[])null);
		assertEquals(1, s.getLines().length);
	}


	@Test public void testAccessors()
	{
		Scanner s = new Scanner("123");
 		assertEquals(0, s.getLineIndex());
 		assertEquals(1, s.getLineCount());
 		assertEquals("123", s.getLine());
		assertEquals(3, s.getLength());
		assertEquals(0, s.getPos());
		s.skip();
		assertEquals(1, s.getPos());
 		assertEquals('2', s.current());
 		assertTrue  (s.currentIsDigit());
 		assertFalse (s.currentHasType(Character.MATH_SYMBOL));
 		assertTrue  (s.next("23"));
 		assertEquals(-1, s.current());
	}


	@Test public void testSkipWhitespace()
	{
		Scanner s = new Scanner(" 1 1", 0);
		s.autoSkipWhitespace(false);
		assertFalse(s.next("1"));
		s.skipWhitespace();
		assertTrue(s.next("1"));
		
		assertFalse(s.next("1"));
		s.autoSkipWhitespace(true);
		assertTrue(s.next("1"));
	}


	@Test public void testNext()
	{
		Scanner s = new Scanner("abc");
		assertFalse(s.next("abcd"));
		assertFalse(s.next("abd"));
		assertTrue(s.nextKeyword("abc"));
		
		s.init("abc,");
		assertTrue(s.nextKeyword("abc"));
	}


	@Test public void testConsumeAny()
	{
		Scanner s = new Scanner("a");
		assertEquals(null, s.consumeAny("b"));
		assertEquals("a", s.consumeAny("b", "a"));
	}


	@Test public void testConsumeToken()
	{
		Scanner s = new Scanner("a");
		assertEquals("a", s.consumeToken());
		
		s.init("a b,d");
		assertEquals("a", s.consumeToken());
		assertEquals("b", s.consumeToken(","));
		
		s.init(" a");
		s.autoSkipWhitespace(false);
		assertNull(s.consumeToken());
	}


	@Test public void testConsumeIdentifier()
	{
		Scanner s = new Scanner("a a12 abc-");
		assertEquals("a", s.consumeIdentifier());
		assertEquals("a12", s.consumeIdentifier());
		assertEquals("abc", s.consumeIdentifier());
		assertEquals('-', s.current());
		assertNull(s.consumeIdentifier());
	}


	@Test public void testConsumeNumber()
	{
		Scanner s = new Scanner("12 a 12.34 b");
		assertEquals(12, s.consumeInt());
		try
		{
			s.consumeInt();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expected a integer (4): '12 a 12.34 b", e.getMessage());
		}
		s.expect("a");
		
		assertEquals(12.34, s.consumeDouble(), 0.0);
		try
		{
			s.consumeDouble();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expected a double (12): '12 a 12.34 b", e.getMessage());
		}
		s.expect("b");
	}


	@Test public void testConsumeHex()
	{
		Scanner s = new Scanner("01Ff");
		byte[] b  = s.consumeHexBytes();
		assertEquals(2, b.length);
		assertEquals(1, b[0]);
		assertEquals(-1, b[1]);
	}
}


