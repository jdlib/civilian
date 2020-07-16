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


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.Resource;
import org.civilian.Resource.Match;
import org.civilian.controller.ControllerService;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.ControllerType;
import org.civilian.testcase1.AlphaController;
import org.civilian.type.TypeLib;


public class ResourceTest extends CivTest
{
	@Test public void testRoot()
	{
		Resource root = new Resource();
		assertTrue	(root.isRoot());
		assertNull	(root.getParent());
		assertSame	(root, root.getRoot());
		assertEquals("", root.getSegment());
		assertNull	(root.getPathParam());
		assertSame	(Route.root(), root.getRoute());
		assertEquals(1, root.size());
		assertEquals(0, root.getChildCount());
		assertEquals("/", root.toString());
	}


	@Test public void testSegmentChild()
	{
		Resource root  = new Resource();
		Resource child = new Resource(root, "segment");
		
		assertFalse	(child.isRoot());
		assertSame	(root, child.getParent());
		assertSame	(root, child.getRoot());
		assertEquals("segment", child.getSegment());
		assertNull	(child.getPathParam());
		assertEquals(2, root.size());
		assertEquals(1, root.getChildCount());
		assertEquals(1, child.size());
		assertEquals(0, child.getChildCount());
		assertEquals("/segment", child.toString());
	}

	
	@Test public void testPathParamChild()
	{
		Resource root  = new Resource();
		Resource child = new Resource(root, PP_SEG);

		assertFalse	(child.isRoot());
		assertSame	(root, child.getParent());
		assertSame	(root, child.getRoot());
		assertNull	(child.getSegment());
		assertSame	(PP_SEG, child.getPathParam());
		assertEquals(2, root.size());
		assertEquals(1, root.getChildCount());
		assertEquals(1, child.size());
		assertEquals(0, child.getChildCount());
		assertEquals("/{ppseg}", child.toString());
		
		// cannot use the same path param in a route 
		try
		{
			new Resource(child, PP_SEG);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}
	

	@Test public void testChildOrder()
	{
		Resource root = new Resource();
		assertArrayEquals2(root.getChildren());
		
		Resource ppInt = new Resource(root, PP_INT);
		assertArrayEquals2(root.getChildren(), ppInt);
		
		// segments come before path params
		Resource seg2 = new Resource(root, "seg2");
		assertArrayEquals2(root.getChildren(), seg2, ppInt);

		// path params are ordered by name
		Resource ppSeg = new Resource(root, PP_SEG);
		assertArrayEquals2(root.getChildren(), seg2, ppInt, ppSeg);

		// segment params are ordered by name
		Resource seg1 = new Resource(root, "seg1");
		assertArrayEquals2(root.getChildren(), seg1, seg2, ppInt, ppSeg);
	}
	
	
	@Test public void testIterator()
	{
		Resource root  		= new Resource();
		Resource child2	 	= new Resource(root, 	"2");
		Resource child1	 	= new Resource(root, 	"1");
		Resource child11 	= new Resource(child1,	"1");
		
		Iterator<Resource> it = root.iterator();
		assertTrue(it.hasNext());
		try
		{
			it.remove();
			fail();
		}
		catch(UnsupportedOperationException e)
		{
		}
		
		assertSame(root, 	it.next());
		assertSame(child1,	it.next());
		assertSame(child11,	it.next());
		assertSame(child2,	it.next());
		assertFalse(it.hasNext());
		
		try
		{
			it.next();
			fail();
		}
		catch(NoSuchElementException e)
		{
		}
	}
	
	
	@Test public void testTree()
	{
		Resource root 	= new Resource();
		Resource child 	= new Resource(root, "a");
		
		Resource.Tree tree = root.getTree();
		assertSame(tree, child.getTree());
		
		assertNull(tree.getDefaultExtension());
		assertNull(tree.getControllerService());
		assertEquals("", tree.getAppPath().toString());
		
		tree.setDefaultExtension(".html");
		assertEquals("html", tree.getDefaultExtension());
	}

	
	@Test public void testControllerInfo()
	{
		Resource root = new Resource();
		assertNull(root.getControllerSignature());

		root.setControllerSignature(null);
		assertNull(root.getControllerSignature());
		
		root.setControllerSignature("test.Controller", "path");
		ControllerSignature sig = root.getControllerSignature();
		assertEquals("test.Controller", sig.getClassName());
		assertEquals("path", sig.getMethodName());
	}
	
	
	@Test public void testControllerService()
	{
		ControllerService service = mock(ControllerService.class);
		
		Resource root = new Resource();
		
		root.getTree().setControllerService(service);
		assertEquals(service, root.getTree().getControllerService());
		root.getTree().setControllerService(service);
		assertEquals(service, root.getTree().getControllerService());
	}
	
	
	@SuppressWarnings("boxing")
	@Test public void testTypeProvider()
	{
		Resource root = new Resource();
		
		// empty
		assertNull(root.getControllerType());
		
		// unavailable
		root.setControllerSignature("MyController", null);
		try
		{
			root.getControllerType();
			fail();
		}
		catch(IllegalStateException e)
		{
			assertEquals("ControllerType unavailable: resource not connected with ControllerService", e.getMessage());
		}
		
		// forwarding: every call for the controllertype is directed to the service
		ControllerService service = mock(ControllerService.class);
		when(service.isReloading()).thenReturn(true);
		root.getTree().setControllerService(service);
		root.getControllerType();
		verify(service).getControllerType(root.getControllerSignature());
		root.getControllerType();
		verify(service, times(2)).getControllerType(root.getControllerSignature());
		
		// caching: controller type is asked one time and then cached in the source
		ControllerType type = mock(ControllerType.class);
		when(service.isReloading()).thenReturn(false);
		root.setControllerSignature("Ctrl2", null);
		when(service.getControllerType(root.getControllerSignature())).thenReturn(type);
		
		assertSame(type, root.getControllerType());
		verify(service, times(1)).getControllerType(root.getControllerSignature());
		
		assertSame(type, root.getControllerType());
		verify(service, times(1)).getControllerType(root.getControllerSignature());
	}
	
	
	
	@Test public void testMatch()
	{
		Resource root 	= new Resource();
		Resource seg  	= new Resource(root, "seg");
		Resource ppInt  = new Resource(root, PP_INT);
		ppInt.setControllerSignature("test.Controller", null);
		
		MatchAssert a = new MatchAssert(root);
		
		a.init("/")
			.complete(true)
			.resource(root)
			.params(0);
		
		a.init("/123")
			.complete(true)
			.resource(ppInt)
			.params(1)
			.param(PP_INT, new Integer(123));
		
		a.init("/123/abc")
			.complete(false)
			.resource(ppInt)
			.params(1)
			.param(PP_INT, new Integer(123));
		
		a.init("/seg")
			.complete(true)
			.resource(seg)
			.params(0);
		
		// PathParams are allowed to consume segments from the PathScanner
		// even if they don't recognize a value. The match algorithm must take this
		// into account.
		PathParam<Integer> ppa 	= PathParams.forSegment("a").converting(TypeLib.INTEGER).precededBySegment("p"); 
		PathParam<String>  ppb 	= PathParams.forSegment("b"); 
		Resource root2 			= new Resource();
		Resource ra  			= new Resource(root2, ppa);
		Resource rb  			= new Resource(root2, ppb);
		assertArrayEquals2(root2.getChildren(), ra, rb); // sorted by param name
		
		a = new MatchAssert(root2);
		
		a.init("/p/123")
			.complete(true)
			.resource(ra)
			.params(1)
			.param(ppa, new Integer(123));

		a.init("/p")
			.complete(true)
			.resource(rb)
			.params(1)
			.param(ppb, "p");
	}
	
	
	private static class MatchAssert
	{
		private MatchAssert(Resource resource)
		{
			resource_ = resource;
		}
		
		
		public MatchAssert init(String path)
		{
			match_ = resource_.match(path);
			return this;
		}
		
		
		public MatchAssert complete(boolean expected)
		{
			assertEquals(expected, match_.completeMatch);
			return this;
		}
		
		
		public MatchAssert params(int size)
		{
			assertEquals(size, match_.pathParams.size());
			return this;
		}

		
		public <T> MatchAssert param(PathParam<T> pp, T expected)
		{
			assertEquals(expected, match_.pathParams.get(pp));
			return this;
		}

		
		public MatchAssert resource(Resource expected)
		{
			assertSame(expected, match_.resource);
			return this;
		}

		
		private final Resource resource_;
		private Match match_;
	}
		
	
	@Test public void testPrint()
	{
		Resource root = new Resource();
		PrintStream s = new PrintStream(new ByteArrayOutputStream());
		root.print(s);
	}

	
	@Test public void testTouch() throws Exception
	{
		Resource root = new Resource();
		root.setControllerSignature(AlphaController.class.getName(), null);
		
		new Resource(root, "a");
		
		root.touchControllerClasses();
	}
	
	
	private static PathParam<String>  PP_SEG = PathParams.forSegment("ppseg"); 
	private static PathParam<Integer> PP_INT = PathParams.forSegment("ppint").converting(TypeLib.INTEGER); 
}
