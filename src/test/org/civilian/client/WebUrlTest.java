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
package org.civilian.client;


import org.civilian.CivTest;
import org.civilian.testcase1.Test1PathParams;
import org.civilian.type.TypeLib;
import org.junit.Test;


public class WebUrlTest extends CivTest
{
	@Test public void testCreate()
	{
		WebUrl url;
		
		url = new WebUrl("http://acme.org/");
		assertEquals("http://acme.org/", url.toString());
		
		url = new WebUrl(new Test1WebRoot("x"));
		assertEquals("x", url.toString());

		url = new WebUrl(new Test1WebRoot("x").getRoute());
		assertEquals("x", url.toString());
	}
	
	
	@Test public void testAddPath()
	{
		WebUrl url = new WebUrl("x");
		assertEquals("x", url.toString());
		
		url.addPath("/path");
		assertEquals("x/path", url.toString());

		url.addPath("/another");
		assertEquals("x/path/another", url.toString());
	}
	
	
	@Test public void testQueryParams()
	{
		WebUrl url = new WebUrl("http://acme.org/");
		assertEquals(0, url.getQueryParamCount());
		url.clearQueryParams();
		
		assertEquals(0, url.getQueryParamCount());
		
		url.addEmptyQueryParam("test");
		assertEquals(1, url.getQueryParamCount());
		url.clearQueryParams();
		assertEquals(0, url.getQueryParamCount());
	
		assertNull(url.getQueryParam("p", false));
		WebUrl.QueryParam p = url.getQueryParam("p", true);  
		assertSame(p, url.getQueryParam(0));  
		assertSame(p, url.getQueryParam("p", true));
		assertEquals(1, url.getQueryParamCount());
		
		url.removeQueryParam("p");
		assertEquals(0, url.getQueryParamCount());

		p = url.getQueryParam("p", true);
		assertEquals(1, url.getQueryParamCount());
		url.removeQueryParam(p);
		assertEquals(0, url.getQueryParamCount());
		url.removeQueryParam((WebUrl.QueryParam)null);
		assertEquals(0, url.getQueryParamCount());
		
		url.addQueryParam("qps", "sv");
		url.addQueryParam("qpi1", 123);
		url.addQueryParam("qpi2", Integer.valueOf(456));
		url.addQueryParam("qpb1", true);
		url.addQueryParam("qpb2", Boolean.FALSE);
		url.addQueryParam("qpd", TypeLib.DOUBLE, Double.valueOf(12.34));
	}


	@Test public void testFragment()
	{
		WebUrl url = new WebUrl("test");
		assertNull(url.getFragment());
		
		url.setFragment("frag");
		assertEquals("frag", url.getFragment());

		assertEquals("test#frag", url.toString());
	}


	@Test public void testPathParams()
	{
		Test1WebRoot root = new Test1WebRoot("http://server");
		
		WebUrl url = new WebUrl(root);
		assertEquals(0, url.getQueryParamCount());

		url = new WebUrl(root.$gammaId);
		assertEquals(1, url.getPathParamCount());
		
		assertEquals(Test1PathParams.GAMMA, url.getPathParamDef(0));
		assertNull(url.getPathParam(0));
		assertNull(url.getPathParam(Test1PathParams.GAMMA));
		assertNull(url.getPathParam(Test1PathParams.BETA));
		
		Integer v1 = Integer.valueOf(123);
		url.setPathParam(v1);
		assertEquals(v1, url.getPathParam(0));
		
		Integer v2 = Integer.valueOf(456);
		url.setPathParam(0, v2);
		assertSame(v2, url.getPathParam(0));
		url.setPathParams(v1);
		assertSame(v1, url.getPathParam(0));
		
		url.setPathParam(Test1PathParams.GAMMA, 789);
		assertEquals(Integer.valueOf(789), url.getPathParam(0));
		
		WebUrl url2 = new WebUrl(root.$gammaId);
		url2.copyPathParams(url);
		assertEquals(Integer.valueOf(789), url2.getPathParam(0));
		
		
		url.clearPathParams();
		assertNull(url.getPathParam(0));
	}


	@Test public void testToString()
	{
		WebUrl url = new WebUrl("http://test.com");
		url.addPath("segment");
		url.addPath("path.txt");
		url.addQueryParam("x");
		url.addQueryParam("y", "1");
		assertEquals("http://test.com/segment/path.txt?x=&y=1", url.toString());
	}
}
