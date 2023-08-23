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


import org.civilian.text.keys.serialize.KeySerializer;
import org.civilian.type.Type;
import org.civilian.util.Check;


/**
 * KeyType is a {@link Type} implementation suitable to serialize values
 * of a KeyList. Additionally it checks that a parsed value is valid,
 * i.e. that it is contained in the KeyList.
 * KeyType uses a {@link KeySerializer} to implement parsing and formating of values.
 */
public class KeyType<VALUE> extends Type<VALUE>
{
	/**
	 * Creates a KeyType. 
	 * @param keyList the associated key list.
	 * @param serializer the key serializer of the key type. If null,
	 * 		then it will be derived from the values contained in the list.
	 */
	public KeyType(KeyList<VALUE> keyList, KeySerializer serializer)
	{
		super(Category.KEY);
		keyList_ 	= Check.notNull(keyList, "keyList");
		serializer_	= Check.notNull(serializer, "serializer"); 
	}


	/**
	 * Formats a key value.
	 */
	public String format(VALUE value)
	{
		return value == null ? "" : getKeySerializer().formatValue(keyList_, value);
	}
	

	/**
	 * Formats a key value. 
	 * @param value a value
	 * @param index the index of the value in the keylist.
	 */
	public String format(VALUE value, int index)
	{
		return value == null ? "" : getKeySerializer().formatValue(keyList_, value, index);
	}
	

	/**
	 * Parses a key value.
	 */
	public VALUE parse(String s) throws Exception
	{
		return s != null ? getKeySerializer().parseValue(keyList_, s) : null;
	}
	

	/**
	 * Returns the class of the first key list element.
	 */
	@SuppressWarnings("unchecked")
	@Override public Class<VALUE> getJavaType()
	{
		VALUE v = keyList_.size() > 0 ? keyList_.getValue(0) : null;
		if (v == null)
			throw new IllegalStateException("no value");
		return (Class<VALUE>)v.getClass();
	}
	
	
	/**
	 * Returns the key serializer. 
	 */
	public KeySerializer getKeySerializer()
	{
		return serializer_;
	}
	
	
	private KeyList<VALUE> keyList_;
	private KeySerializer serializer_;
}
