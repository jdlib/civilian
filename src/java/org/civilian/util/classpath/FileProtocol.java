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
package org.civilian.internal.classpath;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.civilian.util.StringUtil;


class FileProtocol extends Protocol
{
	public static final FileProtocol INSTANCE = new FileProtocol();
	
	
	@Override public boolean accept(URL url)
	{
		return url.getProtocol().equals("file");
	}


	@Override public void scan(ScanContext context, URL rootUrl) throws IOException
	{
		File baseDir = toFile(rootUrl).getAbsoluteFile();
		if (!baseDir.isDirectory())
			throw new IOException("not a directory '" + baseDir.getAbsolutePath() + "'");
		
		Norm norm = new Norm(context, baseDir);
		scan(context, norm, toFile(rootUrl).getAbsoluteFile());
	}


	private void scan(ScanContext context, Norm norm, File file)
	{
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			if (files != null)
			{
				for (File f : files)
					scan(context, norm, f);
			}
		}
		else
		{
			String className = norm.getClassName(file);
			if (className != null)
				context.result.scanned(className);
		}
	}
	

	private File toFile(URL url)
	{
		try 
		{
			return new File(url.toURI());
		} 
		catch(URISyntaxException e) 
		{
			return new File(url.getPath());
		}
	}
	
	
	private static class Norm
	{
		public Norm(ScanContext context, File baseDir) throws IOException
		{
			String path	= baseDir.getAbsolutePath();
			
			String normedPath = normPath(path); 
			String normedSuffix = normPath(context.rootPath); 
			if (!normedPath.endsWith(normedSuffix))
				throw new IOException("baseDir '" + normedPath + "' does not end with basePath '" + normedSuffix + "'");
			
			prefixLength_ = normedPath.length() - normedSuffix.length(); 
		}
		
		
		private String normPath(String path)
		{
			return StringUtil.cutRight(path.replace(File.separatorChar, '/'), "/");
		}

		
		public String getClassName(File file)
		{
			String path = file.getPath();
			if (path.endsWith(".class"))
			{
				path = path.substring(prefixLength_, path.length() - 6);
				return path.replace(File.separatorChar, '.');
			}
			else
				return null;
		}
		
		
		private final int prefixLength_;
	}
}
