package org.civilian.asset;


import org.civilian.resource.Path;


/**
 * A Service which can initialize (or tweak) a newly created Asset.
 */
public interface AssetInitializer
{
	/**
	 * Initialize the asset.
	 * @param assetPath the asset path
	 * @param asset the asset 
	 * @return the asset or null if the asset should be dropped
	 */
	public Asset initAsset(Path assetPath, Asset asset);
}
