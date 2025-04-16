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
package org.civilian.controller.factory;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.civilian.application.AppConfig;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerFactory;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


/**
 * An experimental ControllerFactory which injects CDI managed beans into Controller instances.
 * <p>
 * Right now we only support injection of simple beans (no collections or iterators)
 * into fields of a controller, which are annotated with javax.inject.Inject.
 * <p>
 * To use CDI for controller instances install a CdiControllerFactory during 
 * application setup.
 * @see AppConfig#setControllerFactory(ControllerFactory)
 */
public class CdiControllerFactory implements ControllerFactory
{
	/**
	 * Creates a new CdiControllerFactory.
	 * @throws IllegalStateException if the CDI BeanManager cannot be obtained
	 */
	public CdiControllerFactory()
	{
		// JEE7 beanManager_ = CDI.current().getBeanManager();
		
		try 
		{
			InitialContext initialContext = new InitialContext();
			beanManager_ = (BeanManager)initialContext.lookup("java:comp/BeanManager");
			if (beanManager_ == null)
				throw new IllegalStateException("cannot obtain CDI BeanManager");
		}
		catch (NamingException e)
		{
            throw new IllegalStateException("unable to lookup bean manager", e);
	    }
	}
	
    
	/**
	 * Creates a new CdiControllerFactory.
	 * @param beanManager the beanManager used by the factory 
	 */
	public CdiControllerFactory(BeanManager beanManager)
	{
		beanManager_ = Check.notNull(beanManager, "beanManager");
	}
	
	
	/**
	 * Creates a Controller and resolves the fields which are annotated with @Inject.
	 */
	@Override public Controller createController(Class<? extends Controller> controllerClass) throws Exception
	{
		Injector injector 		= getInjector(controllerClass, Controller.class);
		@SuppressWarnings("deprecation")
		Controller controller 	= controllerClass.newInstance();
		injector.inject(beanManager_, controller);
		return controller;
	}

	
	private Injector getInjector(Class<?> c, Class<?> stopClass)
	{
		Injector injector = injectorCache_.get(c.getName());
		// we construct an Injector for the class if 
		// we look it up the first time or - due to class reloading -
		// the controller class has changed
		if ((injector == null) || (injector.targetClass != c))
		{
			injector = buildInjector(c, stopClass);
			injectorCache_.put(c.getName(), injector);
		}
		return injector;
	}
	
	
	private Injector buildInjector(Class<?> c, Class<?> stopClass)
	{
		ArrayList<InjectorAction> actions = new ArrayList<>();
		
		Class<?> next = c;
		while(buildInjectorActions(next, stopClass, actions))
			next = next.getSuperclass();
		
		return new Injector(c, actions.toArray(new InjectorAction[actions.size()]));
	}
	

	private boolean buildInjectorActions(Class<?> c, Class<?> stopClass, ArrayList<InjectorAction> actions)
	{
		if ((c == null) || (c == stopClass))
			return false;
		
		// check for Inject annotation on fields
	    for (Field field : c.getDeclaredFields()) 
	    {
	    	Annotation[] annotations = field.getAnnotations();
	    	for (int i=0; i<annotations.length; i++)
	    	{
	    		if (ClassUtil.isA(annotations[i], Inject.class))
	    		{
	    			annotations = ArrayUtil.removeAt(annotations, i);
	    			actions.add(buildFieldInjectorAction(field, annotations));
	    			break;
	    		}
	    	}
	    }
	    return true;
	}
	
	
	private InjectorAction buildFieldInjectorAction(Field field, Annotation[] annotations)
	{
        Set<Bean<?>> beans = beanManager_.getBeans(field.getGenericType(), annotations);
        if (beans.isEmpty())
        	throw new IllegalStateException("no bean available for field " + field.getDeclaringClass().getName() + '.' + field.getName());
        return new FieldInjectorAction(field, beans.iterator().next());
	}
	
	
	private static class Injector
	{
		public Injector(Class<?> targetClass, InjectorAction[] actions)
		{
			this.targetClass = targetClass;
			actions_ = actions;
		}
		
		
		public void inject(BeanManager beanManager, Object object) throws Exception
		{
			for (InjectorAction action : actions_)
				action.run(beanManager, object);
		}
		
		
		public final Class<?> targetClass;
		private InjectorAction[] actions_;
	}
	
	
	private static abstract class InjectorAction
	{
		public abstract void run(BeanManager beanManager, Object object) throws Exception;
	}
	
	
	private static class FieldInjectorAction extends InjectorAction
	{
		public FieldInjectorAction(Field field, Bean<?> bean)
		{
			field.setAccessible(true);
			field_ = field;
			bean_  = bean;
			type_  = field.getGenericType();
		}
		
		
		@Override public void run(BeanManager beanManager, Object object) throws Exception
		{
			CreationalContext<?> context = beanManager.createCreationalContext(bean_);
			Object reference = beanManager.getReference(bean_, type_, context);
            field_.set(object, reference);
		}
		
		
		private Bean<?> bean_;
		private Field field_;
		private Type type_;
	}

	
	private BeanManager beanManager_;
	private ConcurrentHashMap<String,Injector> injectorCache_ = new ConcurrentHashMap<>();
}
