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
package org.civilian.util;


import java.lang.annotation.Annotation;
import org.junit.Test;
import org.civilian.CivTest;


public class ClassUtilTest extends CivTest
{
	public enum EnumTest
	{
		ALPHA,
		BETA
	}
	
	
	@Test public void testIsA()
	{
		assertTrue(ClassUtil.isA("", CharSequence.class));
		assertTrue(ClassUtil.isA("", String.class));
		assertFalse(ClassUtil.isA(Integer.valueOf(2), String.class));
		assertFalse(ClassUtil.isA(null, String.class));
		assertFalse(ClassUtil.isA(null, null));
		assertFalse(ClassUtil.isA("", null));
	}
	
	
	@Test public void testNameMethods()
	{
		assertEquals("Test", ClassUtil.cutPackageName("Test"));
		assertEquals("String", ClassUtil.cutPackageName("java.lang.String"));
	}


	@Test public void testPackageName()
	{
		assertEquals(null, ClassUtil.getPackageName((String)null));
		assertEquals(null, ClassUtil.getPackageName((Class<?>)null));
		assertEquals(null, ClassUtil.getPackageName("Abc"));
		assertEquals("org.civilian.util", ClassUtil.getPackageName(getClass()));
		assertEquals("org.civilian.util", ClassUtil.getPackageName(getClass().getName()));
	}


	@Test public void testCreate() throws Exception
	{
		assertSame(getClass(), ClassUtil.createObject(getClass().getName(), CivTest.class, null).getClass());
		
		assertSame(getClass(), ClassUtil.getPotentialClass(getClass().getName(), CivTest.class, null));
		assertNull(ClassUtil.getPotentialClass("x", String.class, null));
		
		try
		{
			ClassUtil.getClass("x", String.class, null);
			fail();
		}
		catch(ClassNotFoundException e)
		{
		}
		  

		try
		{
			ClassUtil.getClass(getClass().getName(), String.class, null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}


	@Test public void testAnnotation() throws Exception
	{
		Annotation[] annos = getClass().getMethod("testAnnotation").getAnnotations();
		assertTrue(ClassUtil.isA(annos[0], Test.class));
		assertEquals(annos[0], ClassUtil.findAnnotation(annos, Test.class));
		assertEquals(null, ClassUtil.findAnnotation(annos, SuppressWarnings.class));
	}
	
	
	@Test public void testUnwrap() throws Exception
	{
		Object a = "a";
		assertEquals(a, ClassUtil.unwrap(a, String.class));
		assertNull(ClassUtil.unwrap(a, Integer.class));
	}
	

	@Test public void testEnum() throws Exception
	{
		assertEquals(EnumTest.ALPHA, ClassUtil.getEnum(EnumTest.class, "ALPHA", null));
		assertEquals(EnumTest.BETA, ClassUtil.getEnum(EnumTest.class, "GAMMA", EnumTest.BETA));
	}
}
