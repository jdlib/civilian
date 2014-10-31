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


import org.civilian.CivTest;
import org.civilian.tool.csp.Argument;
import org.civilian.util.Scanner;
import org.junit.Test;


public class ArgTest extends CivTest
{
	@Test public void test() throws Exception
	{
		assertArg("int n", 				"int n",			"n", "int n");
		assertArg("String[] s", 		"String[] s",		"s", "String[] s");
		assertArg("String s[]", 		"String[] s",		"s", "String[] s");
		assertArg("String s[][]", 		"String[][] s",		"s", "String[][] s");
		assertArg("String...s", 		"String... s",		"s", "String[] s");
		assertArg("String ... s", 		"String... s",		"s", "String[] s");
		assertArg("A.B s", 				"A.B s",			"s", "A.B s");
		assertArg("A.B... s", 			"A.B... s",			"s", "A.B[] s");
		assertArg("List< String > s", 	"List<String > s",	"s", "List<String > s");
		assertArg("List< String>... s", "List<String>... s","s", "List<String>[] s");
	}
	
	
	private void assertArg(String input, String ctorArg, String fieldName, String fieldDecl) throws Exception
	{
		Scanner scanner 	= new Scanner(input);
		Argument argument 	= new Argument(scanner);
		
		StringBuilder s = new StringBuilder();
		argument.ctorArg(s);
		assertEquals(ctorArg, s.toString());
		
		s.setLength(0);
		argument.fieldAssign(s);
		assertEquals("this." + fieldName + " = " + fieldName + ";", s.toString());

		s.setLength(0);
		argument.fieldDecl(s);
		assertEquals("private " + fieldDecl + ";", s.toString());
	}
}
