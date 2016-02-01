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
package org.civilian.type.lib;


import org.civilian.type.Type;
import org.civilian.type.TypeSerializer;
import org.civilian.type.TypeVisitor;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * A DiscreteType is a generic type for discrete number of objects. 
 * Its parse method guarantees that the returned object is an element in that list of objects.
 * Formatting and parsing of lookup objects can either be type based, or
 * generic, using the toString() method of the objects. 
 */
public class DiscreteType<T> extends SimpleType<T>
{
	/**
	 * Creates a new DiscreteType.
	 * @param list an non empty array of objects.
	 */
	@SafeVarargs
	public DiscreteType(Type<T> type, T... list)
	{
		list_ = Check.notNull(list, "list");
		type_ = Check.notNull(type, "type");
	}

	
	@Override public String format(TypeSerializer serializer, T value, Object style)
	{
		return value != null ? type_.format(serializer, value) : serializer.formatNull(); 
	}
	

	@Override public T parse(TypeSerializer serializer, String s) throws Exception
	{
		if (s == null)
			return null;
		
		T value = null;
		boolean valueOk = false;
		
		value = type_.parse(serializer, s);
		for (int i=0; i<list_.length; i++)
		{
			if (ClassUtil.equals(value, list_[i]))
			{
				valueOk = true;
				break;
			}
		}
		
		if (valueOk)
			return value;
		throw new IllegalArgumentException("not a valid entry '" + s + "'");
	}
	

	/**
	 * Throws an UnsupportedOperationException.
	 */
	@Override public <R, P, E extends Exception> R accept(TypeVisitor<R, P, E> visitor, P param) throws E
	{
		throw new UnsupportedOperationException();
	}

	
	/**
	 * Returns the class of the first list element.
	 */
	@Override public Class<T> getJavaType()
	{
		return type_.getJavaType();
	}
	
	
	private T[] list_;
	private Type<T> type_;
}
