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
package org.civilian.asset;


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.internal.asset.CombinedAssetService;
import org.civilian.resource.Path;


public class CombinedAssetServiceTest extends CivTest
{
	@Test public void test() throws Exception
	{
		TestLocation loc1 = new TestLocation("loc1", "test.css");
		loc1.setContentType(ContentType.TEXT_CSS);

		TestLocation loc2 = new TestLocation("loc2", "test.js");
		loc2.setContentType(ContentType.APPLICATION_JAVASCRIPT);

		CombinedAssetService service = new CombinedAssetService(new Path("myassets"), loc1, loc2);
		assertEquals("/myassets", service.getPath().toString()); 
		assertEquals("/myassets", service.getRelativePath().toString());
		assertEquals("/myassets", service.toString());
		assertNull(service.getAsset(new Path("/x")));
		assertNull(service.getAsset(new Path("/myassets")));
		
		Asset asset = service.getAsset(new Path("/loc1/test.css"));
		assertNotNull(asset);
		assertTrue(asset instanceof TestAsset);
		
		asset = service.getAsset(new Path("/loc2/xyy"));
		assertNull(asset);

		asset = service.getAsset(new Path("/loc2/test.js"));
		assertNotNull(asset);
		assertTrue(asset instanceof TestAsset);
		
		assertEquals("/loc1 -> Test\n/loc2 -> Test", service.getInfo());
	}
}
