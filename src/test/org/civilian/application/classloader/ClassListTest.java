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
package org.civilian.controller.classloader;


import org.junit.Test;
import org.civilian.CivTest;


public class ClassListTest extends CivTest
{
	@Test public void test()
	{
		ClassList list = new ClassList();
		assertEquals(0, list.size());
		assertFalse(list.contains("java.lang.String"));
		
		list.add("java.lang.", "java.util.List", "java.util.List");
		assertEquals(2, list.size());
		assertTrue(list.contains("java.lang.String"));
		assertTrue(list.contains("java.util.List"));
		assertFalse(list.contains("java.util.ArrayList"));
		assertFalse(list.contains(null));
		
		list.addClass(java.util.ArrayList.class);
		list.addPackage(java.lang.String.class);
		assertEquals(3, list.size());
		assertTrue(list.contains("java.util.ArrayList"));
	}
}
