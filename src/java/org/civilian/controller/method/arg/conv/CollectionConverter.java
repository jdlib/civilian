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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

import org.civilian.controller.method.arg.MethodArg;
import org.civilian.controller.method.arg.StringMethodArg;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.text.type.TypeSerializer;


public abstract class CollectionConverter<T> extends Converter<Collection<T>>
{
	public CollectionConverter(SimpleConverter<T> sc)
	{
		sc_ = sc;
	}
	

	@Override public Collection<T> getValue(Request request, 
		Response response, 
		StringMethodArg arg,
		TypeSerializer serializer, Collection<T> defaultValue) throws Exception
	{
		String[] values = arg.getValues(request, response);
		return ((values != null) && (values.length > 0)) ? 
			convert(arg, serializer, values) :
			defaultValue;
	}

	
	/**
	 * Converts an array of parameter string values.
	 * @param arg the method arg
	 * @param serializer a serializer
	 * @param values the values, must not be null.
	 * @return the converted collection
	 * @throws Exception if conversion fails
	 */
	public Collection<T> convert(MethodArg arg, TypeSerializer serializer, String... values) throws Exception
	{
		Collection<T> collection = emptyCollection();
		for (String s : values)
			collection.add(sc_.convert(arg, serializer, s));
		return collection;
	}


	public abstract Collection<T> emptyCollection();


	private final SimpleConverter<T> sc_;
}


class ListConverter<T> extends CollectionConverter<T>
{
	public ListConverter(SimpleConverter<T> sc)
	{
		super(sc);
	}
	

	@Override public Collection<T> emptyCollection()
	{
		return new ArrayList<>();
	}
}


class SetConverter<T> extends CollectionConverter<T>
{
	SetConverter(SimpleConverter<T> sc)
	{
		super(sc);
	}


	@Override public Collection<T> emptyCollection()
	{
		return new HashSet<>();
	}
}


class SortedSetConverter<T> extends CollectionConverter<T>
{
	SortedSetConverter(SimpleConverter<T> sc)
	{
		super(sc);
	}


	@Override public Collection<T> emptyCollection()
	{
		return new TreeSet<>();
	}
}