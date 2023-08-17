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
package org.civilian.controller.method.arg.factory;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.servlet.http.Cookie;
import org.civilian.annotation.BeanParam;
import org.civilian.annotation.CookieParam;
import org.civilian.annotation.DefaultValue;
import org.civilian.annotation.HeaderParam;
import org.civilian.annotation.LocaleValue;
import org.civilian.annotation.MatrixParam;
import org.civilian.annotation.Parameter;
import org.civilian.annotation.RequestContent;
import org.civilian.controller.method.arg.MethodArg;
import org.civilian.controller.method.arg.MethodArgProvider;
import org.civilian.controller.method.arg.StringMethodArg;
import org.civilian.controller.method.arg.conv.ConvertingArg;
import org.civilian.controller.method.arg.misc.BeanParamArg;
import org.civilian.controller.method.arg.misc.RequestArg;
import org.civilian.controller.method.arg.misc.ResponseArg;
import org.civilian.controller.method.arg.misc.ResponseContentArg;
import org.civilian.controller.method.arg.reqcontent.ReqContentArgs;
import org.civilian.controller.method.arg.reqparam.CookieParamObjectArg;
import org.civilian.controller.method.arg.reqparam.CookieParamValueArg;
import org.civilian.controller.method.arg.reqparam.HeaderParamValueArg;
import org.civilian.controller.method.arg.reqparam.MatrixParamValueArg;
import org.civilian.controller.method.arg.reqparam.ParameterValueArg;
import org.civilian.controller.method.arg.reqparam.PathParamArg;
import org.civilian.request.Request;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.response.Response;
import org.civilian.type.TypeLib;
import org.civilian.util.ClassUtil;
import org.civilian.util.PathScanner;


/**
 * MethodArgFactory is a factory for MethodArgs.
 */
public class MethodArgFactory
{
	/**
	 * Creates a new MethodArgFactory object. 
	 * @param pathParams the PathParamMap of the application
	 * @param typeLib the TypeLib of the application
	 */
	public MethodArgFactory(PathParamMap pathParams, TypeLib typeLib)
	{
		pathParams_	= pathParams;
		typeLib_	= typeLib;
	}
	
	
	/**
	 * Creates MethodArg objects for the parameters of a controller action method.
	 * For each method parameter the parameter annotations and parameter type are 
	 * evaluated to generate the MethodArg. 
	 * @param method the action method of a controller
	 * @return an array with MethodArg for the method or null, if the method
	 * 		does not have parameters 
	 * @throws IllegalArgumentException if creation of MethodArg fails 
	 */
	public MethodArg[] createParamArgs(Method method) throws IllegalArgumentException
	{
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes.length == 0)
			return null;
		
		Type[] genericParamTypes 	= method.getGenericParameterTypes();
		Annotation[][] paramAnnos 	= method.getParameterAnnotations();
		MethodArg[] result 	= new MethodArg[paramTypes.length];
		Info info					= new Info();
	
		for (int i=0; i<paramTypes.length; i++)
		{
			try
			{
				info.type 			= paramTypes[i];
				info.genericType	= genericParamTypes[i];
				info.annotations	= paramAnnos[i];
				result[i] 			= parse(info, null, true);
			}
			catch(Exception e)
			{
				String message = "method '" + 
					method.getDeclaringClass().getName() + '#' + method.getName() + ": " +  
					"param nr " + (i+1) + " (type '" + info.type.getName() + "') : " + e.getMessage();
				throw new IllegalArgumentException(message, e);
			}
		}

