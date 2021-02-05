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


import org.civilian.CivTest;
import org.civilian.internal.asset.AssetCache;
import org.civilian.internal.asset.CachedAsset;
import org.junit.Test;


public class AssetCacheTest extends CivTest
{
	@Test public void test() throws Exception
	{
		TestAsset asset = new TestAsset("hello");
		TestLocation location = new TestLocation("/test", "/some.css", asset);
		
		AssetCache cache = new AssetCache(location, 5 /*max mem size*/);
		assertEquals("/test", cache.getPath().toString());
		
		// start: asset not loaded and retrieved
		assertEquals(0, location.findCalled());
		
		// first access: asset is put into cache and its content is loaded (since <= cachesize)
		Asset cached = cache.getAsset("/test/some.css");
		assertTrue(cached instanceof CachedAsset);
		assertEquals(1, location.findCalled());

		// second access: asset is take from the cache
		assertSame(cached, cache.getAsset("/test/some.css"));
		assertEquals(1, location.findCalled());
		
		// invalidate asset and prepare a new asset
		asset.isValid = false;
		TestAsset newAsset = new TestAsset("hello world");
		location.setAsset(newAsset);
		
		// second access: cached asset is discarded, new asset retrieved, but not loaded
		// since it is bigger than the chachesize
		assertSame(newAsset, cache.getAsset("/test/some.css"));
		assertEquals(2, location.findCalled());

		// test unknown asset access
		assertNull(cache.getAsset("/test/xxxsome.css"));
	}
}
