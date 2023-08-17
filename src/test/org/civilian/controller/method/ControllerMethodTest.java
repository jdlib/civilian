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
package org.civilian.controller.method;


import java.lang.reflect.Method;
import org.civilian.CivTest;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.content.ContentNegotiation;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.controller.Controller;
import org.civilian.controller.method.arg.factory.MethodArgFactory;
import org.civilian.request.Request;
import org.civilian.resource.PathParamMap;
import org.civilian.type.TypeLib;
import org.junit.Test;


public class ControllerMethodTest extends CivTest
{
	private ControllerMethod getMethod(Class<?> c, String methodName)
	{
		MethodArgFactory argFactory = new MethodArgFactory(PathParamMap.EMPTY, typeLib_); 
		Method method = findMethod(c, methodName);
		return ControllerMethod.create(argFactory, method);
	}
	

	//-----------------------
	// classes with test methods
	//-----------------------
	
	private static class InheritBase extends Controller
	{
		@Get public void get()
		{
		}
	}
	
	
	private static class InheritDerived1 extends InheritBase
	{
	}

	
	private static class InheritDerived2 extends InheritBase
	{
		@Override public void get()
		{
		}
	}

	
	@Test public void testInherit() throws Exception
	{
		ControllerMethod action;
		
		action = getMethod(InheritBase.class, "get");
		assertNotNull	(action);
		assertEquals	("get", action.toString());
		assertEquals	(InheritBase.class, action.getDeclaringClass());
		assertIterator	(action.getRequestMethods(), "GET");
		assertTrue		(action.canInherit(InheritDerived1.class));
		assertFalse		(action.canInherit(InheritDerived2.class));
		assertFalse		(action.canInherit(Controller.class));
		assertFalse		(action.canInherit(null));
		assertEquals	("@Get", action.getInfo());
	}
	
	
	private static class ConsumeController extends Controller
	{
		@Get @Consumes("application/json") public void consumeJson()
		{
		}

		@Get public void consumeAll()
		{
		}
	}

	
	@Test public void testConsume() throws Exception
	{
		ControllerMethod action;
		
		action = getMethod(ConsumeController.class, "consumeJson");
		assertTrue(action.canConsume(ContentType.APPLICATION_JSON));
		assertTrue(action.canConsume(ContentType.ANY));
		assertTrue(action.canConsume(null));
		assertFalse(action.canConsume(ContentType.TEXT_HTML));
		assertEquals(1, action.getConsumesContentTypes().size());
		assertEquals("@Get @Consumes(application/json)", action.getInfo());
		
		action = getMethod(ConsumeController.class, "consumeAll");
		assertTrue(action.canConsume(ContentType.APPLICATION_JSON));
		assertTrue(action.canConsume(ContentType.ANY));
		assertTrue(action.canConsume(ContentType.TEXT_HTML));
		assertSame(ContentTypeList.EMPTY, action.getConsumesContentTypes());
	}

		
	private static class ProducesController extends Controller
	{
		@Get @Produces({ "text/css", "text/html" }) public void producesSome()
		{
		}

		@Get public void producesAll()
		{
		}
	}

	
	@Test public void testProduces() throws Exception
	{
		ControllerMethod action;
		ContentNegotiation conneg;
		 
		action = getMethod(ProducesController.class, "producesSome");
		assertTrue(action.getProducedContentTypes().contains(ContentType.TEXT_CSS));
		assertEquals("@Get @Produces(text/css, text/html)", action.getInfo());
		conneg = new ContentNegotiation(ContentType.TEXT_CSS);
		assertTrue(action.canProduce(conneg));
		conneg = new ContentNegotiation(ContentType.APPLICATION_EXCEL);
		assertFalse(action.canProduce(conneg));

		action = getMethod(ProducesController.class, "producesAll");
		assertSame(ContentTypeList.EMPTY, action.getProducedContentTypes());
		conneg = new ContentNegotiation(ContentType.TEXT_CSS);
		assertTrue(action.canProduce(conneg));
	}
		

	private static class ArgController extends Controller
	{
		@Get public void noArgs()
		{
		}

		@Get public void withArg(Request request)
		{
		}
	}

	

	@Test public void testArgs() throws Exception
	{
		ControllerMethod action;

		action = getMethod(ArgController.class, "noArgs");
		assertEquals(0, action.getArgCount());

		action = getMethod(ArgController.class, "withArg");
		assertEquals(1, action.getArgCount());
	}
	
	
	private static TypeLib typeLib_ = new TypeLib();
}
