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
package org.civilian.util;


import java.util.List;
import org.civilian.CivTest;
import org.junit.Test;


public class ArgumentsTest extends CivTest
{
	@Test public void testArgFile()
	{
		ResourceLoader loader;
		
		loader = ResourceLoaders.forString("test.txt", "a\n#comment\nb c");
		Arguments args = new Arguments(loader, "one", "@test.txt", "two");
		args.consume("one");
		args.consume("a");
		args.consume("b");
		args.consume("c");
		args.consume("two");
		assertFalse(args.hasMore());
		
		loader = ResourceLoaders.forString("test.txt", "@test.txt");
		try
		{
			new Arguments(loader, "@test.txt");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("cyclic inclusion of argument file: test.txt", e.getMessage());
		}
	}


	@Test public void testCreate()
	{
		Arguments args;
			
		args = new Arguments((String[])null);
		assertFalse(args.hasMore());
			
		args = new Arguments((String)null, "", "a");
		assertEquals("a", args.get());
	}


	@Test public void testGet()
	{
		Arguments args = new Arguments("x");
		
		assertTrue(args.hasMore());
		assertTrue(args.hasMore(1));
		assertFalse(args.hasMore(2));
		
		List<String> list = args.getRestArgs();
		assertEquals(1, list.size());
		assertEquals("x", list.get(0));
		
		assertEquals("x", args.get());
		args.replace("a");
		assertEquals("a", args.get());
		assertEquals("a", args.next());
		
		list = args.getRestArgs();
		assertEquals(0, list.size());
		
		try
		{
			args.next();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no arguments left", e.getMessage());
		}

		try
		{
			args.next("x");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("missing argument: x", e.getMessage());
		}
	}


	@Test public void testNextInt()
	{
		Arguments args = new Arguments("123", "a");
		
		assertEquals(123, args.nextInt("test"));
		try
		{
			assertEquals(123, args.nextInt("test"));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("reading test: 'a' is not a integer", e.getMessage());
		}
	}


	@Test public void testNextObject()
	{
		Arguments args = new Arguments("java.lang.String", "java.xxx");
		assertEquals("", args.nextObject("string", String.class));

		try
		{
			args.nextObject("string", String.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("reading string: java.xxx", e.getMessage());
		}
	}
}
