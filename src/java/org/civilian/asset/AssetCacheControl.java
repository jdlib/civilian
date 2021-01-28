package org.civilian.asset;


import org.civilian.Response;


public interface AssetCacheControl
{
    public static final String DEFAULT_MAX_AGE = "max-age=" + (30 * 24 * 60 * 60); // 30 days
    

    public static final AssetCacheControl DEFAULT = (response, asset) -> 
    {
    	response.getHeaders().set("Cache-Control", DEFAULT_MAX_AGE);
	};
	
	
	public void writeHeaders(Response response, Asset asset);
}


