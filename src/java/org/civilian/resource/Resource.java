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
package org.civilian.resource;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.controller.Controller;
import org.civilian.controller.ControllerService;
import org.civilian.controller.ControllerSignature;
import org.civilian.controller.ControllerType;
import org.civilian.resource.pathparam.PathParam;
import org.civilian.response.Url;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;
import org.civilian.util.PathScanner;
import org.civilian.util.PathScanner.Mark;


/**
 * Resource represents a resource of a web application, addressable by an URL.
 * A Resource can be associated with a {@link Controller}.<br>
 * When the application receives a request for such a resource, 
 * an instance of the associated Controller is created and invoked to generate 
 * the response.<br>
 * If no resource directly matches the request,
 * the controller of the closest matching parent resource will be used to process the request,
 * using only the fallback actions of the controller.<br>
 * There are several options how to construct the resource tree of an application:
 * First the resource tree can be constructed at development or compile time by scanning
 * the controller classes of an application. Second are more unusually the application
 * can decide to build its resource tree by hand and map controller classes to it.
 * It would also be possible to enhance the resource tree at runtime, and remap resources
 * to different controllers.  
 */
public class Resource implements Iterable<Resource>
{
	/**
	 * Creates a new root resource.
	 */
	public Resource()
	{
		parent_ 	= null;
		route_  	= Route.root();
		tree_		= new Tree(this);
		segment_	= "";
		pathParam_	= null;
	}
	
	
	/**
	 * Creates a new resource which extends the parent resource by a constant path segment.
	 * The route of the resource is the parent route + the segment.
	 * @param parent the parent resource
	 * @param segment the segment.  
	 */
	public Resource(Resource parent, String segment)
	{
		this(parent, Check.notEmpty(segment, "segment"), null);
	}
	
	
	/**
	 * Creates a new resource which extends the parent resource by a path param.
	 * The route of the resource is the parent route + the pathParam
	 * @param parent the parent resource
	 * @param pathParam the path param
	 */
	public Resource(Resource parent, PathParam<?> pathParam)
	{
		this(parent, null, Check.notNull(pathParam, "pathParam"));
	}
	
	
	/**
	 * Creates a new resource.
	 * Either the segment or the path param has to be provided
	 * @param parent the parent resource
	 * @param segment the segment  
	 * @param pathParam the path param
	 */
	private Resource(Resource parent, String segment, PathParam<?> pathParam)
	{
		if ((segment == null) == (pathParam == null))
			throw new IllegalArgumentException("a segment or path param must be provided");

		parent_ 		= Check.notNull(parent, "parent");
		tree_			= parent.tree_;
		
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
			
			Resource p  = parent;
			while(p != null)
			{
				if (p.pathParam_ == pathParam)
					throw new IllegalArgumentException("the path parameter '" + pathParam + "' was already used in parent resource " + p);
				p = p.parent_;
			}
		}
		
