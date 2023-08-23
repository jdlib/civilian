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
 * SimpleKeyList is a simple KeyList implementation,
 * backed by two arrays for values and texts.
 */
public class SimpleKeyList<V> extends KeyList<V>
{
	/**
	 * Creates a SimpleKeyList.
	 * @param values the key values
	 * @param texts the key texts 
	 */
	public SimpleKeyList(V values[], String texts[])
	{
		this(null, values, texts);
	}
	

	/**
	 * Creates a SimpleKeyList.
	 * @param serializer a serializer
	 * @param values the key values
	 * @param texts the key texts 
	 */
	public SimpleKeyList(KeySerializer serializer, V values[], String texts[])
	{
		super(serializer != null ? serializer : KeySerializers.forFirstValue(values));
		if (texts.length != values.length)
			throw new IllegalArgumentException();
		values_	= values;
		texts_	= texts;
	}
	

	/**
	 * Implementation.
	 */
	@Override public int size()
	{
		return texts_.length;
	}

	
	/**
	 * Implementation.
	 */
	@Override public V getValue(int i)
	{
		try
		{
			return values_[i];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new IllegalArgumentException("invalid index: " + i);
		}
	}

	
	/**
	 * Implementation.
	 */
	@Override public String getText(int i)
	{
		try
		{
			return texts_[i];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new IllegalArgumentException("invalid index: " + i);
		}
	}


	/**
	 * Returns an info string.
	 */
	@Override public String toString()
	{
		return "KeyList[" + size() + "]";
	}


	private final String texts_[];
	private final V values_[];
}
