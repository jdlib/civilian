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
import java.util.HashMap;
import java.util.Iterator;
import org.civilian.content.ContentNegotiation;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.controller.method.ControllerMethod;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.Iterators;


/**
 * ControllerType provides meta-information about a controller class.
 * It provides access to all it's {@link ControllerMethod controller methods}.
 */
public class ControllerType implements Iterable<ControllerMethod>
{
	/**
	 * Creates a new ControllerType.
	 * @param controllerClass the class of the Controller
	 * @param methods the ControllerMethods. The order of the method is obeyed during
	 * 		content-negotiation.
	 */
	ControllerType(Class<? extends Controller> controllerClass, 
		ControllerFactory factory,
		ControllerMethod... methods)
	{
		controllerClass_	= Check.notNull(controllerClass, "controllerClass");
		methods_ 			= Check.notNull(methods, "methods"); // we accept empty arrays, even if the controller is then useless
		factory_			= factory;
		
		// build the map request-method -> controller-method[] 
		for (ControllerMethod ctrlMethod : methods)
		{
			for (Iterator<String> reqMethods = ctrlMethod.getRequestMethods(); reqMethods.hasNext(); )
				addMethod(ctrlMethod, reqMethods.next());
		}
	}
	
	
	private void addMethod(ControllerMethod ctrlMethod, String requestMethod)
	{
		ControllerMethod[] ctrlMethods = reqMethod2ctrlMethod_.get(requestMethod);
		ctrlMethods = ctrlMethods == null ? 
			new ControllerMethod[] { ctrlMethod } : 
			ArrayUtil.addLast(ctrlMethods, ctrlMethod);
		reqMethod2ctrlMethod_.put(requestMethod, ctrlMethods);
	}
	
	
	/**
	 * @return the ControllerClass of the controller.
	 */
	public Class<? extends Controller> getControllerClass()
	{
		return controllerClass_;
	}
	
	
	// Supports unit tests
	boolean contains(ControllerMethod method)
	{
		for (ControllerMethod m : methods_)
		{
			if (m == method)
				return true;
		}
		return false;
	}

	
	/**
	 * @return creates a Controller and initializes it's type.
	 * @see Controller#setControllerType(ControllerType)
	 */
	public Controller createController()
	{
		try
		{
			@SuppressWarnings("deprecation")
			Controller controller = factory_ != null ? 
				factory_.createController(controllerClass_) :
				controllerClass_.newInstance();
			controller.setControllerType(this);
			return controller;
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("could not instantiate controller " + getControllerClass().getName(), e);
		}
	}
		
	
	/**
	 * @return an iterator for all controller methods.
	 */
	@Override public Iterator<ControllerMethod> iterator()
	{
		return Iterators.forValues(methods_);		
	}

	
	/**
	 * @return the number of ControllerMethods.
	 */
	public int getMethodCount()
	{
		return methods_.length;
	}
	
	
	/**
	 * @param i the method index
	 * @return the i-th ControllerMethod.
	 */
	public ControllerMethod getMethod(int i)
	{
		return methods_[i];
	}
	
	
	/**
	 * Returns a ControllerMethod for a request.
	 * @param request a request
	 * @return a NegotiatedMethod object containing the method + selected response content-type
	 * 		or an error code
	 */
	public NegotiatedMethod getMethod(Request request)
	{
		ControllerMethod[] methods = reqMethod2ctrlMethod_.get(request.getMethod());
		return methods == null ?
			new NegotiatedMethod(Response.Status.SC405_METHOD_NOT_ALLOWED) :
			negotiate(methods, 
				request.getContentType(),
				request.getAcceptedContentTypes());
	}
	
	
	/**
	 * Selects a ControllerMethod which matches the request method, request content type
	 * and accepted response content-types.
	 * @param requestMethod the request method
	 * @param requestContentType the request content type
	 * @param acceptedResponseTypes the accepted response content types
	 * @return a NegotiatedMethod object containing the method + selected response content-type
	 * 		or an error code
	 */
	public NegotiatedMethod getMethod(String requestMethod, 
		ContentType requestContentType, 
		ContentTypeList acceptedResponseTypes)
	{
		ControllerMethod[] methods = reqMethod2ctrlMethod_.get(requestMethod);
		if (methods == null)
			return new NegotiatedMethod(Response.Status.SC405_METHOD_NOT_ALLOWED);
		else
			return negotiate(methods, requestContentType, acceptedResponseTypes);
	}	
	
	
	private NegotiatedMethod negotiate(ControllerMethod[] methods, 
		ContentType requestContentType, 
		ContentTypeList acceptedResponseTypes)
	{
		ControllerMethod bestMatch  = null;
		ContentNegotiation conneg 	= new ContentNegotiation(acceptedResponseTypes);
		boolean canConsume			= false;
		
		for (ControllerMethod m : methods)
		{
			if (m.canConsume(requestContentType))
			{
				canConsume = true; 
				if (m.canProduce(conneg))
					bestMatch = m;
			}
		}
		return bestMatch != null ?
			new NegotiatedMethod(bestMatch, conneg.bestType) :
			new NegotiatedMethod(canConsume ?  Response.Status.SC406_NOT_ACCEPTABLE : Response.Status.SC415_UNSUPPORTED_MEDIA_TYPE);
	}	

	
	/**
	 * @param method the method
	 * @return the controller method for the given Java method.
	 */
	public ControllerMethod getMethod(Method method)
	{
		for (ControllerMethod m : methods_)
		{
			if (m.getJavaMethod() == method)
				return m;
		}
		return null;
	}
	

	/**
	 * @return the controller method with the given Java method name.
	 * @param javaName the Java method name
	 */
	public ControllerMethod getMethod(String javaName)
	{
		return getMethod(javaName, (Class<?>[])null);
	}
	
	
	/**
	 * Returns the controller method with the given Java method name and argument types.
	 * @param javaName the Java method name
	 * @param paramTypes the method parameter types. Pass an explicit null if you
	 * 		want to compare the method name only.
	 * @return the method
	 */
	public ControllerMethod getMethod(String javaName, Class<?>... paramTypes)
	{
		for (ControllerMethod m : methods_)
		{
			Method method = m.getJavaMethod();
			if (method.getName().equals(javaName) && ((paramTypes == null) || matchesArgTypes(method, paramTypes)))
				return m;
		}
		return null;
	}


	private boolean matchesArgTypes(Method method, Class<?>[] paramTypes)
	{
		Class<?>[] actualTypes = method.getParameterTypes();
		if (actualTypes.length != paramTypes.length)
			return false;
		for (int i=0; i<actualTypes.length; i++)
		{
			if (!actualTypes[i].equals(paramTypes[i]))
				return false;
		}
		return true;
	}


	/**
	 * Returns a debug string. 
	 */
	@Override public String toString()
	{
		return "Type:" + getControllerClass().getName();
	}
	
	
	private final Class<? extends Controller> controllerClass_;
	private final HashMap<String, ControllerMethod[]> reqMethod2ctrlMethod_ = new HashMap<>();
	private final ControllerMethod[] methods_;
	private final ControllerFactory factory_;
}