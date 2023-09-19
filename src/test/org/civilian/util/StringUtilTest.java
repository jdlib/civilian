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


public class StringUtilTest extends CivTest
{
	@Test public void testStartCase()
	{
		assertEquals("", 	StringUtil.startUpperCase(""));
		assertEquals("Abc", StringUtil.startUpperCase("Abc"));
		assertEquals("ABC", StringUtil.startUpperCase("aBC"));

		assertEquals("", 	StringUtil.startLowerCase(""));
		assertEquals("abc", StringUtil.startLowerCase("abc"));
		assertEquals("aBC", StringUtil.startLowerCase("ABC"));
	}


	@Test public void testSplit()
	{
		assertArrayEquals2(StringUtil.split("a",  ""), "a");
		assertArrayEquals2(StringUtil.split("",  "/"));
		assertArrayEquals2(StringUtil.split("/", "/"));
		assertArrayEquals2(StringUtil.split("ab/cd", "/"), "ab", "cd");
		assertArrayEquals2(StringUtil.split("/ab/cd/", "/"), "ab", "cd");
	}


	@Test public void testBeforeAfter()
	{
		assertEquals("abc", StringUtil.before("abc", '='));
		assertEquals("abc", StringUtil.before("abc=123", '='));
		assertEquals("", 	StringUtil.before("=123", '='));
		assertEquals(null, 	StringUtil.before(null, '='));
		assertEquals(null,  StringUtil.after("123", '='));
		assertEquals("123", StringUtil.after("abc=123", '='));
		assertEquals("", 	StringUtil.after("abc=", '='));
		assertEquals(null, 	StringUtil.after(null, '='));
	}


	@Test public void testCutHave()
	{
		assertNull(StringUtil.cutLeft(null, "x"));
		assertEquals("",	StringUtil.cutLeft("", "x"));
		assertEquals("",	StringUtil.cutLeft("x", "x"));
		assertEquals("ax",	StringUtil.cutLeft("ax", "x"));

		assertNull(StringUtil.haveLeft(null, "x"));
		assertEquals("x",	StringUtil.haveLeft("", "x"));
		assertEquals("x",	StringUtil.haveLeft("x", "x"));
		assertEquals("xa",	StringUtil.haveLeft("a", "x"));

		assertNull(StringUtil.cutRight(null, "x"));
		assertEquals("",	StringUtil.cutRight("", "x"));
		assertEquals("",	StringUtil.cutRight("x", "x"));
		assertEquals("xa",	StringUtil.cutRight("xa", "x"));

		assertNull(StringUtil.haveRight(null, "x"));
		assertEquals("x",	StringUtil.haveRight("", "x"));
		assertEquals("x",	StringUtil.haveRight("x", "x"));
		assertEquals("ax",	StringUtil.haveRight("a", "x"));
	}


	@Test public void testFill()
	{
		assertEquals("a..", StringUtil.fillRight("a", 3, '.'));
		assertEquals("a.",  StringUtil.fillRight("a", 2, '.'));
		assertEquals("a  ", StringUtil.fillRight("a", 3));
		assertEquals("100", StringUtil.fillRight(1, 3));
		assertEquals("12",  StringUtil.fillRight(123, 2));

		assertEquals("..a", StringUtil.fillLeft("a", 3, '.'));
		assertEquals(".a",  StringUtil.fillLeft("a", 2, '.'));
		assertEquals("  a", StringUtil.fillLeft("a", 3));
		assertEquals("001", StringUtil.fillLeft(1, 3));
		assertEquals("12",  StringUtil.fillLeft(123, 2));
	}


	@Test public void testRtrim()
	{
		assertNull("a", StringUtil.rtrim(null));
		assertSame("a", StringUtil.rtrim("a"));
		assertEquals(" a b", StringUtil.rtrim(" a b \t\r\n"));
	}
}
