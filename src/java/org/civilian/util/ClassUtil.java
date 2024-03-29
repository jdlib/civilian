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
	 * Returns the package part of the class name (without a trailing dot).
	 * Alternatively you could use Class.getPackage.getName(), but
	 * since Class.getPackage could return null, this method is safer.
	 * @param c a class
	 * @return the package name
	 */
	public static String getPackageName(Class<?> c)
	{
		return getPackageName(c != null ? c.getName() : null);
	}


	/**
	 * @param className a class name
	 * @return the package part of the given class name (without a trailing dot).
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
	 * @param className a class name
	 * @return the className without the package name
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
	 * @param object an object
	 * @param c a class
	 * @param <T> the class type
	 * @return tests if the object is an instance of the given class.
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
	 * @param loader optional, a ClassLoader
	 * @param <T> the class type
	 * @return the new object
	 * @throws ClassNotFoundException if the class was not found 
	 * @throws IllegalArgumentException if the class is not derived from the given superClass. 
	 * @throws InstantiationException if the instance could not be created 
	 * @throws IllegalAccessException if the instance could not be created 
	 */
	@SuppressWarnings("deprecation")
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
	 * @param loader optional, a ClassLoader
	 * @param <T> the class type
	 * @return the class
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
	 * @param loader optional, a ClassLoader
	 * @param <T> the class type
	 * @return the class
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
	 * @param annotation an annotation
	 * @param c a class
	 * @param <A> the annotation type
	 * @return is an instance?
	 */
	public static <A extends Annotation> boolean isA(Annotation annotation, Class<A> c)
	{
		return (annotation != null) && (c != null) && c.isAssignableFrom(annotation.annotationType());
	}
	

	/**
	 * Returns the first annotation in a list of annotations which is an instance of the
	 * given annotation class.
	 * @param annotations annotations
	 * @param c a class
	 * @param <A> the annotation type
	 * @return the annotation or null
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
	 * @param object an object
	 * @param targetClass the target class	
	 * @param <T> the class type
	 * @return the casted object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unwrap(Object object, Class<T> targetClass)
	{
		return isA(object, targetClass) ? (T)object : null;
	}
	
	
	/**
	 * Translates a Enum name into an Enum object.
	 * If the Enum name is null or not defined, then the default value is returned. This helps 
	 * to avoid the IllegalArgumentException thrown by Enum.valueOf
	 * @param enumClass the enum class
	 * @param name the name of an enum entry
	 * @param defaultValue a default value 
	 * @param <T> the enum type
	 * @return the enum
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
}
