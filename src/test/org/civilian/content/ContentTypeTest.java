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


import org.civilian.CivTest;
import org.junit.Test;


public class ContentTypeTest extends CivTest
{
	@Test public void testBuiltins()
	{
		assertSame(ContentType.APPLICATION_JAVASCRIPT, 
			ContentType.getContentType(ContentType.APPLICATION_JAVASCRIPT.getValue()));
		assertSame(ContentType.Strings.APPLICATION_JAVASCRIPT, 
			ContentType.APPLICATION_JAVASCRIPT.getValue());
	}
	

	@Test public void testParts()
	{
		assertEquals("application/pdf", ContentType.APPLICATION_PDF.getValue());
		assertEquals("application", ContentType.APPLICATION_PDF.getMainPart());
		assertEquals("pdf", ContentType.APPLICATION_PDF.getSubPart());

		assertEquals("*/*", ContentType.ANY.getValue());
		assertSame(null, ContentType.ANY.getMainPart());
		assertSame(null, ContentType.ANY.getSubPart());
	}
	
	
	@Test public void testQuality()
	{
		assertEquals(1.0, ContentType.APPLICATION_XML.getQuality(), 0.0);
		
		ContentType xml05 = ContentType.APPLICATION_XML.withQuality(0.5);
		assertEquals(0.5, xml05.getQuality(), 0.0);
		assertEquals("application/xml; q=0.5", xml05.toString());
		
		try
		{
			ContentType.APPLICATION_XML.withQuality(-0.1);
			fail();
		}
		catch(Exception e)
		{
		}
	}
	
	
	@Test public void testEquals()
	{
		assertFalse(ContentType.TEXT_HTML.equals(null));
		assertEquals(ContentType.TEXT_HTML, new ContentType(ContentType.Types.TEXT, "html"));
	}


	@Test public void testMatches()
	{
		assertTrue(ContentType.ANY.matchesMainPart(null));
		assertTrue(ContentType.ANY.matchesMainPart("*"));
		assertTrue(ContentType.ANY.matchesMainPart("text"));

		assertTrue(ContentType.TEXT_HTML.matchesMainPart(null));
		assertTrue(ContentType.TEXT_HTML.matchesMainPart("*"));
		assertTrue(ContentType.TEXT_HTML.matchesMainPart("text"));
		assertFalse(ContentType.TEXT_HTML.matchesMainPart("image"));

		assertTrue(ContentType.ANY.matchesSubPart(null));
		assertTrue(ContentType.ANY.matchesSubPart("*"));
		assertTrue(ContentType.ANY.matchesSubPart("html"));

		assertTrue(ContentType.TEXT_HTML.matchesSubPart(null));
		assertTrue(ContentType.TEXT_HTML.matchesSubPart("*"));
		assertTrue(ContentType.TEXT_HTML.matchesSubPart("html"));
		assertFalse(ContentType.TEXT_HTML.matchesSubPart("plain"));

		ContentType anyText = new ContentType("text", "*"); 
		ContentType anyXml = new ContentType("*", "xml");
		
		assertMatches(ContentType.ANY, ContentType.ANY, true);
		assertMatches(ContentType.ANY, anyText, true);
		assertMatches(ContentType.ANY, anyXml, true);
		assertMatches(ContentType.ANY, ContentType.TEXT_HTML, true);

		assertMatches(anyText, anyXml, true);
		assertMatches(anyText, ContentType.TEXT_HTML, true);
		assertMatches(anyText, ContentType.TEXT_PLAIN, true);
		assertMatches(anyText, ContentType.APPLICATION_XML, false);

		assertMatches(anyXml, ContentType.APPLICATION_XML, true);
		assertMatches(anyXml, ContentType.TEXT_XML, true);
		assertMatches(anyXml, ContentType.APPLICATION_PDF, false);
	}
	
	
	private void assertMatches(ContentType mt1, ContentType mt2, boolean matches)
	{
		assertTrue(mt1.matches(mt2) == matches);
		assertTrue(mt2.matches(mt1) == matches);
	}


	@Test public void testSpecificity()
	{
		ContentType anyText 	= new ContentType("text", "*"); 
		ContentType anyXml 		= new ContentType("*", "xml");
		
		assertEquals(0, ContentType.ANY.getSpecificity()); 
		assertEquals(1, anyText.getSpecificity()); 
		assertEquals(2, anyXml.getSpecificity()); 
		assertEquals(3, ContentType.APPLICATION_EXCEL.getSpecificity()); 
	}
}
