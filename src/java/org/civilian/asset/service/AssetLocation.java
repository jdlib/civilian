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
package org.civilian.asset.service;


import org.civilian.Logs;
import org.civilian.asset.Asset;
import org.civilian.asset.AssetCacheControl;
import org.civilian.asset.AssetInitializer;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeLookup;
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
	 * @return the absolute path from the server root to the asset location.
	 */
	@Override public Path getPath()
	{
		return path_;
	}
	
	
	/**
	 * @return the path of the asset location relative to the asset root.
	 */
	public Path getRelativePath()
	{
		return relativePath_;
	}

	
	/**
	 * @return true.
	 */
	@Override public boolean hasAssets()
	{
		return true;
	}

	
	/**
	 * Sets the default character encoding of the assets served by this
	 * AssetLocation.
	 * @param encoding the encoding
	 */
	public void setCharEncoding(String encoding)
	{
		charEncoding_ = encoding;
	}

	
	/**
	 * Returns the encoding of the assets served by this
	 * AssetLocation, or null if not known or no common encoding exists.
	 * @return the encoding 
	 */
	public String getCharEncoding()
	{
		return charEncoding_;
	}
	
	
	/**
	 * Sets the default content-type of the assets served by this
	 * AssetLocation.
	 * @param contentType the content type
	 */
	public void setContentType(ContentType contentType)
	{
		contentType_ = contentType;
	}

	
	/**
	 * @return the ContentType associated with the location or
	 * null, if the assets of this location do not have
	 * a specific content-type.
	 */
	public ContentType getContentType()
	{
		return contentType_;
	}
	
	
	/**
	 * Instructs the AssertService to use the given AssetCacheControlfor its assets.
	 * @param cacheControl the cacheControl
	 */
	@Override public void setCacheControl(AssetCacheControl cacheControl)
	{
		cacheControl_ = cacheControl;
	}

	
	/**
	 * @return the AssetCacheControl used by this location.
	 */
	public AssetCacheControl getCacheControl()
	{
		return cacheControl_;
	}

	
	@Override public void setInitializer(AssetInitializer initializer)
	{
		initializer_ = initializer;
	}

	
	public AssetInitializer getInitializer()
	{
		return initializer_;
	}

	
	/**
	 * Returns a asset.
	 * @param path the path of the asset relative to this location
	 * @return the asset or null if not found
	 */
	@Override public Asset getAsset(Path path) throws Exception
	{
		Asset asset = null;
		
		Path childPath = path.cutStart(getRelativePath());
		if (childPath != null)
		{
			asset = find(childPath);
			if (asset != null)
				asset = initAsset(path, asset);
		}
		return asset;
	}
	
	
	/**
	 * Initializes the asset: Applies all setups if not already set
	 * on the asset.
	 * @return the asset. May return null, if the AssetInitializer dropped the asset
	 */
	private Asset initAsset(Path path, Asset asset)
	{
		if (Logs.ASSET.isTraceEnabled())
			Logs.ASSET.trace("{} -> {}", path, asset);
		
		if ((charEncoding_ != null) && (asset.getCharEncoding() == null))
			asset.setCharEncoding(charEncoding_);
		if ((cacheControl_ != null) && (asset.getCacheControl() == null))
			asset.setCacheControl(cacheControl_);
		
		if (asset.getContentType() == null)
		{
			ContentType contentType = contentType_ != null ?
				contentType_ :
				contentTypeLookup_.forFile(path.toString(), ContentType.APPLICATION_OCTET_STREAM);

			asset.setContentType(contentType);
		}
		
		if (initializer_ != null)
			asset = initializer_.initAsset(path, asset);
		
		return asset;
	}
	
	
	/**
	 * Implements the retrieval of an asset for a request.
	 * @param path the path of the asset relative to this location
	 * @return the asset or null if not found
	 * @throws Exception if an exception occurs
	 */
	protected abstract Asset find(Path path) throws Exception;

	
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
	 * @return an info-string describing the location parameters.
	 */
	protected abstract String getInfoParam();

	
	private Path path_;
	private Path relativePath_;
	private String charEncoding_;
	private ContentType contentType_;
	private AssetCacheControl cacheControl_ = AssetCacheControl.DEFAULT;
	private ContentTypeLookup contentTypeLookup_ = ContentTypeLookup.EMPTY;
	private AssetInitializer initializer_;
}
