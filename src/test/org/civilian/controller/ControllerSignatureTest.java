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
import org.civilian.resource.Resource;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.resource.pathparam.PathParams;
import org.civilian.testcase1.AlphaController;


public class ControllerSignatureTest extends CivTest
{
	@Test public void testParse()
	{
		ControllerSignature sig;
		PathParam<String> pp = PathParams.forSegment("p");
		PathParamMap map = new PathParamMap(pp);
		
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
	
	
	@Test public void testTouch() throws Exception
	{
		Resource root = new Resource();
		root.setData(new ControllerSignature(AlphaController.class));
		
		new Resource(root, "a");
		
		ControllerSignature.touchControllerClasses(root);
	}
}
