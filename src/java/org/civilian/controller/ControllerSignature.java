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
import org.civilian.annotation.PathParam;
import org.civilian.annotation.Segment;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * ControllerSignature represents the name of Controller class and filter for its action methods:
 * <ul>
 * <li>match all action methods with no {@link Segment} and no {@link PathParam} annotation
 * <li>match all action methods which have {@link Segment} annotation with a certain value 
 * <li>match all action methods which have {@link PathParam} annotation with a certain value 
 * ControllerSignature are used by {@link Resource#getControllerSignature() resources}
 * to specify which controller and which of its action methods are used to handle requests for the resource.
 */
public class ControllerSignature
{
	private static final char SEPARATOR   		= ':';
	private static final String PARAMPREFIX   	= "$";
	
	
	public static ControllerSignature parse(String signature)
	{
		if (StringUtil.isBlank(signature))
			return null;
		
		String className 		= signature;
		String methodSegment 	= null;
		String methodPathParam 	= null;
		
		int p = signature.indexOf(SEPARATOR);
		if (p >= 0)
		{
			className 	= signature.substring(0, p);
			String p2 	= signature.substring(p + 1);
			if (p2.startsWith(PARAMPREFIX))
				methodPathParam = p2.substring(PARAMPREFIX.length());
			else
				methodSegment = p2;
		}
		return new ControllerSignature(className, methodSegment, methodPathParam);
	}

	
	public ControllerSignature(String className)
	{
		this(className, null, null);
	}
	
	
	public ControllerSignature(Class<?> cls)
	{
		this(Check.notNull(cls, "cls").getName());
	}

	
	private ControllerSignature(String className, String methodSegment, String methodPathParam)
	{
		className_  		= Check.notEmpty(className, "className");
		methodSegment_ 		= methodSegment != null ? Check.notEmpty(methodSegment, "methodName") : null; 
		methodPathParam_	= methodPathParam != null ? Check.notEmpty(methodPathParam, "methodPathParam") : null;	
	}
	
	
	public ControllerSignature withMethodSegment(String methodSegment)
	{
		return new ControllerSignature(className_, methodSegment, null);
	}

	
	public ControllerSignature withMethodPathParam(String methodPathParam)
	{
		return new ControllerSignature(className_, null, methodPathParam);
	}

	
	/**
	 * Returns the class name.
	 */
	public String getClassName()
	{
		return className_;
	}


	/**
	 * Returns the method segment or null.
	 */
	public String getMethodSegment()
	{
		return methodSegment_;
	}
	

	/**
	 * Returns the method path param or null.
	 */
	public String getMethodPathParam()
	{
		return methodPathParam_;
	}

	
	public boolean matchJavaMethod(Method javaMethod)
	{
		Segment segmentAnno = javaMethod.getAnnotation(Segment.class);
		return segmentAnno == null ? methodSegment_ == null : segmentAnno.value().equals(methodSegment_);
	}
		
		
	@Override public int hashCode()
	{
		int hashCode = className_.hashCode();
		if (methodSegment_ != null)
			hashCode ^= methodSegment_.hashCode();
		else if (methodPathParam_ != null)
			hashCode ^= methodPathParam_.hashCode();
		return hashCode;
	}
	

	@Override public boolean equals(Object other)
	{
		if (other instanceof ControllerSignature)
		{
			ControllerSignature o = (ControllerSignature)other;
			return className_.equals(o.className_) && Objects.equals(methodSegment_, o.methodSegment_) && Objects.equals(methodPathParam_, o.methodPathParam_);
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
		if (methodSegment_ != null)
			s += SEPARATOR + methodSegment_;
		else if (methodPathParam_ != null)
			s += SEPARATOR + PARAMPREFIX + methodPathParam_;
		return s;
	}

	
	private final String className_;
	private final String methodSegment_;
	private final String methodPathParam_;
}
