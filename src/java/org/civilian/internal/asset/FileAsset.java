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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.civilian.asset.Asset;


/**
 * FileAsset is an Asset based on a file
 * in the locale file system. A FileAsset is valid
 * if size and last-modified of the file did not change.
 */
public class FileAsset extends Asset
{
	/**
	 * Creates a new FileAsset
	 * @param file the file corresponding to the asset.
	 */
	public FileAsset(File file)
	{
		file_ = file;
		setLength((int)file_.length());
		setLastModified(file.lastModified());
		
		String name = file.getName();
		if (name.endsWith(".gz"))
			setCompression("gzip");
		else if (name.contains(".br."))
			setCompression("br");
	}
	
	
	/**
	 * Returns an InputStream for the file.
	 */
	@Override public InputStream getInputStream() throws IOException
	{
		return new FileInputStream(file_);
	}

	
	/**
	 * Tests if the file size and its modified date has not changed.
	 */
	@Override public boolean isValid()
	{
		return (length() == file_.length()) && (getLastModified() == file_.lastModified());
	}

	
	@Override public String toString()
	{
		return file_.getAbsolutePath();
	}
	
	
	private File file_;
}
