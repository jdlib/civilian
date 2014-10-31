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
package org.civilian.template;


import java.io.PrintWriter;
import java.io.StringWriter;
import org.civilian.CivTest;
import org.civilian.util.Charset;
import org.junit.Before;
import org.junit.Test;


public class HtmlUtilTest extends CivTest
{
	@Before public void before()
	{
		stringOut_.getBuffer().setLength(0);
	}
	
	
	@Test public void testAttr()
	{
		HtmlUtil.attr(out, "a", "b");
		assertOut(" a=\"b\"");

		HtmlUtil.attr(out, null, "b");
		assertOut("");
	}
	
	
	@Test public void testEscape()
	{
		assertEscape("a", 	"a");
		assertEscape("<", 	"&lt;");
		assertEscape("\"", 	"&quot;");
		assertEscape("<a>", "&lt;a&gt;");
		assertEscape("\n", 	"\n");
		assertEscape("\n", 	"&#10;", true);
		
		HtmlUtil.escape(out, "€", true, null);
		assertOut("€");

		HtmlUtil.escape(out, "€", true, Charset.EIGHT_BIT);
		assertOut("&#8364;");
	}
	
	
	private void assertEscape(String input, String expected)
	{
		assertEscape(input, expected, false);
	}
	

	private void assertEscape(String input, String expected, boolean isAttr)
	{
		HtmlUtil.escape(out, input, isAttr);
		assertOut(expected);
	}
	
	
	private void assertOut(String expected)
	{
		String actual = stringOut_.toString();
		stringOut_.getBuffer().setLength(0);
		assertEquals(expected, actual);
	}


	private StringWriter stringOut_ = new StringWriter();
	private PrintWriter out = new PrintWriter(stringOut_, true);
}
