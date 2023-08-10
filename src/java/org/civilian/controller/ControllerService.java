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
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import org.civilian.Controller;
import org.civilian.annotation.PathParam;
import org.civilian.annotation.Segment;
import org.civilian.application.ConfigKeys;
import org.civilian.controller.classloader.ReloadConfig;
import org.civilian.resource.PathParamMap;
import org.civilian.type.TypeLib;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;


/**
 * ControllerService is a service to create {@link ControllerType ControllerTypes}. 
 */
public class ControllerService
{
	/**
	 * Creates a new ControllerService.
	 * @param pathParams the PathParams used by an application. Needed to handle
	 * 		arguments of controller action methods.
	 * @param typeLib the type library used by an application. Needed to handle
	 * 		arguments of controller action methods.
	 */
	public ControllerService(PathParamMap pathParams, TypeLib typeLib, 
		ControllerFactory factory,
		ReloadConfig reloadConfig)
	{
		if (pathParams == null)
			pathParams = PathParamMap.EMPTY;
		if (typeLib == null)
			typeLib = new TypeLib();
		
		loader_ = reloadConfig != null ?
			new DevLoader(pathParams, typeLib, factory, reloadConfig) :
			new RealLoader(pathParams, typeLib, factory, getClass().getClassLoader());
	}

	
	/**
	 * Returns if the service is reloading classes
	 * for each request. To use this feature, develop mode must be true,
	 * and the application needs key {@link ConfigKeys#DEV_CLASSRELOAD}
	 * set to true in its application settings.
	 */
	public boolean isReloading()
	{
		return loader_.isReloading();
	}

	
	/**
	 * Returns a ControllerType for a {@link ControllerSignature controller signature}. 
	 */
	public ControllerType getControllerType(ControllerSignature signature)
	{
		return loader_.getControllerType(signature);
	}

	
	public ControllerType getControllerType(Class<? extends Controller> ctrlClass)
	{
		return getControllerType(new ControllerSignature(ctrlClass.getName()));
	}

	
	/**
	 * CMethods stores the ControllerMethods of a controller.
	 */
	private static class CMethods
	{
		private static final LeveledMethod[] EMPTY_METHODS = new LeveledMethod[0]; 
		

		private CMethods(Class<? extends Controller> controllerClass, ControllerSignature signature, ControllerFactory factory, int hierarchLevel)
		{
			controllerClass_	= Check.notNull(controllerClass, "controllerClass");
			signature_			= Check.notNull(signature, "signatue");
			hierarchLevel_		= hierarchLevel;
			methods_ 			= EMPTY_METHODS;
			factory_			= factory;
		}
		
		
		/**
		 * Creates the method list for the Controller base class.
		 */
		public CMethods(ControllerFactory factory)
		{
			this(Controller.class, new ControllerSignature(Controller.class.getName()), factory, 0);
		}
		
		
		/**
		 * Creates an action method list for a controller class. The methods of the controller class are scanned,
		 * all action methods are extracted from the controller class and then joined with all inherited action
		 * methods of the parent list. 
		 * @param controllerClass the controller class
		 * @param signature consider only methods whose value of its {@link Segment} and {@link PathParam} annotation match the signature	
		 * @param parentList the method list of the parent class
		 * @param typeLib a type library.
		 */
		public CMethods(Class<? extends Controller> controllerClass, ControllerSignature signature, CMethods parentList, 
			PathParamMap pathParams, TypeLib typeLib)
		{
			this(controllerClass, signature, parentList.factory_, parentList.hierarchLevel_ + 1);
			Check.notNull(parentList, "parentList");
			
			// add inheritable action methods
			for (LeveledMethod m : parentList.methods_)
			{
				if (m.method.canInherit(controllerClass))
					methods_ = ArrayUtil.addLast(methods_, m);
			}
			
			// add action methods defined by the controller class
			MethodArgFactory argFactory = new MethodArgFactory(pathParams, typeLib); 
			int inClassIndex = 0;
			for (Method javaMethod : controllerClass.getDeclaredMethods())
			{
				if (signature.matchJavaMethod(javaMethod))
				{
					ControllerMethod method = ControllerMethod.create(argFactory, javaMethod);
					if (method != null)
						methods_ = ArrayUtil.addLast(methods_, new LeveledMethod(method, hierarchLevel_, inClassIndex++));
				}
			}
			
			Arrays.sort(methods_);
		}
		
		
		public ControllerType createType()
		{
			if (Modifier.isAbstract(controllerClass_.getModifiers()))
				return null; // can't invoke abstract controllers
			else
			{
				ControllerMethod[] methods = new ControllerMethod[methods_.length];
				for (int i=0; i<methods.length; i++)
					methods[i] = methods_[i].method;
				return new ControllerType(controllerClass_, factory_, methods);
			}
		}


