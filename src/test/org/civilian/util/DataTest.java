package org.civilian.util;


import org.civilian.CivTest;
import org.junit.Test;


public class DataTest extends CivTest
{
	@Test public void test()
	{
		Data data = new Data();
		assertEquals(0, data.size());
		assertNull(data.get(DataTest.class));
		
		data.add(this);
		assertEquals(1, data.size());
		assertSame(this, data.get(DataTest.class));

		data.add(this);
		assertEquals(1, data.size());
		
		data.remove(this);
		assertEquals(0, data.size());
	}


	@Test public void testSealed()
	{
		assertThrows(UnsupportedOperationException.class, () -> Data.EMPTY.add(this));
		assertThrows(UnsupportedOperationException.class, () -> Data.EMPTY.remove(this));
		assertThrows(UnsupportedOperationException.class, () -> Data.EMPTY.clear());
	}
}
