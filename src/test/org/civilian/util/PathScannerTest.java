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
package org.civilian.resource;


import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.junit.Test;
import org.civilian.CivTest;


public class PathScannerTest extends CivTest
{
	@Test public void testCreate()
	{
		PathScanner scanner;
		
		scanner = new PathScanner(null);
		assertEquals("", scanner.getPath());
		assertEquals(0, scanner.getPosition());
		assertFalse(scanner.hasMore());

		scanner = new PathScanner("");
		assertEquals("", scanner.getPath());
		assertEquals(0, scanner.getPosition());
		assertFalse(scanner.hasMore());

		scanner = new PathScanner("/");
		assertEquals("/", scanner.getPath());
		assertEquals(1, scanner.getPosition());
		assertFalse(scanner.hasMore());

		scanner = new PathScanner("/abc");
		assertEquals("/abc", scanner.getPath());
		assertEquals(1, scanner.getPosition());
		assertTrue(scanner.hasMore());
	}
	
	
	@Test public void testSegments()
	{
		PathScanner scanner;
		
		scanner = new PathScanner("abc/def");
		assertEquals(0, scanner.getPosition());
		assertTrue  (scanner.hasMore());
		assertTrue  (scanner.matchSegment("abc"));
		assertFalse (scanner.matchSegment("ab"));
		assertFalse (scanner.matchSegment("abc/"));
		assertFalse (scanner.matchSegment(""));
		assertEquals("abc", scanner.getSegment());
		scanner.next();
		assertTrue  (scanner.hasMore());
		assertTrue  (scanner.matchSegment("def"));
		assertFalse (scanner.matchSegment("d"));
		assertFalse (scanner.matchSegment("defg"));
		assertFalse (scanner.matchSegment(""));
		assertEquals("def", scanner.getSegment());
		scanner.next();
		assertFalse (scanner.hasMore());

		scanner = new PathScanner("/abc/");
		assertTrue  (scanner.hasMore());
		assertTrue  (scanner.matchSegment("abc"));
		scanner.next();
		assertFalse (scanner.hasMore());
		assertNull  (scanner.getSegment());	
	}


	@Test public void testPattern()
	{
		Pattern any    = Pattern.compile(".+"); 
		Pattern anySeg = Pattern.compile("[^/]+"); 
		Pattern alpha  = Pattern.compile("[a-z]+"); 
		Pattern number = Pattern.compile("[0-9]+");
		Pattern groups = Pattern.compile("a([a-z]{2})/1([0-9]{2})");
		
		PathScanner scanner = new PathScanner("abc/123");
		assertTrue(scanner.matchSegment("abc"));
		assertMatch(scanner.matchPattern(any), 		"abc/123");
		assertMatch(scanner.matchPattern(anySeg), 	"abc");
		assertMatch(scanner.matchPattern(alpha), 	"abc");
		assertMatch(scanner.matchPattern(number),	null);
		assertMatch(scanner.matchPattern(groups),	"abc/123", "bc", "23");
		scanner.next();
		assertTrue(scanner.matchSegment("123"));
		assertMatch(scanner.matchPattern(any), 		"123");
		assertMatch(scanner.matchPattern(anySeg), 	"123");
		assertMatch(scanner.matchPattern(alpha), 	null);
		assertMatch(scanner.matchPattern(number),	"123");

		scanner = new PathScanner("abc/123");
		assertEquals(0, scanner.getPosition());
		
		MatchResult result = assertMatch(scanner.matchPattern(alpha), "abc");
		scanner.next(result);
		assertEquals(4, scanner.getPosition());
		
		result = assertMatch(scanner.matchPattern(number), "123");
		scanner.next(result);
		assertEquals(8, scanner.getPosition());
	}
	

	/**
	 * Tests that extensions in the last segment and a trailing '/index' are ignored.
	 */
	@Test public void testRevert()
	{
		PathScanner scanner = new PathScanner("/a/b/c");
		int pos0 = scanner.getPosition();

		assertTrue(scanner.matchSegment("a"));
		scanner.next();

		assertTrue(scanner.matchSegment("b"));
		scanner.setPosition(pos0);

		assertTrue(scanner.matchSegment("a"));
	}
	
	
	
	/**
	 * Tests that extensions in the last segment and a trailing '/index' are ignored.
	 */
	@Test public void testRemoveExtAndIndex()
	{
		//-------------------------------------------
		// the extension is automatically stripped of the last segment
		PathScanner scanner = new PathScanner("test.html/test.html");
		// in the first segment the extension is still included
		assertFalse(scanner.matchSegment("test"));
		assertTrue (scanner.matchSegment("test.html"));
		scanner.next();
		assertTrue (scanner.hasMore());

		// in the last segement, the extension is ignored
		assertTrue (scanner.matchSegment("test"));
		assertFalse(scanner.matchSegment("test.html"));
		scanner.next();
		assertFalse(scanner.hasMore());
		
		//-------------------------------------------
		// a trailing /index is also removed
		scanner = new PathScanner("test/index");
		assertTrue (scanner.matchSegment("test"));
		scanner.next();
		assertFalse(scanner.hasMore());
		
		//-------------------------------------------
		// a trailing /index + extension is also removed
		scanner = new PathScanner("test/index.html");
		assertTrue (scanner.matchSegment("test"));
		scanner.next();
		assertFalse(scanner.hasMore());
	}
	
	
	
	private MatchResult assertMatch(MatchResult result, String match, String... groups)
	{
		if (match == null)
			assertNull(result);
		else
		{
			assertNotNull("result null", result);
			int n = Math.min(result.groupCount(), groups.length);
			for (int i=1; i<=n; i++)
				assertEquals(groups[i-1], result.group(i));
			assertEquals(result.groupCount(), groups.length);
		}
		return result;
	}
}
