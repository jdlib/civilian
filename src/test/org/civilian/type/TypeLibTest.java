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
package org.civilian.type;


import java.util.Calendar;
import java.util.GregorianCalendar;
import org.civilian.CivTest;
import org.junit.Test;


public class TypeLibTest extends CivTest
{
	@Test public void test()
	{
		TypeLib typeLib 	= new TypeLib();
		TypeLib emptyLib 	= new TypeLib(false);
		
		int n = 24;
		assertEquals(n, typeLib.size());
		assertEquals(0, emptyLib.size());
		
		assertSame(TypeLib.INTEGER, typeLib.get(Integer.class));
		assertSame(TypeLib.INTEGER, typeLib.get(int.class));
		assertSame(TypeLib.INTEGER, typeLib.remove(Integer.class));
		assertEquals(--n, typeLib.size());
		assertSame(TypeLib.LONG, typeLib.remove(long.class));
		assertEquals(--n, typeLib.size());
		assertSame(TypeLib.SHORT, typeLib.remove(TypeLib.SHORT));
		assertEquals(--n, typeLib.size());
		assertNull(typeLib.remove(TypeLib.SHORT));
		assertEquals(TypeLib.STRING, typeLib.remove(TypeLib.STRING));
		assertEquals(--n, typeLib.size());
		
		assertNull(typeLib.get(Integer.class));
		
		assertFalse(emptyLib.iterator().hasNext());
	}


	@Test public void testGet()
	{
		TypeLib typeLib = new TypeLib();
		assertSame(TypeLib.INTEGER, typeLib.get(Integer.class)); 
		assertSame(TypeLib.INTEGER, typeLib.get(int.class)); 
		
		assertSame(TypeLib.DATE_CALENDAR, typeLib.get(Calendar.class));
		assertSame(TypeLib.DATE_CALENDAR, typeLib.get(GregorianCalendar.class));
	}
}
