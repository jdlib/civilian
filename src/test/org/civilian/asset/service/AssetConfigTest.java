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
package org.civilian.asset.service;


import org.civilian.CivTest;
import org.junit.Test;


public class AssetConfigTest extends CivTest
{
	@Test public void testAssetConfig()
	{
		AssetConfig config = new AssetConfig();
		
		assertEquals(AssetConfig.DEFAULT_MAX_CACHE_SIZE, config.getMaxCachedSize());
		config.setMaxCachedSize(15);
		assertEquals(15, config.getMaxCachedSize());
		
		assertNull(config.getLocation("nowhere"));
		assertEquals(0, config.getLocationCount());
		
		AssetLocation x = AssetServices.getCivResourceLocation("x", null, false); 
		config.addLocation(x);
		assertEquals(1, config.getLocationCount());
		assertSame(x, config.getLocation("x"));
		AssetLocation[] locs = config.getLocations();
		assertEquals(1, locs.length);
		assertSame(x, locs[0]);
		config.clearLocations();
		assertEquals(0, config.getLocationCount());
	}
}
