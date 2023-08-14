/*
 * Copyright (C) 2016 Civilian Framework.
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
package org.civilian.text.type;


import org.civilian.CivTest;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.type.TypeMap;
import org.junit.Test;


public class TypeMapTest extends CivTest
{
	@Test public void test()
	{
		TypeMap map = new TypeMap();
		
		assertNull(map.get(TypeLib.STRING));
		
		map.use("default").byDefault();
		assertSame("default", map.get(TypeLib.STRING));
		assertSame("default", map.get(TypeLib.DATE_JAVA_UTIL));
		
		map.use("date").on(Type.Category.DATE);
		assertSame("default", map.get(TypeLib.STRING));
		assertSame("date", map.get(TypeLib.DATE_JAVA_UTIL));
		
		map.use("string").on(TypeLib.STRING);
		assertSame("string", map.get(TypeLib.STRING));
		assertSame("date", map.get(TypeLib.DATE_JAVA_UTIL));
		
		map.use("utildate").on(TypeLib.DATE_JAVA_UTIL);
		assertSame("string", map.get(TypeLib.STRING));
		assertSame("utildate", map.get(TypeLib.DATE_JAVA_UTIL));
		
		map.use(null).on(TypeLib.DATE_JAVA_UTIL);
		assertSame("date", map.get(TypeLib.DATE_JAVA_UTIL));

		map.use(null).on(Type.Category.DATE);
		assertSame("default", map.get(TypeLib.DATE_JAVA_UTIL));

		map.use(null).byDefault();
		assertSame(null, map.get(TypeLib.DATE_JAVA_UTIL));
	}
}
