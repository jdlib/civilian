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
package org.civilian.tool.source;


import java.io.File;
import java.net.URL;
import org.civilian.CivTest;
import org.civilian.util.ClassUtil;
import org.junit.Test;


public class JavaPackageDetectorTest extends CivTest
{
	@Test public void test() throws Exception
	{
		File rootDir = findSourceRootDir();
		if (rootDir != null)
		{
			File srcFile = new File(rootDir, getClass().getName().replace('.', '/') + ".java");
			if (!srcFile.exists())
				fail(srcFile.getAbsolutePath() + " does not exist");
			
			JavaPackageDetector d = new JavaPackageDetector();
			String packageName = d.detect(srcFile);
			assertEquals(ClassUtil.getPackageName(getClass()), packageName);
		}
	}
	
	
	private File findSourceRootDir() throws Exception
	{
		URL url = getClass().getResource(getClass().getSimpleName() + ".class");
		try
		{
			File file = new File(url.toURI());
			if (!file.exists())
				return null;
			for (int i=0; i<6; i++)
				file = file.getParentFile();
			
			return new File(file, "src/test"); 
		}
		catch (Exception e)
		{
			if (!"file:".equals(url.getProtocol()))
				return null;
			throw e;
		}
	}
}
