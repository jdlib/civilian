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
package org.civilian.controller.method.arg.conv;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.civilian.controller.method.arg.MethodArg;
import org.civilian.controller.method.arg.StringMethodArg;
import org.civilian.request.BadRequestException;
import org.civilian.request.Request;
import org.civilian.text.type.TypeSerializer;
import org.civilian.type.Type;
import org.civilian.type.TypeLib;


public abstract class SimpleConverter<T> extends Converter<T>
{
	@Override public T getValue(Request request, StringMethodArg arg, 
		TypeSerializer serializer,
		T defaultValue) throws Exception
	{
		T value = null;

		// s = null -> value = null
		// s = ""   -> value = null possible, for instance for number conversions
		String s = arg.getValue(request);
		if (s != null)
			value = convert(arg, serializer, s);
		
		return value != null ? value : defaultValue;
	}

	
	/**
	 * Converts a string parameter into a value.
	 * @param arg the call argument which provided the string value or null	if 
	 * 		not called in the context of a argument injection
	 * @param stringValue the value, must not be null.
	 * @throws Exception thrown if conversion throws an error. If arg is not null, 
	 * 		the error is catched and turned into a BadRequestException 
	 */
	public T convert(MethodArg arg, TypeSerializer serializer, String stringValue) 
		throws Exception
	{
		try
		{
			return convertImpl(serializer, stringValue);
		}
		catch(Exception e)
		{
			if (arg != null)
			{
				String message = arg.toString() + ": invalid value \"" + stringValue + '"';
				throw new BadRequestException(message, e).setErrorValue(stringValue);
			}
			else
				throw e;
		}
	}

	
	/**
	 * Converts a string parameter into a value.
	 * @param stringValue the value, must not be null.
	 */
	protected abstract T convertImpl(TypeSerializer serializer, String stringValue) throws Exception;


	public T nullValue()
	{
		return null;
	}
}


class StringConverter extends SimpleConverter<String>
{
	@Override
	protected String convertImpl(TypeSerializer serializer, String stringValue) throws Exception
	{
		return stringValue;
	}
}


class TypedConverter<T> extends SimpleConverter<T>
{
	public TypedConverter(Type<T> type, boolean isPrimitive)
	{
		type_ = type;
		isPrimitive_ = isPrimitive;
	}

	
	@Override protected T convertImpl(TypeSerializer serializer, String stringValue) throws Exception
	{
		return serializer.parse(type_, stringValue);
	}


	@Override public T nullValue()
	{
		return isPrimitive_ ? 
			(T)TypeLib.getPrimitiveDefaultValue(type_.getJavaPrimitiveType()) :
			null;
	}
	
	
	private Type<T> type_;
	private boolean isPrimitive_;
}



class CtorConverter<T> extends SimpleConverter<T>
{
	public CtorConverter(Constructor<T> ctor)
	{
		ctor_ = ctor;
	}

	
	@Override
	protected T convertImpl(TypeSerializer serializer, String stringValue) throws Exception
	{
		return ctor_.newInstance(stringValue);
	}
	
	
	private Constructor<T> ctor_;
}


class MethodConverter<T> extends SimpleConverter<T>
{
	public MethodConverter(Method method)
	{
		method_ = method;
	}

	
	@SuppressWarnings("unchecked") 
	@Override
	protected T convertImpl(TypeSerializer serializer, String stringValue) throws Exception
	{
		return (T)method_.invoke(null, stringValue);
	}
	
	
	private Method method_;
}
