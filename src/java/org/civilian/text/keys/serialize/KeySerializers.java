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
package org.civilian.text.keys.serialize;


import org.civilian.type.TypeLib;


/**
 * KeySerializers is a factory class to create KeySerializers for different
 * value types.
 */
public abstract class KeySerializers
{
	/**
	 * A a KeySerializer for key lists with string values.
	 */
	public static final KeySerializer FOR_STRINGS 	= new StringSerializer();

	/**
	 * A a KeySerializer for key lists with boolean values.
	 */
	public static final KeySerializer FOR_BOOLEANS	= new TypeBasedSerializer<>(TypeLib.BOOLEAN);

	/**
	 * A a KeySerializer for key lists with integer values.
	 */
	public static final KeySerializer FOR_INTEGERS  = new TypeBasedSerializer<>(TypeLib.INTEGER);

	/**
	 * A a KeySerializer which uses Object.toString() to format a value to a string.
	 * This key serializer is slightly inefficient for large KeyLists and outright bad if value.toString()
	 * is costly (e.g. Locale.toString())
	 */
	public static final KeySerializer TO_STRING		= new ToStringSerializer();
	
	
	/**
	 * A KeySerializer which formats a value by converting its zero-based index to a string.
	 */
	public static final KeySerializer TO_INDEX		= new IndexSerializer();
	

	/**
	 * Creates a KeySerializer for enums. The name of the enum value is used as string form.
	 */
	public static <E extends Enum<E>> KeySerializer forEnum(Class<E> enumClass)
	{
		return new EnumSerializer<>(enumClass);
	}
	
	
	/**
	 * Creates a KeySerializer for a KeyList whose values have the given class.  
	 * @param valueClass May be null.
	 */
	public static KeySerializer detect(Class<?> valueClass)
	{
		if (valueClass == null)
			return TO_STRING;
		else if (valueClass == String.class)
			return FOR_STRINGS;
		else if (valueClass == Boolean.class)
			return FOR_BOOLEANS;
		else if (valueClass == Integer.class)
			return FOR_INTEGERS;
		else
			return TO_INDEX;
	}
}
