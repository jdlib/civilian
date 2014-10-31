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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.tool.scaffold.Scaffold;
import org.civilian.util.FileType;
import org.civilian.util.IoUtil;


public class ScaffoldTest extends CivTest
{
	@Test public void testHelp() throws Exception
	{
		PrintStream out = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
		Scaffold.main(new String[] {});
		System.setOut(out);
	}
	
	
	@Test public void test() throws Exception
	{
		File dir = Files.createTempDirectory("scaffold").toFile();
		try
		{
			String[] args = new String[] {
				"-eclipse",
				"-enc", "UTF-8",
				"-locales", "en,de",
				"-package", "com.acme.test",
				"-text",
				"-tomcat",
				dir.getAbsolutePath(),
				"Test"
			};
			Scaffold.main(args);
			assertFiles(dir);
		}
		finally
		{
			IoUtil.delete(dir);
		}
	}
	
	
	private void assertFiles(File dir)
	{
		// base files
		assertFile(dir, "build.xml");
		assertFile(dir, "build.properties");
		assertFile(dir, "ivy.xml");
		assertDir (dir, "bin");
		File srcDir = assertDir (dir, "src");
		File webDir = assertDir (dir, "web");
		
		// src-files
		File srcWebDir = assertDir (srcDir, "com/acme/test/web");
		assertFile(srcWebDir, "TestApp.java");
		assertFile(srcWebDir, "IndexController.java");
		assertFile(srcWebDir, "IndexTemplate.csp");
		assertFile(srcWebDir, "IndexTemplate.java");
		
		File srcTextDir = assertDir (srcDir, "com/acme/test/text");
		assertFile(srcTextDir, "msg_en.properties");
		assertFile(srcTextDir, "msg_de.properties");
		
		// web-inf files
		File webInfDir = assertDir(webDir, "WEB-INF");
		assertFile(webInfDir, "civilian.ini");
		assertFile(webInfDir, "web.xml");
		File classesDir = assertDir(webInfDir, "classes");
		assertFile(classesDir, "simplelogger.properties");
		
		// eclipse files
		assertFile(dir, ".classpath");
		assertFile(dir, ".project");

		// tomcat files
		assertFile(dir, "tomcat.server.xml");
	}


	private void assertFile(File dir, String name)
	{
		File file = new File(dir, name);
		FileType.EXISTENT_FILE.check(file);
	}
	

	private File assertDir(File dir, String name)
	{
		File subdir = new File(dir, name);
		FileType.EXISTENT_DIR.check(subdir);
		return subdir;
	}
}
