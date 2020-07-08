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
package org.civilian.tool.scaffold;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.civilian.util.FileType;
import org.civilian.util.StringUtil;


class Project
{
	public Project(File root, String encoding, String packageName)
	{
		FileType.DIR.check(root);
		this.root 			= new Dir(root);
		this.bin			= new Dir(root, "bin");
		this.settings		= new Dir(root, ".settings");
		this.src  			= new Dir(root, "src");
		this.lib  			= new Dir(root, "lib");
		this.res  			= new Dir(root, "res");
		this.resLaunch		= new Dir(res, "launch");
		this.web  			= new Dir(root, "web");
		this.webInf  		= new Dir(web, "WEB-INF");
		this.webInfClasses  = new Dir(webInf, "classes");
		this.webInfLib 		= new Dir(webInf, "lib");
		this.encoding		= encoding;
		this.srcText		= createPackageDir(packageName + ".text");
		this.srcWeb			= createPackageDir(packageName + ".web");
		this.srcWebUsers	= createPackageDir(packageName + ".web.users");
		this.srcWebUserId	= createPackageDir(packageName + ".web.users.id");
	}
	
	
	private PackageDir createPackageDir(String packageName)
	{
		File file = src;
		for (String part : packageName.split("\\."))
			file = new File(file, part);
		return new PackageDir(file, packageName); 
	}

	
	@SuppressWarnings("serial")
	class Dir extends File
	{
		public Dir(File file)
		{
			super(file.getAbsolutePath());
		}
		
		
		public Dir(File parent, String name)
		{
			super(parent, name);
		}

	
		public String getEncoding()
		{
			return Project.this.encoding;
		}

		
		public String getRelativePath() throws IOException
		{
			String path = getAbsolutePath();
			path = StringUtil.cutLeft(path, root.getAbsolutePath());
			path = path.replace('\\', '/');
			path = StringUtil.cutLeft(path, "/");
			return path;
		}
		
		
		public void makeDir() throws IOException
		{
			if (!exists())
			{
				Log.print("create dir " + getAbsolutePath());
				if (!mkdirs())
					throw new IOException("failed to create " + getAbsolutePath());
			}
		}

		
		public void write(String fileName, String content) throws IOException
		{
			write(fileName, content, null);
		}
		
		
		public void write(String fileName, String content, String encoding) throws IOException
		{
			makeDir();
			if (encoding == null)
				encoding = getEncoding();
			File file = new File(this, fileName);
			Log.print("write file " + file.getAbsolutePath());
			try(OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), encoding))
			{
				out.write(content);
			}
		}
	}
	
	
	@SuppressWarnings("serial")
	class PackageDir extends Dir
	{
		private PackageDir(File file, String packageName)
		{
			super(file);
			this.packageName = packageName;
			this.packagePath = packageName.replace('.', '/');
		}
		
		
		public final String packageName;
		public final String packagePath;
	}
	
	
	public final Dir root;
	public final Dir lib;
	public final Dir bin;
	public final Dir res;
	public final Dir resLaunch;
	public final Dir settings;
	public final Dir src;
	public final PackageDir srcText;
	public final PackageDir srcWeb;
	public final PackageDir srcWebUsers;
	public final PackageDir srcWebUserId;
	public final Dir web;
	public final Dir webInf;
	public final Dir webInfClasses;
	public final Dir webInfLib;
	public final String encoding;
}
