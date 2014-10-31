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


/**
 * A TypeSerializer represents a certain schema of parsing and formatting
 * values from or to a string.
 */
public interface TypeSerializer
{
	/**
	 * Formats a BigDecimal.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatBigDecimal(BigDecimal value, Object style);

	
	/**
	 * Formats a NigInteger.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatBigInteger(BigInteger value, Object style);

	
	/**
	 * Formats a boolean.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatBoolean(boolean value, Object style);


	/**
	 * Formats a Boolean.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatBoolean(Boolean value, Object style);
	
	
	/**
	 * Formats a byte.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatByte(byte value, Object style);
	
	
	/**
	 * Formats a Byte.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatByte(Byte value, Object style);

	
	/**
	 * Formats a char.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatChar(char value, Object style);

	
	/**
	 * Formats a Character.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatCharacter(Character value, Object style);


	/**
	 * Formats a Date.
	 * @param year the year (the year 2001 is specified as 2001)
	 * @param month the month (counting from 1 to 12)
	 * @param day the day (counting from 1 to 31)
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatDate(int year, int month, int day, Object style);


	/**
	 * Formats a double.
	 * @param value a double
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatDouble(double value, Object style);


	/**
	 * Formats a Double.
	 * @param value a double
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatDouble(Double value, Object style);
	
	
	/**
	 * Formats a float.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatFloat(float value, Object style);
	
	
	/**
	 * Formats a Float.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatFloat(Float value, Object style);

	
	/**
	 * Formats a int.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatInt(int value, Object style);

	
	/**
	 * Formats a Integer.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatInteger(Integer value, Object style);

	
	/**
	 * Formats a long.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatLong(long value, Object style);

	
	/**
	 * Formats a Long.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatLong(Long value, Object style);


	/**
	 * Formats a null.
	 */
	public String formatNull();

	
	/**
	 * Formats a short.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatShort(short value, Object style);
	
	
	/**
	 * Formats a Short.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatShort(Short value, Object style);


	/**
	 * Formats a String.
	 * @param value the value
	 * @param style an optional style object, hinting formatting variants to the type serializer
	 */
	public String formatString(String value, Object style);


	/**
	 * Parses a BigInteger from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract BigInteger parseBigInteger(String s) throws Exception;

	
	/**
	 * Parses a BigDecimal from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract BigDecimal parseBigDecimal(String s) throws Exception;
	
	
	/**
	 * Parses a Boolean from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Boolean parseBoolean(String s) throws Exception;

	
	/**
	 * Parses a Byte from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Byte parseByte(String s) throws Exception;

	
	/**
	 * Parses a Character from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Character parseCharacter(String s) throws Exception;
	
	
	/**
	 * Parses a Date from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract <T> T parseDate(String s, DateType<T> dateType) throws Exception;
	
	
	/**
	 * Parses a Double from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Double parseDouble(String s) throws Exception;

	
	/**
	 * Parses a Float from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Float parseFloat(String s) throws Exception;
	

	/**
	 * Parses a Integer from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Integer parseInteger(String s) throws Exception;

	
	/**
	 * Parses a Long from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Long parseLong(String s) throws Exception;
	
	
	/**
	 * Parses a Short from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract Short parseShort(String s) throws Exception;
	
	
	/**
	 * Parses a String from a String.
	 * @throws Exception thrown if parsing fails
	 */
	public abstract String parseString(String s) throws Exception;
}
