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


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerNaming;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.method.MethodAnnotations;
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;


class ControllerClassScan
{
	public ControllerClassScan(String rootPackageName, 
		ControllerNaming naming,
		PathParamMap pathParams)
	{
		rootPackageName_ 	= Check.notNull(rootPackageName, "rootPackageName");
		naming_		 		= Check.notNull(naming, "naming");
		extFactory_ 		= new ResourceExt.Factory(pathParams);
		packages_.put(rootPackageName_, new ControllerPackage(root_, rootPackageName_));  
	}
	
	
	public ResourceInfo getRoot()
	{
		return root_;
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
		{
			cp = createPackage(packageName);
			packages_.put(packageName, cp);
		}
		return cp;
	}
	
	
	private ControllerPackage createPackage(String packageName)
	{
		ControllerPackage parent 	= null;
		String segment		 		= null;
		if (!packageName.equals(rootPackageName_))
		{
			int p 	= packageName.lastIndexOf('.');
			segment = naming_.packagePart2Segment(packageName.substring(p + 1));
			parent 	= getPackage(packageName.substring(0, p));
		}
		
		Class<?> infoClass		= getPackageInfoClass(packageName);
		ResourceExt extension	= extFactory_.getExtension(infoClass, segment, false);
		return new ControllerPackage(parent, packageName, extension);
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
	 * Scan a controller class and map to resources. By default the controller resource
	 * extends the package resource by the controller class name.
	 * This method also handles:
	 * - mapping of default controllers, as defined by the DefaultController annotation
	 * - mapping of classes with a PathParam annotation
	 * - mapping of classes with a Segment annotation
	 * @param cls the controller class
	 */
	public void scan(Class<? extends Controller> cls)
	{
		ControllerPackage cp 	= getPackage(ClassUtil.getPackageName(cls));
		String segment 			= naming_.className2Segment(cls.getSimpleName());
		
		ResourceExt extension	= extFactory_.getExtension(cls, segment, true);
		ResourceInfo resInfo	= extension == null ? cp.resInfo : cp.resInfo.getChild(extension);
		
		ControllerSignature sig = new ControllerSignature(cls);
		resInfo.setControllerSignature(sig);
		
		Set<ResourceExt> methodExts = new HashSet<>();
		collectMethodExtensions(cls, methodExts);
		for (ResourceExt methodExt : methodExts)
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
				ResourceExt ext = extFactory_.getExtension(method, null, false);
				if (ext != null)
					methodExtensions.add(ext);
			}
		}
	}
	
	
	private final ResourceInfo root_ = new ResourceInfo();
	private final String rootPackageName_;
	private final ResourceExt.Factory extFactory_;
	private final ControllerNaming naming_;
	private final Map<String,ControllerPackage> packages_ = new HashMap<>(); 
}
