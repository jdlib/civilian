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


import java.util.regex.Pattern;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.response.UriEncoder;
import org.civilian.type.TypeLib;
import org.civilian.util.Date;


public class PathParamTest extends CivTest
{
	@SuppressWarnings("boxing")
	@Test public void testSegment()
	{
		// PathParams.forSegment()
		PathParam<String> stringPP = PathParams.forSegment("stringparam");
		assertEquals("/{stringparam}", stringPP.toString());
		PathScanner scanner = new PathScanner("/abc/def");
		assertParse(stringPP, scanner,	"abc");
		assertParse(stringPP, scanner,	"def");
		assertBuild(stringPP, "a b", 	"/a%20b");

		// PathParams.forIntSegment()
		PathParam<Integer> intPP = PathParams.forIntSegment("intparam");
		assertEquals("/{intparam}", intPP.toString());
		scanner = new PathScanner("/123/def");
		assertParse(intPP, scanner, new Integer(123));
		assertNull (intPP.parse(scanner));
		assertBuild(intPP, 456, "/456");

		// PathParams.forDateSegment()
		PathParam<Date> datePP = PathParams.forDateSegment("dateparam", TypeLib.DATE_CIVILIAN);
		assertEquals("/{dateparam}", datePP.toString());
		scanner = new PathScanner("/20121103/abc");
		assertParse(datePP, scanner, new Date(2012, 11, 03));
		assertNull (datePP.parse(scanner));
		assertBuild(datePP, new Date(2012, 12, 04), "/20121204");

		// PathParams.forSegment(Type)
		PathParam<Double> doublePP = PathParams.forSegment("doubleparam", TypeLib.DOUBLE);
		assertEquals("/{doubleparam}", doublePP.toString());
		scanner = new PathScanner("/1.234/abc");
		assertParse(doublePP, scanner, new Double(1.234));
		assertNull (doublePP.parse(scanner));
		assertBuild(doublePP, new Double(12.34), "/12.34");

		// PathParams.forSegmentPattern()
		PathParam<String> wcPP = PathParams.forSegmentPattern("idparam", "id*");
		assertEquals("/{idparam}", wcPP.toString());
		assertEquals("/{idparam : String=id*}", wcPP.toDetailedString());
		scanner = new PathScanner("/id123/def");
		assertParse(wcPP, scanner, "123");
		assertNull (wcPP.parse(scanner));
		assertBuild(wcPP, "789", "/id789");
	}


	@Test public void testYMD()
	{
		PathParam<Date> pathParam = PathParams.forYearMonthDay("ymd", TypeLib.DATE_CIVILIAN);
		assertEquals("/{ymd}", pathParam.toString());
		assertEquals("/{ymd : Date=yyyy/mm/dd}", pathParam.toDetailedString());
		PathScanner scanner = new PathScanner("/2012/11/10/2012/13/13");
		assertParse(pathParam, scanner, new Date(2012, 11, 10));
		assertNull(pathParam.parse(scanner));
		assertBuild(pathParam, new Date(2011, 10, 9), "/2011/10/09");
	}


	@Test public void testRegex()
	{
		PathParam<String> ppId = PathParams.forPattern("id", Pattern.compile("id([^/]+)"), "id*");
		assertEquals("/{id}", ppId.toString());
		PathScanner scanner = new PathScanner("/id0123/else");
		assertParse	(ppId, scanner, "0123");
		assertNull	(ppId.parse(scanner));
		assertBuild	(ppId, "456", "/id456");

		PathParam<Integer> ppNr = PathParams.forPattern("nr", Pattern.compile("nr([0-9]+)"), "nr*", TypeLib.INTEGER);
		assertEquals("/{nr}", ppNr.toString());
		assertEquals("/{nr : Integer=nr([0-9]+)}", ppNr.toDetailedString());

		scanner = new PathScanner("/nr0123/else");
		assertParse	(ppNr, scanner, new Integer(123));
		assertNull	(ppNr.parse(scanner));
		assertBuild	(ppNr, new Integer(456), "/nr456");
		
		// type is integer, but patter does not completely enforce it: but still the type check fails
		PathParam<Integer> ppNrSloppy = PathParams.forPattern("nr", Pattern.compile("nr([^/]+)"), "nr*", TypeLib.INTEGER);
		scanner = new PathScanner("/nr9/nrx");
		assertParse	(ppNrSloppy, scanner, Integer.valueOf(9));
		assertNull	(ppNrSloppy.parse(scanner));
	}
	
	
	@Test public void testMultiSegment()
	{
		PathParam<String[]> multi = PathParams.forMultiSegments("multi", 2);
		assertEquals("/{multi}", multi.toString());
		assertEquals("/{multi : String[]=minSize=2}", multi.toDetailedString());
		
		String s[] = { "a", "b", "c" };
		assertBuild	(multi, s, "/a/b/c");
		
		PathScanner scanner = new PathScanner("/a");
		assertNull	(multi.parse(scanner));
		assertTrue(scanner.matchSegment("a")); // not consumed

		scanner = new PathScanner("/a/b/c");
		assertArrayEquals2(s, multi.parse(scanner));
		assertFalse(scanner.hasMore());
	}
	
	
	private <T> void assertParse(PathParam<T> pattern, PathScanner scanner, T value)
	{
		assertEquals(value, pattern.parse(scanner));
	}

	
	private <T> void assertBuild(PathParam<T> pattern, T value, String path)
	{
		assertEquals(path, pattern.buildPath(value, uriEncoder_));
	}
	

	private static final UriEncoder uriEncoder_ = new UriEncoder(); 
}
