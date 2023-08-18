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
package org.civilian.controller.method.arg.misc;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.civilian.controller.method.arg.MethodArg;
import org.civilian.request.Request;
import org.civilian.response.Response;


public class BeanParamArg extends MethodArg
{
	public BeanParamArg(Class<?> beanClass, Setter... setters) throws Exception
	{
		beanClass_ 	= beanClass;
		setters_ 	= setters;
	}
	
	
	@Override public Object getValue(Request request, Response response) throws Exception
	{
		@SuppressWarnings("deprecation")
		Object bean = beanClass_.newInstance();
		
		for (Setter setter : setters_)
			setter.injectParam(request, response, bean);
			
		return bean;
	}
	

	public static abstract class Setter
	{
		public Setter(MethodArg arg)
		{
			arg_ = arg;
		}
		
		
		public void injectParam(Request request, Response response, Object bean) throws Exception
		{
			Object value = arg_.getValue(request, response);
			try
			{
				inject(value, bean);
			}
			catch(Exception e)
			{
				throw new IllegalArgumentException("error when injecting value " + value + " from " + arg_ + " into " + this, e);
			}
		}

		
		public abstract void inject(Object value, Object bean) throws Exception;
		
		
		@Override public abstract String toString();
		

		private final MethodArg arg_;
	}
	

	public static class MethodSetter extends Setter
	{
		public MethodSetter(MethodArg arg, Method method)
		{
			super(arg);
			method_ = method;
		}
		

		@Override public void inject(Object value, Object bean) throws Exception
		{
			method_.invoke(bean, value);
		}
		
		
		@Override public String toString()
		{
			return method_.toString();
		}
		
		
		private final Method method_;
	}
	
	
	public static class FieldSetter extends Setter
	{
		public FieldSetter(MethodArg arg, Field field)
		{
			super(arg);
			field_ = field;
			if (!Modifier.isPublic(field_.getModifiers()))
				field_.setAccessible(true);
		}
		

		@Override public void inject(Object value, Object bean) throws Exception
		{
			field_.set(bean, value);
		}
		
		
		@Override public String toString()
		{
			return field_.toString();
		}
		
		
		private final Field field_;
	}
	
	
	private final Class<?> beanClass_;
	private final Setter[] setters_;
}
