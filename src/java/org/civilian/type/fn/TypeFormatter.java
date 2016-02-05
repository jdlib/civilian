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
package org.civilian.type.fn;


import org.civilian.text.keys.KeyType;
import org.civilian.type.DiscreteType;
import org.civilian.type.Type;


public class TypeFormatter
{
	@FunctionalInterface
	public static interface Function<T>
	{
		public String format(Type<? extends T> type, T value, Object style); 
	}
	
	
	public static final Function<Object> KEY_FUNCTION = new Function<Object>()
	{
		@SuppressWarnings("unchecked")
		@Override public String format(Type<? extends Object> type, Object value, Object style)
		{
			return ((KeyType<Object>)type).format(value);
		}
	};
	
	
	public TypeFormatter()
	{
		this(null, null);
	}
	
	
	public TypeFormatter(TypeMap map, Object owner)
	{
		map_ 	= map != null ? map : new TypeMap();
		owner_	= owner != null ? owner : this;
	}

	
	public <T> String format(Type<T> type, T value)
	{
		return format(type, value, null);
	}
	
	
	public <T> String format(Type<T> type, T value, Object style)
	{
		if (value == null)
			return nullValue_;
		
		Function<T> fn = get(type);
		if (fn != null)
			return fn.format(type, value, style);
		
		throw new UnsupportedTypeException(owner_, "format", type);
	}
	
	
	public <T> Function<T> get(Type<T> type)
	{
		return map_.get(type);
	}
	
	
	public <T> TypeMap.Builder<T> use(Function<T> fn)
	{
		return map_.use(fn);
	}

	
	public <T> TypeMap.Builder<T> use(java.util.function.Function<T,String> simpleFn)
	{
		return use((t,v,s) -> simpleFn.apply(v));
	}

	
	public <T> void useNull(String nullValue)
	{
		nullValue_ = nullValue;
	}
	
	
	protected <T> String formatDiscrete(Type<? extends T> type, T value, Object style)
	{
		@SuppressWarnings("unchecked")
		DiscreteType<T> dt = (DiscreteType<T>)type;
		return format(dt.getElementType(), value, style);
	}

	
	protected <T> String formatEnum(Type<? extends T> type, T value, Object style)
	{
		return ((Enum<?>)value).name();
	}
	
	
	private TypeMap map_; 
	private Object owner_;
	private String nullValue_ = "";
}
