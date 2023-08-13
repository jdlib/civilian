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
package org.civilian.internal.asset;


import org.civilian.asset.Asset;
import org.civilian.asset.AssetCacheControl;
import org.civilian.asset.AssetInitializer;
import org.civilian.asset.AssetService;
import org.civilian.content.ContentTypeLookup;
import org.civilian.resource.Path;


/**
 * CombinedAssetService is an AssetService which forwards
 * requests to a list of AssetServices.
 * It can have an not-empty relative path, which is then effectively
 * prepended to the paths of its services.
 */
public class CombinedAssetService extends AssetService
{
	public CombinedAssetService(Path relativePath, AssetService... children)
	{
		path_ 		= relativePath_ = relativePath;
		children_ 	= children;
	}
	
	
	/**
	 * Makes the own path absolute and then forwards to the children.
	 */
	@Override public void init(Path parentPath, String defaultEncoding, ContentTypeLookup lookup)
	{
		// make own path absolute
		path_ = parentPath.add(relativePath_); 
		
		// forward to the children, using our own absolute path as parent path
		for (AssetService provider : children_)
			provider.init(path_, defaultEncoding, lookup);
	}
	
	
	/**
	 * Returns the absolute path from the server root to the asset root.
	 */
	@Override public Path getPath()
	{
		return path_;
	}
	

	/**
	 * Returns the relative path of the asset root below the application path. 
	 */
	@Override public Path getRelativePath()
	{
		return relativePath_;
	}	
	
	
	/**
	 * Returns true, if the number of children is &gt; 0.
	 */
	@Override public boolean hasAssets()
	{
		return children_.length > 0;
	}
	
	
	/**
	 * Asks all child services to get the asset and returns
	 * the first hit. 
	 * @param path the path of the asset relative to asset root.
	 */
	@Override public Asset getAsset(Path path) throws Exception
	{
		for (AssetService child : children_)
		{
			Asset asset = child.getAsset(path);
			if (asset != null)
				return asset;
		}
		return null;
	}
	
	
	@Override public void setCacheControl(AssetCacheControl cacheControl)
	{
		for (AssetService child : children_)
			child.setCacheControl(cacheControl);
	}

	
	@Override public void setInitializer(AssetInitializer initializer)
	{
		for (AssetService child : children_)
			child.setInitializer(initializer);
	}

	
	/**
	 * Returns an info string.
	 */
	@Override public String getInfo()
	{
		StringBuilder info = new StringBuilder();
		for (int i=0; i<children_.length; i++)
		{
			if (i > 0)
				info.append('\n');
			info.append(children_[i].getInfo());
		}
		return info.toString();
	}
	

	private Path path_;
	private Path relativePath_;
	private AssetService[] children_;
}
