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


import java.io.File;
import org.civilian.asset.Asset;
import org.civilian.asset.AssetLocation;
import org.civilian.resource.Path;
import org.civilian.util.FileType;
import org.civilian.util.StringUtil;


/**
 * AssetDirectory is an AssetLocation whose Assets
 * are files in the local file-system. An AssetDirectory
 * is defined by a root directory. Asset paths are then translated into 
 * file paths relative to that root directory.
 */
public class DirectoryLocation extends AssetLocation
{
	/**
	 * Creates a new AssetDirectory.
	 * @param path the path of the AssetLocation.
	 * @param rootDir the root directory of the AssetDirectory. 
	 */
	public DirectoryLocation(String path, File rootDir)
	{
		super(path);
		rootDir_ = FileType.DIR.check(rootDir, "AssetDirectory");
	}


	/**
	 * Returns a FileAsset, if the file corresponding to the path exists.
	 */
	@Override protected Asset find(Path assetPath) throws Exception
	{
		String name = StringUtil.cutLeft(assetPath.getValue(), "/");
		File file = new File(rootDir_, name);
		return file.exists() && !file.isDirectory() ? new FileAsset(file) : null;
	}
	

	/**
	 * Returns the root directory.
	 */
	@Override protected String getInfoParam()
	{
		return rootDir_.toString();
	}
	
	
	private File rootDir_;
}
