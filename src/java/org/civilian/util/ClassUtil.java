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


import java.lang.annotation.Annotation;


/**
 * A collection of class, annotation and Enum related utility methods.
 */
public abstract class ClassUtil
{
	/**
	 * Tests if two objects are equal.
	 */
	public static boolean equals(Object s1, Object s2)
	{
		return s1 == null ? s2 == null : s1.equals(s2);
	}
	

	/**
	 * Returns the package part of the class name (without a trailing dot).
	 * Alternatively you could use Class.getPackage.getName(), but
	 * since Class.getPackage could return null, this method is safer.
	 */
	public static String getPackageName(Class<?> c)
	{
		return getPackageName(c != null ? c.getName() : null);
	}


	/**
	 * Returns the package part of the given class name (without a trailing dot).
	 * 
	 */
	public static String getPackageName(String className)
	{
		if (className == null)
			return null;
		else
		{	
			int p = className.lastIndexOf('.');
			return (p == -1) ? null : className.substring(0, p);
		}
	}
	
	
	/**
	 * Removes the package from the class name and returns the rest.
	 * In case of an inner class, this will return
	 * the inner class name, prefixed by all outer class names. 
	 */
	public static String cutPackageName(String className)
	{
		if (className == null)
			return null;
		else
		{
			int p = className.lastIndexOf('.');
			return (p == -1) ? className : className.substring(p + 1);
		}
	}
	
	
	/**
	 * Tests if the object is an instance of the given class.
	 */
	public static <T> boolean isA(Object object, Class<T> c)
	{
		return (object != null) && (c != null) && c.isAssignableFrom(object.getClass());
	}


	/**
	 * Creates an object instance for a given class name.
	 * @param className the name of the class
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass. 
	 * @throws ClassNotFoundException if the class was not found 
	 * @throws IllegalArgumentException if the class is not derived from the given superClass. 
	 * @throws InstantiationException if the instance could not be created 
	 * @throws IllegalAccessException if the instance could not be created 
	 */
	public static <T> T createObject(String className, Class<T> superClass, ClassLoader loader) 
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Class<? extends T> c = getClass(className, superClass, loader);
		return c.newInstance();
	}
	
	
	/**
	 * Returns a class for a given class name.
	 * @param className the name of the class
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass. 
	 * @throws ClassNotFoundException if the class was not found 
	 * @throws IllegalArgumentException if the class is not derived from the given superClass. 
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getClass(String className, Class<T> superClass, ClassLoader loader)
		throws ClassNotFoundException 
	{
		Check.notNull(className, "className");
		Check.notNull(superClass, "superClass");
		
		Class<?> c = loader != null ? loader.loadClass(className) : Class.forName(className);
		Check.isSuperclassOf(superClass, c);
		return (Class<? extends T>)c;
	}
	
	
	/**
	 * Returns the class for a given class name or null if the class
	 * cannot be found.
	 * @param className the name of the class
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass. 
	 * @throws IllegalArgumentException if the class is not derived from the given superClass. 
	 */
	public static <T> Class<? extends T> getPotentialClass(String className, Class<T> superClass, ClassLoader loader)  
	{
		try
		{
			return getClass(className, superClass, loader);
		}
		catch(ClassNotFoundException e)
		{
			return null;
		}
	}

	
	/**
	 * Tests if the annotation is an instance of the given annotation class.
	 */
	public static <A extends Annotation> boolean isA(Annotation annotation, Class<A> c)
	{
		return (annotation != null) && (c != null) && c.isAssignableFrom(annotation.annotationType());
	}
	

	/**
	 * Returns the first annotation in a list of annotations which is an instance of the
	 * given annotation class.
	 */
	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A findAnnotation(Annotation[] annotations, Class<A> c)
	{
		for (Annotation a : annotations)
		{
			if (isA(a, c))
				return (A)a;
		}
		return null;
	}

	
	/**
	 * Tests if an object is an instance of a given class.
	 * If yes the object is casted to that class and returned, else null is returned.
	 * Additionally if the provided object is an object array all elements of the array
	 * will be tested if they can be unwrapped to the target class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unwrap(Object object, Class<T> targetClass)
	{
		if (isA(object, targetClass))
			return (T)object;
		if (object instanceof Object[])
		{
			for (Object element : (Object[])object)
			{
				T t = unwrap(element, targetClass);
				if (t != null)
					return t;
			}
		}
		return null;
	}
	
	
	/**
	 * Translates a Enum name into an Enum object.
	 * If the Enum name is null or not defined, then the default value is returned. This helps 
	 * to avoid the IllegalArgumentException thrown by Enum.valueOf 
	 */
	public static <T extends Enum<T>> T getEnum(Class<T> enumClass, String name, T defaultValue)
	{
		try
		{
			if (name != null)
				return Enum.valueOf(enumClass, name);
		}
		catch(IllegalArgumentException e)
		{
		}
		return defaultValue;
	}
	

	/**
	 * Returns the package name of the class, with all '.' characters
	 * replaced by '/'.
	 */
	public static String buildResourcePath(Class<?> c)
	{
		return getPackageName(c).replace('.', '/');
	}


	/**
	 * Builds the resource path for the class and appends the file name.
	 * The returned string is suitable to be passed to ClasLoader.getResource().
	 */
	public static String buildResourceFile(Class<?> c, String name)
	{
		return buildResourcePath(c) + '/' + name;
	}
}
