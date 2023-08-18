package org.civilian.asset;


import org.civilian.response.Response;
import org.civilian.util.http.HeaderNames;


public interface AssetCacheControl
{
    public static final String DEFAULT_MAX_AGE = "max-age=" + (30 * 24 * 60 * 60); // 30 days
    

    public static final AssetCacheControl DEFAULT = (response, asset) -> 
    {
    	response.getHeaders().set(HeaderNames.CACHE_CONTROL, DEFAULT_MAX_AGE);
	};
	
	
    public static final AssetCacheControl NO_CACHE = (response, asset) -> 
    {
    	response.getHeaders().set(HeaderNames.CACHE_CONTROL, "no-cache");
	};

	
	public void writeHeaders(Response response, Asset asset);
}


