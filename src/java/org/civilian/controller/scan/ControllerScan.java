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
package org.civilian.controller.scan;


import java.lang.reflect.Modifier;
import java.util.Set;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerConfig;
import org.civilian.resource.Resource;
import org.civilian.util.classpath.ClassPathScan;


/**
 * ResourceScan scans the class path, collects all controller classes
 * of an application and creates a resource tree.
 */
public class ControllerScan
{
	/**
	 * Creates a ResourceScan.
	 * @param config the controller config
	 * @param classLoader a class loader
	 * @param verbose verbose output?
	 */
	public ControllerScan(
		ControllerConfig config,
		ClassLoader classLoader,
		boolean verbose) 
	{
		classLoader_	= classLoader != null ? classLoader : getClass().getClassLoader();
		classScan_ 	= new ControllerClassScan(config.getRootPackage(), config.getNaming(), config.getPathParams()); 
		verbose_		= verbose;
		
		scanClassPath(config.getRootPackage());
		rootInfo_ = classScan_.getRoot();
		rootInfo_.sortChildren();
	}

	
	public ResourceInfo getRootInfo()
	{
		return rootInfo_;
	}
	
	
	public Resource getRootResource()
	{
		return rootInfo_.toResource();
	}

	
	private void scanClassPath(String rootPackageName)
	{
		if (verbose_)
			log("scanning classes below " + rootPackageName);
		
		ClassPathScan scan = new ClassPathScan(rootPackageName);
		Set<String> candidateClasses;
		try
		{
			candidateClasses = scan.collect(classScan_.getNaming()::isControllerClass);
		}
		catch (Exception e)
		{
			throw new ScanException("error during classpath scan", e);
		}
		
		if (verbose_)
			log("found " + candidateClasses.size() + " potential resource classes");
		
		for (String c : candidateClasses)
			scanClass(c);
	}
	
	
	@SuppressWarnings("unchecked")
	private void scanClass(String className)
	{
		Class<?> c;
		try
		{
			c = Class.forName(className, false, classLoader_);
		}
		catch(ClassNotFoundException e)
		{
			// weird situation
			throw new ScanException("class '" + className + "' not found, but was part of classpath scan", e);
		}
		
		// ignore if not a Controller class (naming false positive) or if abstract 
		// (map annotations on abstract classes are ignored)
		if (Controller.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()))
			classScan_.scan((Class<? extends Controller>)c);
	}
	
	
	public static void log(String message)
	{
		System.out.println(message);
	}
		
	
	private final ClassLoader classLoader_;
	private final ControllerClassScan classScan_;
	private final boolean verbose_;
	private final ResourceInfo rootInfo_;
}
