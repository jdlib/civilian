/*
 * Copyright (C) 2016 Civilian Framework.
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


import org.civilian.util.Check;


/**
 * TypeMap is a helper class which allows to map a type to another
 * object. These Mappings can be established:
 * - for a single type: Type t -> value 
 * - for a type family: Type.Category -> value
 * - for a default value: any type -> value
 */
public class TypeMap
{
	public class Builder<T>
	{
		private Builder(Object value)
		{
			value_ = value;
		}
		
		
		public Builder<T> on(Type<? extends T> type)
		{
			set(type, value_);
			return this;
		}
		
		
		public Builder<T> on(Type.Category category)
		{
			set(category, value_);
			return this;
		}

		
		public Builder<T> byDefault()
		{
			setDefault(value_);
			return this;
		}

		
		private Object value_;
	}
	
		
	@SuppressWarnings("unchecked")
	public <VALUE> VALUE get(Type<?> type)
	{
		Check.notNull(type, "type");
		Object v = null; 
		
		int n = type.ordinal();
		if (n < typeValues_.length)
			v = typeValues_[n];

		if ((v == null) && ((n = type.category().ordinal()) < categoryValues_.length))   
			v = categoryValues_[n]; 
		
		return (VALUE)(v != null ? v : defaultValue_); 
	}
	
	
	public void set(Type<?> type, Object value)
	{
		Check.notNull(type, "type");
		typeValues_ = set(typeValues_, type.ordinal(), value);	
	}
	
	
	public void set(Type.Category category, Object value)
	{
		Check.notNull(category, "category");
		
		categoryValues_ = set(categoryValues_, category.ordinal(), value);	
	}
	
	
	private Object[] set(Object[] array, int n, Object value)
	{
		if (n >= array.length)
		{
			int newLen = 1 + n;
			Object[] newArray = new Object[newLen];
			System.arraycopy(array, 0, newArray, 0, array.length);
			array = newArray;	
		}
		array[n] = value;
		return array;
	}
	

	public void setDefault(Object newDefault)
	{
		defaultValue_ = newDefault;
	}
	
	
	public <T> Builder<T> use(Object value)
	{
		return new Builder<>(value);
	}
	
	
	private Object[] typeValues_ = EMPTY_VALUES;
	private Object[] categoryValues_  = EMPTY_VALUES;
	private Object defaultValue_;
	private static Object[] EMPTY_VALUES = new Object[0];
}
