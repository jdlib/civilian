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


/**
 * ValueKeyList is a KeyList based on a list of values.
 * It returns value.toString() as text for a value. 
 */
public class ValueKeyList<VALUE> extends KeyList<VALUE>
{
	@SafeVarargs public ValueKeyList(VALUE... values)
	{
		this(null, values);
	}
	
	
	@SafeVarargs public ValueKeyList(KeySerializer serializer, VALUE... values)
	{
		super(serializer != null ? serializer : KeySerializers.forFirstValue(values));
		values_ = Check.notNull(values, "values");
	}

	
	@Override public int size()
	{
		return values_.length;
	}


	@Override public VALUE getValue(int index)
	{
		return values_[index];
	}


	@Override public String getText(int index)
	{
		return values_[index].toString();
	}

	
	private final VALUE values_[];
}
