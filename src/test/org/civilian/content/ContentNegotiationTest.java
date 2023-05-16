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


public class ContentNegotiationTest extends CivTest
{
	@Test public void test1()
	{
		ContentTypeList accepted = new ContentTypeList
		(
			ContentType.TEXT_HTML,
			ContentType.TEXT_PLAIN.withQuality(0.5),
			ContentType.ANY.withQuality(0.1)
		); 
		
		ContentNegotiation cn = new ContentNegotiation(accepted);
		assertTrue(cn.evaluate(new ContentType("application/*")));
		assertBest(cn, "application/*", 0.1, 1.0, 1);

		assertTrue(cn.evaluate(ContentType.APPLICATION_JSON));
		assertBest(cn, "application/json", 0.1, 1.0, 2);

		assertFalse(cn.evaluate(ContentType.APPLICATION_JAVASCRIPT));
		assertFalse(cn.evaluate(ContentType.APPLICATION_JSON));
		assertBest(cn, "application/json", 0.1, 1.0, 2);

		assertTrue(cn.evaluate(ContentType.TEXT_PLAIN));
		assertBest(cn, "text/plain", 0.5, 1.0, 0);

		assertTrue(cn.evaluate(ContentType.TEXT_HTML));
		assertBest(cn, "text/html", 1, 1.0, 0);
	}
	
	
	/**
	 * http://scribbles.fried.se/2011/04/browser-views-in-jersey-and-fed-up.html
	 */
	@Test public void testServerPrioritize()
	{
		// accepts xml and json with quality 1.0
		ContentTypeList accepted = new ContentTypeList
		(
			ContentType.APPLICATION_XML,
			ContentType.APPLICATION_JSON
		);
		
		ContentNegotiation cn = new ContentNegotiation(accepted);
		
		assertTrue(cn.evaluate(new ContentType("application/xml")));
		assertBest(cn, "application/xml", 1.0, 1.0, 0);
		assertFalse(cn.evaluate(new ContentType("application/json")));
		
		// now boot produces-preference of json to 2
		cn = new ContentNegotiation(accepted);
		assertTrue(cn.evaluate(new ContentType("application/xml")));
		assertBest(cn, "application/xml", 1.0, 1.0, 0);
		assertTrue(cn.evaluate(new ContentType("application/json").withQuality(2.0)));
		assertBest(cn, "application/json", 1.0, 2.0, 0);
	}
	


	private void assertBest(ContentNegotiation cn, String value, double q, double qs, int distance)
	{
		CombinedContentType ctype = cn.bestType;
		assertEquals(value, ctype.getValue());
		assertEquals("q", q, ctype.getQuality(), 0.0);
		assertEquals("qs", qs, ctype.getServerQuality(), 0.0);
		assertEquals("distance", distance, ctype.getDistance());
	}
}
