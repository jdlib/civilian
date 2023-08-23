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


import org.civilian.form.Select;
import org.civilian.text.keys.serialize.KeySerializer;
import org.civilian.util.Check;


/**
 * A KeyList is an ordered list of keys which are text-value pairs.
 * KeyList are used to back certain form controls like {@link Select}.
 * KeyList supports serialization of its values by using a {@link KeyType}.
 * @param <VALUE> the type of the values of the key list.
 */
public abstract class KeyList<VALUE>
{
	/**
	 * Creates a KeyList with a specific KeySerializer.
	 * @param serializer the key serializer. If null, then the key serializer is derived
	 * 		from the values contained in the key list.
	 */
	protected KeyList(KeySerializer serializer)
	{
		type_ = createType(serializer);
	}

	
	/**
	 * Creates a KeyList with a specific KeyType.
	 * @param type the key type. If null, then the KeyType is created by the key list.
	 */
	protected KeyList(KeyType<VALUE> type)
	{
		type_ = Check.notNull(type, "type");
	}
	
	
	/**
	 * Factory method to create a KeyType. Called when no explicit KeyType 
	 * is passed to the KeyList constructor.
	 * @param serializer the key serializer passed to the key list constructors or null.
	 */
	protected KeyType<VALUE> createType(KeySerializer serializer)
	{
		return new KeyType<>(this, serializer);
	}

	
	/**
	 * Returns the size of the KeyList.
	 */
	public abstract int size();

	
	/**
	 * Returns the index of the key with the given value or -1 if not contained in the list.
	 */
	public int indexOf(VALUE value)
	{
		int size = size();
		if (value == null)
		{
			for (int i=0; i<size; i++)
			{
				if (getValue(i) == null)
					return i;
			}
		}
		else
		{
			for (int i=0; i<size; i++)
			{
				if (value.equals(getValue(i)))
					return i;
			}
		}
		return -1;
	}
	

	/**
	 * Returns the value of the key with the given index.
	 */
	public abstract VALUE getValue(int index);
	

	/**
	 * Returns the first value of the key with the given text.
	 */
	public VALUE getValue(String text)
	{
		if (text != null)
		{
			int size = size();
			for (int i=0; i<size; i++)
			{
				if (text.equals(getText(i)))
					return getValue(i);
			}
		}
		return null;
	}

	
	/**
	 * Returns the text of the key with the given index.
	 */
	public abstract String getText(int index);


	/**
	 * Returns the text of the key with the given value.
	 */
	public String getText(VALUE value)
	{
		int index = indexOf(value);
		return index == -1 ? null : getText(index);
	}

	
	/**
	 * Returns the index of the given text, or -1 if not contained in the list. 
	 */
	public int getTextIndex(String text)
	{
		int size = size();
		for (int i=0; i<size; i++)
		{
			if (getText(i).equals(text))
				return i;
		}
		return -1;
	}
	
	
	/**
	 * Returns a type implementation suitable to serialize KeyList values.
	 */
	public KeyType<VALUE> getType()
	{
		return type_;
	}
	
	
	private final KeyType<VALUE> type_;
}
