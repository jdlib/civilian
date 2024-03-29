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
package org.civilian.type;


import java.util.Objects;
import org.civilian.util.Check;


/**
 * A DiscreteType is a generic type for discrete number of objects. 
 * Its parse method guarantees that the returned object is an element in that list of objects.
 * Formatting and parsing of lookup objects can either be type based, or
 * generic, using the toString() method of the objects. 
 */
public class DiscreteType<T> extends Type<T>
{
	/**
	 * Creates a new DiscreteType.
	 * @param type a type
	 * @param list an non empty array of objects.
	 */
	@SafeVarargs
	public DiscreteType(Type<T> type, T... list)
	{
		super(Category.DISCRETE);
		list_ = Check.notNull(list, "list");
		type_ = Check.notNull(type, "type");
	}
	
	
	public Type<T> getElementType()
	{
		return type_;
	}

	
	public int size()
	{
		return list_.length; 
	}
	
	
	public T getValue(int i)
	{
		return list_[i]; 
	}

	
	public int indexOf(T value)
	{
		for (int i=0; i<list_.length; i++)
		{
			if (Objects.equals(value, list_[i]))
				return i;
		}
		return -1;
	}
	

	/**
	 * Returns the class of the first list element.
	 */
	@Override public Class<T> getJavaType()
	{
		return type_.getJavaType();
	}
	
	
	private final T[] list_;
	private final Type<T> type_;
}
