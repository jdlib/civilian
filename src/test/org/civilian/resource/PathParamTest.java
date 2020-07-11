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
import org.civilian.type.TypeLib;
import org.civilian.util.Date;


public class PathParamTest extends CivTest
{
	@SuppressWarnings("boxing")
	@Test public void testSegment()
	{
		// PathParams.forSegment()
		PathParamAssert.of(PathParams.forSegment("stringparam"))
			.toString("/{stringparam}")
			.build("a b", "/a%20b")
			.scan("/abc/def", "abc", "def");
		
		// PathParams.forIntSegment()
		PathParamAssert.of(PathParams.forIntSegment("intparam"))
			.toString("/{intparam}")
			.build(456, "/456")
			.scan("/abc/def", null, "abc")
			.scan("/456/def", 456,  "def");
		
		// PathParams.forSegment(Type)
		PathParamAssert.of(PathParams.forSegment("dateparam", TypeLib.DATE_CIVILIAN))
			.toString("/{dateparam}")
			.build(new Date(2012, 12, 04), "/20121204")
			.scan("/abc/def", null, "abc")
			.scan("/20121103/def", new Date(2012, 11, 03), "def");

		// PathParams.forSegmentPattern()
		PathParamAssert.of(PathParams.forSegmentPattern("idparam", "id*"))
			.toString("/{idparam}")
			.toDetailedString("/{idparam : String=id*}")
			.build("789", "/id789")
			.scan("/abc/def", null, "abc")
			.scan("/id123/def", "123", "def");
	}


	@Test public void testYMD()
	{
		PathParamAssert.of(PathParams.forYearMonthDay("ymd", TypeLib.DATE_CIVILIAN))
			.toString("/{ymd}")
			.toDetailedString("/{ymd : Date=yyyy/mm/dd}")
			.build(new Date(2011, 10, 9), "/2011/10/09")
			.scan("/abc/def", null, "abc")
			.scan("/2012/11/10/def", new Date(2012, 11, 10), "def");
	}


	@Test public void testRegex()
	{
		PathParamAssert.of(PathParams.forPattern("id", Pattern.compile("id([^/]+)"), "id*"))
			.toString("/{id}")
			.build("456", "/id456")
			.scan("/abc/def", null, "abc")
			.scan("/id0123/def", "0123", "def");

		PathParamAssert.of(PathParams.forPattern("nr", Pattern.compile("nr([0-9]+)"), "nr*", TypeLib.INTEGER))
			.toString("/{nr}")
			.toDetailedString("/{nr : Integer=nr([0-9]+)}")
			.build(new Integer(456), "/nr456")
			.scan("/abc/def", null, "abc")
			.scan("/nr0123/def", new Integer(123), "def");
		
		// type is integer, but pattern does not completely enforce it: but still the type check fails
		PathParamAssert.of(PathParams.forPattern("nr", Pattern.compile("nr([^/]+)"), "nr*", TypeLib.INTEGER))
			.scan("/abc/def", null, "abc")
			.scan("/nrx/def", null, "nrx")
			.scan("/nr9/def", new Integer(9), "def");
	}
	
	
	@Test public void testMultiSegment()
	{
		String s[] = { "a", "b", "c" };

		PathParamAssert.of(PathParams.forMultiSegments("multi", 2))
			.toString("/{multi}")
			.toDetailedString("/{multi : String[]=minSize=2}")
			.build(s, "/a/b/c")
			.scan("/a", null, "a") // not consumed
			.scan("/a/b/c", s);
	}
	
	
	@Test public void testConverting()
	{
		Integer n = Integer.valueOf(1234);
		
		PathParamAssert.of(PathParams.converting(PathParams.forSegment("conv"), TypeLib.INTEGER))
			.toString("/{conv}")
			.toDetailedString("/{conv : Integer=/<segment>}")
			.build(n, "/1234")
			.scan("/abc/def", null, "abc")
			.scan("/1234/def", n, "def");
	}
	
	
	@Test public void testPrefixed()
	{
		PathParamAssert.of(PathParams.prefixed("p", PathParams.forSegment("prefixed")))
			.toString("/{prefixed}")
			.toDetailedString("/{prefixed : String=/p/<segment>}")
			.build("a", "/p/a")
			.scan("/abc/def", null, "abc")
			.scan("/p/def/ghi", "def", "ghi")
		;
	}
}