		parent.addChild(this);
	}
	
	
	private synchronized void initTypeProvider(boolean recursive)
	{
		typeProvider_ = ControllerTypeProvider.create(this);
		
		if (recursive)
		{
			for (Resource child : children_)
				child.initTypeProvider(recursive);
		}
	}
	
	
	private synchronized void addChild(Resource resource)
	{
		Resource[] children = ArrayUtil.addLast(children_, resource);
		Arrays.sort(children, COMPARATOR);
		children_ = children;
	}
	
	
	/**
	 * Returns the resource tree which exposes properties
	 * shared by all resources belonging to the tree.
	 */
	public Tree getTree()
	{
		return tree_;
	}
			
	
	/**
	 * Returns if this resource is the root resource.
	 */
	public boolean isRoot()
	{
		return parent_ == null;
	}

	
	/**
	 * Returns the root resource.
	 */
	public Resource getRoot()
	{
		Resource resource = this;
		while(resource.parent_ != null)
			resource = resource.parent_;
		return resource;
	}

	
	/**
	 * Returns the parent resource or null if this is the root resource
	 */
	public Resource getParent()
	{
		return parent_;
	}
	
	
	/**
	 * Returns the segment by which this resource extends
	 * the parent resource. Returns null if the resource extends 
	 * by a path-param. Returns "" if the resource is the root.
	 */
	public String getSegment()
	{
		return segment_;
	}
	

	/**
	 * Returns the PathParam by which this resource extends the parent resource
	 * Returns null, if the resource is the root or extends by a segment.
	 */
	public PathParam<?> getPathParam()
	{
		return pathParam_;
	}
	
	
	/**
	 * Returns the Route from the application root to this resource.
	 */
	public Route getRoute()
	{
		return route_;
	}

	
	/**
	 * Returns the controller signature. It describes the controller class
	 * which handles requests to this resource. See {@link ControllerSignature}
	 * for more info.
	 * @return the signature. If the resource is not a associated with
	 * 		a controller, null is returned.
	 */
	public ControllerSignature getControllerSignature()
	{
		return ctrlSignature_;
	}
	
	
	/**
	 * Sets the controller signature of the resource.
	 */
	public void setControllerSignature(ControllerSignature signature)
	{
		tree_.mapResource(this, ctrlSignature_, signature);
		ctrlSignature_ = signature;
		initTypeProvider(false);
	}

	
	/**
	 * Returns the ContollerType of the Controller associated with the resource.
	 * If the Resource does not have a Controller, null is returned. 
	 * @throws IllegalStateException thrown if the resource has a controller,
	 * 		but the resource tree to which this Resource belongs is 
	 * 		not connected to a ControllerService
	 */
	public ControllerType getControllerType() throws IllegalStateException
	{
		return typeProvider_.getControllerType(this);
	}
	
	
	/**
	 * Returns the number of resources in the subtree starting with this Resource.
	 */
	public int size()
	{
		int count = 1;
		for (int i=getChildCount() - 1; i>=0; i--)
			count += getChild(i).size();
		return count;
	}
	
	
	/**
	 * Returns the number of child resources.
	 */
	public int getChildCount()
	{
		return children_.length;
	}

	
	/**
	 * Returns the i-th child resource.
	 */
	public Resource getChild(int i)
	{
		return children_[i];
	}


	/**
	 * Returns a the children 
	 */
	public Resource[] getChildren()
	{
		return children_.clone();
	}

	
	/**
	 * Finds the descendant resource of this resource which matches
	 * the path. 
	 * @param path a path string 
	 * @return MatchResult stores the result of the match operation.
	 */
	public Match match(String path)
	{
		Resource resource 		= this;
		PathScanner scanner 	= new PathScanner(path);
		Mark mark				= scanner.mark();
		Map<PathParam<?>,Object> pathParams = new LinkedHashMap<>();
		boolean completeMatch;
		
		while (true)
		{
			// we continue as long as we can match a child
			// matching is possible even if scanner.hasMore() returns false
			// because of PathParams which return a non-null value even if reading no segment
			// (e.g. OptionalPathParam, MultiSegmentPathParam)
			Resource child = resource.matchChild(scanner, mark, pathParams);
			if (child == null)
			{
				completeMatch = !scanner.hasMore();
				break;
			}
			resource = child;
		}
		
		return new Match(resource, completeMatch, pathParams);
	}
	
	
	/**
	 * Returns the first child which matches segment given by the PathScanner.
	 * @return If not null, a matching child has been found. The PathScanner position
	 * 		has been moved past the matched segments. If path params have been recognized
	 * 		they were added to param map. Else null is returned.
	 */
	private Resource matchChild(PathScanner scanner, Mark mark, Map<PathParam<?>, Object> pathParams)
	{
		Resource[] children = children_;
		for (Resource child : children)
		{
			if (child.matches(scanner, mark, pathParams))
				return child;
		}
		return null;
	} 

	
	/**
	 * Returns if the resource matches segments given by the path scanner.
	 * @return If true, the resource matched one or more segments. The PathScanner position
	 * 		has been moved past those segments. If path params have been recognized
	 * 		they were added to param map.
	 */
 	private boolean matches(PathScanner scanner, Mark mark, Map<PathParam<?>, Object> pathParams)
	{
 		if (segment_ != null)
 		{
 			if (scanner.matchSegment(segment_))
 			{
 				scanner.next();
 				return true;
 			}
 		}
 		else
 		{
 			mark.update();
 			Object paramValue = pathParam_.parse(scanner);
 			if (paramValue != null)
 			{
 				pathParams.put(pathParam_, paramValue);
 				return true;
 			}
 			else
 				mark.revert();
 		}
		return false;
	}

 	
 	/**
	 * Recursively go trough the resource tree and instantiate the controller classes.
	 * This allows for a test during application startup (in production mode) 
	 * if the resource tree has valid controller classes.   
	 */
	public void touchControllerClasses() throws ClassNotFoundException
	{
		if (ctrlSignature_ != null)
			Class.forName(ctrlSignature_.getClassName());
		for (int i=0; i<getChildCount(); i++)
			getChild(i).touchControllerClasses();
	}
	

	/**
	 * Prints the resource tree starting with this resource.
	 */
	public void print(PrintStream out)
	{
		print(new PrintWriter(out, true));
	}
	
	
	/**
	 * Prints the resource tree starting with this resource.
	 */
	public void print(PrintWriter out)
	{
		String s = toString(); 
		out.print(s);
		for (int i=s.length(); i<30; i++)
			out.print(" ");
		if (ctrlSignature_ != null)
		{
			out.print(" c=");
			out.print(ctrlSignature_);
			out.print(",");
		}
		out.println();
		
		for (int i=0; i<getChildCount(); i++)
			getChild(i).print(out);
	}
	
	
	/**
	 * Returns a depth-first iterator for the resource tree
	 * starting with this resource.
	 */
	@Override public Iterator<Resource> iterator()
	{
		return new It(this);
	}
	
	
	/**
	 * A depth-first iterator of the resource tree.
	 */
	private static class It implements Iterator<Resource>
	{
		public It(Resource root)
		{
			next_ = root;
		}
		
		
		@Override public boolean hasNext()
		{
			return next_ != null;
		}
		
		
		/**
		 * Advances to the next resource.
		 * @return returns false if the are no more resources to enumerate
		 */
		@Override public Resource next()
		{
			if (next_ == null)
				throw new NoSuchElementException();
			
			Resource result = next_;
			
			int nextChild = 0;
			while(next_ != null)
			{
				if (nextChild < next_.getChildCount())
				{
					next_ = next_.getChild(nextChild);
					childIndexStack_.push(Integer.valueOf(++nextChild));
					break;
				}
				if (childIndexStack_.isEmpty())
				{
					next_ = null;
					break; 
				}
				nextChild = childIndexStack_.pop().intValue();
				next_ = next_.getParent();
			}
			
			return result;
		}
		
		
		@Override public void remove()
		{
			throw new UnsupportedOperationException();
		}
		
		
		private Resource next_;
		private Stack<Integer> childIndexStack_ = new Stack<>();
	}


	/**
	 * MatchResult stores the result of a {@link Resource#match(String)} operation.
	 */
	public static class Match
	{
		public Match(Resource resource, boolean completeMatch, Map<PathParam<?>,Object> pathParams)
		{
			this.resource		= resource;
			this.completeMatch	= completeMatch;
			this.pathParams 	= pathParams;
		}
		
		
		/**
		 * Specifies if the resource completely matches the request path (true),
		 * or is the best partial match (false)
		 */
		public final boolean completeMatch;
		
		/**
		 * The matched resource.  
		 */
		public final Resource resource;

		/**
		 * The path parameters collected during the match operation.
		 */
		public final Map<PathParam<?>,Object> pathParams;
	}
	
	
	@SuppressWarnings("serial")
	private static class ResComparator implements Comparator<Resource>, Serializable
	{
		@Override public int compare(Resource o1, Resource o2)
		{
			if (o1.segment_ != null)
				return o2.segment_ == null ? -1 : o1.segment_.compareTo(o2.segment_);
			else
				return o2.pathParam_ == null ? 1 : o1.pathParam_.getName().compareTo(o2.pathParam_.getName()); 
		}
	}
	

	/**
	 * Tree stores properties shared by all resources
	 * belonging to the same tree.
	 */
	public static class Tree
	{
		private Tree(Resource root)
		{
			root_ = root;
		}
				
				
		/**
		 * Sets the application path for all resoures belonging to
		 * this resource tree.
		 * The path is automatically preprended to a {@link Url}
		 * when the Url is constructed from a Resource.
		 * When the application is initialized it will automatically initialize
		 * the path on it's own resource tree.
		 * @see Application#getPath()
		 * @see #getAppPath()
		 * @param appPath the app path
		 */
		public void setAppPath(Path appPath)
		{
			appPath_ = Check.notNull(appPath, "appPath"); 
		}
		

		/**
		 * Returns the path of the application to which this resource belongs.
		 * The path is automatically preprended to a {@link Url}
		 * when the Url is constructed from a Resource.
		 * Returns the root path if the resource is not attached to an application yet.
		 */
		public Path getAppPath()
		{
			return appPath_;
		}

		
		/**
		 * Makes the controller service available for all resources belonging to
		 * this resource tree.
		 */
		public void setControllerService(ControllerService service)
		{
			if (controllerService_ != service)
			{
				controllerService_ = service;
				root_.initTypeProvider(true);
			}
		}

		
		/**
		 * Returns the ControllerService associated with the resource tree.
		 * @see #setControllerService(ControllerService)
		 */
		public ControllerService getControllerService()
		{
			return controllerService_;
		}

		
		/**
		 * Sets the default extension used for all resources belonging to
		 * this resource tree
		 * The default extension is automatically added to a {@link Url}
		 * when the Url is constructed from a Resource.<br>
		 * If a default extension is defined in the application config, it will
		 * be automatically set on the applications resource tree during application setup.
		 * @param extension the extension or null if no default extension should be used.
		 * @see AppConfig#setDefaultResExtension(String)
		 * @see Url#Url(ResponseProvider, Resource) 
		 */
		public void setDefaultExtension(String extension)
		{
			// we use a static method to express that it affects the whole tree
			defaultExtension_ =  IoUtil.normExtension(extension); 
		}
		

		/**
		 * Returns the default extension which should be appended to 
		 * Urls built for resource of this resource tree.
		 * When a Url is built using {@link Url#Url(ResponseProvider, Resource)}.
		 * the extension is automatically appended to the Url.
		 * @return the extension (without a leading dot), or null if
		 * 		no extension should be appended to the url.
		 */
		public String getDefaultExtension()
		{
			return defaultExtension_;
		}

		
		/**
		 * Returns the resource to which the controller with the given 
		 * signature is mapped.
		 */
		public Resource getResource(ControllerSignature controllerSignature)
		{
			return sig2resource_.get(controllerSignature);
		}
		
		
		private void mapResource(Resource resource, ControllerSignature oldSignature, ControllerSignature newSignature)
		{
			if (oldSignature != null)
				sig2resource_.remove(oldSignature);
			if (newSignature != null)
				sig2resource_.put(newSignature, resource);
		}

		
		private final Resource root_;
		private Path appPath_ = Path.ROOT;
		private String defaultExtension_;
		private ControllerService controllerService_;
		private ConcurrentHashMap<ControllerSignature,Resource> sig2resource_ = new ConcurrentHashMap<>();
	}

	
	/**
	 * Returns a route representation of this resource.
	 */
	@Override public String toString()
	{
		return route_.toString();
	}
	
	
	private final Tree tree_;
	private final Resource parent_;
	private final String segment_;
	private final PathParam<?> pathParam_;
	private final Route route_;
	private ControllerSignature ctrlSignature_;
	private ControllerTypeProvider typeProvider_ = ControllerTypeProvider.EMPTY;
	private Resource[] children_ = EMPTY;
	private static Resource[] EMPTY = new Resource[0];
	private static ResComparator COMPARATOR = new ResComparator(); 
}
