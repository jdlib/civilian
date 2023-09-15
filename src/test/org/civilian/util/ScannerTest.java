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
	private final Scanner scanner = new Scanner();
	private final ScannerAssert a = new ScannerAssert(scanner);
	
	
	@Test public void testInit()
	{
		scanner.input((String)null);
		a.pos(0).length(0).line("");
		
		scanner.input("abc");
		a.pos(0).length(3).line("abc");
	}
	
	
	@Test public void testNextQuotedString()
	{
		scanner.input("'123' 'abc' \"\" 'ab\"c'#");
		a.current('\'');
		a.nextQuotedString().returns("123");
		a.nextQuotedString('"').returns(null).nextQuotedString('\'').returns("abc");
		a.nextQuotedString(true).returns("\"\"");
		a.nextQuotedString('\'', true).returns("'ab\"c'");
		a.current('#');
	}
	

	@Test public void testMultLines()
	{
		scanner.input("a", "b", "c");
		a.inputLineCount(3);
		for (int i=0; i<3; i++)
			a.inputLineIndex(i).inputNextLine(i < 2);
		
		scanner.input();
		a.inputLineCount(0).inputLineIndex(0).line("");

		scanner.input((String[])null);
		a.inputLineCount(0).inputLineIndex(0).line("");
	}


	@Test public void testAccessors()
	{
		scanner.input("123");
		a.inputLineIndex(0).inputLineCount(1).line("123").length(3).pos(0).current('1');
		scanner.skip();
		a.pos(1).current('2');
 		assertTrue  (scanner.currentIsDigit());
 		assertFalse (scanner.currentHasType(Character.MATH_SYMBOL));
 		assertTrue  (scanner.next("23"));
 		a.current(-1);
	}


	@Test public void testSkipWhitespace()
	{
		Scanner s = new Scanner(" 1 1");
		s.setAutoSkipWhitespace(false);
		assertFalse(s.next("1"));
		s.skipWhitespace();
		assertTrue(s.next("1"));
		
		assertFalse(s.next("1"));
		s.setAutoSkipWhitespace(true);
		assertTrue(s.next("1"));
	}


	@Test public void testNextString()
	{
		scanner.input.lines(" abc white");
		a.next("abcd").returns(false).pos(1); // has skipped whitespace
		a.next("abd").returns(false).pos(1);
		a.next("abc").returns(true).pos(4);
		a.next("white").returns(true); // has skipped whitespace
		a.expect().next("space").fails("next(\"space\")");
	}


	@Test public void testNextChar()
	{
		scanner.input.lines("a b");
		a.next('x').returns(false).pos(0);
		a.expect().next('x').fails("next('x')");
		a.next('a').returns(true).pos(1);
		a.next('b').returns(true); // has skipped whitespace
	}

	
	@Test public void testNextKeyword()
	{
		scanner.input.lines(" encoding ISO-8859-1");
		a.expect().nextKeyword("a").fails("x")
		 .nextKeyword("enc").returns(false)
		 .nextKeyword("encoding").returns(true)
		 .rest(" ISO-8859-1");
	}


	@Test public void testNextIdentifier()
	{
		scanner.input.lines("a a12 abc-");
		a.nextIdentifier().returns("a")
		 .nextIdentifier().returns("a12")
		 .nextIdentifier().returns("abc")
		 .nextIdentifier().returns(null)
		 .expect().nextIdentifier().fails("nextIdentifier");
	}


	@Test public void testConsumeToken()
	{
		Scanner s = new Scanner("a");
		assertEquals("a", s.consumeToken());
		
		s.input("a b,d");
		assertEquals("a", s.consumeToken());
		assertEquals("b", s.consumeToken(","));
		
		s.input(" a");
		s.setAutoSkipWhitespace(false);
		assertNull(s.consumeToken());
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
		s.expect().next('a');
		
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
		s.expect().next('b');
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


