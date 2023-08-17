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
package org.civilian.controller.method;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.civilian.annotation.Consumes;
import org.civilian.annotation.Delete;
import org.civilian.annotation.Get;
import org.civilian.annotation.Head;
import org.civilian.annotation.Options;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.annotation.Put;
import org.civilian.annotation.RequestMethod;
import org.civilian.content.ContentNegotiation;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.controller.MethodArgFactory;
import org.civilian.controller.method.arg.MethodArg;
import org.civilian.internal.controller.MethodAnnotations;
import org.civilian.request.Request;
import org.civilian.util.Iterators;
import org.civilian.util.StringUtil;


/**
 * ControllerMethod represents a Java method in a controller class which can
 * be called to produce a response to a resource request. These methods
 * are also called action methods 
 * A controller method qualifies as action method:
 * <ul>
 * <li>The method is public and not static
 * <li>Its return type is void
 * <li>It either has a {@link Get @Get}, {@link Post @Post}, {@link Put @Put}, {@link Delete @Delete}, 
 * 	   {@link Head @Head}, {@link Options @Options} or {@link RequestMethod @RequestMethod} annotation
 * 	   or it overrides an inherited action method.
 * </ul>
 * A controller can publish multiple action methods. Content negotiation is used
 * to select the method which finally handles the request.   
 */
public class ControllerMethod 
{
	/**
	 * Creates a ControllerMethod.
	 * @param argFactory an MethodArgFactory instance which can help to create standard MethodArgs.
	 * @param javaMethod a method of a controller class
	 * @return the ControllerMethod or null, if the method is not a valid action method. 
	 */
	public static ControllerMethod create(MethodArgFactory argFactory, Method javaMethod)
	{
		MethodAnnotations ma = MethodAnnotations.of(javaMethod);
		return ma != null ? new ControllerMethod(javaMethod, argFactory.createParamArgs(javaMethod), ma) : null;
	}
	
	
	private ControllerMethod(Method javaMethod, MethodArg[] args, MethodAnnotations annotations)
	{
		javaMethod_ 	= javaMethod;
		args_			= args;
		requestMethods_	= annotations.getRequestMethods();
		produces_		= annotations.getProduces();
		consumes_		= annotations.getConsumes();
		javaMethod_.setAccessible(true);
	}

	
	/**
	 * Returns name of the Java method represented by this action.
	 */
	public String getName()
	{
		return javaMethod_.getName();
	}

	
	/**
	 * Returns the Java method represented by this action.
	 */
	public Method getJavaMethod()
	{
		return javaMethod_;
	}

	
	/**
	 * Returns the controller class which declared the method.
	 */
	public Class<?> getDeclaringClass()
	{
		return javaMethod_.getDeclaringClass();
	}

	
	/**
	 * Returns an iterator for all supported HTTP request methods.
	 * @see Request#getMethod()
	 */
	public Iterator<String> getRequestMethods()
	{
		return Iterators.forValues(requestMethods_);
	}
	
	
	/**
	 * Returns if the ControllerMethod can be inherited by the given derived controller class.
	 * For this the derived controller class must not override the method. 
	 */
	public boolean canInherit(Class<?> controllerClass)
	{
		if ((controllerClass != null) &&
			javaMethod_.getDeclaringClass().isAssignableFrom(controllerClass))
		{
			// inherit annotation was set: now test if the 
			// derived resourceClass does not shadow our method
			Method m;
			try
			{
				m = controllerClass.getMethod(javaMethod_.getName(), javaMethod_.getParameterTypes());
				if (m.getDeclaringClass() != controllerClass)
					return true;
			}
			catch (Exception e)
			{
			}
		}
		return false;
	}

	
	/**
	 * Returns if this ControllerMethod can handle requests with
	 * the given content type. Returns true if either no {@link Consumes} annotation
	 * was set on the method or the {@link Consumes} annotation
	 * matches that content type.
	 */
	public boolean canConsume(ContentType contentType)
	{
		if (consumes_ == null)
		{
			// if we don't have a @Consumes definition we accept all
			return true;
		}
		else
			return consumes_.matchesSome(contentType != null ? contentType : ContentType.ANY);
	}

	
	/**
	 * Returns a ContentTypeList for all content types defined by a {@link Consumes} annotation
	 * on the action method. The list is empty if no {@link Consumes} annotation was set.
	 */
	public ContentTypeList getConsumesContentTypes()
	{
		return consumes_ != null ? consumes_ : ContentTypeList.EMPTY;
	}	

	
	/**
	 * Returns if this action can produce a ContentType which is more than suitable
	 * than the previously best type found by the ContentNegotiation.
	 */
	public boolean canProduce(ContentNegotiation conneg)
	{
		return produces_ == null ? conneg.evaluate(ContentType.ANY) : conneg.evaluate(produces_);
	}	
	
	
	/**
	 * Returns a ContentTypeList for all content types defined by a {@link Produces} annotation
	 * on the action method. The list is empty if no {@link Produces} annotation was set.
	 */
	public ContentTypeList getProducedContentTypes()
	{
		return produces_ != null ? produces_ : ContentTypeList.EMPTY;
	}	


	/**
	 * Returns the number of arguments injected into the method.
	 */
	public int getArgCount()
	{
		return args_ == null ? 0 : args_.length;
	}
	
	
	public MethodArg getArgument(int i)
	{
		return args_[i];
	}

	
	/**
	 * Invokes the action method on the controller.
	 */
	public void invoke(Object controller, Request request) throws Exception
	{
		Object[] argValues = null;
		if (args_ != null)
			argValues = buildArgValues(request);
		
		try
		{
			javaMethod_.invoke(controller, argValues);
		}
		catch(InvocationTargetException e)
		{
			if (e.getCause() instanceof Error)
				throw (Error)e.getCause();
			if (e.getCause() instanceof Exception)
				throw (Exception)e.getCause();
			else
				throw e;
		}
		
		if (argValues != null)
		{
			for (int i=0; i<argValues.length; i++)
				args_[i].postProcess(request, argValues[i]);
		}
	}
	
	
	private Object[] buildArgValues(Request request) throws Exception
	{
		Object[] argValues = new Object[args_.length];
		for (int i=0; i<argValues.length; i++)
			argValues[i] = args_[i].getValue(request);
		return argValues;
	}

	
	/**
	 * Returns an information string of the Action for debug purposes.
	 */
	public String getInfo()
	{
		StringBuilder s = new StringBuilder();
		for (String rm : requestMethods_)
		{
			s.append('@');
			s.append(StringUtil.startUpperCase(rm.toLowerCase()));
		}
		getInfo(consumes_, "Consumes", s);
		getInfo(produces_, "Produces", s);
		return s.toString();
	}
	

	private void getInfo(ContentTypeList contentTypes, String what, StringBuilder s)
	{
		if (contentTypes != null)
		{
			s.append(" @");
			s.append(what);
			s.append('(');
			for (int i=0; i<contentTypes.size(); i++)
			{
				if (i > 0)
					s.append(", ");
				s.append(contentTypes.get(i).getValue());
			}
			s.append(')');
		}
	}
	
	
	/**
	 * Returns the name of the java method.
	 */
	@Override public String toString()
	{
		return getName();
	}

	
	private Method javaMethod_;
	private String[] requestMethods_;
	private ContentTypeList produces_;
	private ContentTypeList consumes_;
	private MethodArg[] args_;
}
