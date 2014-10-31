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


import java.net.URL;
import org.civilian.internal.classpath.ScanContext;
import org.civilian.internal.classpath.Vfs;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.VirtualFileVisitor;
import org.jboss.vfs.VisitorAttributes;


public class Vfs3Impl extends Vfs
{
	@Override public void scan(ScanContext context, URL rootUrl) throws Exception
	{
		if (rootUrl != null)
		{
			VirtualFile vf = VFS.getChild(rootUrl.toURI());
			vf.visit(new Visitor(context, vf.getPathName()));
		}
	}


	private static class Visitor implements VirtualFileVisitor 
	{
		public Visitor(ScanContext context, String rootPath)
		{
			context_  = context;
			rootPath_ = rootPath;
		}
		
		@Override public VisitorAttributes getAttributes()
		{
			return VisitorAttributes.RECURSE;
		}

		@Override public void visit(VirtualFile file)
		{
			String path = file.getPathName();
			if (path.startsWith(rootPath_) && path.endsWith(".class"))
			{
				String className = context_.rootPackage + path.substring(rootPath_.length(), path.length() - 6).replace('/', '.');
				context_.result.scanned(className);
			}
		}
		
		private String rootPath_;
		private ScanContext context_; 
	}
}
