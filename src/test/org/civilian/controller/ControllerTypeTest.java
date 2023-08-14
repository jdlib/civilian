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
package org.civilian.controller;


import java.lang.reflect.Method;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.Controller;
import org.civilian.annotation.Get;
import org.civilian.application.classloader.ClassLoaderFactory;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.request.Request;
import org.civilian.resource.PathParamMap;
import org.civilian.response.Response;
import org.civilian.type.TypeLib;


public class ControllerTypeTest extends CivTest
{
	public static class TestController extends Controller
	{
		@Get public void test1()
		{
		}

		@Get public void test2(Request request)
		{
		}
	}
	
	
	@Test public void test()
	{
		ControllerType type = service_.getControllerType(TestController.class);

		assertSame(TestController.class, type.getControllerClass());
		assertTrue(type.createController() instanceof TestController);
		
		// actions
		assertEquals(2, type.getMethodCount());
		ControllerMethod test1 = type.getMethod("test1");
		ControllerMethod test2 = type.getMethod("test2");
		assertEquals("test1", test1.getName());
		assertTrue(type.iterator().hasNext());
		assertTrue(type.contains(test1));
		assertTrue(test1 == type.getMethod(0) || test2 == type.getMethod(0));
		assertSame(test1, type.getMethod("test1"));
		assertSame(test1, type.getMethod("test1", (Class<?>[])null));
		assertSame(test2, type.getMethod("test2", Request.class));
		assertSame(test2, type.getMethod("test2", (Class<?>[])null));
		assertSame(test2, type.getMethod(test2.getJavaMethod()));
		assertNull(type.getMethod((Method)null));
		assertNull(type.getMethod("test2", Request.class, Request.class));
		assertNull(type.getMethod("test2", Response.class));
		assertEquals("Type:" + TestController.class.getName(), type.toString());
	}

	
	@Test public void testMethodForRequest()
	{
		ControllerType type = service_.getControllerType(TestController.class);
		
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn("POST");
		
		NegotiatedMethod negMethod = type.getMethod(request);
		assertEquals(Response.Status.METHOD_NOT_ALLOWED, negMethod.getError());

		when(request.getMethod()).thenReturn("GET");
		when(request.getContentType()).thenReturn(null);
		when(request.getAcceptedContentTypes()).thenReturn(new ContentTypeList(ContentType.ANY));
		negMethod = type.getMethod(request);
		assertNotNull(negMethod.getMethod());
		assertNotNull("test1", negMethod.getMethod().getName());

		negMethod = type.getMethod(request);
		assertNotNull(negMethod.getMethod());
		assertNotNull("test2", negMethod.getMethod().getName());
	}
	

	@Test public void testBase()
	{
		ControllerType type = service_.getControllerType(Controller.class);
		
		assertEquals(0, type.getMethodCount());
		assertFalse(type.contains(null));
	}
	
	
	private static TypeLib typeLib_ = new TypeLib();
	private static ControllerService service_ = new ControllerService(PathParamMap.EMPTY, typeLib_, null, new ClassLoaderFactory.Production());
}
