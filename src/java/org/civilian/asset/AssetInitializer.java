package org.civilian.asset;


/**
 * A Service which can initialize (or tweak) a newly created Asset.
 */
public interface AssetInitializer
{
	/**
	 * Initialize the asset. 
	 * @return the asset or null if the asset should be dropped
	 */
	public Asset initAsset(Asset asset);
}
