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
package org.civilian.controller.method.arg.conv;


import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.civilian.type.TypeLib;


/**
 * Factory class for CollectionConverter implementations.
 */
public abstract class CollectionConverters
{
	public static <T> CollectionConverter<T> create(TypeLib typeLib, Class<?> classType)
	{
		return create(typeLib, classType, classType);
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> CollectionConverter<T> create(TypeLib typeLib, Class<?> classType, java.lang.reflect.Type genericType)
	{
		// the type must either be a List, Set or SortedSet
		if ((classType == List.class) || (classType == Set.class) || (classType == SortedSet.class))
		{
			// determine the element type
			Class<?> elemType = getElementType(genericType);
			if (elemType != null)
			{
				// try to create a SimpleConverter for the elements
				SimpleConverter<?> sc = SimpleConverters.create(typeLib, elemType);
				if (sc != null)
				{
					if (classType == List.class)
						return new ListConverter(sc);
					else if (classType == Set.class)
						return new SetConverter(sc);
					else
						return new SortedSetConverter(sc);
				}
			}
		}
		// can'T create a CollectionConverter
		return null; 
	}
	
	
	private static Class<?> getElementType(java.lang.reflect.Type genericType)
	{
		if (!(genericType instanceof ParameterizedType))
			return String.class;
			
		ParameterizedType ptype = (ParameterizedType)genericType;
		java.lang.reflect.Type[] actualTypeArgs = ptype.getActualTypeArguments();
		if ((actualTypeArgs.length == 1) && (actualTypeArgs[0] instanceof Class))
			return (Class<?>)actualTypeArgs[0];
		
		return null;
	}
}
