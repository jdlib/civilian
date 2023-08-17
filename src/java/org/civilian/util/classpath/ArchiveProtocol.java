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


import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.civilian.util.ArrayUtil;


class ArchiveProtocol extends Protocol
{
	private static final String[] PROTOCOL_KEYS = { "jar", "zip", "wsjar" /*Websphere*/ };
	private static final String SEPARATOR = "!/";

	public static final ArchiveProtocol INSTANCE = new ArchiveProtocol();

	
	@Override public boolean accept(URL url)
	{
		return ArrayUtil.contains(PROTOCOL_KEYS, url.getProtocol()) && url.getPath().contains(SEPARATOR);
	}


	@Override public void scan(ScanContext context, URL rootUrl) throws IOException
	{
		URLConnection con = rootUrl.openConnection();
		
		JarFile jarFile;
		String rootEntryPath;
		boolean closeJarFile;
		
		if (con instanceof JarURLConnection) 
		{
			JarURLConnection jarCon = (JarURLConnection)con;
			jarCon.setUseCaches(false);
			jarFile = jarCon.getJarFile();
			
			JarEntry jarEntry = jarCon.getJarEntry();
			rootEntryPath 	  = jarEntry != null ? jarEntry.getName() : "";
			closeJarFile 	  = false;
		}
		else 
		{
			String file = rootUrl.getFile();
			int p = file.indexOf(SEPARATOR);
			if (p != -1)
			{
				jarFile = toJarFile(file.substring(0, p));
				rootEntryPath = file.substring(p + SEPARATOR.length());
			}
			else 
			{
				jarFile = new JarFile(file);
				rootEntryPath = "";
			}
			closeJarFile = true;
		}
		
		try
		{
			scan(context, jarFile, rootEntryPath);
		}
		finally
		{
			if (closeJarFile)
				jarFile.close();
		}
	}
	

	private void scan(ScanContext context, JarFile jarFile, String rootEntryPath) throws IOException
	{
		for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) 
		{
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.startsWith(rootEntryPath) && name.endsWith(".class"))
			{
				String className = name.substring(0, name.length() - 6).replace('/', '.');
				context.result.scanned(className);
			}
		}
	}
	
	
	private JarFile toJarFile(String url) throws IOException 
	{
		if (url.startsWith("file:")) 
		{
			try 
			{
				URI uri = new URI(url.replace(" ", "%20"));
				return new JarFile(uri.getSchemeSpecificPart());
			}
			catch (URISyntaxException ex) 
			{
				return new JarFile(url.substring(5));
			}
		}
		return new JarFile(url);
	}
}
