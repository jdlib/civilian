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
 * IndexSerializer formats a value to the stringified index of the value
 * within the KeyList. Parsing a value from a string means to convert the 
 * stringified value to an index integer and return the value for that index.
 * This serializer is a good fit for complex values for which not type exists
 * and toString() is not efficient or usable. 
 */
class IndexSerializer extends KeySerializer 
{
	/**
	 * Parse the string to an integer index and return the value with that index. 
	 */
	@Override public <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s)
	{
		int n = Integer.parseInt(s);
		if ((n < 0) || (n >= keyList.size()))
			throw rejectValue(s);
		return keyList.getValue(n);
	}
	
	
	/**
	 * Returns the integer index of the value as string. 
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index)
	{
		return String.valueOf(index);
	}


	/**
	 * Returns the integer index of the value as string. 
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value)
	{
		return formatValue(keyList, value, keyList.indexOf(value)); 
	}
}


