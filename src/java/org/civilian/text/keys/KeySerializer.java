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
 * KeySerializer is a helper class to serialize a key value.
 * {@link KeySerializers} is a factory class to create KeySerializers for different
 * value types.
 */
public abstract class KeySerializer
{
	/**
	 * Parses a value from a String. If the parsed value is not contained in the keylist
	 * it will be rejected.
	 * @param keyList the key list
	 * @param s the formatted value
	 */
	public abstract <VALUE> VALUE parseValue(KeyList<VALUE> keyList, String s) throws Exception;

	
	/**
	 * Formats a value to a String.
	 * @param keyList the key list
	 * @param value the value. Must not be null.
	 * @param index the index of the value within the keylist.
	 */
	public abstract <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value, int index);


	/**
	 * Formats a value to a String.
	 * @param keyList the key list
	 * @param value the value. Must not be null.
	 */
	public abstract <VALUE> String formatValue(KeyList<VALUE> keyList, VALUE value);


	/**
	 * Creates an IllegalArgumentException for an invalid value. 
	 */
	protected static IllegalArgumentException rejectValue(Object s)
	{
		return new IllegalArgumentException("not a valid value '" + s + "'");
	}
}




