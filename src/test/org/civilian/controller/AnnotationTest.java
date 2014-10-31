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
import java.util.Iterator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.annotation.BeanParam;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.CookieParam;
import org.civilian.annotation.DefaultValue;
import org.civilian.annotation.Get;
import org.civilian.annotation.Head;
import org.civilian.annotation.MatrixParam;
import org.civilian.annotation.Options;
import org.civilian.annotation.PathParam;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.annotation.Parameter;
import org.civilian.annotation.RequestContent;
import org.civilian.annotation.RequestMethod;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.controller.ControllerMethod;
import org.civilian.resource.PathParamMap;
import org.civilian.resource.PathParams;
import org.civilian.response.protocol.NgReply;
import org.civilian.type.TypeLib;
import org.civilian.util.Value;


public class AnnotationTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		typeLib_ 	= new TypeLib();
		pathParams_ = new PathParamMap(null);
		pathParams_.addAndSeal(PathParams.forIntSegment("customerId"));
	}
	
	
	//--------------------------------
	// Base and Demo define methods which we use as input
	// to ControllerMethod.create.
	// actually it is not required to be part of a Controller class
	// to generate a ControllerMethod
	
	public static class Base
	{
		@Get public void overwritten()
		{
		}
	}
	

	// defines invalid actions methods 
	public static class Ignored  
	{
		public void noMethodAnnotation()
		{
			// not a ControllerMethod since it has no RequestMethod annotation
		}
		
		@Get public static void staticMethod()
		{
			// not a ControllerMethod since it is static
		}

		@Get public static int notVoid()
		{
			// not a ControllerMethod since the return type is not void
			return 1;
		}
	}
	
	
	public static class Demo extends Base 
	{
		@RequestMethod("PATCH")
		public void recognizedExplicit()
		{
			// Action is recognized when using the RequestMethod annotation  
		}
		
		@Options
		public void recognizedAbbreviated()
		{
			// Action is recognized when using a abbreviated method annotations
		}

		@Get
		@RequestMethod({"GET", "POST", "GET", "PATCH"})
		public void methodReduction()
		{
			// artificial cumulation is reduced to a proper set
		}

		@Post public void extensionNone()
		{
			// this action only accepts requests with extension null 
		}

		@Head public void producesAny()
		{
			// this action promises to produce any content type
		}

		@Get @Produces(ContentType.Strings.TEXT_HTML)
		public void producesHtml()
		{
			// this action promises to produce text/html
		}
		
		@Get @Produces({ContentType.Strings.ANY, "text/*", ContentType.Strings.TEXT_HTML})
		public void producesMulti()
		{
			// this action promises to produce text/html and text/* and */*:
			// they are sorted by specificity, more specific first
		}
		
		@Get @Produces("text/html; q=0.5")
		public void producesQualified()
		{
			// this action promises to produce text/html with a quality of 0.5:
		}
		
		@Post public void consumesAny()
		{
		}

		@Post @Consumes("application/json, text/xml")
		public void consumesSome()
		{
		}
		
		
		@Get public void inject(
			@Parameter("min") @DefaultValue("1") int minValue,
			@Parameter("max") int maxValue, 
			@Parameter("val") Value<Integer> wrapped, 
			@MatrixParam("mode") short mode,
			@PathParam("customerId") Integer customerId,
			@CookieParam("store") String test,
			@BeanParam Bean bean,
			@RequestContent String content,
			Request request,
			Response response,
			NgReply reply,
			@Custom Object custom)
		{
		}
		

		@Override public void overwritten()
		{
		}
	}
	
	
	// used for inject test of @BeanParam
	private static class Bean
	{
	}
	

	/**
	 * Static methods and methods missing a RequestMethod annotation
	 * are not recognized as resource actions. 
	 */
	@Test public void testIgnored()
	{
		MethodArgFactory factory = new MethodArgFactory(pathParams_, typeLib_); 
		for (Method method : Ignored.class.getDeclaredMethods())
		{
			ControllerMethod action = ControllerMethod.create(factory, method);
			if (action != null)
				fail(method.getName() + " is not a valid action method");
		}
	}

	
	/**
	 * ControllerMethods can either use the explicit @RequestMethod annotation or
	 * the abbreviations like @GET, etc... 
	 */
	@Test public void testRecognized()
	{
		ControllerMethod method;
		
		method = createMethod("recognizedExplicit");
		assertRequestMethods(method.getRequestMethods(), "PATCH");

		method = createMethod("recognizedAbbreviated");
		assertRequestMethods(method.getRequestMethods(), "OPTIONS");

		method = createMethod("methodReduction");
		assertRequestMethods(method.getRequestMethods(), "GET", "POST", "PATCH");
	}
	
	
	private void assertRequestMethods(Iterator<String> it, String... methods)
	{
		for (int i=0; it.hasNext(); i++)
			assertEquals(methods[i], it.next());
		assertFalse(it.hasNext());
	}
	
	
	/**
	 * Test the @Produces annotation.
	 */
	@Test public void testProduces()
	{
		ControllerMethod action;
		
		action = createMethod("producesAny");
		assertEquals(0, action.getProducedContentTypes().size());

		action = createMethod("producesHtml");
		assertContentTypes(action.getProducedContentTypes(), ContentType.TEXT_HTML);

		// ContentTypes are sorted by concreteness
		action = createMethod("producesMulti");
		assertContentTypes(action.getProducedContentTypes(), ContentType.TEXT_HTML, new ContentType("text/*"), ContentType.ANY);
		
		// ContentTypes can have a quality
		action = createMethod("producesQualified");
		assertContentTypes(action.getProducedContentTypes(), ContentType.TEXT_HTML.withQuality(0.5));
	}
	
	
	/**
	 * Test the @Consumes annotation.
	 */
	@Test public void testConsumes()
	{
		ControllerMethod action;
		
		action = createMethod("consumesAny");
		assertEquals(0, action.getConsumesContentTypes().size());
		assertTrue(action.canConsume(ContentType.ANY));

		action = createMethod("consumesSome");
		assertContentTypes(action.getConsumesContentTypes(), ContentType.APPLICATION_JSON, ContentType.TEXT_XML);
		assertFalse(action.canConsume(ContentType.APPLICATION_EXCEL));
		assertTrue(action.canConsume(ContentType.APPLICATION_JSON));
		assertTrue(action.canConsume(ContentType.TEXT_XML));
	}
	
	
	/**
	 * Test the @QueryParm, @MatrixParam, @PathParam and @DefaultValue annotations.
	 */
	@Test public void testInjectParams()
	{
		ControllerMethod action;
		
		action = createMethod("inject");
		assertEquals(12, action.getArgCount());
		int next = 0;  
		assertEquals("Parameter \"min\"",			action.getArgument(next++).toString());
		assertEquals("Parameter \"max\"",			action.getArgument(next++).toString());
		assertEquals("Parameter \"val\"",			action.getArgument(next++).toString());
		assertEquals("MatrixParam \"mode\"",		action.getArgument(next++).toString());
		assertEquals("PathParam \"customerId\"",	action.getArgument(next++).toString());
		assertEquals("CookieParam \"store\"", 		action.getArgument(next++).toString());
		assertEquals("BeanParam", 					action.getArgument(next++).toString());
		assertEquals("RequestContent", 				action.getArgument(next++).toString());
		assertEquals("Request", 					action.getArgument(next++).toString());
		assertEquals("Response",					action.getArgument(next++).toString());
		assertEquals("ResponseContent", 			action.getArgument(next++).toString());
		assertEquals("Custom", 						action.getArgument(next++).toString());
	}
	
	
	/**
	 * Test that overwritten resource methods are recognized.
	 */
	@Test public void testOverride()
	{
		@SuppressWarnings("unused")
		ControllerMethod action;
		
		action = createMethod("overwritten");
	}
	
	
	private ControllerMethod createMethod(String methodName)
	{
		MethodArgFactory argFactory = new MethodArgFactory(pathParams_, typeLib_); 
		Method method = findMethod(Demo.class, methodName);
		ControllerMethod action = ControllerMethod.create(argFactory, method);
		if (action == null)
			fail(methodName + " is not a resource action");
		return action;
	}
	
	
	private void assertContentTypes(ContentTypeList list, ContentType... types)
	{
		int n = Math.min(list.size(), types.length);
		for (int i=0; i<n; i++)
			assertEquals(types[i], list.get(i));
		assertEquals("size", types.length, list.size());
	}
	
	
	private static TypeLib typeLib_ = new TypeLib();
	private static PathParamMap pathParams_;
}
