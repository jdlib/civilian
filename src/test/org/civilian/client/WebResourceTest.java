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
package org.civilian.client;


import org.civilian.CivTest;
import org.junit.Test;


public class WebResourceTest extends CivTest
{
	@Test public void test()
	{
		WebResource root = new Test1WebRoot("http://acme.org/app");
		assertEquals("http://acme.org/app", root.toString());
		assertTrue(root.isRoot());
		assertNull(root.getParent());
		assertNull(root.getSegment());
		assertNull(root.getPathParam());
		assertEquals(4, root.getChildCount());
	}
}
