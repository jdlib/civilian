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
package org.civilian.controller;


import java.lang.reflect.Method;
import java.util.Objects;
import org.civilian.Resource;
import org.civilian.annotation.Segment;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * ControllerSignature represents the name of Controller class and an 
 * optional name filter for its action methods.
 * ControllerSignature are used by {@link Resource#getControllerSignature() resources}
 * to specify which controller is used to handle requests for the resource.
 */
public class ControllerSignature
{
	public static final char SEPARATOR = ':';
	
	
	public static ControllerSignature parse(String signature)
	{
		if (StringUtil.isBlank(signature))
			return null;
		
		String className = signature;
		String methodName = null;
		
		int p = signature.indexOf(SEPARATOR);
		if (p >= 0)
		{
			className 	= signature.substring(0, p);
			methodName 	= signature.substring(p + 1);
		}
		return new ControllerSignature(className, methodName);
	}

	
	public ControllerSignature(String className)
	{
		this(className, null);
	}
	
	
	public ControllerSignature(String className, String methodName)
	{
		className_  = Check.notEmpty(className, "className");
		methodName_ = methodName != null ? Check.notEmpty(methodName, "methodName") : null; 
	}

	
	/**
	 * Builds a signature out of class name and method filter.
	 */
	public static String build(String className, String filter)
	{
		if (className == null)
			return null;
		else if (filter == null)
			return className;
		else
			return className + SEPARATOR + filter;
	}


	/**
	 * Returns the class name.
	 */
	public String getClassName()
	{
		return className_;
	}


	/**
	 * Returns the method name or null.
	 */
	public String getMethodName()
	{
		return methodName_;
	}
	

	public boolean matchJavaMethod(Method javaMethod)
	{
		Segment segmentAnno = javaMethod.getAnnotation(Segment.class);
		return segmentAnno == null ? methodName_ == null : segmentAnno.value().equals(methodName_);
	}
		
		
	@Override public int hashCode()
	{
		return methodName_ != null ? Objects.hash(className_, methodName_) : className_.hashCode();
	}
	

	@Override public boolean equals(Object other)
	{
		if (other instanceof ControllerSignature)
		{
			ControllerSignature o = (ControllerSignature)other;
			return className_.equals(o.className_) && Objects.equals(methodName_, o.methodName_);
		}
		else
			return false;
	}

	
	
	/**
	 * Builds a signature out of class name and method filter.
	 */
	@Override public String toString()
	{
		String s = className_;
		if (methodName_ != null)
			s += SEPARATOR + methodName_;
		return s;
	}

	
	private final String className_;
	private final String methodName_;
}
