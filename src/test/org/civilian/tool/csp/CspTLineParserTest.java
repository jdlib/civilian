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


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.util.Scanner;


public class CspTLineParserTest extends CivTest
{
	private final Scanner scanner = new Scanner();
	private final CspTLineParser parser = new CspTLineParser(scanner);
	
	
	@Test public void test()
	{
		assertParseFail("\t x", "template indent may not contain a mix of tab and space chars");
		assertParseFail(" \tx", "template indent may not contain a mix of tab and space chars: line uses a space indent character, but previous lines used tab characters");
		
		parse("@@test");
		assertEquals(CspTLineParser.Type.LITERAL, parser.type);
	}
	
	
	private void assertParseFail(String s, String error)
	{
		try
		{
			parse(s);
		}
		catch (Exception e)
		{
			if (!e.getMessage().contains(error))
				fail("'" + e.getMessage() + "' does not contain '" + error + "'");
		}
	}
	
	
	private void parse(String s)
	{
		scanner.input(s);
		parser.parse();
	}
}
