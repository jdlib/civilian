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


import java.util.Collection;
import java.util.HashMap;
import org.civilian.content.ContentTypeLookup;
import org.civilian.resource.Path;
import org.civilian.server.Server;
import org.civilian.util.Check;


/**
 * AssetConfig is used to configure which and how an application
 * serves assets.
 */
public class AssetConfig
{
	/**
	 * The default value for the maximum size of assets which 
	 * will be cached in memory.
	 */
	public static final int DEFAULT_MAX_CACHE_SIZE = 1024*1024;
	
	/**
	 * @return the maximum size of files which are cached in memory. The default size is 1MB.  
	 */
	public int getMaxCachedSize()
	{
		return maxCachedSize_;
	}
	
	
	/**
	 * Sets the maximum size of asset files which are 
	 * cached in memory.
	 * @param maxCachedSize the size
	 */
	public void setMaxCachedSize(int maxCachedSize)
	{
		Check.greaterEquals(maxCachedSize, 1, "maxCachedSize");
		maxCachedSize_ = maxCachedSize;
	}

	
	/**
	 * Clears the AssetLocation list.
	 */
	public void clearLocations()
	{
		locations_.clear();
	}
	
	
	/**
	 * @return the number of AssetLocations.
	 */
	public int getLocationCount()
	{
		return locations_.size();
	}
	
	
	/**
	 * Adds a AssetLocation to the location list.
	 * @param location a location
	 */
	public void addLocation(AssetLocation location)
	{
		Check.notNull(location, "location");
		AssetLocation old = locations_.put(location.getRelativePath().toString(), location);
		if (old != null)
			throw new IllegalStateException("location '" + location + "' and '" + old + "' are both registered for path '" + location.getRelativePath() + "'");
	}
	
	
	/**
	 * Adds a collection of AssetLocations to the location list.
	 * @param locations some locations
	 */
	public void addLocations(Collection<AssetLocation> locations)
	{
		Check.notNull(locations, "locations");
		for (AssetLocation location : locations)
			addLocation(location);
	}

	
	/**
	 * @param path a path
	 * @return the AssetLocation for a relative location path.
	 */
	public AssetLocation getLocation(String path)
	{
		path = Path.norm(path);
		return locations_.get(path);
	}


	/**
	 * @return the AssetLocation.
	 */
	public AssetLocation[] getLocations()
	{
		AssetLocation[] locations = new AssetLocation[getLocationCount()];
		locations_.values().toArray(locations);
		return locations;
	}

	
	/**
	 * Returns the lookup service which should be used to derive
	 * content-types from asset file names. By default the ContentLookup of the 
	 * {@link Server#getContentTypeLookup() server} is used. 
	 * @return the lookup
	 */
	public ContentTypeLookup getContentTypeLookup()
	{
		return contentTypeLookup_;
	}
	
	
	/**
	 * Sets the lookup which should be used to derive
	 * content-types from asset file names.
	 * @param lookup the lookup
	 */
	public void setContentTypeLookup(ContentTypeLookup lookup)
	{
		contentTypeLookup_ = Check.notNull(lookup, "lookup");
	}
	
	
	private ContentTypeLookup contentTypeLookup_ = ContentTypeLookup.DEFAULT;
	private int maxCachedSize_ = DEFAULT_MAX_CACHE_SIZE;
	private final HashMap<String,AssetLocation> locations_ = new HashMap<>();
}
