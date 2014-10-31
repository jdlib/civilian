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
package org.civilian.tool.resbundle;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.civilian.CivTest;
import org.junit.Test;


public class ResBundleCompilerTest extends CivTest
{
	@Test public void testHelp() throws Exception
	{
		PrintStream out = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
		ResBundleCompiler.main(new String[] {});
		System.setOut(out);
	}


	
	@Test public void testGenerate() throws Exception
	{
		File excel 			= findTestFile("message.xls");
		File tempDir 		= createTempDir();
		String tempPath		= tempDir.getAbsolutePath();
		String excelPath	= excel.getAbsolutePath();
		
		try
		{
			run("-out:dir", tempPath, excelPath);
			run("-constClass", "TestIds", "-enc", "ISO-8859-1", "-out:dir", tempPath, excelPath);
			run("-constClass", "#file", "-idClass", "#msgId", "-out:dir", tempPath, excelPath);
			run("-constClass", "TestIds", "-idClass", "#inline", "-out:dir", tempPath, excelPath);
			run("-constClass", "TestIds", "-idClass", "test.MymsgId", "-out:dir", tempPath, excelPath);
		}
		finally
		{
			for (File f : tempDir.listFiles())
				f.delete();
			tempDir.delete();
		}
	}
	
	
	private void run(String... args) throws Exception
	{
		ResBundleCompiler.main(args);
	}
}
