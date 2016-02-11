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
package org.civilian.type;


import java.math.BigDecimal;
import java.math.BigInteger;
import org.civilian.util.Check;


public class SimpleType<T> extends Type<T>
{	
	public static final SimpleType<BigDecimal> BIGDECIMAL 	= new SimpleType<>(BigDecimal.class);
	public static final SimpleType<BigInteger> BIGINTEGER 	= new SimpleType<>(BigInteger.class);
	public static final SimpleType<Boolean> BOOLEAN			= new SimpleType<>(Boolean.class, boolean.class);
	public static final SimpleType<Byte> BYTE				= new SimpleType<>(Byte.class, byte.class);
	public static final SimpleType<Character> CHARACTER		= new SimpleType<>(Character.class, char.class);
	public static final SimpleType<Double> DOUBLE			= new SimpleType<>(Double.class, double.class);
	public static final SimpleType<Float> FLOAT				= new SimpleType<>(Float.class, float.class);
	public static final SimpleType<Integer> INTEGER			= new SimpleType<>(Integer.class, int.class);
	public static final SimpleType<Long> LONG				= new SimpleType<>(Long.class, long.class);
	public static final SimpleType<Short> SHORT 			= new SimpleType<>(Short.class, short.class);
	public static final SimpleType<String> STRING 			= new SimpleType<>(String.class);
	
	
	public SimpleType(Class<T> javaType)
	{
		this(javaType, null);
	}
	
	
	public SimpleType(Class<T> javaType, Class<T> javaPrimitiveType)
	{
		super(Category.SIMPLE);
		javaType_ = Check.notNull(javaType, "javaType");
		javaPrimitiveType_ = javaPrimitiveType;
	}

	
	@Override public Class<T> getJavaType()
	{
		return javaType_;
	}


	@Override public Class<T> getJavaPrimitiveType()
	{
		return javaPrimitiveType_;
	}

	
	/**
	 * Returns the simple name of the type class.
	 */
	@Override public String toString()
	{
		return getJavaType().getSimpleName() + "Type";
	}
	
	
	private final Class<T> javaType_;
	private final Class<T> javaPrimitiveType_;
}
