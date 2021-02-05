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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import org.civilian.Response;
import org.civilian.content.ContentType;
import org.civilian.util.Check;
import org.civilian.util.HttpHeaders;


/**
 * Asset represents a static application resource.
 * A static resource is an image, css file or javascript file, etc.
 */
public abstract class Asset
{
    protected static final SimpleDateFormat HTTP_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
    static 
    {
        TimeZone gmtZone = TimeZone.getTimeZone("GMT");
        HTTP_DATE_FORMAT.setTimeZone(gmtZone);
    }
    

	/**
	 * Returns the content type of the asset or null
	 * if not known or determined.
	 */
	public abstract ContentType getContentType();
	
	
	/**
	 * Sets the content type of the asset.
	 */
	public abstract void setContentType(ContentType contentType);

	
	/**
	 * Returns the character encoding of the asset, or null if not known
	 * @return the encoding
	 */
	public abstract String getCharEncoding();
	
	
	/**
	 * Sets the encoding of the asset.
	 * @param encoding encoding
	 */
	public abstract void setCharEncoding(String encoding);

	
	/**
	 * Returns the compression applied to the asset content or null.
	 * @return the compression
	 */
	public abstract String getCompression();
	
	
	/**
	 * Sets the compression applied to the asset content.
	 * @param compression the compression
	 */
	public abstract void setCompression(String compression);

	
	/**
	 * Returns the byte length of the asset data.
	 * @return the length
	 */
	public abstract long length();
	
	
	/**
	 * Sets the byte length of the asset data.
	 * @param length the length
	 */
	public abstract void setLength(long length);
	
	
	/**
	 * Returns the cache control of the Asset.
	 */
	public abstract AssetCacheControl getCacheControl();
	
	
	/**
	 * Sets the content type of the asset.
	 * @param value the value
	 */
	public abstract void setCacheControl(AssetCacheControl value);

	
	/**
	 * Sets the last modified date of the asset.
	 * @param ms the date in milliseconds since epoch.
	 * 		Pass a value &lt; 0 for unknown dates.
	 */
	public abstract void setLastModified(long ms);
	
	
	/**
	 * Returns the last modified date of the asset.
	 * @return the date as milliseconds since epoch, or -1 if not known.
	 */
	public abstract long getLastModified();

	
	/**
	 * Returns the last modified date of the asset, formatted as HTTP string.
	 * @return the date or null if not known.
	 */
	public abstract String getLastModifiedHttp();


	/**
	 * Returns the asset content as byte array.
	 */
	public abstract byte[] getContent() throws IOException;
	
	
	/**
	 * Returns if an cached asset file is still valid, i.e.
	 * its source has not changed since the Asset was created.
	 */
	public abstract boolean isValid();
	
	
	/**
	 * Writes the asset to the response.
	 * @param response the response
	 * @param writeContent should the content writen (false if we
	 * 		are answering a HEAD request)
	 */
	public void write(Response response, boolean writeContent) throws IOException
	{
		response.setStatus(Response.Status.SC200_OK);
		writeHeaders(response);
		if (writeContent && (length() > 0) && checkIfModified(response))
			writeContent(response);
	}
	

	/**
	 * Writes a last-modified and max-age header to the response,
	 * if the last modified date is known.
	 */
	protected abstract void writeHeaders(Response response);
 	
	
	/**
	 * Allows to access the protected method {@link #writeHeaders(Response)}
	 */
	protected void writeHeaders(Asset asset, Response response)
	{
		asset.writeHeaders(response);
	}


	/**
	 * Sets content-related headers and writes the asset content
	 * to the response.
	 */
	protected abstract void writeContent(Response response) throws IOException;
	

	/**
	 * Allows to access the protected method {@link #writeContent(Asset,Response)}
	 */
	protected void writeContent(Asset asset, Response response) throws IOException
	{
		asset.writeContent(response);
	}


	protected boolean checkIfModified(Response response)
	{
		long modifiedSince = response.getRequest().getHeaders().getDate(HttpHeaders.IF_MODIFIED_SINCE);
		if (modifiedSince != -1)
		{
			if (getLastModified() < modifiedSince + 1000)
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
		String encoding = Check.notNull(getCharEncoding(), "charEncoding");
		return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
	}
	
	
	/**
	 * Returns an Asset which keeps its content in memory if applicable.
	 */
	public abstract Asset cache() throws IOException;

	
	/**
	 * Returns a debug string for the asset.
	 */
	@Override public abstract String toString();
}