		return result;
	}
	

	/**
	 * Creates an MethodArg object for the parameter of a setter method.
	 * The method annotations and parameter type are 
	 * evaluated to generate the MethodArg. The MethodArg
	 * defaults to the parameter with the name of the bean property.
	 * @param method a setter method
	 * @param beanProperty the name of beam property which is set by the method.  
	 */
	public MethodArg createSetterMethodArg(Method method, String beanProperty, boolean mustExist)
	{
		Class<?>[] paramTypes = method.getParameterTypes();
		if (paramTypes.length != 1)
			return null;
		
		Info info = new Info(paramTypes[0], method.getGenericParameterTypes()[0], method.getAnnotations());
		try
		{
			return parse(info, beanProperty, mustExist);
		}
		catch(Exception e)
		{
			String message = "method '" + 
				method.getDeclaringClass().getName() + '#' + method.getName() +  
				": parameter type '" + info.type.getName() + "' : " + e.getMessage();
			throw new IllegalArgumentException(message, e);
		}
	}

	
	/**
	 * Creates an MethodArg object for a field.
	 * The field annotations and type are 
	 * evaluated to generate the MethodArg. 
	 * The MethodArg defaults to the request parameter with the field name.
	 */
	public MethodArg createFieldArg(Field field, boolean mustExist)
	{
		Info info = new Info(field.getType(), field.getGenericType(), field.getAnnotations());
		try
		{
			return parse(info, field.getName(), mustExist);
		}
		catch(Exception e)
		{
			String message = "field '" + 
				field.getDeclaringClass().getName() + '#' + field.getName() +  
				": type '" + field.getName() + "' : " + e.getMessage();
			throw new IllegalArgumentException(message, e);
		}
	}

	
	private MethodArg parse(Info info, String parameterName, boolean mustExist) throws Exception
	{
		if (info.type == Response.class)
			return new ResponseArg();
		
		if (info.type == Request.class)
			return new RequestArg();

		if (info.findAnnotation(RequestContent.class) != null)
			return ReqContentArgs.create(info.type, info.genericType);
		
		if (org.civilian.response.ResponseContent.class.isAssignableFrom(info.type) ||
			(info.findAnnotation(org.civilian.annotation.ResponseContent.class) != null))
			return new ResponseContentArg(info.type);
		
		MethodArg arg = parsePathParamArg(info);
		if (arg != null)
			return arg;
		
		arg = parseRequestParamArgument(info);
		if (arg != null)
			return arg;
		
		arg = parseBeanParamArgument(info);
		if (arg != null)
			return arg;
		
		arg = parseCustomArgument(info);
		if (arg != null)
			return arg;
		
		if (parameterName != null)
			return parseRequestParamArg(info, new ParameterValueArg(parameterName), true);
		
		if (!mustExist)
			return null;

		throw new IllegalArgumentException("parameter has no recognized annotation and is not an injectable context variable");
	}
	
	
	private MethodArg parseCustomArgument(Info info) throws Exception
	{
		for (Annotation a : info.annotations)
		{
			org.civilian.annotation.MethodArgProvider caf = a.annotationType().getAnnotation(org.civilian.annotation.MethodArgProvider.class);
			if (caf != null)
			{
				MethodArgProvider f = ClassUtil.createObject(caf.value(), MethodArgProvider.class, null);
				return f.create(a, info.type, info.genericType, info.annotations);
			}
		}
		return null;
	}
	
	
	private <T> MethodArg parsePathParamArg(Info info)
	{
		org.civilian.annotation.PathParam ppAnno = info.findAnnotation(org.civilian.annotation.PathParam.class);
		if (ppAnno == null)
			return null;
		
		@SuppressWarnings("unchecked")
		PathParam<T> pathParam = (PathParam<T>)pathParams_.get(ppAnno.value());
		if (pathParam == null)
			throw new IllegalArgumentException("references unknown path parameter '" + ppAnno.value() + "'");
		
		Class<?> pt = !info.type.isPrimitive() ? info.type : TypeLib.getObjectClassForPrimitiveType(info.type);
		if (!pt.isAssignableFrom(pathParam.getType()))
		{
			throw new IllegalArgumentException("wrong type for PathParam '" + 
				pathParam.getName() + 
				"' (expected '" + pathParam.getType().getName() + "')");
		}
		
		return new PathParamArg<>(pathParam, getDefaultValue(info, pathParam));
	}
	
	
	private<T> MethodArg parseBeanParamArgument(Info info) throws Exception
	{
		return info.findAnnotation(BeanParam.class) != null ? new BeanParamArg(this, info.type) : null;
	}
	
	
	/**
	 * Returns a RequestParamArg if the method parameter is annotated
	 * with a @Parameter, @MatrixParam, @HeaderParam annotation.  
	 */
	private<T> MethodArg parseRequestParamArgument(Info info) throws Exception
	{
		Parameter qp = info.findAnnotation(Parameter.class);
		if (qp != null)
			return parseRequestParamArg(info, new ParameterValueArg(qp.value()), false);
		
		HeaderParam hp = info.findAnnotation(HeaderParam.class);
		if (hp != null)
			return parseRequestParamArg(info, new HeaderParamValueArg(hp.value()), false);

		MatrixParam mp = info.findAnnotation(MatrixParam.class);
		if (mp != null)
			return parseRequestParamArg(info, new MatrixParamValueArg(mp.value()), false);
		
		CookieParam cp = info.findAnnotation(CookieParam.class);
		if (cp != null)
		{
			if (info.genericType == Cookie.class)
				return new CookieParamObjectArg(cp.value());
			else
				return new CookieParamValueArg(cp.value());
		}
		
		return null;
	}

	
	private<T> MethodArg parseRequestParamArg(Info info, StringMethodArg arg, boolean ignoreUnsupportedTypes) throws Exception
	{
		DefaultValue dv = info.findAnnotation(DefaultValue.class);
		String defaultValue = dv != null ? dv.value() : null;
		
		LocaleValue lv  = info.findAnnotation(LocaleValue.class);
		
		return ConvertingArg.create(arg, defaultValue, lv != null, typeLib_, info.type, info.genericType);
	}
	
	
	private <T> T getDefaultValue(Info info, PathParam<T> pathParam)
	{
		DefaultValue dv = info.findAnnotation(DefaultValue.class);
		if (dv == null)
			return null;
		else
		{
			PathScanner scanner = new PathScanner(dv.value());
			T value = pathParam.parse(scanner);
			if (value == null)
				throw new IllegalArgumentException("default parameter value '" + dv.value() + "' is not a valid path parameter of " + pathParam);
			return value;
		}
	}

	
	private static class Info
	{
		public Info()
		{
		}
		

		public Info(Class<?> type, java.lang.reflect.Type genericType, Annotation[] annotations)
		{
			this.type = type;
			this.genericType = genericType;
			this.annotations = annotations;
		}
		
		
		public <A extends Annotation> A findAnnotation(Class<A> c)
		{
			return ClassUtil.findAnnotation(this.annotations, c);
		}
		
		
		public Class<?> type;
		public java.lang.reflect.Type genericType;
		public Annotation[] annotations;
	}
	

	private final PathParamMap pathParams_;
	private final TypeLib typeLib_;
}
