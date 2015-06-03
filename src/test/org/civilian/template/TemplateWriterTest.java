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
package org.civilian.template;


import java.io.IOException;
import java.io.StringWriter;
import org.junit.Before;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.template.TemplateWriter;


public class TemplateWriterTest extends CivTest
{
	@Before public void before()
	{
		stringOut = new StringWriter();
		out = new TemplateWriter(stringOut);
		out.setLineSeparator("\n");
	}
	
		
	@Test public void testTabs() throws Exception
	{
		out.setTabChars(". ");
		
		assertEquals(0, out.getTabCount());
		out.decreaseTab();
		assertEquals(0, out.getTabCount());
		out.setTabCount(1);
		assertEquals(1, out.getTabCount());
		out.decreaseTab();
		assertEquals(0, out.getTabCount());
		
		out.close();
	}


	@Test public void testError() throws Exception
	{
		IOException e = new IOException(); 
		assertNull(out.getError());
		out.setError(e);
		out.setError(new IOException());
		assertSame(e, out.getError());
		assertTrue(out.checkError());
	}
	
	
	@Test public void testOutput() throws Exception
	{
		out.println(false);
		assertOutput("false\n");

		out.println('c');
		assertOutput("c\n");

		out.println(1);
		assertOutput("1\n");

		out.println(2L);
		assertOutput("2\n");

		out.println(3.1f);
		assertOutput("3.1\n");

		out.println(4.2d);
		assertOutput("4.2\n");

		out.println(new char[] { 'd', 't' });
		assertOutput("dt\n");

		out.println(Integer.valueOf(14));
		assertOutput("14\n");
	}
	
	
	private void assertOutput(String s)
	{
		assertEquals(stringOut.toString(), s);
		stringOut.getBuffer().setLength(0);
	}


	@Test public void testContext() throws Exception
	{
		assertNull(out.getContext(String.class));
		String a = "a";
		out.addContext(a);
		assertSame(a, out.getContext(String.class));
		out.addContext("b");
		assertSame(a, out.getContext(String.class));
		
		Integer one = Integer.valueOf(1);
		out.addContext(one);
		assertSame(one, out.getContext(Integer.class));
	}

	
	private StringWriter stringOut;
	private TemplateWriter out;
}
