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
package org.civilian.text.type;


import java.text.ParseException;
import java.util.function.Function;
import org.civilian.text.Style;
import org.civilian.text.keys.KeyType;
import org.civilian.type.AutoType;
import org.civilian.type.DiscreteType;
import org.civilian.type.EnumType;
import org.civilian.type.Type;
import org.civilian.type.TypeMap;
import org.civilian.util.Value;


/**
 * A TypeSerializer represents a certain schema of parsing and formatting
 * values from or to a string.
 */
public abstract class TypeSerializer
{
	/**
	 * Formatter is a service to turn a typed value into a string.
	 */
	@FunctionalInterface
	public static interface Formatter<T>
	{
		/**
		 * Returns a string representation of a typed value 
		 * @param type a type
		 * @param value a value 
		 * @param style a style or null
		 * @return the string
		 */
		public String format(Type<? extends T> type, T value, Style style); 
	}
	
	
	/**
	 * Parser is a service to parse a typed value from a string.
	 */
	@FunctionalInterface
	public static interface Parser<T>
	{
		/**
		 * Parses and returns a typed value from a String. 
		 * @param type a type
		 * @param s a string
		 * @return the parsed value
		 * @throws Exception thrown when a parse error occurs
		 */
		public T parse(Type<T> type, String s) throws Exception; 
		
		
		/**
		 * Returns a value which represents the empty string.
		 * @return the default implementation returns null.
		 */
		default public T parseEmpty() throws Exception
		{
			return null;
		}
	}

	
	/**
	 * SimpleParser is a service to parse a value from a string.
	 */
	public static interface SimpleParser<T>
	{
		/**
		 * Parses and returns a value from a String. 
		 * @param s a string
		 * @return the parsed value
		 * @throws Exception thrown when a parse error occurs
		 */
		public T parse(String s) throws Exception; 
	}
	
	
	protected static final Parser<String> PARSE_STRING = new Parser<String>()
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
	
	
	protected TypeSerializer()
	{
		this(null);
	}
	
	
	protected TypeSerializer(TypeSerializer other)
	{
		formatMap_	= other != null ? other.formatMap_ : new TypeMap();
		parseMap_	= other != null ? other.parseMap_  : new TypeMap();
	}

	
	//------------------------- 
	// format 
	//-------------------------
	

	/**
	 * Formats a typed value into a string.
	 * Calls {@link #format(Type, Object, Style)} using a null style.
	 * @return the formatted value
	 */
	public <T> String format(Type<T> type, T value)
	{
		return format(type, value, null);
	}
	
	
	/**
	 * Formats a typed value into a string using the given style.
	 * @return the formatted value
	 */
	public <T> String format(Type<T> type, T value, Style style)
	{
		if (value == null)
			return formattedNull_;
		
		Formatter<T> fn = getFormatter(type);
		if (fn != null)
			return fn.format(type, value, style);
		
		throw new UnsupportedTypeException(this, "format", type);
	}
	
	
	/**
	 * Returns a Formatter for a type.
	 */
	public <T> Formatter<T> getFormatter(Type<T> type)
	{
		return formatMap_.get(type);
	}
	
	
	public <T> TypeMap.Builder<T> useFormatter(Formatter<T> fn)
	{
		return formatMap_.use(fn);
	}

	
	public <T> TypeMap.Builder<T> useSimpleFormatter(Function<T,String> simpleFn)
	{
		return useFormatter((t,v,s) -> simpleFn.apply(v));
	}

	
	public <T> void useFormatNull(String nullValue)
	{
		formattedNull_ = nullValue;
	}
	
	
	protected <T> String formatDiscrete(Type<? extends T> type, T value, Style style)
	{
		@SuppressWarnings("unchecked")
		DiscreteType<T> dt = (DiscreteType<T>)type;
		return format(dt.getElementType(), value, style);
	}

	
	@SuppressWarnings("unchecked")
	protected <T> String formatAuto(Type<? extends T> type, T value, Object style)
	{
		return ((AutoType<T>)type).format(value);
	}
	
	
	//------------------------- 
	// parse 
	//-------------------------
	
	
	public <T> T parse(Type<T> type, String s) throws ParseException
	{
		try
		{
			if (s == null)
				return parseNull(type);

			Parser<T> fn = getParser(type);
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
		throw new UnsupportedTypeException(this, "parse", type);
	}
	
	
	/**
	 * Parses the value from a string.
	 * @param type a Type object
	 * @param s the string representation 
	 * @return the Value object
	 */
	public <T> Value<T> parseValue(Type<T> type, String s)
	{
		try
		{
			return new Value<>(parse(type, s));
		}
		catch(Exception e)
		{
			return new Value<>(e, s);
		}
	}

	
	public <T> T[] parseArray(Type<T> type, String s[]) throws ParseException
	{
		int n = s == null ? 0 : s.length;
		T[] result = type.createArray(n);
		for (int i=0; i<n; i++)
			result[i] = parse(type, s[i]);
		return result;
	}

	
	public <T> Parser<T> getParser(Type<T> type)
	{
		return parseMap_.get(type);
	}
	
	
	protected <T> T parseNull(Type<T> type) throws Exception
	{
		return null;
	}

	
	public <T> TypeMap.Builder<T> useParser(Parser<T> fn)
	{
		return parseMap_.use(fn);
	}
	
	
	public <T> TypeMap.Builder<T> useSimpleParser(SimpleParser<T> fn)
	{
		Parser<T> fn2 = (t,s) -> fn.parse(s);
		return useParser(fn2);
	}

	
	protected Character parseCharacter(Type<Character> type, String s) throws Exception
	{
		if (s.length() != 1)
			throw new ParseException("not a character: " + s, 0);
		return Character.valueOf(s.charAt(0));
	};
		

	protected <T> T parseDiscrete(Type<T> type, String s) throws Exception
	{
		DiscreteType<T> dt = (DiscreteType<T>)type;
		T value = parse(dt.getElementType(), s);
		if (dt.indexOf(value) < 0)
			throw new ParseException("not a valid entry '" + s + "'", 0);
		return value;
	}

	
	protected <T> T parseKey(Type<T> type, String s) throws Exception
	{
		return ((KeyType<T>)type).parse(s);
	}

	
	@SuppressWarnings("unchecked")
	protected <T> T parseEnum(Type<T> type, String s) throws Exception
	{
		EnumType<?> et = (EnumType<?>)type;
		return (T)Enum.valueOf(et.getJavaType(), s);
	}

	
	protected <T> T parseAuto(Type<T> type, String s) throws Exception
	{
		return ((AutoType<T>)type).parse(s);
	}

	
	protected final TypeMap formatMap_; 
	protected final TypeMap parseMap_; 
	private String formattedNull_ = "";
}
