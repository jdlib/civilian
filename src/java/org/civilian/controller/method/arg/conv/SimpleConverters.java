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


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;


/**
 * Factory class for SimpleConverter implementations.
 */
public abstract class SimpleConverters
{
	private static final StringConverter STRING_INSTANCE = new StringConverter();
	
	
	/**
	 * Creates a new SimpleConverter. 
	 * @param typeLib a type library 
	 * @param c the class. SimpleConverter does not handle parameterized types.
	 * @param <T> the return type
	 * @return the converter or null if class c is not supported 
	 */
	@SuppressWarnings("unchecked")
	public static <T> SimpleConverter<T> create(TypeLib typeLib, Class<?> c)
	{
		Check.notNull(c, "class");
		
		// for strings
		if (c == String.class)
			return (SimpleConverter<T>)STRING_INSTANCE;
		
		// for classes with a civilian Type implementation
		Type<?> type = typeLib.get(c);
		if (type != null)
			return (SimpleConverter<T>)new TypedConverter<>(type, c.isPrimitive());

		// for classes with a string constructor
		try
		{
			Constructor<?> stringCtor = c.getConstructor(String.class);
			return (SimpleConverter<T>)new CtorConverter<>(stringCtor);
		}
		catch(NoSuchMethodException e)
		{
		}
		
		// for classes with a static valueOf(String) or fromString(String) methods
		for (int i=0; i<2; i++)
		{
			try
			{
				Method method = c.getMethod(i == 0 ? "valueOf" : "fromString", String.class);
				if (Modifier.isStatic(method.getModifiers()) && (method.getReturnType() == c))
					return new MethodConverter<>(method);
			}
			catch(NoSuchMethodException e)
			{
			}
		}
		
		// can't create a converter
		return null;
	}
}
