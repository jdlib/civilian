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
import java.util.List;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.Segment;
import org.civilian.annotation.Produces;
import org.civilian.annotation.RequestMethod;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.util.ClassUtil;


/**
 * Helper class to extract annotated information from a controller action method
 * (or any of its overriden ancestor methods).
 */
public class MethodAnnotations
{
	/**
	 * Returns a MethodAnnotations object for a controller action method, 
	 * if the method is an action method, i.e. if
	 * <ul>
	 * <li>the method is public and not static and has return type void
	 * <li>at least one {@link RequestMethod} annotation is present on the method itself or one of its overriden ancestors
	 * </ul>
	 * @return a MethodAnnotations object or null
	 */
	public static MethodAnnotations of(Method javaMethod)
	{
		int mod = javaMethod.getModifiers();
		if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && (javaMethod.getReturnType() == void.class))
		{
			AnnotationLookup lookup = new AnnotationLookup(javaMethod); 
			List<String> reqMethods = lookup.addRequestMethods(null);
			if (reqMethods != null)
				return new MethodAnnotations(lookup, reqMethods);
		}
		return null;
	}
	
	
	/**
	 * Returns the value of {@link Segment} annotation of a controller action method
	 * or null if the method has no path annotation or is not a action method.
	 */
	public static String getPath(Method javaMethod)
	{
		Segment pathAnno = javaMethod.getAnnotation(Segment.class);
		if (pathAnno != null)
		{
			String path = pathAnno.value();
			// ignore path's with length 0
			if ((path.length() > 0) && (of(javaMethod) != null)) 
				return path;
		}
		return null;
	}

	
	private MethodAnnotations(AnnotationLookup lookup, List<String> reqMethods)
	{
		lookup_ 	= lookup; 
		reqMethods_	= reqMethods.toArray(new String[reqMethods.size()]);
	}

	
	public String[] getRequestMethods()
	{
		return reqMethods_;
	}

	
	public ContentTypeList getProduces()
	{
		ContentTypeList produces = null;
		Produces annotation = lookup_.getAnnotation(Produces.class);
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
		Consumes annotation = lookup_.getAnnotation(Consumes.class);
		if (annotation != null)
			consumes = extractContentTypes(null, annotation.value());
		return consumes;
	}


	private ContentTypeList extractContentTypes(Comparator<ContentType> comparator, String[] p)
	{
		ContentTypeList list = ContentTypeList.parse(comparator, p);
		return list.size() == 0 ? null : list;
	}

	
	/**
	 * AnnotationLookup can return annotations declared on a Java method
	 * or on any of its overriden method ancestors. 
	 */
	private static class AnnotationLookup
	{
		public AnnotationLookup(Method javaMethod)
		{
			this(javaMethod, javaMethod.getParameterTypes());
		}


		private AnnotationLookup(Method javaMethod, Class<?>[] paramTypes)
		{
			javaMethod_ = javaMethod;

			Method superMethod = findOverwrittenMethod(javaMethod.getName(), paramTypes, javaMethod.getDeclaringClass().getSuperclass());
			next_ = superMethod != null ? new AnnotationLookup(superMethod, paramTypes) : null;
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
		

	    private <T extends Annotation> T getAnnotation(Class<T> annotationClass) 
	    {
	    	T annotation = javaMethod_.getAnnotation(annotationClass);
			if ((annotation == null) && (next_ != null))
				annotation = next_.getAnnotation(annotationClass);
			return annotation;
	    }
	    
		
		private List<String> addRequestMethods(List<String> list)
		{
			for (Annotation annotation : javaMethod_.getAnnotations())
				list = addRequestMethods(annotation, list);
			if ((list == null) && (next_ != null))
				list = next_.addRequestMethods(null);
			return list;
		}
		
		
		private List<String> addRequestMethods(Annotation annotation, List<String> list)
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
			

		private List<String> addRequestMethods(List<String> list, String... methods)
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
		private final AnnotationLookup next_;
	}
	
	
	private AnnotationLookup lookup_;
	private String[] reqMethods_;
}
