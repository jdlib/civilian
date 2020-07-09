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


import org.civilian.CivTest;
import org.junit.Test;


public class TypeLibraryTest extends CivTest
{
	@Test public void test()
	{
		TypeLib defaultLibrary = new TypeLib();
		TypeLib emptyLibrary = new TypeLib(false);
		
		assertEquals(16, defaultLibrary.size());
		assertEquals(0, emptyLibrary.size());
		
		assertSame(TypeLib.INTEGER, defaultLibrary.get(Integer.class));
		assertSame(TypeLib.INTEGER, defaultLibrary.get(int.class));
		assertSame(TypeLib.INTEGER, defaultLibrary.remove(Integer.class));
		assertEquals(15, defaultLibrary.size());
		assertSame(TypeLib.LONG, defaultLibrary.remove(long.class));
		assertEquals(14, defaultLibrary.size());
		assertSame(TypeLib.SHORT, defaultLibrary.remove(TypeLib.SHORT));
		assertEquals(13, defaultLibrary.size());
		assertNull(defaultLibrary.remove(TypeLib.SHORT));
		assertEquals(TypeLib.STRING, defaultLibrary.remove(TypeLib.STRING));
		assertEquals(12, defaultLibrary.size());
		
		assertNull(defaultLibrary.get(Integer.class));
		
		assertFalse(emptyLibrary.iterator().hasNext());
	}
}
