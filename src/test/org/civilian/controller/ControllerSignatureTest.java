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
package org.civilian.controller;


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.resource.PathParam;
import org.civilian.resource.PathParamMap;
import org.civilian.resource.PathParams;


public class ControllerSignatureTest extends CivTest
{
	@Test public void testParse()
	{
		ControllerSignature sig;
		PathParamMap map = new PathParamMap();
		PathParam<String> pp = map.add(PathParams.forSegment("p"));
		
		sig = ControllerSignature.parse(null, map);
		assertNull(sig);
		
		sig = ControllerSignature.parse("c", map);
		assertEquals("c", sig.getClassName());
		assertNull(sig.getMethodSegment());
		
		sig = ControllerSignature.parse("c:m", map);
		assertEquals("c", sig.getClassName());
		assertEquals("m", sig.getMethodSegment());

		sig = ControllerSignature.parse("c:$p", map);
		assertEquals("c", sig.getClassName());
		assertSame(pp, sig.getMethodPathParam());
	}
}
