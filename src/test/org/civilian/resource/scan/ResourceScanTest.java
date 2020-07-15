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
package org.civilian.resource.scan;


import java.util.Iterator;
import java.util.NoSuchElementException;
import org.civilian.CivTest;
import org.civilian.Resource;
import org.civilian.controller.ControllerNaming;
import org.civilian.testcase1.Test1PathParams;
import org.junit.Test;


public class ResourceScanTest extends CivTest
{
	@Test public void test() throws Exception
	{
		ResourceScan scan = new ResourceScan("org.civilian.testcase1", new ControllerNaming(), Test1PathParams.MAP, null, false);
		Resource resource = scan.getRootResource();
		// resource.print(System.out);
		
		Iterator<Resource> it = resource.iterator();
		assertNext(it, "/", 					"org.civilian.testcase1.IndexController");
		assertNext(it, "/alpha", 				"org.civilian.testcase1.AlphaController");
		assertNext(it, "/beta",					"org.civilian.testcase1.beta.IndexController");
		assertNext(it, "/beta/imspecial.txt",	"org.civilian.testcase1.beta.PathController");
		assertNext(it, "/beta/some",			"org.civilian.testcase1.beta.SomeController");
		assertNext(it, "/beta/some/thing",		"org.civilian.testcase1.beta.SomeController:thing");
		assertNext(it, "/beta/{beta}", 			"org.civilian.testcase1.beta.object.IndexController");
		assertNext(it, "/mixed", 				"org.civilian.testcase1.miXed.IndexController");
		assertNext(it, "/mixed/something",		"org.civilian.testcase1.miXed.someThingController");
		assertNext(it, "/{gammaId}", 			"org.civilian.testcase1.GammaController");
		assertFalse(it.hasNext());
		try
		{
			it.next();
			fail();
		}
		catch(NoSuchElementException e)
		{
		}
	}
	
	
	private void assertNext(Iterator<Resource> it, String route, String ctrlSig)
	{
		assertTrue(it.hasNext());
		Resource r = it.next();
		assertEquals(route, 	r.getRoute().toString()); 
		assertEquals(ctrlSig, 	r.getControllerSignature()); 
	}
}
