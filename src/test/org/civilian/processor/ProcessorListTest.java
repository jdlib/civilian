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
package org.civilian.processor;


import java.util.ArrayList;
import java.util.Iterator;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.Request;


public class ProcessorListTest extends CivTest
{
	@Test public void test() throws Exception
	{
		TestProcessor p = new TestProcessor(); 
		
		ProcessorList plist = new ProcessorList();
		assertEquals(0, plist.size());
		
		ArrayList<Processor> list  = new ArrayList<>();
		list.add(p);
		
		plist = new ProcessorList(list);
		assertEquals("size 1", plist.getInfo());
		assertEquals(1, plist.size());
		assertSame(p, plist.get(0));
		
		Iterator<Processor> it = plist.iterator();
		assertSame(p, it.next());
		assertFalse(it.hasNext());
		
		// process
		Request request = mock(Request.class);
		
		// process: success false
		p.result = false;
		assertFalse(plist.process(request));

		// process: success true
		p.result = true;
		assertTrue(plist.process(request));
		assertEquals(2, p.called);

		// process: success true
		p.error = new IllegalArgumentException(); 
		try
		{
			plist.process(request);
			fail();
		}
		catch(Exception e)
		{
			assertSame(p.error, e);
			assertEquals(3, p.called);
		}
		
		// close
		assertFalse(p.closed);
		plist.close();
		assertTrue(p.closed);
	}
	
	
	private static class TestProcessor extends Processor
	{
		@Override public String getInfo() 
		{
			return getClass().getSimpleName();
		}

		
		@Override public boolean process(Request request, ProcessorChain chain) throws Exception
		{
			called++;
			if (error != null)
				throw error;
			return result;
		}
		
		
		@Override public void close()
		{
			closed = true;
		}

		
		public boolean result; 
		public boolean closed; 
		public int called; 
		public Exception error; 
	}
}
