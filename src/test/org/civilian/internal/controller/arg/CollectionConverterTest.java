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
package org.civilian.internal.controller.arg;


import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.controller.method.arg.conv.CollectionConverter;
import org.civilian.controller.method.arg.conv.CollectionConverters;
import org.civilian.text.type.LocaleSerializer;
import org.civilian.type.TypeLib;



public class CollectionConverterTest extends CivTest
{
	public void valid(List<Integer> p1, Set<Boolean> p2, SortedSet<String> p3)
	{
	}
	
	
	public void invalid(Class<String> c, HashMap<String,String> map, List<List<String>> list)
	{
	}

	
	@Test public void testInvalidTypes() throws Exception
	{
		// test unsupported types
		assertNull(CollectionConverters.create(typeLib_, null));
		assertNull(CollectionConverters.create(typeLib_, String.class));

		Method method = findMethod(getClass(), "invalid");
		Type[] genericTypes 	= method.getGenericParameterTypes();
		Class<?>[] paramTypes	= method.getParameterTypes();
		for (int i=0; i<genericTypes.length; i++)
			assertNull(CollectionConverters.create(typeLib_, paramTypes[i], genericTypes[i]));
	}
	
	
	@Test public void testConvert() throws Exception
	{
		Method method 		= findMethod(getClass(), "valid");
		Class<?>[] types 	= method.getParameterTypes();
		Type[] genTypes 	= method.getGenericParameterTypes();
		assertEquals(3, genTypes.length);
		
		assertConvert(types[0], genTypes[0], ArrayList.class,	"1",	Integer.valueOf(1));
		assertConvert(types[1], genTypes[1], HashSet.class,		"true",	Boolean.TRUE);
		assertConvert(types[2], genTypes[2], TreeSet.class,		"a",	"a");
	}
	
	
	private void assertConvert(Class<?> type, Type genType, Class<?> expClass, String input, Object converted) throws Exception
	{
		// test parameterized
		CollectionConverter<?> converter = assertCreate(type, genType, expClass);
		Collection<?> c = converter.convert(null, serializer_, input);
		assertEquals(1, c.size());
		assertEquals(converted, c.iterator().next());
		
		// test raw
		converter = assertCreate(type, type, expClass);
		c = converter.convert(null, serializer_, input);
		assertEquals(1, c.size());
		assertEquals(input, c.iterator().next());
	}
	
	
	private CollectionConverter<?> assertCreate(Class<?> type, Type genType, Class<?> collClass)
	{
		CollectionConverter<?> converter = CollectionConverters.create(typeLib_, type, genType);
		assertNotNull(converter);
		
		Collection<?> c = converter.emptyCollection();
		assertNotNull(c);
		assertTrue(c.isEmpty());
		assertEquals(collClass, c.getClass());
		
		return converter;
	}
	
	
	private static TypeLib typeLib_ = new TypeLib();
	private static LocaleSerializer serializer_ = new LocaleSerializer(Locale.GERMAN);
}
