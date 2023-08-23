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


import org.civilian.text.keys.KeyList;
import org.civilian.text.type.StandardSerializer;
import org.civilian.type.Type;
import org.civilian.util.Check;


/**
 * TypeBasedSerializer uses a Type to serialize values.
 */
class TypeBasedSerializer<T> extends KeySerializer 
{
	public TypeBasedSerializer(Type<T> type)
	{
		type_ = Check.notNull(type, "type");
	}
	
	
	/**
	 * We first parse a value using the type and then test if the value is contained in the list.
	 */
	@Override public <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s) throws Exception
	{
		@SuppressWarnings("unchecked")
		VALUE v 	= (VALUE)StandardSerializer.INSTANCE.parse(type_, s);
		int index 	= keyList.indexOf(v);
		if (index < 0)
			throw rejectValue(s);
		// we return the value contained in the list and not the parsed value
		return keyList.getValue(index);
	}
	
	
	/**
	 * We format the value using the standard serializer.
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index)
	{
		return formatValue(keyList, value);
	}


	/**
	 * We format the value using the standard serializer.
	 */
	@SuppressWarnings("unchecked")
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value)
	{
		return StandardSerializer.INSTANCE.format(type_, (T)value);
	}
	
	
	private final Type<T> type_;
}