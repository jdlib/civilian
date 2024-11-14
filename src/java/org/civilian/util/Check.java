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
	 * @param <T> the class type
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
	 * Checks that an array is not null and has length &gt; 0.
	 * @param array the array
	 * @return the array
	 * @param what describes the array.
	 * @exception IllegalArgumentException if the array is empty.
	 * @param <T> the class type
	 */
	public static <T> T[] notEmpty(T[] array, String what)
	{
		notNull(array, what);
		if (array.length == 0)
			throw new IllegalArgumentException(what + " empty");
		return array;
	}


	/**
	 * Checks that a CharSequence is not null and has length &gt; 0.
	 * @param s the CharSequence
	 * @param what describes the string
	 * @param <T> the CharSequence type
	 * @return the CharSequence
	 * @exception IllegalArgumentException if the CharSequence is empty.
	 */
	public static <T extends CharSequence> T notEmpty(T s, String what)
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
	 * Checks that a int value is &gt;= another value.
	 * @param n the value
	 * @param min the minimum
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
	 * @param superClass the super class candidate
	 * @param derivedClass the derived class candidate
	 */
	public static void isSuperclassOf(Class<?> superClass, Class<?> derivedClass)
	{
		if (!superClass.isAssignableFrom(derivedClass))
			throw new IllegalArgumentException("class '" + derivedClass.getName() + "' is not derived from '" + superClass.getName() + "'");
	}


	public static <T> T isA(Object object, Class<T> type)
	{
		return isA(object, type, null);
	}


	/**
	 * Checks that an object is an instance of a class.
	 * @param object the object
	 * @param type a class
	 * @param what describes the object.
	 * @return the object
	 * @exception IllegalArgumentException if the object is null.
	 * @param <T> the class type
	 */
	@SuppressWarnings("unchecked")
	public static <T> T isA(Object object, Class<T> type, String what)
	{
		notNull(object, what);
		if (!type.isAssignableFrom(object.getClass()))
			isAFailed(object, type, what);
		return (T)object;
	}


	private static void isAFailed(Object object, Class<?> type, String what)
	{
		String cname = object.getClass().getName();
		String descr = what == null ? cname : what + " (" + cname + ')';
		if (!cname.equals(type.getName()))
			throw new IllegalArgumentException(descr + " is not a " + type.getName());
		else
			throw new IllegalStateException(descr + " was loaded by " + object.getClass().getClassLoader() + " and not by " + type.getClassLoader());
	}
}
