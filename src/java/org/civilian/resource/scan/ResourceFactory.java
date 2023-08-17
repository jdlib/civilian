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


import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.civilian.controller.Controller;
import org.civilian.controller.ControllerNaming;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.method.MethodAnnotations;
import org.civilian.resource.PathParam;
import org.civilian.resource.PathParamMap;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.StringUtil;


class ResourceFactory
{
	public ResourceFactory(String rootPackageName, 
		ControllerNaming naming,
		PathParamMap pathParams)
	{
		rootPackageName_ 	= Check.notNull(rootPackageName, "rootPackageName");
		naming_		 		= Check.notNull(naming, "naming");
		pathParamMap_ 		= Check.notNull(pathParams, "pathParams");
		packages_.put(rootPackageName_, new ControllerPackage(root_, rootPackageName_));  
	}
	
	
	public ResourceInfo getRoot()
	{
		return root_;
	}


	public String getRootPackage()
	{
		return rootPackageName_;
	}
	
	
	public ControllerNaming getNaming()
	{
		return naming_;
	}
	
	
	private ControllerPackage getPackage(String packageName)
	{
		if (!packageName.startsWith(rootPackageName_))
			throw new IllegalArgumentException("invalid package " + packageName);
		
		ControllerPackage cp = packages_.get(packageName);
		if (cp == null)
			cp = mapPackage(packageName);
		return cp;
	}
	
	
	private ControllerPackage mapPackage(String packageName)
	{
		ControllerPackage parent 	= null;
		String segment		 		= null;
		if (!packageName.equals(rootPackageName_))
		{
			int p = packageName.lastIndexOf('.');
			segment = naming_.packagePart2Segment(packageName.substring(p + 1));
			parent 	= getPackage(packageName.substring(0, p));
		}
		
		Class<?> infoClass		= getPackageInfoClass(packageName);
		ResourceExt extension	= getExtension(infoClass, segment, false);
		ControllerPackage cp	= new ControllerPackage(parent, packageName, extension);
		packages_.put(packageName, cp);
		return cp;
	}

	
	private Class<?> getPackageInfoClass(String packageName)
	{
		try
		{
			return Class.forName(packageName + ".package-info");
		}
		catch(ClassNotFoundException e)
		{
			return null;
		}
	}

	
	/**
	 * Maps a controller class to a resource. By default the controller resource
	 * extends the package resource by the controller class name.
	 * This method also handles:
	 * - mapping of default controllers, as defined by the DefaultController annotation
	 * - mapping of classes with a PathParam annotation
	 * - mapping of classes with a Path annotation
	 * @param cp the controller package, containing the resource to which the package is mapped
	 * @param cls the controller class
	 */
	public void mapController(Class<? extends Controller> cls)
	{
		ControllerPackage cp 	= getPackage(ClassUtil.getPackageName(cls));
		String segment 			= naming_.className2Segment(cls.getSimpleName());
		
		ResourceExt extension	= getExtension(cls, segment, true);
		ResourceInfo resInfo	= extension == null ? cp.resInfo : cp.resInfo.getChild(extension);
		
		ControllerSignature sig = new ControllerSignature(cls);
		resInfo.setControllerSignature(sig);
		
		tmpMethodExts_.clear();
		collectMethodExtensions(cls, tmpMethodExts_);
		for (ResourceExt methodExt : tmpMethodExts_)
		{
			ResourceInfo methodRes = resInfo.getChild(methodExt);
			if (methodExt.pathParam != null)
				methodRes.setControllerSignature(sig.withMethodPathParam(methodExt.pathParam));
			else
				methodRes.setControllerSignature(sig.withMethodSegment(methodExt.segment));
		}
	}
	
	
	private void collectMethodExtensions(Class<? extends Controller> c, Set<ResourceExt> methodExtensions)
	{
		for (Method method : c.getDeclaredMethods())
		{
			if (MethodAnnotations.of(method) != null)
			{
				ResourceExt ext = getExtension(method, null, false);
				if (ext != null)
					methodExtensions.add(ext);
			}
		}
	}
	
	
	/**
	 * Reads @PathParam and @Segment annotations for package-info and controller classes.
	 * @param segment the default segment derived from the last package part or 
	 * 		the simple name of the controller class. 
	 */
	private ResourceExt getExtension(AnnotatedElement annotated, String defaultSegment, boolean allowEmptySegment)
	{
		PathParam<?> pp = null;
		String segment 	= null;
		
		if (annotated != null)
		{
			pp = extractPathParam(annotated);
			segment = extractSegment(annotated, allowEmptySegment);
			
			if ((pp != null) && (segment != null))
				throw new ScanException(annotated + ": cannot specify both @PathParam and @Segment annotations");
		}
		
		if (pp != null)
			return new ResourceExt(pp);
		else if (segment != null)
			return new ResourceExt(segment);
		else if (defaultSegment != null)
			return new ResourceExt(defaultSegment);
		else
			return null;
	}
	
	
	/**
	 * Evaluate @PathParam.
	 */
	private PathParam<?> extractPathParam(AnnotatedElement annotated)
	{
		PathParam<?> pp = null;
		org.civilian.annotation.PathParam aParam = annotated.getAnnotation(org.civilian.annotation.PathParam.class);
		if (aParam != null)
		{
			pp = pathParamMap_.get(aParam.value());
			if (pp == null)
				throw new ScanException(annotated + ": annotation @PathParam specifies unknown path parameter '" + aParam.value() + "'");
		}
		return pp;
	}
	
	
	/**
	 * Evaluate @Segment.
	 */
	private String extractSegment(AnnotatedElement annotated, boolean allowEmptySegment)
	{
		String segment = null;
		org.civilian.annotation.Segment aSegment = annotated.getAnnotation(org.civilian.annotation.Segment.class);
		if (aSegment != null)
		{
			segment = aSegment.value().trim();
			segment = StringUtil.cutLeft(segment, "/");
			segment = StringUtil.cutRight(segment, "/").trim();
			if (StringUtil.isBlank(segment))
			{
				if (!allowEmptySegment)
					segmentError(annotated,  aSegment, "results in an empy segment");
				segment = null;
			}
			else if (segment.indexOf('/') >= 0)
				segmentError(annotated, aSegment, "may not contain a '/' character");
		}
		return segment;
	}
	
	
	private void segmentError(AnnotatedElement annotated, org.civilian.annotation.Segment aSegment, String msg)
	{
		throw new ScanException(annotated + ": @Segment annotation '" + aSegment.value() + "' " + msg);
	}
	
	
	private final ResourceInfo root_ = new ResourceInfo();
	private final String rootPackageName_;
	private final PathParamMap pathParamMap_;
	private final ControllerNaming naming_;
	private final Map<String,ControllerPackage> packages_ = new HashMap<>(); 
	private final Set<ResourceExt> tmpMethodExts_ = new HashSet<>();
}
