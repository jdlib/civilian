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


/**
 * ToStringSerializer uses value.toString() to format a value.
 */
class ToStringSerializer extends KeySerializer 
{
	/**
	 * To parse a value, we iterate over all values and compare the string value
	 * with value.toString().
	 * This is slightly inefficient for large KeyLists and outright bad if value.toString()
	 * is costly (e.g. Locale.toString())
	 */
	@Override public <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s)
	{
		for (int i=keyList.size()-1; i>=0; i--)
		{
			VALUE value = keyList.getValue(i);
			if ((value != null) && s.equals(value.toString())) 
				return value;
		}
		throw rejectValue(s);
	}
	
	
	/**
	 * We call value.toString().
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index)
	{
		return formatValue(keyList, value);
	}


	/**
	 * We call value.toString().
	 */
	@Override public <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value)
	{
		return value.toString();
	}
}
