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


import java.nio.charset.Charset;
import java.util.concurrent.ConcurrentHashMap;
import org.civilian.Logs;
import org.civilian.asset.Asset;
import org.civilian.asset.AssetCacheControl;
import org.civilian.asset.AssetInitializer;
import org.civilian.content.ContentTypeLookup;
import org.civilian.resource.Path;
import org.civilian.util.Check;


/**
 * AssetCache maintains a cache for assets.
 */
public class AssetCache extends AssetService
{
	/**
	 * Creates a new AssetCache.
	 * @param implementation provides assets if they are not yet cached.
	 * @param maxMemSize if the size of an Asset is smaller than maxMemSize
	 * 		than its contents will be held in memory. 
	 */
	public AssetCache(AssetService implementation, int maxMemSize)
	{
		implementation_ = Check.notNull(implementation, "implementation");
		maxMemSize_  	= maxMemSize;
	}
	

	/**
	 * Forwards to the implementation.
	 */
	@Override public void init(Path parentPath, Charset defaultEncoding, ContentTypeLookup lookup)
	{
		implementation_.init(parentPath, defaultEncoding, lookup);
	}

	
	/**
	 * Returns the path of the AssetService implementation. 
	 */
	@Override public Path getPath()
	{
		return implementation_.getPath();
	}


	/**
	 * Forwards to the implementation.
	 */
	@Override public boolean hasAssets()
	{
		return implementation_.hasAssets();
	}

	
	/**
	 * Returns the Asset. It looks up the cache and if not found
	 * or invalid, asks the implementation to provide the asset. 
	 */
	@Override public Asset getAsset(Path path) throws Exception
	{
		Asset asset = getCachedAsset(path);
		if (asset == null)
			asset = findAsset(path);
		return asset;
	}
	
	
	private Asset getCachedAsset(Path path)
	{
		String key  = path.toString();
		Asset asset = cache_.get(key);
		if (asset != null)
		{
			if (!asset.isValid())
			{
				cache_.remove(key);
				asset = null; 
			}
			else if (Logs.ASSET.isTraceEnabled())
				Logs.ASSET.trace("{} cached", path);
		}
		return asset;
	}

	
	private Asset findAsset(Path path) throws Exception
	{
		Asset asset = implementation_.getAsset(path);
		if (asset != null)
		{
			if (asset.length() <= maxMemSize_)
				asset = asset.cache();
			Asset oldAsset = cache_.putIfAbsent(path.toString(), asset);
			if (oldAsset != null)
				asset = oldAsset;
		}
		return asset;
	}
	
	
	@Override public void setCacheControl(AssetCacheControl cacheControl)
	{
		implementation_.setCacheControl(cacheControl);
	}

	
	@Override public void setInitializer(AssetInitializer initializer)
	{
		implementation_.setInitializer(initializer);
	}

	
	/**
	 * Returns an info string.
	 */
	@Override public String getInfo()
	{
		return "AssetCache[maxMem=" + maxMemSize_ + "]\n" + implementation_.getInfo();
	}
	
	
	private final int maxMemSize_;
	private final AssetService implementation_;
	private final ConcurrentHashMap<String,Asset> cache_ = new ConcurrentHashMap<>();
}
