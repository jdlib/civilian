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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;

import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.util.Check;


/**
 * TypeLibrary is a collection of Type implementations. It also provides constants
 * for the predefined types.
 * Each {@link Application} has a TypeLibrary which is especially used when parameter
 * values are parsed or formatted in a locale dependent way. 
 * If you need additional Type implementations not covered by the standard library,
 * you should implement them for yourself and add them to the applications type library
 * during application setup (see {@link AppConfig#setTypeLib(TypeLib)}
 */
public class TypeLib implements Iterable<Type<?>>
{
	public static final Type<BigDecimal>				BIGDECIMAL 		= SimpleType.BIGDECIMAL;
	public static final Type<BigInteger> 				BIGINTEGER 		= SimpleType.BIGINTEGER;
	public static final Type<Boolean> 					BOOLEAN  		= SimpleType.BOOLEAN;
	public static final Type<Byte> 						BYTE 			= SimpleType.BYTE;
	public static final Type<Character> 				CHARACTER		= SimpleType.CHARACTER;
	public static final DateType<java.util.Calendar> 	DATE_CALENDAR	= DateCalendarType.INSTANCE;
	public static final DateType<LocalDate> 			DATE_LOCAL		= DateLocalType.INSTANCE;
	public static final DateType<java.util.Date> 		DATE_JAVA_UTIL	= DateJavaUtilType.INSTANCE;
	public static final DateType<java.sql.Date> 		DATE_JAVA_SQL	= DateJavaSqlType.INSTANCE;
	public static final DateTimeType<LocalDateTime> 	DATETIME_LOCAL	= DateTimeLocalType.INSTANCE;
	public static final Type<Double> 					DOUBLE  		= SimpleType.DOUBLE;
	public static final Type<Float> 					FLOAT 			= SimpleType.FLOAT;
	public static final Type<Integer> 					INTEGER 		= SimpleType.INTEGER;
	public static final Type<Long> 						LONG  			= SimpleType.LONG;
	public static final Type<Short> 					SHORT  			= SimpleType.SHORT;
	public static final Type<String> 					STRING 			= SimpleType.STRING;
	public static final TimeType<LocalTime>				TIME_LOCAL		= TimeLocalType.INSTANCE;


	/**
	 * Returns the default type library. 
	 */
	public static TypeLib getDefaultTypeLib()
	{
		return defaultTypeLib_;
	}


	/** 
	 * Creates a new type library which contains all type implementations from the lib subpackge.
	 */
	public TypeLib()
	{
		this(true);
	}
	
	
	/**
	 * Creates a new TypeLibrary.
	 * @param addDefaults if true, then all type implementations from the lib subpackage
	 * 		are added, else the type library is initially empty.
	 */
	public TypeLib(boolean addDefaults)
	{
		if (addDefaults)
		{
			put(BIGDECIMAL);
			put(BIGINTEGER);
			put(BOOLEAN);
			put(BYTE);
			put(CHARACTER);
			put(DATE_CALENDAR);
			put(DATE_JAVA_UTIL);
			put(DATE_JAVA_SQL);
			put(DATE_LOCAL);
			put(DATETIME_LOCAL);
			put(DOUBLE);
			put(FLOAT);
			put(INTEGER);
			put(LONG);
			put(SHORT);
			put(STRING);
			put(TIME_LOCAL);

			put(boolean.class, 	BOOLEAN);
			put(byte.class, 	BYTE);
			put(double.class, 	DOUBLE);
			put(float.class,	FLOAT);
			put(int.class,		INTEGER);
			put(long.class,		LONG);
			put(short.class,	SHORT);
		}
	}

	
	/**
	 * Returns the number of type contained in the library. 
	 */
	public int size()
	{
		return map_.size();
	}
	
	
	/**
	 * Adds a type to the library. 
	 */
	public <T> void put(Type<T> type)
	{
		Check.notNull(type, "type");
		put(type.getJavaType(), type);
	}
	
	
	private <T> void put(Class<? extends T> cls, Type<T> type)
	{
		map_.put(cls, type);
	}

	
	/** 
	 * Returns a Type for a Java class. 
	 */
	@SuppressWarnings("unchecked")
	public <T> Type<? super T> get(Class<T> c)
	{
		Type<T> type = (Type<T>)map_.get(c);
		return type != null ? type : getExtended(c);
	}
	
	
	@SuppressWarnings("unchecked")
	private <T> Type<? super T> getExtended(Class<T> c)
	{
		while (c != null)
		{
			c = (Class<T>)c.getSuperclass();
			Type<? super T> type = (Type<? super T>)map_.get(c);
			if (type != null)
				return type;
		}
		return null;
	}
	
		
	/** 
	 * Returns a Type for a Java class. 
	 * @throws IllegalArgumentException if no type is registered for the class.
	 */
	public <T> Type<? super T> getSafe(Class<T> c)
	{
		Type<? super T> type = get(c);
		if (type == null)
			throw new IllegalArgumentException("no type for '" + c + "' defined");
		return type;
	}

	
	/** 
	 * Removes a Type from the library. 
	 */
	@SuppressWarnings("unchecked")
	public <T> Type<T> remove(Class<T> c)
	{
		return (Type<T>)map_.remove(c.isPrimitive() ? getObjectClassForPrimitiveType(c) : c);
	}
	
	
	/** 
	 * Removes a Type from the library. 
	 */
	public <T> Type<T> remove(Type<T> type)
	{
		return remove(type.getJavaType());
	}

	
	/** 
	 * Returns an iterator for the types. 
	 */
	@Override public Iterator<Type<?>> iterator()
	{
		return map_.values().iterator();
	}

	
	/**
	 * Returns the associated class of a class of primitive type.
	 * @param c a class for a primitive type (e.g. int.class)
	 */
	public static Class<?> getObjectClassForPrimitiveType(Class<?> c)
	{
		Object value = getPrimitiveDefaultValue(c);
		return value != null ? value.getClass() : null;
	}
	
	
	/**
	 * Returns a default value for a primitive java type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getPrimitiveDefaultValue(Class<T> c)
	{
		return (T)PRIMITIVE_MAP.get(c);
	}
	

	private HashMap<Class<?>, Type<?>> map_ = new HashMap<>();
	private static final TypeLib defaultTypeLib_ = new TypeLib();
	private static final HashMap<Class<?>, Object> PRIMITIVE_MAP = new HashMap<>();
	static 
	{
		PRIMITIVE_MAP.put(boolean.class, Boolean.FALSE);
		PRIMITIVE_MAP.put(byte.class, 	 Byte.valueOf((byte)0));
		PRIMITIVE_MAP.put(short.class, 	 Short.valueOf((short)0));
		PRIMITIVE_MAP.put(char.class, 	 Character.valueOf((char)0));
		PRIMITIVE_MAP.put(int.class, 	 Integer.valueOf(0));
		PRIMITIVE_MAP.put(long.class, 	 Long.valueOf(0L));
		PRIMITIVE_MAP.put(float.class, 	 Float.valueOf(0.0f));
		PRIMITIVE_MAP.put(double.class,  Double.valueOf(0.0d));
	}
}
