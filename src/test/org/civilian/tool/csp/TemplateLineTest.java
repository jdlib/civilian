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
package org.civilian.tool.csp;


import org.junit.Test;
import org.civilian.CivTest;


public class TemplateLineTest extends CivTest
{
	@Test public void test()
	{
		TemplateLine line = new TemplateLine();
		
		assertFalse(line.parse("\t x"));
		assertEquals("template indent may not contain a mix of tab and space chars", line.error);

		assertFalse(line.parse(" \tx"));
		assertEquals("template indent may not contain a mix of tab and space chars: line uses a space indent character, but previous lines used tab characters", line.error);
		
		assertTrue(line.parse("@@test"));
		assertEquals(TemplateLine.Type.LITERAL, line.type);
	}
}
