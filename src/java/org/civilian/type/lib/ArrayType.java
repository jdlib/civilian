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


import java.lang.reflect.Array;
import java.util.regex.Pattern;
import org.civilian.type.ListType;
import org.civilian.type.Type;
import org.civilian.type.TypeSerializer;
import org.civilian.type.TypeVisitor;
import org.civilian.util.Check;


/**
 * A Type implementation for array types.
 */
public class ArrayType<T> extends ListType<T[],T>
{
	private static final Pattern LIST_SPLITTER = Pattern.compile("\\s*\\,\\s*");

	
	public ArrayType(Type<T> elementType)
	{
		elementType_ = Check.notNull(elementType, "elementType");
	}
	
	
	/**
	 * Returns false.
	 */
	@Override public boolean isSimpleType()
	{
		return false;
	}
	

	/**
	 * Returns the element type.
	 */
	@Override public Type<T> getElementType()
	{
		return elementType_;
	}


	/**
	 * Calls {@link #format(TypeSerializer, Object, Object)} with a null style parameter.
	 */
	@Override public String format(TypeSerializer serializer, T[] values)
	{
		return format(serializer, values, null);
	}

	
	/**
	 * Returns a "naive" concatenation of the values, separated by ",".
	 * Obviously this method returns a wrong result if a formatted value contains a comma.
	 */
	@Override public String format(TypeSerializer serializer, T[] values, Object style)
	{
		if (values == null)
			return null;
		
		StringBuilder s = new StringBuilder();
		for (int i=0; i<values.length; i++)
		{
			if (i > 0)
				s.append(",");
			s.append(elementType_.format(serializer, values[i]));
		}
		  
		return s.toString();
	}
	

	/**
	 * Assumes that the string contains formatted values, separated by ","
	 * The method splits the string and parses the parts.
	 */
	@Override public T[] parse(TypeSerializer serializer, String s) throws Exception
	{
		return s == null ? null : parseList(serializer, LIST_SPLITTER.split(s));
	}

	
	/**
	 * Parses the string array and returns an array of converted values.
	 */
	@Override public T[] parseList(TypeSerializer serializer, String... s) throws Exception
	{
		if (s == null)
			return null;
		else
		{
			T[] result = createArray(s.length); 
			for (int i=0; i<s.length; i++)
				result[i] = elementType_.parse(serializer, s[i]);
			return result;
		}
	}

	
	@Override public <R, P, E extends Exception> R accept(TypeVisitor<R, P, E> visitor, P param) throws E
	{
		return visitor.visitArray(param, elementType_);
	}


	@SuppressWarnings("unchecked")
	@Override public Class<T[]> getJavaType()
	{
		if (javaType_ == null)
			javaType_ = (Class<T[]>)Array.newInstance(elementType_.getJavaType(), 0).getClass();
		return javaType_;
	}

	
	@Override public Class<T[]> getJavaPrimitiveType()
	{
		return null;
	}


	@SuppressWarnings("unchecked")
	public T[] createArray(int length)
	{
		return (T[])Array.newInstance(elementType_.getJavaType(), length);
	}

	
	private Type<T> elementType_;
	private Class<T[]> javaType_; 
}
