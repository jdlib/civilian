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


import java.util.ArrayList;
import java.util.Collections;
import org.civilian.Resource;
import org.civilian.controller.ControllerSignature;
import org.civilian.resource.PathParam;


/**
 * ResourceInfo holds information about a Resource.
 * It is created during resource scan and can be used 
 * to create a runtime resource tree or to generate 
 * a resources class.
 */
public class ResourceInfo implements Comparable<ResourceInfo>
{
	/**
	 * Creates a info object for the root resource.
	 */
	public ResourceInfo() 
	{
		parent_ 	= null;
		segment_	= null;
		pathParam_	= null;
		path_		= "/";
	}
	
	
	/**
	 * Creates a ResourceInfo for a child resource.
	 */
	private ResourceInfo(ResourceInfo parent, ResourceExt ext)
	{
		parent_ 	= parent;
		segment_	= ext.segment;
		pathParam_	= ext.pathParam;
		path_		= parent.appendPath(segment_ != null ? '/' + segment_ : pathParam_.toString());  
	}

	
	public ResourceInfo getParent()
	{
		return parent_;
	}
	
	
	public boolean isRoot()
	{
		return parent_ == null;
	}

	
	private String appendPath(String s)
	{
		return parent_ != null ? path_ + s : s;  
	}
	
	
	public String getSegment()
	{
		return segment_;
	}
	
	
	public PathParam<?> getPathParam()
	{
		return pathParam_;
	}
	

	/**
	 * Returns the number of child Path objects.
	 */
	public int getChildCount()
	{
		return children_ != null ? children_.size() : 0;
	}

	
	/**
	 * Returns the i-th child.
	 */
	public ResourceInfo getChild(int i)
	{
		return children_.get(i);
	}

	
	public ResourceInfo getChild(ResourceExt ext)
	{
		for (int i=0; i<getChildCount(); i++)
		{
			ResourceInfo child = getChild(i);
			if ((ext.segment != null) ? ext.segment.equals(child.segment_) : (ext.pathParam == child.pathParam_))
				return child;
		}
		return addChild(new ResourceInfo(this, ext));
	}
	
	
	private ResourceInfo addChild(ResourceInfo path)
	{
		if (children_ == null)
			children_ = new ArrayList<>();
		children_.add(path);
		return path;
	}


	public void setPackage(ControllerPackage cp)
	{
		if (package_ != null)
			throw new ScanException("path '" + path_ + "' is mapped to package '" + package_ + "' and '" + cp + "'");
		package_ = cp;
	}
	
	
	public ControllerPackage getPackage()
	{
		return package_;
	}

	
	public void setControllerInfo(String className, String methodPath)
	{
		if (controllerSignature_ != null)
			throw new ScanException("resource '" + path_ + "' is mapped to class '" + className + "' and '" + controllerSignature_ + "'");
			
		controllerSignature_ = ControllerSignature.build(className, methodPath);
	}
	
	
	public String getControllerSignature()
	{
		return controllerSignature_;
	}
	

	/**
	 * Recursively sorts the resource children.
	 */
	void sortChildren()
	{
		if (children_ != null)
		{
			Collections.sort(children_);
			for (ResourceInfo child : children_)
				child.sortChildren();
		}
	}

	
	/**
	 * Sorts the children lexicographically by their path string.
	 * (Mostly to have a nice ordering in the generated resource class,
	 * or in Civilian Admin). 
 	 * At runtime the resources will be sorted again,
	 * prioritizing nodes with a segment!
	 * (see ResourceNode.compareTo)
	 */
	@Override public int compareTo(ResourceInfo other)
	{
		return path_.compareTo(other.path_);
	}

	
	@Override public int hashCode()
	{
		return path_.hashCode();
	}
	
	
	@Override public boolean equals(Object other)
	{
		return (other instanceof ResourceInfo) && (((ResourceInfo)other).path_.equals(path_)); 
	}

	
	@Override public String toString()
	{
		return path_;
	}

	
	/**
	 * Creates a resource tree out of the tree represented by this info.
	 */
	Resource toResource()
	{
		if (!isRoot())
			throw new IllegalStateException("not root");
		Resource root = new Resource();
		toResource(root, this);
		return root; 
	}
	
	
	private static void toResource(Resource resource, ResourceInfo resInfo)
	{
		resource.setControllerSignature(resInfo.controllerSignature_);
		
		int n = resInfo.getChildCount();
		for (int i=0; i<n; i++)
		{
			ResourceInfo childInfo = resInfo.getChild(i);
			Resource childRes = new Resource(resource, childInfo.segment_, childInfo.pathParam_);
			toResource(childRes, childInfo);
		}
	}


	private final ResourceInfo parent_;
	private final PathParam<?> pathParam_;
	private final String segment_;
	private final String path_;
	private ControllerPackage package_;
	private String controllerSignature_;
	private ArrayList<ResourceInfo> children_;
}
