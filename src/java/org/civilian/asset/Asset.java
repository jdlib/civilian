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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import org.civilian.content.ContentType;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.util.Check;
import org.civilian.util.http.HeaderNames;


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
	 * @return the content type of the asset or null
	 * if not known or determined.
	 */
	public abstract ContentType getContentType();
	
	
	/**
	 * Sets the content type of the asset.
	 * @param contentType the content type
	 */
	public abstract void setContentType(ContentType contentType);

	
	/**
	 * @return the character encoding of the asset, or null if not known.
	 */
	public abstract Charset getEncoding();
	
	
	/**
	 * Sets the encoding of the asset.
	 * @param encoding the encoding
	 */
	public abstract void setEncoding(Charset encoding);

	
	/**
	 * @return the byte length of the asset data.
	 */
	public abstract long length();
	
	
	/**
	 * @return the cache control of the Asset.
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
	 * @throws IOException if an I/O error occurs
	 * @return the byte array
	 */
	public abstract byte[] getContent() throws IOException;
	
	
	/**
	 * Returns if an cached asset file is still valid, i.e.
	 * its source has not changed since the Asset was created.
	 * @return is valid?
	 */
	public abstract boolean isValid();
	
	
	/**
	 * Writes the asset to the response.
	 * @param request the request, used to access the If-Modified-Since header
	 * @param response the response
	 * @param writeContent should the content writen (false if we
	 * 		are answering a HEAD request)
	 * @throws IOException if an I/O error occurs
	 */
	public final void write(Request request, Response response, boolean writeContent) throws IOException
	{
		response.setStatus(Response.Status.SC200_OK);
		writeHeaders(response);
		if (writeContent && (length() > 0) && checkIfModified(request, response))
			writeContent(response);
	}
	
	
	/**
	 * Writes a last-modified and max-age header to the response,
	 * if the last modified date is known.
	 * @param response the response
	 */
	protected abstract void writeHeaders(Response response);
 	
	
	/**
	 * Allows to access the protected method {@link #writeHeaders(Response)}
	 * @param asset an asset
	 * @param response a response
	 */
	protected void writeHeaders(Asset asset, Response response)
	{
		asset.writeHeaders(response);
	}


	/**
	 * Sets content-related headers and writes the asset content
	 * to the response.
	 * @param response the response
	 * @throws IOException if an I/O error occurs
	 */
	protected abstract void writeContent(Response response) throws IOException;
	

	/**
	 * Allows to access the protected method {@link #writeContent(Asset,Response)}
	 * @param asset an asset
	 * @param response a response
	 * @throws IOException if an I/O error occurs
	 */
	protected void writeContent(Asset asset, Response response) throws IOException
	{
		asset.writeContent(response);
	}


	protected boolean checkIfModified(Request request, Response response)
	{
		long modifiedSince = request.getHeaders().getDate(HeaderNames.IF_MODIFIED_SINCE);
		if ((modifiedSince != -1) && (getLastModified() < modifiedSince + 1000))
		{
			response.setStatus(Response.Status.NOT_MODIFIED);
			return false;
        }
		else
			return true;
	}


	/**
	 * @return an InputStream for the asset content.
	 * @throws IOException if an I/O error occurs
	 */
	public abstract InputStream getInputStream() throws IOException;
	
	
	/**
	 * Returns a Reader for the asset content.
	 * If no encoding is set on the asset, an IllegalArgumentException is thrown.
	 * @throws IOException if an I/O error occurs
	 * @return the reader
	 */
	public Reader getReader() throws IOException
	{
		Charset encoding = Check.notNull(getEncoding(), "encoding");
		return new BufferedReader(new InputStreamReader(getInputStream(), encoding));
	}
	
	
	/**
	 * @return an Asset which keeps its content in memory if applicable.
	 * @throws IOException if an I/O error occurs
	 */
	public abstract Asset cache() throws IOException;

	
	/**
	 * Returns a debug string for the asset.
	 */
	@Override public abstract String toString();
}
