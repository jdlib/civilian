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
package org.civilian.tool.csp;


import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.civilian.CivTest;
import org.junit.Test;


public class CspTest extends CivTest
{
	private static final Pattern LN_SPLIT = Pattern.compile("\\n");
	
	
	@Test public void testCompile1() throws Exception
	{
		assertCompile("test1");
	}


	@Test public void testCompile2() throws Exception
	{
		assertCompile("test2");
	}


	@Test public void testCompile3() throws Exception
	{
		assertCompile("test3");
	}


	@Test public void testCompile4() throws Exception
	{
		assertCompile("test4");
	}


	@Test public void testCompile5() throws Exception
	{
		assertCompile("test5");
	}


	@Test public void testCompile6() throws Exception
	{
		assertCompile("test6");
	}


	@Test public void testCompile7() throws Exception
	{
		assertCompile("test7");
	}


	@Test public void testEmptyLine() throws Exception
	{
		assertCompile("test_empty_line");
	}

	
	@Test public void testHatSyntax() throws Exception
	{
		assertCompile("test_hat_syntax");
	}

	
	private void assertCompile(String testCase) throws Exception
	{
		String actual = compile(testCase);

		File outFile = findTestFile(testCase + ".out");
		String expected = read(outFile);

		compareFiles(expected, actual);
	}


	private String compile(String testCase) throws Exception
	{
		File cspFile = findTestFile(testCase + ".csp");
		File tmpFile = File.createTempFile("csptest", ".tmp");
		try
		{
			ArrayList<String> args = new ArrayList<>();
			args.add("-v");
			args.add("0");
			args.add("-force");
			args.add("-out:file");
			args.add(tmpFile.getAbsolutePath());
			args.add(cspFile.getAbsolutePath());
			CspCompiler.main(args.toArray(new String[args.size()]));

			return read(tmpFile);
		}
		finally
		{
			tmpFile.delete();
		}
	}


	@Test public void testError1() throws Exception
	{
		assertError("err1", "[src=err1.csp:ln=1:col=1]", "cannot template package, please provide an explicit package in the template", "x");
	}


	@Test public void testError2() throws Exception
	{
		assertError("err2", "[src=err2.csp:ln=3:col=27]", "relative import '../../../data.Data1' can't be applied to package 'com.test'", "import.../../../data.Data1");
	}


	@Test public void testError3() throws Exception
	{
		assertError("err3", "[src=err3.csp:ln=3:col=1]", "expected the template command, but reached end of file", "package-access");
	}


	@Test public void testError4() throws Exception
	{
		assertError("err4", "[src=err4.csp:ln=3:col=10]", "invalid input: 'x'", "template.x");
	}


	@Test public void testError5() throws Exception
	{
		assertError("err5", "[src=err5.csp:ln=3:col=16]", "argument 'String' needs a name and type", "template(String)");
	}


	@Test public void testError6() throws Exception
	{
		assertError("err6", "[src=err6.csp:ln=4:col=1]", "expected closing bracket ')' of template argument list", "");
	}


	private void assertError(String file, String location, String message, String line) throws Exception
	{
		try
		{
			compile(file);
			fail("error not recognized");
		}
		catch(CspException e)
		{
			String[] actualErrorLines = LN_SPLIT.split(e.getMessage().replace('"', '\''));
			assertEquals("message", message, actualErrorLines[1]);
			assertEquals("location", location, actualErrorLines[0]);
			assertEquals("line", line, actualErrorLines[2].substring(5)); // cut prefix "line="
		}
	}


	@Test public void testCmdLine() throws Exception
	{
		try(SysOut out = new SysOut())
		{
			// coverage printHelp()
			CspCompiler.main(new String[0]);
		}
	}
}
