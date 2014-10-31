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
package org.civilian.resource;


import java.util.Iterator;
import org.civilian.CivTest;
import org.junit.Test;


public class PathParamMapTest extends CivTest
{
	@SuppressWarnings("unused")
	public static class Params
	{
		public static final PathParamMap MAP = new PathParamMap(Params.class);
		public static final PathParam<String> ID = MAP.addAndSeal(PathParams.forSegment("id"));
		
		public final Object X = new Object();
		private static final Object Y = new Object();
		private static final PathParam<String> IDCOPY = ID;
	}
	
	
	@Test public void testAccess()
	{
		Iterator<PathParam<?>> it = Params.MAP.iterator();
		
		try
		{
			it.remove();
			fail();
		}
		catch(UnsupportedOperationException e)
		{
		}
		
		assertSame(Params.ID, it.next());
		assertFalse(it.hasNext());
		
		assertSame(Params.ID, Params.MAP.get("id"));
		
		assertEquals(1, Params.MAP.toMap().size());
	}


	@Test public void testSealed()
	{
		assertTrue(Params.MAP.isSealed());
		try
		{
			Params.MAP.add(PathParams.forSegment("id2"));
		}
		catch(IllegalStateException e)
		{
		}
		
		// does not have an effect
		Params.MAP.seal();
	}


	@Test public void testConstants() throws Exception
	{
		assertNull(PathParamMap.EMPTY.getConstantsClass());
		assertNull(PathParamMap.EMPTY.getConstantField(Params.ID));

		assertEquals(Params.class, Params.MAP.getConstantsClass());
		assertEquals(Params.class.getField("ID"), Params.MAP.getConstantField(Params.ID));
		assertEquals(Params.class.getName() + ".ID", Params.MAP.getConstant(Params.ID));
		
		try
		{
			Params.MAP.getConstant(PathParams.forSegment("x"));
		}
		catch(IllegalArgumentException e)
		{
		}
		
	}
}
