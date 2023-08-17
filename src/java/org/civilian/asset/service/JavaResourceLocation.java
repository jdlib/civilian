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


import java.io.File;
import java.net.URL;
import org.civilian.asset.Asset;
import org.civilian.asset.FileAsset;
import org.civilian.asset.UrlAsset;
import org.civilian.resource.Path;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * ClassPathLocation is an AssetLocation which
 * returns Java classpath resources as Assets.
 */
public class JavaResourceLocation extends AssetLocation
{
	/**
	 * Creates a new ClassPathLocation. 
	 * @param path the location path
	 * @param resourcePrefix the prefix path of the resources.
	 * 	Must not be empty for security reasons.
	 */
	public JavaResourceLocation(String path, String resourcePrefix)
	{
		super(path);
		Check.notEmpty(resourcePrefix, "resourcePrefix");
		resourcePrefix_ = StringUtil.cutLeft(resourcePrefix, "/");
	}


	/**
	 * Translates the path into a resource path, reads the resource file
	 * and returns it as Asset object. 
	 */
	@Override protected Asset find(Path relativePath) throws Exception
	{
		String resource = resourcePrefix_ + relativePath.toString();
		URL url = getClass().getClassLoader().getResource(resource);
		if (url == null)
			return null;

		Asset assetFile = null;
		if ("file".equalsIgnoreCase(url.getProtocol()))
		{
			File file = new File(url.toURI());
			if (!file.isDirectory())
				assetFile = new FileAsset(file);
			return assetFile;
		}
		return new UrlAsset(url);
	}
	
	
	/**
	 * Returns a debug string.
	 */
	@Override protected String getInfoParam()
	{
		return "res:" + resourcePrefix_;
	}

	
	private String resourcePrefix_;
}
