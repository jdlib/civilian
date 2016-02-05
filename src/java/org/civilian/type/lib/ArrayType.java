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
	 * Create a list object from an value array.
	 */
	@Override public T[] create(T[] values)
	{
		return values;
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


	private Type<T> elementType_;
	private Class<T[]> javaType_; 
}
