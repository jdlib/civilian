package org.civilian.asset;


import java.io.IOException;
import org.civilian.response.Response;
import org.civilian.util.Check;
import org.civilian.util.http.HeaderNames;


/**
 * A ProxyAsset which adds a Content-Encoding header.
 * Use case: wrap an existing asset which is already compressed and
 * add the appropriate content encoding header.
 */
public class ContentEncodedAsset extends ProxyAsset
{
	public ContentEncodedAsset(Asset asset, String contentEncoding)
	{
		super(asset);
		contentEncoding_ = Check.notNull(contentEncoding, "contentEncoding");
	}
	

	@Override protected void writeHeaders(Response response)
	{
		super.writeHeaders(response);
		response.getHeaders().set(HeaderNames.CONTENT_ENCODING, contentEncoding_);
	}
	
	
	@Override public Asset cache() throws IOException
	{
		return new ContentEncodedAsset(asset_.cache(), contentEncoding_); 
	}
	
	
	private final String contentEncoding_;
}
