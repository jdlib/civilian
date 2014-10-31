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
 * Check defines helper methods for argument checking.
 */
public class Check
{
	/**
	 * Checks that an object is not null.
	 * @param object the object
	 * @param what describes the object.
	 * @return the object
	 * @exception IllegalArgumentException if the object is null.
	 */
	public static <T> T notNull(T object, String what)
	{
		if (object == null)
			throw new IllegalArgumentException(what + " is null");
		return object;
	}
	
	
	/**
	 * Checks that an object is null.
	 * @param object the object
	 * @param what describes the object.
	 * @exception IllegalArgumentException if the object is null.
	 */
	public static void isNull(Object object, String what)
	{
		if (object != null)
			throw new IllegalArgumentException(what + " is already set");
	}


	/**
	 * Checks that an array is not null and has length > 0.
	 * @param array the array
	 * @return the array
	 * @param what describes the array.
	 * @exception IllegalArgumentException if the array is empty.
	 */
	public static <T> T[] notEmpty(T[] array, String what)
	{
		notNull(array, what);
		if (array.length == 0)
			throw new IllegalArgumentException(what + " empty");
		return array;
	}


	/**
	 * Checks that a string is not null and has length > 0.
	 * @param s the string
	 * @return the string
	 * @param what describes the string.
	 * @exception IllegalArgumentException if the string is empty.
	 */
	public static String notEmpty(String s, String what)
	{
		notNull(s, what);
		if (s.length() == 0)
			throw new IllegalArgumentException(what + " empty");
		return s;
	}


	/**
	 * Checks that a double is between [min, max].
	 * @param v the double
	 * @param min the minimum
	 * @param max the maximum
	 * @param what describes the value
	 * @return the value
	 * @exception IllegalArgumentException if the value is outside the range.
	 */
	public static double between(double v, double min, double max, String what)
	{
		if ((v < min) || (v > max))
			throw new IllegalArgumentException(what + " must be between " + min + " and " + max + ": " + v);
		return v;
	}


	/**
	 * Checks that a int is between [min, max].
	 * @param v the values
	 * @param min the minimum
	 * @param max the maximum
	 * @param what describes the value
	 * @return the value
	 * @exception IllegalArgumentException if the value is outside the range.
	 */
	public static int between(int v, int min, int max, String what)
	{
		if ((v < min) || (v > max))
			throw new IllegalArgumentException(what + " must be between " + min + " and " + max + ": " + v);
		return v;
	}

	
	/**
	 * Checks that a int value is >= another value.
	 * @param n the value
	 * @param what describes the value
	 * @return the value
	 * @exception IllegalArgumentException if the value is outside the range.
	 */
	public static int greaterEquals(int n, int min, String what)
	{
		if (n < min)
			throw new IllegalArgumentException(what + " must >= " + min + ", but is " + n);
		return n;
	}
	

	/**
	 * Checks that the first class is a superclass of the second class.
	 */
	public static void isSuperclassOf(Class<?> superClass, Class<?> derivedClass)
	{
		if (!superClass.isAssignableFrom(derivedClass))
			throw new IllegalArgumentException("class '" + derivedClass.getName() + "' is not derived from '" + superClass.getName() + "'");
	}
}