		public ControllerSignature getSignature()
		{
			return signature_;
		}
		
		
		private LeveledMethod[] methods_;
		private final Class<? extends Controller> controllerClass_;
		private final int hierarchLevel_;
		private final ControllerSignature signature_;
		private final ControllerFactory factory_;
	}
	
	
	private static class LeveledMethod implements Comparable<LeveledMethod>
	{
		public LeveledMethod(ControllerMethod method, int hierarchyLevel, int inClassIndex)
		{
			this.method 		= method;
			this.hierarchyLevel = hierarchyLevel;
			this.inClassIndex 	= inClassIndex;
		}

	
		@Override public int compareTo(LeveledMethod other)
		{
			if (other.hierarchyLevel != hierarchyLevel)
				return other.hierarchyLevel - hierarchyLevel;
			else
				return other.inClassIndex - inClassIndex;
		}
		

		@Override public int hashCode()
		{
			return method.hashCode();
		}
		
		
		@Override public boolean equals(Object other)
		{
			return (other instanceof LeveledMethod) && (((LeveledMethod)other).method == method); 
		}

		
		public final ControllerMethod method;
		public final int hierarchyLevel;
		public final int inClassIndex;
	}
	

	/**
	 * Loader implements ControllerService.getControllerType(String signature).
	 */
	private abstract static class Loader
	{
		public abstract ControllerType getControllerType(ControllerSignature signature);

		
		public abstract boolean isReloading();
	}
	
	
	/**
	 * DevLoader is a Loader for development mode with controller class reloading turned on.
	 */
	private static class DevLoader extends Loader
	{
		public DevLoader(PathParamMap pathParams, TypeLib typeLib, ControllerFactory factory, ReloadConfig reloadConfig)
		{
			pathParams_	 	= pathParams;
			typeLib_	 	= typeLib;
			factory_		= factory;
			reloadConfig_	= reloadConfig;
		}
		
		
		@Override public boolean isReloading()
		{
			return true;
		}
		
		
		@Override public ControllerType getControllerType(ControllerSignature signature)
		{
			RealLoader loader = new RealLoader(pathParams_, typeLib_, factory_, reloadConfig_.createClassLoader());
			return loader.getControllerType(signature);
		}
		

		private final PathParamMap pathParams_;
		private final TypeLib typeLib_;
		private final ControllerFactory factory_;
		private final ReloadConfig reloadConfig_;
	}

	
	/**
	 * RealLoader is a Loader which implements loading of ControllerTypes.
	 * ControllerTypes are cached.
	 */
	private static class RealLoader extends Loader
	{
		public RealLoader(PathParamMap pathParams, TypeLib typeLib, 
			ControllerFactory factory, ClassLoader classLoader)
		{
			pathParams_	 = pathParams;
			typeLib_	 = typeLib;
			classLoader_ = classLoader;
			addMethods(new CMethods(factory));
		}
		

		@Override public boolean isReloading()
		{
			return false;
		}
		
		
		private void addMethods(CMethods methods)
		{
			signature2methods_.put(methods.getSignature(), methods);
		}
		
		
		@Override public synchronized ControllerType getControllerType(ControllerSignature signature)
		{
			if (signature != null)
			{
				CMethods methods = getMethods(signature); 
				if (methods != null)
					return methods.createType();
			}
			return null;
		}

		
		/**
		 * Returns the controller methods for a controller class.
		 */
		private CMethods getMethods(ControllerSignature signature)
		{
			CMethods methods = signature2methods_.get(signature); 
			if (methods == null)
			{
				Class<? extends Controller> ctrlClass = constructControllerClass(signature.getClassName());
				methods = findMethods(ctrlClass, signature);
			}
			return methods;
		}
		
		
		private Class<? extends Controller> constructControllerClass(String name)
		{
			try
			{
				Class<?> c = Class.forName(name, false, classLoader_);
				return checkClass(c);
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalArgumentException("class '" + name + "' not found", e);
			}
		}
		
			
		private CMethods findMethods(Class<? extends Controller> controllerClass, ControllerSignature signature)
		{
			Class<? extends Controller> superClass = checkClass(controllerClass.getSuperclass());
			
			CMethods parentMethods = getMethods(new ControllerSignature(superClass.getName()));
			
			CMethods methods = new CMethods(controllerClass, signature, parentMethods, pathParams_, typeLib_);
			addMethods(methods);
			
			return methods;
		}
		
		
		@SuppressWarnings("unchecked")
		private Class<? extends Controller> checkClass(Class<?> c)
		{
			if (!Controller.class.isAssignableFrom(c))
				throw new IllegalStateException("'" + c.getName() + "' is not a controller class: maybe there is a classloader problem?");
			return (Class<? extends Controller>)c;
		}
		
		
		private final PathParamMap pathParams_;
		private final TypeLib typeLib_;
		private final ClassLoader classLoader_;
		private final HashMap<ControllerSignature, CMethods> signature2methods_ = new HashMap<>();
	}

	
	@Override public String toString()
	{
		return getClass().getSimpleName();
	}

	
	private final Loader loader_;
}
