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


import org.civilian.Logs;
import org.civilian.content.ContentTypeLookup;
import org.civilian.resource.Path;
import org.civilian.resource.PathProvider;
import org.slf4j.Logger;


/**
 * AssetService is able to return an asset for a request path.
 * AssertService implementations can be created via the factory {@link AssetServices}. 
 */
public abstract class AssetService implements PathProvider
{
	protected static final Logger log = Logs.ASSET;

	
	/**
	 * Equips the AssetService with context information.
	 * @param parentPath the path of a parent resource. When an AssetService 
	 * 		is created only its relative path will be initialized
	 * 		(and its path set to the relative path). After initialization
	 * 		of an application is done, all configured AssetServices will
	 * 		be initialized.
	 * @param defaultEncoding the default encoding which should be
	 * 		used by the AssetService if it has no own encoding. 
	 * @param lookup can be used by the AssetService to determine the content-type of 
	 * 		an asset
	 * @see Asset#setContentType(org.civilian.content.ContentType)
	 */
	public abstract void init(Path parentPath, String defaultEncoding, ContentTypeLookup lookup);

	
	/**
	 * Returns an asset or null if not found.
	 */
	public Asset getAsset(String assetPath) throws Exception
	{
		return getAsset(new Path(assetPath));
	}
	
	
	/**
	 * Returns an asset for a given path.
	 * @param path the asset path
	 * @return the asset or null if not found
	 */
	public abstract Asset getAsset(Path path) throws Exception;


	/**
	 * Returns an info string describing the AssetService.
	 * @return the info string
	 */
	public abstract String getInfo();


	/**
	 * Returns if the AssetService can serve assets. If an application
	 * does not define any AssetServices, it still will have an AssetService 
	 * which just can't serve any assets.
	 */
	public abstract boolean hasAssets();
	
	
	/**
	 * Instructs the AssertService to use the given AssetCacheControlfor its assets.
	 * @param cacheControl the cacheControl
	 */
	public abstract void setCacheControl(AssetCacheControl cacheControl);
	
	
	/**
	 * Instructs the AssertService to use the given AssetInitializer to initialize its assets.
	 * @param initializer the initializer
	 */
	public abstract void setInitializer(AssetInitializer initializer);

	
	/**
	 * Returns the asset path as string.
	 */
	@Override public final String toString()
	{
		return getPath().toString();
	}
}
