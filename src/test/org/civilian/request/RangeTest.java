package org.civilian.request;


import org.civilian.CivTest;
import org.civilian.request.Range.Part;
import org.junit.Test;


public class RangeTest extends CivTest
{
	@Test public void testErrors()
	{
		assertRangeNull("");
		assertRangeNull("a");
		assertRangeNull("bytes");
		assertRangeNull("bytes=");
		assertRangeNull("bytes=1");
		assertRangeNull("bytes=1;");
	}
	
	
	@Test public void testOk()
	{
		assertRange("bytes=0-", 0, -1);
		assertRange("bytes=-500", -1, 500);
		assertRange("bytes=200-500", 200, 500);
		assertRange("bytes=200-100", 200, 100);
		assertRange("bytes=0-0, -1", 0, 0, -1, 1);
	}
	
	
	private void assertRangeNull(String rangeHeader)
	{
		Range range = Range.parse(rangeHeader);
		assertNull(range);
	}


	private void assertRange(String rangeHeader, long... startEnds)
	{
		Range range = Range.parse(rangeHeader);
		assertNotNull(range);
		assertEquals(range.size() * 2, startEnds.length);
		
		int n = 0;
		for (Part part : range)
		{
			assertEquals(startEnds[n++], part.start);
			assertEquals(startEnds[n++], part.end);
		}
	}
}
