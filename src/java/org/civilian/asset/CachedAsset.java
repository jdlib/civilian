package org.civilian.internal.asset;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.civilian.asset.Asset;
import org.civilian.response.Response;
import org.civilian.util.IoUtil;


public class CachedAsset extends ProxyAsset
{
	public CachedAsset(Asset asset) throws IOException
	{
		super(asset);
		try(InputStream in = asset.getInputStream())
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IoUtil.copy(in, out);
			bytes_ = out.toByteArray();
		}
	}
	
	
	@Override public long length()
	{
		return bytes_.length;
	}
	
	
	@Override public byte[] getContent()
	{
		return bytes_;
	}

	
	@Override public InputStream getInputStream()
	{
		return new ByteArrayInputStream(bytes_);
	}
	

	@Override protected void writeContent(Response response) throws IOException
	{
		response.getContentStream().write(bytes_);
	}

	
	@Override public Asset cache() throws IOException
	{
		return this;
	}


	private final byte[] bytes_;
}
