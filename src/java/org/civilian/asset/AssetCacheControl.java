package org.civilian.asset;


import org.civilian.Response;
import org.civilian.util.HttpHeaders;


public interface AssetCacheControl
{
    public static final String DEFAULT_MAX_AGE = "max-age=" + (30 * 24 * 60 * 60); // 30 days
    

    public static final AssetCacheControl DEFAULT = (response, asset) -> 
    {
    	response.getHeaders().set(HttpHeaders.CACHE_CONTROL, DEFAULT_MAX_AGE);
	};
	
	
    public static final AssetCacheControl NO_CACHE = (response, asset) -> 
    {
    	response.getHeaders().set(HttpHeaders.CACHE_CONTROL, "no-cache");
	};

	
	public void writeHeaders(Response response, Asset asset);
}


