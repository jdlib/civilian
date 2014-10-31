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


import org.junit.Test;
import org.civilian.CivTest;


public class ErrorProcessorTest extends CivTest
{
	@Test public void test() throws Exception
	{
		ErrorProcessor p;
		
		p = new ErrorProcessor(503, "msg", new Error("x"));
		assertEquals("503 x", p.getInfo());

		p = new ErrorProcessor(504, "msg", null);
		assertEquals("504 msg", p.getInfo());
		
		p = new ErrorProcessor(505, null, null);
		assertEquals("505", p.getInfo());
	}
}
