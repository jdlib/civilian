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


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.response.UriEncoder;


public class RouteTest extends CivTest
{
	public static final PathParam<String> PATTERN1 = PathParams.forSegment("p1");
	public static final PathParam<String> PATTERN2 = PathParams.forSegment("p2");
	public static final PathParam<String> PATTERN3 = PathParams.forSegment("p3");
	public static final UriEncoder ENCODER = new UriEncoder();
	
	
	/**
	 * Test create methods.
	 */
	@Test public void testCreate()
	{
		// Route.root()
		assertCreate("/", 			RootRoute.class,		Route.root()); 

		// Route.path()
		assertCreate("/", 			RootRoute.class, 	 	Route.constant(null)); 
		assertCreate("/", 			RootRoute.class, 	 	Route.constant("")); 
		assertCreate("/", 			RootRoute.class, 	 	Route.constant("/"));
		assertCreate("/abc", 		ConstantRoute.class, 	Route.constant("/abc")); 
		assertCreate("abc", 		ConstantRoute.class, 	Route.constant("abc")); 
		assertCreate("/abc", 		ConstantRoute.class, 	Route.constant("/abc")); 
		assertCreate("http:x", 		ConstantRoute.class, 	Route.constant("http:x")); 
		assertCreate("http:x/", 	ConstantRoute.class, 	Route.constant("http:x/")); 
	}
	
	
	/**
	 * Test properties of root route.
	 */
	@Test public void testRootRoute()
	{
		Route route = Route.root();
		assertEquals("/", route.build(null, ENCODER));
		
		assertTrue(route.isRoot());
		assertEquals(1, route.size());
		assertEquals(0, route.getPathParamCount());
		assertEquals(null, route.getPathParam(0));
		assertEquals(-1, route.indexOf(PATTERN1));
		
		// can be invoked without failure and side-effects
		route.extractPathParams(null, null);

		assertSame(route, route.add((String)null));
		assertSame(route, route.add(""));
		assertSame(route, route.add("/"));

		Route route2 = route.add("def");
		assertNotSame(route2, route);
		assertEquals("/def", route2.build(null, ENCODER));
	}

	
	/**
	 * Test properties of constant routes.
	 */
	@Test public void testConstantRoute()
	{
		Route route = assertCreate("/abc", ConstantRoute.class, Route.constant("/abc"));
		assertEquals("/abc", route.build(null, ENCODER));
		
		assertFalse(route.isRoot());
		assertEquals(1, route.size());
		assertEquals(0, route.getPathParamCount());
		assertEquals(null, route.getPathParam(0));
		assertEquals(-1, route.indexOf(PATTERN1));
		
		// can be invoked without failure and side-effects
		route.extractPathParams(null, null);

		assertSame(route, route.add((String)null));
		assertSame(route, route.add(""));
		assertSame(route, route.add("/"));

		Route route2 = route.add("def");
		assertNotSame(route2, route);
		assertEquals("/abc/def", route2.build(null, ENCODER));
	
		assertCreate("/abc/{p1}", RouteList.class, route.add(PATTERN1));

		route = assertCreate("http://www.test.com/", ConstantRoute.class, Route.constant("http://www.test.com/"));
		route2 = route.add("/def");
		assertNotSame(route2, route);
		assertEquals("http://www.test.com/def", route2.build(null, ENCODER));
		assertTrue(route2 instanceof ConstantRoute);
	}
	
	
	/**
	 * Test Urls that start with an arbitrary prefix.
	 */
	public void testUrlConstantRoutes()
	{
		Route url1 = Route.constant("http://test.com");
		Route url2 = Route.constant("http://test.com/");
		assertEquals("http://test.com",  url1.build(null, ENCODER));
		assertEquals("http://test.com/", url1.build(null, ENCODER));
		
		assertEquals("http://test.com/customers",  url1.add("/customers").build(null, ENCODER));
		assertEquals("http://test.com/customers/", url1.add("/customers/").build(null, ENCODER));
		assertEquals("http://test.com/customers",  url2.add("/customers").build(null, ENCODER));
		assertEquals("http://test.com/customers/", url2.add("/customers/").build(null, ENCODER));
		
		Object[] params = new Object[] { "pp" }; 
		assertEquals("http://test.com/pp",  url1.add(PATTERN1).build(params, ENCODER));
		assertEquals("http://test.com/pp",  url2.add(PATTERN1).build(params, ENCODER));
	}
	
	
	/**
	 * Test Route.toString when the passed string builder is not empty.
	 * (as done by Url)
	 */
	public void testUrlBuild()
	{
		StringBuilder s = new StringBuilder("http://test.com/");
		
		Route.constant("/customers/").build(null, ENCODER);
		assertEquals("http://test.com/customers/", s.toString());

		Object[] params = new Object[] { "pp" }; 
		Route.root().add(PATTERN1).build(params, ENCODER);
		assertEquals("http://test.com/pp", s.toString());
	}
	

	
	/**
	 * Test properties of pattern routes.
	 */
	@Test public void testPatternRoute()
	{
		Route route = assertCreate("/{p1}", PathParamRoute.class, Route.root().add(PATTERN1)); 
		assertEquals(1, route.size());
		assertFalse(route.isRoot());
		
		// build fails if path param not set
		Object[] params = new Object[1]; 
		try
		{
			assertEquals("ignore", route.build(params, ENCODER));
			fail();
		}
		catch(IllegalStateException e)
		{
		}
		
		// build fails if path param has wrong value
		try
		{
			params[0] = Boolean.TRUE; // String is expected
			assertEquals("ignore", route.build(params, ENCODER));
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}

		params[0] = "www";
		assertEquals("/www", route.build(params, ENCODER));
		
		assertEquals(1, route.getPathParamCount());
		assertEquals(PATTERN1, route.getPathParam(0));
		assertEquals(null, route.getPathParam(1));
		assertEquals(0,  route.indexOf(PATTERN1));
		assertEquals(-1, route.indexOf(PATTERN2));

		assertSame(route, route.add((String)null));
		assertSame(route, route.add(""));
		assertSame(route, route.add("/"));
	}
	
	
	/**
	 * Test properties of route lists.
	 */
	@Test public void testRouteList()
	{
		Route route = assertCreate("/{p1}/x/{p2}", RouteList.class, Route.root().add(PATTERN1).add("x").add(PATTERN2)); 
		Object[] params = new Object[2]; 
		params[0] = "www";
		params[1] = "yyy";
		assertFalse(route.isRoot());
		assertEquals(3, route.size());
		assertEquals("/www/x/yyy", route.build(params, ENCODER));
		assertEquals(2, route.getPathParamCount());
		assertEquals(PATTERN1, route.getPathParam(0));
		assertEquals(PATTERN2, route.getPathParam(1));
		assertEquals(null, route.getPathParam(2));
		assertEquals(0,  route.indexOf(PATTERN1));
		assertEquals(1,  route.indexOf(PATTERN2));
		assertEquals(-1, route.indexOf(PATTERN3));

		assertSame(route, route.add((String)null));
		route = route.add("y");
		assertEquals(4, route.size());
		assertEquals("/{p1}/x/{p2}/y", route.toString());

		// test append of 
		route = assertCreate("/{p1}/x", RouteList.class, Route.root().add(PATTERN1).add("x")); 
		assertEquals(2, route.size());
		route = route.add("y");
		assertEquals(2, route.size());
		assertEquals("/{p1}/x/y", route.toString());
	}
	
	
	/**
	 * Make sure that patterns are properly escaped.
	 */
	@Test public void testPatternEscaping()
	{
		Route route = assertCreate("/{p1}/x", RouteList.class, Route.root().add(PATTERN1).add("x")); 

		Object[] params = new Object[1]; 
		params[0] = "%/ ";
		assertEquals("/%25%2F%20/x", route.build(params, ENCODER));
	}
	
	
	private Route assertCreate(Class<? extends Route> c, Route route)
	{
		assertEquals(c, route.getClass());
		return route;
	}
	
	
	@SuppressWarnings("unchecked")
	private <T extends Route> T assertCreate(String s, Class<T> c, Route route)
	{
		assertCreate(c, route);
		assertEquals(s, route.toString());
		return (T)route;
	}
}
