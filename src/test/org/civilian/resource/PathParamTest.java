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


import java.time.LocalDate;
import java.util.regex.Pattern;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.type.TypeLib;


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
		PathParamAssert.of(PathParams.forSegment("some").converting(TypeLib.INTEGER, "intparam"))
			.toString("/{intparam}")
			.build(456, "/456")
			.scan("/abc/def", null, "def")		// first segment consumed but could not convert	
			.scan("/456/def", 456,  "def");		// first segment consumed and successfully converted
		
		// PathParams.forSegment(Type)
		PathParamAssert.of(PathParams.forSegment("dateparam").converting(TypeLib.DATE_LOCAL))
			.toString("/{dateparam}")
			.build(LocalDate.of(2012, 12, 04), "/20121204")
			.scan("/abc/def", 	   null, "def")							// first segment consumed but could not convert	
			.scan("/20121103/def", LocalDate.of(2012, 11, 03), "def");	// first segment consumed and successfully converted

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
		PathParamAssert.of(PathParams.forYearMonthDay("ymd", TypeLib.DATE_LOCAL))
			.toString("/{ymd}")
			.toDetailedString("/{ymd : LocalDate=yyyy/mm/dd}")
			.build(LocalDate.of(2011, 10, 9), "/2011/10/09")
			.scan("/abc/def", null, "abc")
			.scan("/2012/11/10/def", LocalDate.of(2012, 11, 10), "def");
	}


	@Test public void testRegex()
	{
		PathParamAssert.of(PathParams.forPattern("id", Pattern.compile("id([^/]+)"), "id*"))
			.toString("/{id}")
			.build("456", "/id456")
			.scan("/abc/def", null, "abc")
			.scan("/id0123/def", "0123", "def");

		PathParamAssert.of(PathParams.forPattern("nr", Pattern.compile("nr([0-9]+)"), "nr*").converting(TypeLib.INTEGER))
			.toString("/{nr}")
			.toDetailedString("/{nr : Integer=nr([0-9]+)}")
			.build(new Integer(456), "/nr456")
			.scan("/abc/def", null, "abc")
			.scan("/nr0123/def", new Integer(123), "def");
		
		// type is integer, but pattern does not completely enforce it: but still the type check fails
		PathParamAssert.of(PathParams.forPattern("nr", Pattern.compile("nr([^/]+)"), "nr*").converting(TypeLib.INTEGER))
			.scan("/abc/def", null, "abc")				// not consumed
			.scan("/nrx/def", null, "def")				// consumed but not converted
			.scan("/nr9/def", new Integer(9), "def");	// consumed and converted
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
		
		PathParamAssert.of(PathParams.forSegment("conv").converting(TypeLib.INTEGER))
			.toString("/{conv}")
			.toDetailedString("/{conv : Integer=/<segment>}")
			.build(n, "/1234")
			.scan("/abc/def", null, "def")		// abc could not be converted to an integer but was consumed
			.scan("/1234/def", n, "def");
	}
	
	
	@Test public void testPrefixed()
	{
		PathParamAssert.of(PathParams.forSegment("prefixed").precededBySegment("p"))
			.toString("/{prefixed}")
			.toDetailedString("/{prefixed : String=/p/<segment>}")
			.build("a", "/p/a")
			.scan("/abc/def", null, "abc")		// prefix segment not recognized, nothing consumed
			.scan("/p", null, null)				// prefix segment recognized and consumed, but no value returned
			.scan("/p/def/ghi", "def", "ghi")	// prefix segment recognized and consumed, value segment consumed and returned
		;
	}
}
