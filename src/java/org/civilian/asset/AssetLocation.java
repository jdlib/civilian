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


import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeLookup;
import org.civilian.internal.Logs;
import org.civilian.resource.Path;
import org.civilian.util.Check;


/**
 * AssetLocation is the base class of AssetService implementations
 * which know how to construct real assets.   
 */
public abstract class AssetLocation extends AssetService
{
	/**
	 * Creates an AssetLocation.
	 * @param relativePath the relative path below the asset root under which 
	 * 		assets of this AssetLocation are published.
	 */
	public AssetLocation(String relativePath)
	{
		path_ = relativePath_ = new Path(relativePath); 
	}

	
	/**
	 * Initializes the AssetLocation with context information.
	 */
	@Override public void init(Path parentPath, String defaultEncoding, ContentTypeLookup lookup)
	{
		path_ = Check.notNull(parentPath, "parentPath").add(relativePath_);
		contentTypeLookup_ 	= Check.notNull(lookup, "lookup");
		if (charEncoding_ == null)
			charEncoding_ = defaultEncoding;
	}

	
	/**
	 * Returns the absolute path from the server root to the 
	 * asset location.
	 */
	@Override public Path getPath()
	{
		return path_;
	}
	
	
	/**
	 * Returns the path of the asset location relative to the asset root.
	 */
	@Override public Path getRelativePath()
	{
		return relativePath_;
	}

	
	/**
	 * Returns true.
	 */
	@Override public boolean hasAssets()
	{
		return true;
	}

	
	/**
	 * Sets the default character encoding of the assets served by this
	 * AssetLocation.
	 */
	public void setCharEncoding(String encoding)
	{
		charEncoding_ = encoding;
	}

	
	/**
	 * Returns the encoding of the assets served by this
	 * AssetLocation, or null if not known or no common encoding exists. 
	 */
	public String getCharEncoding()
	{
		return charEncoding_;
	}
	
	
	/**
	 * Sets the default content-type of the assets served by this
	 * AssetLocation.
	 */
	public void setContentType(ContentType contentType)
	{
		contentType_ = contentType;
	}

	
	/**
	 * Returns the ContentType associated with the location or
	 * null, if the assets of this location do not have
	 * a specific content-type.
	 */
	public ContentType getContentType()
	{
		return contentType_;
	}

	
	/**
	 * Returns a asset.
	 * @param assetPath the path of the asset relative to this location
	 * @return the asset or null if not found
	 */
	@Override public Asset getAsset(Path assetPath) throws Exception
	{
		Asset asset = null;
		
		Path childPath = assetPath.cutStart(getRelativePath());
		if (childPath != null)
		{
			asset = find(childPath);
			if (asset != null)
			{
				if (Logs.ASSET.isTraceEnabled())
					Logs.ASSET.trace("{} -> {}", assetPath, asset);
				
				if (asset.getCharEncoding() == null)
					asset.setCharEncoding(charEncoding_);
				
				if (asset.getContentType() == null)
				{
					ContentType contentType = contentType_ != null ?
						contentType_ :
						contentTypeLookup_.forFile(assetPath.toString(), ContentType.APPLICATION_OCTET_STREAM);
	
					asset.setContentType(contentType);
				}
			}
		}
		
		return asset;
	}
	
	
	/**
	 * Implements the retrieval of an asset for a request.
	 * @param assetPath the path of the asset relative to this location
	 * @return the asset or null if not found
	 */
	protected abstract Asset find(Path assetPath) throws Exception;

	
	/**
	 * Returns an info-string describing the location.
	 */
	@Override public String getInfo()
	{
		String info = path_ + " -> " +  getInfoParam();
		if (charEncoding_ != null)
			info += ", " + charEncoding_;
		return info;
	}
	
	
	/**
	 * Returns an info-string describing the location parameters.
	 */
	protected abstract String getInfoParam();

	
	private Path path_;
	private Path relativePath_;
	private String charEncoding_;
	private ContentType contentType_;
	private ContentTypeLookup contentTypeLookup_ = ContentTypeLookup.EMPTY;
}
