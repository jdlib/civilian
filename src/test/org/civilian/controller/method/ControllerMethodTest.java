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


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.response.Response;
import org.junit.Test;


public class ControllerMethodTest extends CivTest
{
	private static final MethodArgFactory ARG_FACTORY = new MethodArgFactory(PathParamMap.EMPTY, TYPELIB); 
	
	
	private static ControllerMethod getMethod(Class<?> c, String methodName)
	{
		Method method = findMethod(c, methodName);
		return ControllerMethod.create(ARG_FACTORY, method);
	}
	

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
		ControllerMethod method;
		
		method = getMethod(InheritBase.class, "get");
		assertNotNull	(method);
		assertEquals	("get", method.toString());
		assertEquals	(InheritBase.class, method.getDeclaringClass());
		assertIterator	(method.getRequestMethods(), "GET");
		assertTrue		(method.canInherit(InheritDerived1.class));
		assertFalse		(method.canInherit(InheritDerived2.class));
		assertFalse		(method.canInherit(Controller.class));
		assertFalse		(method.canInherit(null));
		assertEquals	("@Get", method.getInfo());
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
		ControllerMethod method;
		
		method = getMethod(ConsumeController.class, "consumeJson");
		assertTrue(method.canConsume(ContentType.APPLICATION_JSON));
		assertTrue(method.canConsume(ContentType.ANY));
		assertTrue(method.canConsume(null));
		assertFalse(method.canConsume(ContentType.TEXT_HTML));
		assertEquals(1, method.getConsumesContentTypes().size());
		assertEquals("@Get @Consumes(application/json)", method.getInfo());
		
		method = getMethod(ConsumeController.class, "consumeAll");
		assertTrue(method.canConsume(ContentType.APPLICATION_JSON));
		assertTrue(method.canConsume(ContentType.ANY));
		assertTrue(method.canConsume(ContentType.TEXT_HTML));
		assertSame(ContentTypeList.EMPTY, method.getConsumesContentTypes());
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
		ControllerMethod method;
		ContentNegotiation conneg;
		 
		method = getMethod(ProducesController.class, "producesSome");
		assertTrue(method.getProducedContentTypes().contains(ContentType.TEXT_CSS));
		assertEquals("@Get @Produces(text/css, text/html)", method.getInfo());
		conneg = new ContentNegotiation(ContentType.TEXT_CSS);
		assertTrue(method.canProduce(conneg));
		conneg = new ContentNegotiation(ContentType.APPLICATION_EXCEL);
		assertFalse(method.canProduce(conneg));

		method = getMethod(ProducesController.class, "producesAll");
		assertSame(ContentTypeList.EMPTY, method.getProducedContentTypes());
		conneg = new ContentNegotiation(ContentType.TEXT_CSS);
		assertTrue(method.canProduce(conneg));
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
		ControllerMethod method;

		method = getMethod(ArgController.class, "noArgs");
		assertEquals(0, method.getArgCount());

		method = getMethod(ArgController.class, "withArg");
		assertEquals(1, method.getArgCount());
	}


	private static class InvokeController extends Controller
	{
		@Get public void noResult()
		{
		}

		@Get public String withResult()
		{
			return "Hi";
		}
	}

	
	@Test public void testInvoke() throws Exception
	{
		ControllerMethod method;
		InvokeController controller = new InvokeController();
		Request request = null;
		Response response = mock(Response.class);

		method = getMethod(InvokeController.class, "noResult");
		method.invoke(controller, request, response);
		verify(response, times(0)).writeContent(any());

		method = getMethod(InvokeController.class, "withResult");
		method.invoke(controller, request, response);
		verify(response, times(1)).writeContent("Hi");
	}
}
