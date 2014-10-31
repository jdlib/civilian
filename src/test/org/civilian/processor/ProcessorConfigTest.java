package org.civilian.processor;


import java.util.NoSuchElementException;
import org.civilian.CivTest;
import org.junit.Test;


public class ProcessorConfigTest extends CivTest
{
	@Test public void test()
	{
		ProcessorConfig config = new ProcessorConfig();
		
		IpFilter ipFilter = new IpFilter();
		TestProcessor t1 = new TestProcessor();
		TestProcessor t2 = new TestProcessor();
		
		try
		{
			config.addBefore(ResourceDispatch.class, ipFilter);
			fail();
		}
		catch(NoSuchElementException e)
		{
		}
		
		try
		{
			config.addAfter(ResourceDispatch.class, ipFilter);
			fail();
		}
		catch(NoSuchElementException e)
		{
		}
		
		assertEquals(-1, config.getList().indexOf(ipFilter));
		config.addLast(ipFilter);
		assertEquals(0, config.getList().indexOf(ipFilter));
		
		config.addBefore(IpFilter.class, t1);
		config.addAfter(IpFilter.class, t2);
		assertEquals(0, config.getList().indexOf(t1));
		assertEquals(1, config.getList().indexOf(ipFilter));
		assertEquals(2, config.getList().indexOf(t2));
	}
}
