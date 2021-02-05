package org.civilian.internal.asset;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.civilian.Response;
import org.civilian.asset.Asset;
import org.civilian.asset.AssetCacheControl;
import org.civilian.content.ContentType;
import org.civilian.util.Check;
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

	
	@Override public String getCompression()
	{
		return compression_;
	}

	
	@Override public void setCompression(String compression)
	{
		compression_ = compression;
	}

	
	/**
	 * Returns the byte length of the asset data.
	 * @return the length
	 */
	@Override public long length()
	{
		return length_;
	}

	
	@Override public void setLength(long length)
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

	
	/**
	 * Reads the asset content into memory.
	 */
	@Override public void readContent() throws IOException
	{
		if (content_ == null)
		{
			try(InputStream in = getInputStream())
			{
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				IoUtil.copy(in, out);
				setContent(out.toByteArray()); 
			}
		}
	}

	
	/**
	 * Sets the asset content.
	 */
	public void setContent(byte[] content)
	{
		content_ = Check.notNull(content, "content");
		setLength(content.length);
	}
	
	
	/**
	 * Returns the asset content, or null if not
	 * yet read into memory. 
	 */
	@Override public byte[] getContent()
	{
		return content_;
	}
	
	
	@Override public void write(Response response, boolean writeContent) throws IOException
	{
		response.setStatus(Response.Status.SC200_OK);
		writeHeaders(response);
		if (writeContent && (length_ > 0) && checkIfModified(response))
			writeContent(response);
	}

	
	@Override protected void writeHeaders(Response response)
	{
		if (contentType_ != null)
			response.setContentType(contentType_);
		if (charEncoding_ != null)
			response.setCharEncoding(charEncoding_);
		if (compression_ != null)
			response.getHeaders().set(HttpHeaders.CONTENT_ENCODING, compression_);
		if (length_ >= 0)
			response.setContentLength(length_);
		if (lastModifiedHttp_ != null)
			response.getHeaders().set(HttpHeaders.LAST_MODIFIED, lastModifiedHttp_);
		if (cacheControl_ != null)
			cacheControl_.writeHeaders(response, this);
	}

	
	@Override protected void writeContent(Response response) throws IOException
	{
		OutputStream out = response.getContentStream();
		if (content_ != null)
			out.write(content_);
		else
		{
			try(InputStream in = getInputStream())
			{
				IoUtil.copy(in, out);
			}
		}
	}

	
	private byte[] content_;
	private ContentType contentType_;
	private String charEncoding_;
	private long length_ = -1L;
	private long lastModified_ = -1L;
	private String lastModifiedHttp_;
	private String compression_;
	private AssetCacheControl cacheControl_; 
}
