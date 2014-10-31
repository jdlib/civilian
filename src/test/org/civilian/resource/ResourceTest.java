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
import org.civilian.controller.ControllerService;
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
		Resource child = new Resource(root, PP1);

		assertFalse	(child.isRoot());
		assertSame	(root, child.getParent());
		assertSame	(root, child.getRoot());
		assertNull	(child.getSegment());
		assertSame	(PP1, child.getPathParam());
		assertEquals(2, root.size());
		assertEquals(1, root.getChildCount());
		assertEquals(1, child.size());
		assertEquals(0, child.getChildCount());
		assertEquals("/{pp1}", child.toString());
		
		// cannot use the same path param in a route 
		try
		{
			new Resource(child, PP1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}
	

	@Test public void testUndecidedChild()
	{
		Resource root = new Resource();
		try
		{
			new Resource(root, null, null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}

		try
		{
			new Resource(root, "x", PP1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}
	
	
	@Test public void testChildOrder()
	{
		Resource root	= new Resource();
		
		Resource pp2 = new Resource(root, PP2);
		assertEquals(1, root.getChildCount());
		assertSame	(pp2, root.getChild(0));
		
		// segments came before path params
		Resource seg2 = new Resource(root, "seg2");
		assertEquals(2, root.getChildCount());
		assertSame	(seg2, root.getChild(0));
		assertSame	(pp2, root.getChild(1));

		// path params are ordered by name
		Resource pp1 = new Resource(root, PP1);
		assertEquals(3, root.getChildCount());
		assertSame	(seg2, root.getChild(0));
		assertSame	(pp1, root.getChild(1));
		assertSame	(pp2, root.getChild(2));

		// segment params are ordered by name
		Resource seg1 = new Resource(root, "seg1");
		assertEquals(4, root.getChildCount());
		assertSame	(seg1, root.getChild(0));
		assertSame	(seg2, root.getChild(1));
		assertSame	(pp1, root.getChild(2));
		assertSame	(pp2, root.getChild(3));
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
		assertEquals("test.Controller:path", root.getControllerSignature());
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
		Resource root = new Resource();
		Resource seg  = new Resource(root, "seg");
		Resource pp2  = new Resource(root, PP2);
		pp2.setControllerSignature("test.Controller", null);
		
		Resource.Match match;
		
		match = root.match("/");
		assertEquals(true, match.completeMatch);
		assertTrue(match.pathParams.isEmpty());

		match = root.match("/123");
		assertEquals(true, match.completeMatch);
		assertEquals(1, match.pathParams.size());
		assertEquals(new Integer(123), match.pathParams.get(PP2));

		match = root.match("/123/abc");
		assertEquals(false, match.completeMatch);
		assertSame(pp2, match.resource);

		match = root.match("/seg");
		assertEquals(true, match.completeMatch);
		assertSame(seg, match.resource);
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
		root.setControllerSignature(AlphaController.class.getName());
		
		new Resource(root, "a");
		
		root.touchControllerClasses();
	}
	
	
	private static PathParam<String>  PP1 = PathParams.forSegment("pp1"); 
	private static PathParam<Integer> PP2 = PathParams.forSegment("pp2", TypeLib.INTEGER); 
}
