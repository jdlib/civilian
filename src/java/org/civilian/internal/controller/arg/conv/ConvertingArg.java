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
package org.civilian.internal.controller.arg.conv;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import org.civilian.Request;
import org.civilian.controller.MethodArg;
import org.civilian.internal.controller.arg.StringMethodArg;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.TypeSerializer;
import org.civilian.type.fn.StandardSerializer;
import org.civilian.util.Value;


/**
 * Base class for MethodArgs whose
 * request value is String based and needs conversion
 * to the type of the method parameter.
 */
public class ConvertingArg<T> extends MethodArg 
{
	public static <T> MethodArg create(StringMethodArg arg,
		String defaultValue,
		boolean isLocaleValue,
		TypeLib typeLib, 
		Class<?> paramType) throws Exception
	{
		return create(arg, defaultValue, isLocaleValue, typeLib, paramType, paramType);
	}
	
	
	public static <T> MethodArg create(StringMethodArg arg,
		String defaultValue,
		boolean isLocaleValue,
		TypeLib typeLib, 
		Class<?> type,
		Type genericType) throws Exception
	{
		// 1. genericType is Value or Value<X>
		if (type == Value.class)
			return createValueArg(arg, defaultValue, isLocaleValue, typeLib, genericType);
		
		
		// 2. collection converters
		CollectionConverter<T> collConverter = CollectionConverters.create(typeLib, type, genericType);
		if (collConverter != null)
		{
			Collection<T> dv = defaultValue != null ? 
				collConverter.convert(null, StandardSerializer.INSTANCE, defaultValue) :
				collConverter.emptyCollection();
			return new ConvertingArg<>(arg, dv, isLocaleValue, collConverter);
		}
		
		// 3. simple converters
		SimpleConverter<T> simpleConverter = SimpleConverters.create(typeLib, type);
		if (simpleConverter != null)
		{
			T dv = defaultValue != null ? 
				simpleConverter.convert(null, StandardSerializer.INSTANCE, defaultValue) :
				simpleConverter.nullValue();
			return new ConvertingArg<>(arg, dv, isLocaleValue, simpleConverter);
		}
		
		throw new IllegalArgumentException("type '" + genericType + " is not supported");
	}

	
	private static <T> MethodArg createValueArg(StringMethodArg arg,
		String defaultValue,
		boolean isLocaleValue,
		TypeLib typeLib, 
		Type genericType) throws Exception
	{
		Class<?> valueType = null;
		Type valueGenericType = null;
		
		if (!(genericType instanceof ParameterizedType))
			valueGenericType = valueType = String.class;
		else
		{
			ParameterizedType ptype = (ParameterizedType)genericType;
			Type[] ptypeArgs = ptype.getActualTypeArguments();
			if (ptypeArgs.length == 1)
			{
				valueGenericType = ptypeArgs[0];
				valueType = extractClass(valueGenericType);
			}
		}
		
		if (valueType == null)
			return null;
		else
		{
			MethodArg convertingArg = create(arg, defaultValue, isLocaleValue, typeLib, valueType, valueGenericType);
			return new ValueArg(convertingArg);
		}
	}
	
	
	private static Class<?> extractClass(Type type)
	{
		if (type instanceof Class)
			return (Class<?>)type;
		
		if (type instanceof ParameterizedType)
		{
			ParameterizedType ptype = (ParameterizedType)type;
			if (ptype.getRawType() instanceof Class)
				return (Class<?>)ptype.getRawType();
		}
		
		return null;
	}
	

	/**
	 * Creates a new ConvertingArg object.
	 * @param arg the inner arg
	 * @param defaultValue the default value, if the parameter is not part of the request
	 */
	protected ConvertingArg(StringMethodArg arg, T defaultValue, boolean isLocaleValue, Converter<T> converter)
	{
		arg_ 			= arg;
		defaultValue_	= defaultValue;
		isLocaleValue_	= isLocaleValue;
		converter_		= converter;
	}
	
	
	/**
	 * Extracts the parameter from the request and
	 * converts into the target value which will be injected into
	 * argument variable. 
	 */
	@Override public T getValue(Request request) throws Exception
	{
		TypeSerializer serializer = isLocaleValue_ ? 
			request.getLocaleSerializer() : 
			StandardSerializer.INSTANCE;
		return converter_.getValue(request, arg_, serializer, defaultValue_);
	}

	
	/**
	 * Builds a string representation of the argument.
	 */
	@Override public String toString()
	{
		return arg_.toString();
	}
	

	private StringMethodArg arg_;
	private T defaultValue_;
	private boolean isLocaleValue_;
	private Converter<T> converter_;
}


