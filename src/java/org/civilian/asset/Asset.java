/*
 * Copyright (C) 2014 Civilian Framework.
 *
 * Licensed under the Civilian License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.civilian-framework.org/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.civilian.asset;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.civilian.Response;
import org.civilian.content.ContentType;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;


/**
 * Asset represents a static application resource.
 * A static resource is an image, css file or javascript file, etc.<br>
 */
public abstract class Asset
{
    private static final String MAX_AGE = String.valueOf(30 * 24 * 60 * 60); // 30 days 
    private static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    static 
    {
        TimeZone gmtZone = TimeZone.getTimeZone("GMT");
        HTTP_DATE_FORMAT.setTimeZone(gmtZone);
    }
    

	/**
	 * Returns the content type of the asset or null
	 * if not known or determined.
	 */
	public ContentType getContentType()
	{
		return contentType_;
	}
	
	
	/**
	 * Sets the content type of the asset.
	 */
	public void setContentType(ContentType contentType)
	{
		contentType_ = contentType;
	}

	
	/**
	 * Returns the encoding of the asset, or null if not known
	 */
	public String getEncoding()
	{
		return encoding_;
	}
	
	
	/**
	 * Sets the encoding of the asset.
	 */
	public void setEncoding(String encoding)
	{
		encoding_ = encoding;
	}

	
	/**
	 * Returns the byte length of the asset data.
	 */
	public long length()
	{
		return length_;
	}
	
	
	/**
	 * Sets the byte length of the asset data.
	 */
	public void setLength(long length)
	{
		length_ = length;
	}
	
	
	/**
	 * Sets the last modified date of the asset.
	 * @param ms the date in milliseconds since epoch.
	 * 		Pass a value &lt; 0 for unknown dates.
	 */
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
	
	
	/**
	 * Returns the last modified date of the asset.
	 * @return the date as milliseconds since epoch, or -1 if not known.
	 */
	public long lastModified()
	{
		return lastModified_;
	}

	
	/**
	 * Returns if an cached asset file is still valid, i.e.
	 * its source has not changed since the Asset was created.
	 */
	public abstract boolean isValid();
	
	
	/**
	 * Reads the asset content into memory.
	 */
	public void readContent() throws IOException
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
	protected byte[] getContent()
	{
		return content_;
	}
	
	
	/**
	 * Writes the asset content to the response.
	 */
	public void write(Response response, boolean writeContent) throws IOException
	{
		writeHeaders(response);
		if (writeContent && 
			(length_ != 0) &&
			checkIfModified(response))
		{
			writeContent(response);
		}
	}
	

	/**
	 * Writes a last-modified and max-age header to the response,
	 * if the last modified date is known.
	 */
	protected void writeHeaders(Response response)
	{
		if (lastModifiedHttp_ != null)
		{
			response.getHeaders().set("Last-Modified", lastModifiedHttp_);
			response.getHeaders().set("max-age", MAX_AGE);
		}
	}
 	
	
	/**
	 * Sets content-related headers and writes the asset content
	 * to the response.
	 */
	protected void writeContent(Response response) throws IOException
	{
		response.setStatus(Response.Status.SC200_OK);
		if (contentType_ != null)
			response.setContentType(contentType_);
		if (encoding_ != null)
			response.setContentEncoding(encoding_);
		if (length_ >= 0)
			response.setContentLength(length_);
		
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
	

	private boolean checkIfModified(Response response)
	{
		long modifiedSince = response.getRequest().getHeaders().getDate("If-Modified-Since");
		if (modifiedSince != -1)
		{
			if (lastModified_ < modifiedSince + 1000)
			{
				response.setStatus(Response.Status.NOT_MODIFIED);
				return false;
            }
		}
        return true;
	}


	/**
	 * Returns an InputStream for the asset content.
	 */
	public abstract InputStream getInputStream() throws IOException;
	
	
	/**
	 * Returns a Reader for the asset content.
	 * If no encoding is set on the asset, an IllegalArgumentException
	 * is thrown.
	 */
	public Reader getReader() throws IOException
	{
		Check.notNull(encoding_, "encoding");
		return new BufferedReader(new InputStreamReader(getInputStream(), encoding_));
	}

	
	/**
	 * Returns a debug string for the asset.
	 */
	@Override public abstract String toString();
	

	private byte[] content_;
	private ContentType contentType_;
	private String encoding_;
	private long length_ = -1L;
	private long lastModified_ = -1L;
	private String lastModifiedHttp_;
}
