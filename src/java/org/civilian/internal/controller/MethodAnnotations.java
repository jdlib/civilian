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
package org.civilian.internal.controller;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.Path;
import org.civilian.annotation.Produces;
import org.civilian.annotation.RequestMethod;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.util.ClassUtil;


/**
 * Helper class to extract annotated information from a controller action method. 
 */
public class MethodAnnotations
{
	/**
	 * Returns a method annotations object for a controller action method, or
	 * null if the method is not a action method.
	 */
	public static MethodAnnotations create(Method javaMethod)
	{
		int mod = javaMethod.getModifiers();
		if (Modifier.isPublic(mod) &&
			!Modifier.isStatic(mod) && 
			(javaMethod.getReturnType() == void.class))
		{
			MethodAnnotations ma = new MethodAnnotations(javaMethod); 
			if (ma.isActionMethod())
				return ma;
		}
		return null;
	}
	
	
	/**
	 * Returns the value of {@link Path} annotation of a controller action method
	 * or null if the method has no path annotation or is not a action method.
	 */
	public static String getPath(Method javaMethod)
	{
		Path pathAnno = javaMethod.getAnnotation(Path.class);
		if (pathAnno != null)
		{
			String path = pathAnno.value();
			// ignore path's with length 0
			if ((path.length() > 0) && (create(javaMethod) != null)) 
				return path;
		}
		return null;
	}

	
	public MethodAnnotations(Method javaMethod)
	{
		list_ = new AntnList(javaMethod); 

		ArrayList<String> reqMethods = list_.addRequestMethods(null);
		if (reqMethods != null)
			reqMethods.toArray(requestMethods_ = new String[reqMethods.size()]);
	}

	
	public boolean isActionMethod()
	{
		return requestMethods_ != null;
	}
	
	
	public String[] getRequestMethods()
	{
		return requestMethods_;
	}

	
	public ContentTypeList getProduces()
	{
		ContentTypeList produces = null;
		Produces annotation = list_.getAnnotation(Produces.class);
		if (annotation != null)
		{
			// we sort by specificity to speed up content negotiation
			produces = extractContentTypes(ContentType.Compare.BY_SPECIFICITY, annotation.value());
		}
		return produces;
	}

	
	public ContentTypeList getConsumes()
	{
		ContentTypeList consumes = null;
		Consumes annotation = list_.getAnnotation(Consumes.class);
		if (annotation != null)
			consumes = extractContentTypes(null, annotation.value());
		return consumes;
	}


	private ContentTypeList extractContentTypes(Comparator<ContentType> comparator, String[] p)
	{
		ContentTypeList list = ContentTypeList.parse(comparator, p);
		return list.size() == 0 ? null : list;
	}

	
	private class AntnList
	{
		public AntnList(Method javaMethod)
		{
			this(javaMethod, javaMethod.getParameterTypes());
		}


		private AntnList(Method javaMethod, Class<?>[] paramTypes)
		{
			javaMethod_  = javaMethod;
			annotations_ = javaMethod.getAnnotations();

			Method superMethod = findOverwrittenMethod(javaMethod.getName(), paramTypes, javaMethod.getDeclaringClass().getSuperclass());
			next_ = superMethod != null ? new AntnList(superMethod, paramTypes) : null;
		}
		
		
		private Method findOverwrittenMethod(String methodName, Class<?>[] paramTypes, Class<?> c)
		{
			while(c != null)
			{
				try
				{
					return c.getDeclaredMethod(methodName, paramTypes);
				}
				catch (NoSuchMethodException e)
				{
					c = c.getSuperclass();
				}
			}
			return null;
		}
		

	    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) 
	    {
	    	T annotation = javaMethod_.getAnnotation(annotationClass);
			if ((annotation == null) && (next_ != null))
				annotation = next_.getAnnotation(annotationClass);
			return annotation;
	    }
	    
		
		public ArrayList<String> addRequestMethods(ArrayList<String> list)
		{
			for (Annotation annotation : annotations_)
				list = addRequestMethods(annotation, list);
			if ((list == null) && (next_ != null))
				list = next_.addRequestMethods(null);
			return list;
		}
		
		
		private ArrayList<String> addRequestMethods(Annotation annotation, ArrayList<String> list)
		{
			if (annotation != null)
			{
				if (ClassUtil.isA(annotation, RequestMethod.class))
					list = addRequestMethods(list, ((RequestMethod)annotation).value());
				else
					list = addRequestMethods(annotation.annotationType().getAnnotation(RequestMethod.class), list);
			}
			return list;
		}
			

		private ArrayList<String> addRequestMethods(ArrayList<String> list, String... methods)
		{
			if (list == null)
				list = new ArrayList<>();
			for (String method : methods)
			{
				if (!list.contains(method))
					list.add(method);
			}
			return list;
		}
		
		
		private final Method javaMethod_;
		private final Annotation[] annotations_;
		private final AntnList next_;
	}
	
	
	private AntnList list_;
	private String[] requestMethods_;
}
