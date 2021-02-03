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


import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.resource.Path;


public class PathTest extends CivTest
{
	@Test public void testNorm()
	{
		assertNorm(null,	"");	
		assertNorm("",		"");	
		assertNorm("/", 	"");	
		assertNorm("//", 	"");	
		assertNorm("/a/",	"/a");	
		assertNorm("a/",	"/a");	
		assertNorm("/a",	"/a");	
		assertNorm("/a/b/",	"/a/b");	
		assertNorm("a/b/",	"/a/b");	
	}
	
	
	private void assertNorm(String input, String expected)
	{
		String actual = Path.norm(input);
		assertEquals(expected, actual);
		
		Path path = new Path(input);
		assertEquals(expected, path.toString());
	}


	@Test public void testAdd()
	{
		Path root = Path.ROOT;
		assertSame(root, root.add((String)null));
		assertSame(root, root.add((Path)null));
		assertSame(root, root.add(""));
		assertSame(root, root.add("/"));
		
		Path a = new Path("a");
		assertSame(a, root.add(a));
		
		assertAdd(a, "b", "/a/b");
	}
	

	private void assertAdd(Path path, String add, String expected)
	{
		Path new1 = path.add(add);
		assertEquals(expected, new1.toString());

		Path new2 = path.add(new Path(add));
		assertEquals(expected, new2.toString());
	}
	

	@Test public void testStartsWith()
	{
		assertStartsWith("/a",		"/b",		false, null);
		assertStartsWith("/abc",	"/ab",		false, null);
		assertStartsWith("/abc",	"/abcd",	false, null);
		assertStartsWith("/abcd",	"/abc",		false, null);
		assertStartsWith("/abc",  	"/abc",		true,  "");
		assertStartsWith("/abc",  	"/abc",		true,  "");
		assertStartsWith("/abc/x", 	"/abc",		true,  "/x");
	}
	
	
	private void assertStartsWith(String p, String s, boolean yes, String cut)
	{
		Path path 	= new Path(p);
		Path start	= new Path(s);
		if (yes)
		{
			assertTrue(path.startsWith(start));
			assertEquals(cut, path.cutStart(start).toString());
		}
		else
		{
			assertFalse(path.startsWith(start));
			assertNull(path.cutStart(start));
		}
	}


	@Test public void testExtension()
	{
		assertNull(Path.ROOT.getExtension());
		assertNull(new Path("a").getExtension());
		assertEquals("html", new Path("a.html").getExtension());
		assertEquals("en.html", new Path("a.en.html").getExtension());
	}
	
	
	@Test public void testLastSegment()
	{
		assertEquals("", Path.ROOT.getLastSegment());
		assertEquals("a", new Path("a").getLastSegment());
		assertEquals("b", new Path("a/b").getLastSegment());
	}

	
	@Test public void testAccessors()
	{
		Path path = new Path("test");
		assertEquals('/', path.charAt(0));
		assertEquals("te", path.subSequence(1, 3));
		assertEquals("/test", path.print());
		assertEquals("/test", path.getValue());
		assertEquals("/", new Path(null).print());
	}


	@Test public void testAddTo()
	{
		assertAddTo("",  	"", "/");
		assertAddTo("/", 	"", "/");
		assertAddTo("/abc", "", "/abc");
		
		assertAddTo("",		"/a", "/a");
		assertAddTo("b",	"/a", "b/a");
		assertAddTo("b/",	"/a", "b/a");
	}
	
	
	private void assertAddTo(String s, String path, String expected)
	{
		StringBuilder sb = new StringBuilder(s);
		new Path(path).addTo(sb);
		assertEquals(expected, sb.toString());
	}
	
	
	@Test public void testPrint()
	{
		TestPrintWriter out = new TestPrintWriter();
		
		Path x = new Path("/x");
		
		// subpath ~ root
		Path.ROOT.print(out, null);
		out.assertOut("/");
		Path.ROOT.print(out, "");
		out.assertOut("/");
		Path.ROOT.print(out, "/");
		out.assertOut("/");
		
		// not-root subpath starts with '/'
		Path.ROOT.print(out, "/y");
		out.assertOut("/y");
		x.print(out, "/y");
		out.assertOut("/x/y");

		// not-root subpath starts not with '/'
		Path.ROOT.print(out, "y");
		out.assertOut("/y");
		x.print(out, "y");
		out.assertOut("/x/y");
	}
	
	
	private static class TestPrintWriter extends PrintWriter
	{
		public TestPrintWriter()
		{
			super(new StringWriter());
		}
		
		
		public void assertOut(String expected)
		{
			StringWriter s 	= (StringWriter)out; 
			String actual 	= s.toString();
			s.getBuffer().setLength(0);
			assertEquals(expected, actual);
		}
		
	}
	
}
