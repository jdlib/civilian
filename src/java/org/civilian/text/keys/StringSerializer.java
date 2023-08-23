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

/**
 * StringTypeSerializer is a ValueSerializer for string-based KeyLists.
 */
class StringSerializer extends KeySerializer 
{
	/**
	 * The value does not need to be parsed but we test if the value is contained in the list.
	 */
	@Override public <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s)
	{
		@SuppressWarnings("unchecked")
		int index = ((KeyList<String>)keyList).indexOf(s);
		if (index < 0)
			throw rejectValue(s);
		// we return the value contained in the list and not the parsed value
		return keyList.getValue(index);
	}
	
	
	/**
	 * Returns the value.
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index)
	{
		return (String)value;
	}


	/**
	 * Returns the value.
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value)
	{
		return (String)value;
	}
}


