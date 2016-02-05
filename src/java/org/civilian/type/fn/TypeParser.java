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


import java.text.ParseException;
import org.civilian.text.keys.KeyType;
import org.civilian.type.DiscreteType;
import org.civilian.type.EnumType;
import org.civilian.type.Type;


public class TypeParser
{
	@FunctionalInterface
	public static interface Function<T>
	{
		public T parse(Type<T> type, String s) throws Exception; 
		
		
		default public T parseEmpty() throws Exception
		{
			return null;
		}
	}

	
	public static interface SimpleFn<T>
	{
		public T parse(String s) throws Exception; 
	}
	
	
	public static final Function<String> STRING_FUNCTION = new Function<String>()
	{
		@Override public String parse(Type<String> type, String s) throws Exception
		{
			return s;
		}
		
		
		@Override public String parseEmpty() throws Exception
		{
			return "";
		}
	};
	
	
	public static final Function<Object> KEY_FUNCTION = new Function<Object>()
	{
		@Override public Object parse(Type<Object> type, String s) throws Exception
		{
			return ((KeyType<?>)type).parse(s);
		}
	};

	
	public static class KeyFunction<T> implements Function<T>
	{
		@Override public T parse(Type<T> type, String s) throws Exception
		{
			return ((KeyType<T>)type).parse(s);
		}
	};

	
	public static final SimpleFn<Character> CHAR_FUNCTION = s -> {
		if (s.length() != 1)
			throw new ParseException("not a character: " + s, 0);
		return new Character(s.charAt(0));
	};
		

	public TypeParser()
	{
		this(null, null);
	}
	
	
	public TypeParser(TypeMap map, Object owner)
	{
		map_ 	= map   != null ? map   : new TypeMap();
		owner_	= owner != null ? owner : this;
	}

	
	public <T> Function<T> get(Type<T> type)
	{
		return map_.get(type);
	}
	
	
	public <T> TypeMap.Builder<T> use(Function<T> fn)
	{
		return map_.use(fn);
	}
	
	
	public <T> TypeMap.Builder<T> use(SimpleFn<T> fn)
	{
		Function<T> fn2 = (t,s) -> fn.parse(s);
		return use(fn2);
	}

	
	public <T> T parse(Type<T> type, String s) throws ParseException
	{
		try
		{
			if (s == null)
				return parseNull(type);

			TypeParser.Function<T> fn = get(type);
			if (fn != null)
				return s.length() != 0 ? fn.parse(type, s) : fn.parseEmpty();
		}
		catch (ParseException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			ParseException pe = new ParseException(s, 0);
			pe.initCause(e);
			throw pe;
		}
		throw new UnsupportedTypeException(owner_, "parse", type);
	}
	
	
	public <T> T[] parse(Type<T> type, String s[]) throws ParseException
	{
		int n = s == null ? 0 : s.length;
		T[] result = type.createArray(n);
		for (int i=0; i<n; i++)
			result[i] = parse(type, s[i]);
		return result;
	}

	
	protected <T> T parseNull(Type<T> type) throws Exception
	{
		return null;
	}

	
	protected <T> T parseDiscrete(Type<T> type, String s) throws Exception
	{
		DiscreteType<T> dt = (DiscreteType<T>)type;
		T value = parse(dt.getElementType(), s);
		if (dt.indexOf(value) < 0)
			throw new ParseException("not a valid entry '" + s + "'", 0);
		return value;
	}

	
	@SuppressWarnings("unchecked")
	protected <T> T parseEnum(Type<T> type, String s) throws Exception
	{
		EnumType<?> et = (EnumType<?>)type;
		return (T)Enum.valueOf(et.getJavaType(), s);
	}

	
	private TypeMap map_; 
	private Object owner_;
}
