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
package org.civilian.util;


import org.civilian.type.Type;
import org.civilian.type.fn.TypeSerializer;


/**
 * Value represents a value.
 * It either
 * <ul>
 * <li>has a value, i.e. the internal value is not null
 * <li>is empty, i.e. the internal value is null
 * <li>has an error, indicating errors when the value was parsed from a string representation.
 * </ul>
 */
public class Value<T>
{
	/**
	 * Creates an empty value.
	 */
	public Value()
	{
	}
	
	
	/**
	 * Creates a new Value.
	 */
	public Value(T value)
	{
		setValue(value);
	}

	
	/**
	 * Creates a new Value for the given Type.
	 * and parses the value from a string.
	 * @param type a Type object
	 * @param string the string representation 
	 * @param serializer a TypeSerializer which understands the string format.
	 */
	public Value(Type<T> type, String string, TypeSerializer serializer)
	{
		setValue(type, string, serializer);
	}

	
	/**
	 * Returns the contained value.
	 */
	public T getValue()
	{
		return value_;
	}
	
	
	/**
	 * Returns if the contained value is not null. 
	 */
	public boolean hasValue()
	{
		return value_ != null;
	}

	
	/**
	 * Sets the value and clears any error.
	 */
	public void setValue(T value)
	{
		value_      = value;
		error_ 		= null;
		errorValue_ = null;
	}
	
	
	/**
	 * Parses the value from a string.
	 * @param type a Type object
	 * @param string the string representation 
	 * @param serializer a TypeSerializer which understands the string format.
	 * @return true if the value was successfully parsed, false if not.
	 */
	public boolean setValue(Type<T> type, String string, TypeSerializer serializer)
	{
		try
		{
			T value = serializer.parse(type, string);
			setValue(value);
			return true;
		}
		catch(Exception e)
		{
			setError(e, string);
			return false;
		}
	}

	
	/**
	 * Returns the error.
	 * @return the error or null. 
	 */
	public Throwable getError()
	{
		return error_;
	}
	
	
	/**
	 * Returns the original string value
     * which represented the value but could not be parsed.
	 */
	public String getErrorValue()
	{
		return errorValue_;
	}

	
	/**
	 * Returns if the Value has an error.
	 */
	public boolean hasError()
	{
		return error_ != null;
	}
	
	
	/**
	 * Sets the error and clears the value.
	 */
	public void setError(Throwable error)
	{
		setError(error, null);
	}
	
	
	/**
	 * Sets the error and clears the value.
	 * @param errorValue the original string value
     * 		which represented the value but could not be parsed.
	 */
	public void setError(Throwable error, String errorValue)
	{
		value_ = null;
		error_ = error;
		errorValue_ = errorValue;
	}

	
	/**
	 * Returns a debug string representation of the value.
	 */
	@Override public String toString()
	{
		return "Value[" + value_ + ']';
	}

	
	private T value_;
	private Throwable error_;
	private String errorValue_;
}
