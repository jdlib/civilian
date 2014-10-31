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
package org.civilian.content;


import java.util.Comparator;
import java.util.Iterator;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;


public class ContentTypeListTest extends CivTest
{
	@Test public void testParse()
	{
		Iterator<ContentType> it;
		
		it = parse((String[])null);
		assertFalse(it.hasNext());

		it = parse("");
		assertFalse(it.hasNext());
		
		it = parse("text/plain, text/html");
		assertContentType(it.next(), "text/plain", 1.0);
		assertContentType(it.next(), "text/html", 1.0);
		
		it = parse("text/x-dvi; q=.8; mxb=100000; mxt=5.0, text/x-c");
		assertContentType(it.next(), "text/x-dvi", 0.8);
		assertContentType(it.next(), "text/x-c", 1.0);

		// content types sorted by their quality
		it = parse(ContentType.Compare.BY_QUALITY, "text/a,text/b; q=.7", "text/c; q=0.8");
		assertContentType(it.next(), "text/a", 1.0);
		assertContentType(it.next(), "text/c", 0.8);
		assertContentType(it.next(), "text/b", 0.7);
		
		// content types sorted by their specificity
		it = parse(ContentType.Compare.BY_SPECIFICITY, "text/*; q=1.0, text/html; q=0.3, */*; q=0.5");
		assertContentType(it.next(), "text/html", 0.3);
		assertContentType(it.next(), "text/*", 1.0);
		assertContentType(it.next(), "*/*", 0.5);
	}
	
	
	@Test public void testToString()
	{
		ContentTypeList list = new ContentTypeList(ContentType.TEXT_HTML, ContentType.TEXT_PLAIN.withQuality(0.5));
		assertEquals("text/html,text/plain; q=0.5", list.toString());
	}
	
	
	private Iterator<ContentType> parse(String... acceptHeaders)
	{
		return ContentTypeList.parse(acceptHeaders).iterator();
	}


	private Iterator<ContentType> parse(Comparator<ContentType> comp, String... acceptHeaders)
	{
		return ContentTypeList.parse(comp, acceptHeaders).iterator();
	}

	
	private void assertContentType(ContentType type, String s)
	{
		assertEquals(s, type.getValue());
	}
	
	
	private void assertContentType(ContentType type, String s, double quality)
	{
		assertContentType(type, s);
		assertEquals(quality, type.getQuality(), 0.0);
	}
}
