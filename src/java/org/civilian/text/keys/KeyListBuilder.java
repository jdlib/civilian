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


import java.lang.reflect.Array;
import java.util.ArrayList;

import org.civilian.util.Check;


/**
 * KeyListBuilder is a helper class to construct KeyLists.
 * @param <VALUE> the keylist value type
 */
public class KeyListBuilder<VALUE>
{
	/**
	 * Adds a key.
	 * @param value the key value
	 * @param text the key text
	 * @return this
	 */
	public KeyListBuilder<VALUE> add(VALUE value, String text)
	{
		Check.notNull(text, "text");
		if (valuePrototype_ == null)
			valuePrototype_ = value;
		values_.add(value);
		texts_.add(text);
		return this;
	}
	
	
	/**
	 * Explicitly sets the key serializer which should be used by the keylist.
	 * @param serializer the serializer
	 * @return this
	 */
	public KeyListBuilder<VALUE> setSerializer(KeySerializer serializer)
	{
		serializer_ = serializer;
		return this;
	}
	
	
	/**
	 * @return creates the key list.
	 */
	public KeyList<VALUE> end()
	{
		if (values_.size() == 0)
			return KeyLists.<VALUE>empty();
		if (valuePrototype_ != null)
		{
			@SuppressWarnings("unchecked")
			VALUE[] values = (VALUE[])Array.newInstance(valuePrototype_.getClass(), values_.size());
			values_.toArray(values);
			return new SimpleKeyList<>(serializer_, values, texts_.toArray(new String[values.length]));
		}
			
		throw new IllegalStateException("cannot build a keylist without a non-null value");
	}
	 

	private Object valuePrototype_;
	private KeySerializer serializer_;
	private final ArrayList<VALUE> values_ = new ArrayList<>();
	private final ArrayList<String> texts_ = new ArrayList<>();
}
