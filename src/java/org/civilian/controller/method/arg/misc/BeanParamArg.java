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


import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;

import org.civilian.controller.method.arg.MethodArg;
import org.civilian.controller.method.arg.factory.MethodArgFactory;
import org.civilian.request.Request;


public class BeanParamArg extends MethodArg
{
	public BeanParamArg(MethodArgFactory factory, Class<?> beanClass) throws Exception
	{
		beanClass_ = beanClass;

		Init init = new Init(factory);
		
		// 1. use bean setters
		BeanInfo info = Introspector.getBeanInfo(beanClass);
		for (PropertyDescriptor pd : info.getPropertyDescriptors())
			init.addSetter(pd.getWriteMethod(), pd.getName());
		
		// 2. use public methods 
		for (Method method : beanClass.getMethods())
			init.addSetter(method, null);
		
		// 3. use public fields 
		for (Field field : beanClass.getFields())
			init.addSetter(field);

		setters_ = init.getSetters();
	}
	
	
	@Override public Object getValue(Request request) throws Exception
	{
		@SuppressWarnings("deprecation")
		Object bean = beanClass_.newInstance();
		
		for (Setter setter : setters_)
			setter.injectParam(request, bean);
			
		return bean;
	}
	

	private static abstract class Setter
	{
		public Setter(MethodArg arg)
		{
			arg_ = arg;
		}
		
		
		public void injectParam(Request request, Object bean) throws Exception
		{
			Object value = arg_.getValue(request);
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
	

	private static class MethodSetter extends Setter
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
	
	
	private static class FieldSetter extends Setter
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

	
	private static class Init
	{
		public Init(MethodArgFactory factory)
		{
			factory_ = factory;
		}
		
		
		public Setter[] getSetters()
		{
			return setters_.toArray(new Setter[setters_.size()]);
		}

		
		public void addSetter(Method method, String propertyName)
		{
			if (method != null)
			{
				MethodArg arg = factory_.createSetterMethodArg(method, propertyName, false);
				if (arg != null)
					add(new MethodSetter(arg, method), method.toString());
			}
		}
		
		
		public void addSetter(Field field)
		{
			if (field != null)
			{
				MethodArg arg = factory_.createFieldArg(field, false);
				if (arg != null)
					add(new FieldSetter(arg, field), field.toString());
			}
		}


		private void add(Setter setter, String key)
		{
			if (!done_.contains(key))
			{
				done_.add(key);
				setters_.add(setter);
			}
		}
		
		
		private final MethodArgFactory factory_;
		private final ArrayList<Setter> setters_ = new ArrayList<>();
		private final HashSet<String> done_ = new HashSet<>();
	}
	
	
	private final Class<?> beanClass_;
	private final Setter[] setters_;
}
