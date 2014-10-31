package org.civilian.content;


import org.civilian.CivTest;
import org.junit.Test;


public class ContentTypeLookupTest extends CivTest
{
	@Test public void testDefault()
	{
		assertEquals(ContentType.TEXT_HTML, ContentTypeLookup.DEFAULT.forFile("test.html"));
		assertEquals(null, ContentTypeLookup.DEFAULT.forFile(null));
		assertEquals(ContentType.APPLICATION_EXCEL, ContentTypeLookup.DEFAULT.forFile(null, ContentType.APPLICATION_EXCEL));
		assertEquals(ContentType.TEXT_HTML, ContentTypeLookup.DEFAULT.forExtension("html"));
		assertEquals(null, ContentTypeLookup.DEFAULT.forExtension(null));
		assertEquals(ContentType.APPLICATION_EXCEL, ContentTypeLookup.DEFAULT.forExtension(null, ContentType.APPLICATION_EXCEL));
	}


	@Test public void testConstant()
	{
		ContentTypeLookup lookup = ContentTypeLookup.constant(ContentType.APPLICATION_PDF);
		assertEquals(ContentType.APPLICATION_PDF, lookup.forFile("aaa.xyz"));
		assertEquals(ContentType.APPLICATION_PDF, lookup.forFile(null));
		assertEquals(ContentType.APPLICATION_PDF, lookup.forExtension("xyz"));
		assertEquals(ContentType.APPLICATION_PDF, lookup.forExtension(null));
	}
}
