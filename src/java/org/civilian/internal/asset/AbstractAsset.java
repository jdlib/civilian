package org.civilian.internal.asset;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import org.civilian.Response;
import org.civilian.asset.Asset;
import org.civilian.asset.AssetCacheControl;
import org.civilian.content.ContentType;
import org.civilian.util.HttpHeaders;
import org.civilian.util.IoUtil;


public abstract class AbstractAsset extends Asset
{
	@Override public ContentType getContentType()
	{
		return contentType_;
	}
	
	
	@Override public void setContentType(ContentType contentType)
	{
		contentType_ = contentType;
	}

	
	@Override public String getCharEncoding()
	{
		return charEncoding_;
	}
	
	
	@Override public void setCharEncoding(String encoding)
	{
		charEncoding_ = encoding;
	}

	
	/**
	 * Returns the byte length of the asset data.
	 * @return the length
	 */
	@Override public long length()
	{
		return length_;
	}

	
	protected void setLength(long length)
	{
		length_ = length;
	}

	
	@Override public AssetCacheControl getCacheControl()
	{
		return cacheControl_;
	}

	
	@Override public void setCacheControl(AssetCacheControl value)
	{
		cacheControl_ = value;
	}

	
	@Override
	public void setLastModified(long ms)
	{
		if (ms < 0)
		{
			lastModified_ = -1L;
			lastModifiedHttp_ = null;
		}
		else
		{
			lastModified_ = ms;
			Date date = new Date(ms);
			synchronized(HTTP_DATE_FORMAT)
			{
				lastModifiedHttp_ = HTTP_DATE_FORMAT.format(date);
			}
		}
	}
	
	
	@Override public long getLastModified()
	{
		return lastModified_;
	}

	
	@Override public String getLastModifiedHttp()
	{
		return lastModifiedHttp_;
	}

	
	@Override public byte[] getContent() throws IOException
	{
		try(InputStream in = getInputStream())
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IoUtil.copy(in, out);
			return out.toByteArray(); 
		}
	}
	
	
	@Override protected void writeHeaders(Response response)
	{
		if (contentType_ != null)
			response.setContentType(contentType_);
		if (charEncoding_ != null)
			response.setCharEncoding(charEncoding_);
		if (length_ >= 0)
			response.setContentLength(length_);
		if (lastModifiedHttp_ != null)
			response.getHeaders().set(HttpHeaders.LAST_MODIFIED, lastModifiedHttp_);
		if (cacheControl_ != null)
			cacheControl_.writeHeaders(response, this);
	}

	
	@Override protected void writeContent(Response response) throws IOException
	{
		try(InputStream in = getInputStream())
		{
			IoUtil.copy(in, response.getContentStream());
		}
	}

	
	@Override public Asset cache() throws IOException
	{
		return new CachedAsset(this);
	}

	
	private ContentType contentType_;
	private String charEncoding_;
	private long length_ = -1L;
	private long lastModified_ = -1L;
	private String lastModifiedHttp_;
	private AssetCacheControl cacheControl_; 
}
