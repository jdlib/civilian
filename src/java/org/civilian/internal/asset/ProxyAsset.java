package org.civilian.internal.asset;


import java.io.IOException;
import java.io.InputStream;
import org.civilian.Response;
import org.civilian.asset.Asset;
import org.civilian.asset.AssetCacheControl;
import org.civilian.content.ContentType;
import org.civilian.util.Check;


/**
 * An asset implementation which by default forwards all method calls to another asset.
 */
public abstract class ProxyAsset extends Asset
{
	public ProxyAsset(Asset asset)
	{
		asset_ = Check.notNull(asset, "asset");
	}


	@Override public ContentType getContentType()
	{
		return asset_.getContentType();
	}


	@Override public void setContentType(ContentType value)
	{
		asset_.setContentType(value);
	}


	@Override public String getCharEncoding()
	{
		return asset_.getCharEncoding();
	}


	@Override public void setCharEncoding(String value)
	{
		asset_.setCharEncoding(value);
	}


	@Override public long length()
	{
		return asset_.length();
	}


	@Override public AssetCacheControl getCacheControl()
	{
		return asset_.getCacheControl();
	}


	@Override public void setCacheControl(AssetCacheControl value)
	{
		asset_.setCacheControl(value);
	}


	@Override public void setLastModified(long ms)
	{
		asset_.setLastModified(ms);
	}


	@Override public long getLastModified()
	{
		return asset_.getLastModified();
	}


	@Override public String getLastModifiedHttp()
	{
		return asset_.getLastModifiedHttp();
	}


	@Override public byte[] getContent() throws IOException
	{
		return asset_.getContent();
	}


	@Override public boolean isValid()
	{
		return asset_.isValid();
	}


	@Override protected void writeHeaders(Response response)
	{
		writeHeaders(asset_, response);
	}


	@Override protected void writeContent(Response response) throws IOException
	{
		writeContent(asset_, response);
	}


	@Override public InputStream getInputStream() throws IOException
	{
		return asset_.getInputStream();
	}


	@Override public String toString()
	{
		return getClass().getSimpleName() + ':' + asset_.toString();
	}
	
	
	protected final Asset asset_;
}
