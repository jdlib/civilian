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
package org.civilian.client;


import java.io.PrintWriter;
import java.util.ArrayList;
import org.civilian.resource.Route;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.util.Check;


/**
 * WebResource represents a resource of an application, addressable by an URL.
 * WebResource is intended to be used by a client program to build remote URLs.
 */
public class WebResource
{
	/**
	 * Creates a new root WebResource.
	 * @param url the URL of the web application.
	 */
	public WebResource(String url)
	{
		root_		= this;
		parent_ 	= null;
		route_  	= Route.constant(url);
		segment_	= null;
		pathParam_	= null;
	}
	
	
	/**
	 * Creates a new resource which extends another resource.
	 * @param parent the parent resource.
	 * @param segment the route of this resource is the parent route + the path segment  
	 */
	public WebResource(WebResource parent, String segment)
	{
		this(parent, Check.notNull(segment, "segment"), null);
	}
	
	
	/**
	 * Creates a new resource which extends another resource.
	 * @param parent the parent resource.
	 * @param pathParam the route of this resource is the parent route + the pathParam 
	 */
	public WebResource(WebResource parent, PathParam<?> pathParam)
	{
		this(parent, null, Check.notNull(pathParam, "pathParam"));
	}
	
	
	protected WebResource(WebResource parent, String segment, PathParam<?> pathParam)
	{
		if ((segment == null) == (pathParam == null))
			throw new IllegalArgumentException("a segment or pathParam must be provided");

		parent_ 		= Check.notNull(parent, "parent");
		root_			= parent.getRoot();
		
		if (segment != null)
		{
			segment_ 	= segment;
			pathParam_	= null;
			route_		= parent.route_.addSegment(segment);
		}
		else
		{
			segment_ 	= null;
			pathParam_	= pathParam;
			route_		= parent.route_.addPathParam(pathParam);
			
			WebResource p = parent;
			while(p != null)
			{
				if (p.pathParam_ == pathParam)
					throw new IllegalArgumentException("the path parameter '" + pathParam + "' was already used in parent resource " + p);
				p = p.parent_;
			}
		}
	}

	
	/**
	 * @return if this resource is the root resource.
	 */
	public boolean isRoot()
	{
		return root_ == this;
	}

	
	/**
	 * @return the root resource.
	 */
	public WebResource getRoot()
	{
		return root_;
	}

	
	/**
	 * @return the parent resource.
	 */
	public WebResource getParent()
	{
		return parent_;
	}
	
	
	/**
	 * @return the segment of this Resource by which the parent resource
	 * is extended. Returns null, if the resource is the root or extends by a path-param.
	 */
	public String getSegment()
	{
		return segment_;
	}
	

	/**
	 * @return the PathParam of this Resource by which the parent resource
	 * is extended. Returns null, if the resource is the root or extends by a segment.
	 */
	public PathParam<?> getPathParam()
	{
		return pathParam_;
	}
	
	
	/**
	 * @return the Route from the application root to this resource.
	 */
	public Route getRoute()
	{
		return route_;
	}

	
	/**
	 * @return he number of child resources.
	 */
	public int getChildCount()
	{
		return children_ != null ? children_.size() : 0;
	}

	
	/**
	 * @param i the child index
	 * @return the i-th child resource.
	 */
	public WebResource getChild(int i)
	{
		return children_.get(i);
	}

	
	/**
	 * Adds a new child resource.
	 * @param child the resource
	 * @return the resource
	 */
	protected WebResource addChild(WebResource child)
	{
		if (children_ == null)
			children_ = new ArrayList<>();
		children_.add(child);
		return child;
	}


	/**
	 * Prints the resource tree starting with this resource.
	 * @param out a PrintWriter
	 */
	public void print(PrintWriter out)
	{
		String s = toString(); 
		out.print(s);
		for (int i=s.length(); i<30; i++)
			out.print(' ');
		out.println();
		
		for (int i=0; i<getChildCount(); i++)
			getChild(i).print(out);
	}

	
	/**
	 * Returns a route representation of this resource.
	 */
	@Override public String toString()
	{
		return route_.toString();
	}

	
	private final WebResource root_;
	private final WebResource parent_;
	private final String segment_;
	private final PathParam<?> pathParam_;
	private final Route route_;
	private ArrayList<WebResource> children_;
}
