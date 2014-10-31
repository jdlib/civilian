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
package org.civilian.tool.resource;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.testcase1.Test1App;
import org.civilian.tool.resource.ClientConstGenerator;
import org.civilian.util.IoUtil;


public class ClientConstGeneratorTest extends CivTest
{
	@Test public void testHelp() throws Exception
	{
		PrintStream out = System.out;
		System.setOut(new PrintStream(new ByteArrayOutputStream()));
		ClientConstGenerator.main(new String[] {});
		System.setOut(out);
	}


	@Test public void testGenerate() throws Exception
	{
		File file = File.createTempFile("clientgen", ".java");
		try
		{
			String[] args = new String[] {
				"-ts", "false",
				"-enc", "UTF-8",
				"-java", "org.civilian.client.Test1WebRoot",
				"-javaout:file", file.getAbsolutePath(),
				Test1App.class.getName()
			};
			ClientConstGenerator.main(args);
			
			String expected = readTestFile("/org/civilian/client/Test1WebRoot.txt"); 
			String actual   = read(file); 
			assertEquals(expected, actual);
		}
		finally
		{
			IoUtil.delete(file);
		}
	}
}
