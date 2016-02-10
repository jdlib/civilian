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
package org.civilian.internal.asset;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.civilian.asset.Asset;


/**
 * A MemoryAsset is a virtual Asset implementation,
 * keeping the asset content in a byte array in memory.
 * The MemoryAsset is assumed to be valid as long as it exists.
 */
public class MemoryAsset extends Asset
{
	private static byte[] convert(String encoding, String content)
	{
		try
		{
			return content.getBytes(encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalStateException(e);
		}
	}
	
	
	/**
	 * Creates a new MemoryAsset.
	 * @param encoding the encoding
	 * @param content the content as string
	 */
	public MemoryAsset(String encoding, String content)
	{
		this(encoding, convert(encoding, content));
	}
	
	
	/**
	 * Creates a new MemoryAsset.
	 * @param encoding the encoding
	 * @param bytes the byte content
	 */
	public MemoryAsset(String encoding, byte[] bytes)
	{
		setEncoding(encoding);
		setContent(bytes);
		setLastModified(System.currentTimeMillis());
	}
	

	/**
	 * Returns the stored bytes as InputStream.
	 */
	@Override public InputStream getInputStream() throws IOException
	{
		return new ByteArrayInputStream(getContent());
	}

	
	/**
	 * Returns true.
	 */
	@Override public boolean isValid()
	{
		return true;
	}
	
	
	@Override public String toString()
	{
		return "MemAsset@" + System.identityHashCode(this);
	}
}
