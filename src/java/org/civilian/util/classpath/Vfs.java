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
package org.civilian.util.classpath;


import java.net.URL;
import org.civilian.util.ClassUtil;


public abstract class Vfs
{
	public static Vfs create()
	{
		try
		{
			// this will fail if jboss libraries are not in the classpath
			String className = Vfs.class.getName() + "3Impl";
			return ClassUtil.createObject(className, Vfs.class, null);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	
	public abstract void scan(ScanContext context, URL rootUrl) throws Exception;
}
