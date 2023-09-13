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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;


/**
 * Helper methods for array handling.
 */
public abstract class ArrayUtil
{
	/**
	 * Creates a new array, with the given item prepended.
	 * @param array an array
	 * @param item an item
	 * @param <T> the element type
	 * @return the new array
	 */
	public static <T> T[] addFirst(T[] array, T item)
	{
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
		System.arraycopy(array, 0, result, 1, array.length);
		result[0] = item;
		return result;
	}

	
	/**
	 * Creates a new array, with the given item appended to the end.
	 * @param array an array
	 * @param item an item
	 * @param <T> the element type
	 * @return the new array
	 */
	public static <T> T[] addLast(T[] array, T item)
	{
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
		System.arraycopy(array, 0, result, 0, array.length);
		result[array.length] = item;
		return result;
	}


	/**
	 * Creates a new array, with the given item appended to the end.
	 * @param array1 the first array
	 * @param array2 the second array
	 * @param <T> the element type
	 * @return the new array
	 */
	public static <T> T[] concat(T[] array1, T[] array2)
	{
		if (array1 == null) 
			return array2;
		else if (array2 == null)
			return array1;
		else
		{
			@SuppressWarnings("unchecked")
			T[] result = (T[])Array.newInstance(array1.getClass().getComponentType(), array1.length + array2.length);
			System.arraycopy(array1, 0, result, 0, array1.length);
			System.arraycopy(array2, 0, result, array1.length, array2.length);
			return result;
		}
	}

	
	/**
	 * Returns a new array, with the first count items removed.
	 * @param array an array
	 * @param count a element count
	 * @param <T> the element type
	 * @return the new array
	 */
	public static <T> T[] removeFirst(T[] array, int count)
	{
		if (array.length < count)
			throw new IllegalArgumentException("array length is " + array.length);
		if (count == 0)
			return array;
		
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - count);
		System.arraycopy(array, count, result, 0, result.length);
		return result;
	}
	
	
	/**
	 * Returns a new array, with the item at the given index removed
	 * @param <T> the element type
	 * @param array an array
	 * @param index an index into the array
	 * @return the array
	 */
	public static <T> T[] removeAt(T[] array, int index)
	{
		if ((index < 0) || (index >= array.length))
			return array;
		
		@SuppressWarnings("unchecked")
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), array.length - 1);
		if (index > 0)
			System.arraycopy(array, 0, result, 0, index);
		if (index < array.length - 1)
			System.arraycopy(array, index + 1, result, index, array.length - 1 - index);
		return result;
	}

	
	/**
	 * Tests if the array contains the item.
	 * @param <T> the element type
	 * @param array an array
	 * @param item an item
	 * @return contains?
	 */
	public static <T> boolean contains(T[] array, T item)
	{
		if (array != null)
		{
			for (int i=0; i<array.length; i++)
			{
				if (Objects.equals(array[i], item))
					return true;
			}
		}
		return false;
	}


	/**
	 * Collects the items of an Enumeration and returns them as an array.
	 * @param it an Enumeration
	 * @param c the element class
	 * @param <T> the element type
	 * @return the array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Enumeration<T> it, Class<T> c)
	{
		ArrayList<T> list = new ArrayList<>(); 
		while(it.hasMoreElements())
			list.add(it.nextElement());
		return list.toArray((T[])Array.newInstance(c, list.size()));
	}
}
