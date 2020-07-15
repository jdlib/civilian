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
package org.civilian.resource.scan;


import java.lang.reflect.Modifier;
import java.util.Set;
import org.civilian.Application;
import org.civilian.Controller;
import org.civilian.Resource;
import org.civilian.controller.ControllerNaming;
import org.civilian.internal.classpath.ClassPathScan;
import org.civilian.resource.PathParamMap;


/**
 * ResourceScan scans the class path, collects all controller classes
 * of an application and creates a resource tree.
 * The resource tree determines which dynamic resources are known to the
 * application. 
 * @see Application#generateResourceTree(org.civilian.controller.classloader.ReloadConfig)
 */
public class ResourceScan
{
	public ResourceScan(String rootPackage, 
		ControllerNaming naming,
		PathParamMap pathParams,
		ClassLoader classLoader) 
		throws ScanException
	{
		classLoader_	= classLoader != null ? classLoader : getClass().getClassLoader();
		resFactory_ 	= new ResourceFactory(rootPackage, naming, pathParams); 
	}

	
	public Resource run() throws ScanException
	{
		return getInfo().toResource();
	}
	
	
	public ResourceInfo getInfo() throws ScanException
	{
		scanClassPath();
		ResourceInfo root = resFactory_.getRoot();
		root.sortChildren();
		return root;
	}
	
	
	private void scanClassPath() throws ScanException
	{
		String rootPackage = resFactory_.getRootPackage();
		if (verbose)
			log("scanning classes below " + rootPackage);
		
		ClassPathScan scan = new ClassPathScan(rootPackage);
		Set<String> candidateClasses;
		try
		{
			candidateClasses = scan.collect(resFactory_.getNaming()::isControllerClass);
		}
		catch (Exception e)
		{
			throw new ScanException("error during classpath scan", e);
		}
		
		if (verbose)
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
			resFactory_.mapController((Class<? extends Controller>)c);
	}
	
	
	public static void log(String message)
	{
		System.out.println(message);
	}
		
	
	private ClassLoader classLoader_;
	private ResourceFactory resFactory_;
	boolean verbose;
}
