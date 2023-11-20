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
		this(null);
	}
	
	
	/**
	 * Creates a new Value.
	 * @param value the value
	 */
	public Value(T value)
	{
		value_ 		= value;
		error_ 		= null;
		errorValue_ = null;
	}
	
	
	/**
	 * Creates a error Value.
	 * @param error the error
	 * @param errorValue the original string value
     * 		which represented the value but could not be parsed.
	 */
	public Value(Throwable error, String errorValue)
	{
		value_ 		= null;
		error_ 		= Check.notNull(error, "error");
		errorValue_ = errorValue;
	}

	
	/**
	 * @return the contained value.
	 */
	public T getValue()
	{
		return value_;
	}
	
	
	/**
	 * @return if the contained value is not null. 
	 */
	public boolean hasValue()
	{
		return value_ != null;
	}
	
	
	/**
	 * @return the error or null. 
	 */
	public Throwable getError()
	{
		return error_;
	}
	
	
	/**
	 * @return the original string value
     * which represented the value but could not be parsed.
	 */
	public String getErrorValue()
	{
		return errorValue_;
	}

	
	/**
	 * @return if the Value has an error.
	 */
	public boolean hasError()
	{
		return error_ != null;
	}

	
	/**
	 * @return a debug string representation of the value.
	 */
	@Override public String toString()
	{
		return "Value[" + value_ + ']';
	}

	
	private final T value_;
	private final Throwable error_;
	private final String errorValue_;
}
