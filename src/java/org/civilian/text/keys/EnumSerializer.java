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
package org.civilian.text.keys;


import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * EnumSerializer is a key serializer for enums.
 */
class EnumSerializer<E extends Enum<E>> extends KeySerializer 
{
	/**
	 * Creates a EnumSerializer.
	 */
	public EnumSerializer(Class<E> enumClass)
	{
		enumClass_ = Check.notNull(enumClass, "enumClass");
	}
	
	
	/**
	 * To parse a value, we iterate over all values and compare the string value
	 * with value.toString().
	 * This is slightly inefficient for large KeyLists and outright bad if value.toString()
	 * is costly (e.g. Locale.toString())
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Override public <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s) throws Exception
	{
		E value = ClassUtil.getEnum(enumClass_, s, null);
		if (value == null)
			throw rejectValue(s);
		return (VALUE)value;
	}
	

	/**
	 * Returns the name of the enum value.
	 */
	@SuppressWarnings("unchecked")
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index)
	{
		return ((E)value).name();
	}


	/**
	 * Returns the name of the enum value.
	 */
	@SuppressWarnings("unchecked")
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value)
	{
		return ((E)value).name();
	}
	
	
	private final Class<E> enumClass_;
}
