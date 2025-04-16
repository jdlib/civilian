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


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import jakarta.servlet.Servlet;
import org.civilian.CivTest;
import org.civilian.server.Server;
import org.junit.Test;


public class ImportListTest extends CivTest
{
	@Test public void test()
	{
		ImportList list = new ImportList();
		
		list.add(getClass());
		list.add(getClass().getName());
		
		assertEquals(1, list.size());
		assertEquals(getClass().getName(), list.get(0));
		list.clear();
		assertEquals(0, list.size());
		
		StringWriter s = new StringWriter();
		PrintWriter  w = new PrintWriter(s);
		
		assertFalse(list.write(w));
		assertEquals("", s.toString());
		
		list.add(Server.class);
		list.add("org.junit.Test");
		list.add(Servlet.class);
		list.add(List.class);
		assertTrue(list.write(w, "org.civilian"));
		String output = s.toString().replace("\r", "");
		assertEquals("import java.util.List;\n" +
			"import jakarta.servlet.Servlet;\n" +
			"import org.junit.Test;\n" + 
			"import org.civilian.server.Server;\n", output);
	}
}
