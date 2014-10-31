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


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.content.CombinedContentType;


public class CombinedContentTypeTest extends CivTest
{
	@Test public void testCreate()
	{
		CombinedContentType ct;
		
		ct = CombinedContentType.create(ContentType.TEXT_HTML, ContentType.TEXT_HTML.withQuality(0.5));
		assertCombined(ct, "text/html", 1.0, 0.5, 0);

		ct = CombinedContentType.create(new ContentType("text", "*", 0.5), new ContentType("text", "html", 0.8));
		assertCombined(ct, "text/html", 0.5, 0.8, 1);

		ct = CombinedContentType.create(new ContentType("*", "*", 0.2), new ContentType("text", "*", 0.9));
		assertCombined(ct, "text/*", 0.2, 0.9, 1);

		ct = CombinedContentType.create(ContentType.TEXT_HTML, new ContentType("*", "*", 0.9));
		assertCombined(ct, "text/html", 1.0, 0.9, 2);

		ct = CombinedContentType.create(ContentType.TEXT_HTML, ContentType.TEXT_HTML);
		assertCombined(ct, "text/html", 1.0, 1.0, 0);

		// incompatible main type
		ct = CombinedContentType.create(ContentType.APPLICATION_JSON, ContentType.TEXT_XML);
		assertNull(ct);
		
		// incompatible sub type
		ct = CombinedContentType.create(ContentType.TEXT_HTML, ContentType.TEXT_XML);
		assertNull(ct);
	}


	@Test public void testCombine()
	{
		ContentType textAny = new ContentType("text", "*"); 
		
		CombinedContentType.Combined c;
		
		c = new CombinedContentType.Combined(ContentType.ANY, ContentType.ANY);
		assertCombined(c, null, null, 0);

		c = new CombinedContentType.Combined(ContentType.TEXT_HTML, ContentType.TEXT_HTML);
		assertCombined(c, "text", "html", 0);

		c = new CombinedContentType.Combined(textAny, ContentType.ANY);
		assertCombined(c, "text", null, 1);

		c = new CombinedContentType.Combined(textAny, ContentType.TEXT_HTML);
		assertCombined(c, "text", "html", 1);

		c = new CombinedContentType.Combined(ContentType.ANY, ContentType.TEXT_HTML);
		assertCombined(c, "text", "html", 2);

		c = new CombinedContentType.Combined(ContentType.APPLICATION_EXCEL, ContentType.TEXT_HTML);
		assertCombined(c, null, null, -1);

		c = new CombinedContentType.Combined(ContentType.TEXT_CSS, ContentType.TEXT_HTML);
		assertCombined(c, "text", null, -1);
	}
	
	
	@Test public void testNegotiate()
	{
		CombinedContentType best = CombinedContentType.negotiate(ContentType.ANY, ContentType.ANY, null);
		assertCombined(best, "*/*", 1.0, 1.0, 0);

		CombinedContentType newBest = CombinedContentType.negotiate(new ContentType("text", "*"), ContentType.TEXT_HTML, best);
		assertCombined(newBest, "text/html", 1.0, 1.0, 1);
	}
	
	
	private void assertCombined(CombinedContentType ctype, String value, double q, double qs, int distance)
	{
		assertEquals(value, ctype.getValue());
		assertEquals(value, q, ctype.getQuality(), 0.0);
		assertEquals(value, qs, ctype.getServerQuality(), .0);
		assertEquals(value, distance, ctype.getDistance());
	}


	private void assertCombined(CombinedContentType.Combined c, String type, String subType, int distance)
	{
		assertEquals(type, c.type);
		assertEquals(subType, c.subType);
		assertEquals(distance, c.distance);
	}
}
