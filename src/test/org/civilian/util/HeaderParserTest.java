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
import org.civilian.internal.HeaderParser;


/**
 * Tests HeaderParser.
 */
public class HeaderParserTest extends CivTest
{
	@Test public void testEmpty()
	{
		HeaderParser parser = new HeaderParser("");
		assertEnd  (parser);
	}
	
	
	@Test public void test1()
	{
		HeaderParser parser = new HeaderParser("form-data; name=\"photo\"; filename=\"photo.jpg\"");
		assertItem (parser, "form-data");
		assertParam(parser, "name", "photo");
		assertParam(parser, "filename", "photo.jpg");
		assertEnd  (parser);
	}
	
	
	@Test public void test2()
	{
		HeaderParser parser = new HeaderParser("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		assertItem (parser, "text/html");
		assertItem (parser, "application/xhtml+xml");
		assertItem (parser, "application/xml");
		assertParam(parser, "q", "0.9");
		assertItem (parser, "*/*");
		assertParam(parser, "q", "0.8");
		assertEnd  (parser);
	}
	
	
	private void assertEnd(HeaderParser parser)
	{
		assertEquals(HeaderParser.Token.END, parser.next());
	}
	
	
	private void assertItem(HeaderParser parser, String item)
	{
		assertEquals(HeaderParser.Token.ITEM, parser.next());
		assertEquals(item, parser.item);
	}
	
	
	private void assertParam(HeaderParser parser, String name, String value)
	{
		assertEquals(HeaderParser.Token.PARAM, parser.next());
		assertEquals(name, parser.paramName);
		assertEquals(value, parser.paramValue);
	}
}
