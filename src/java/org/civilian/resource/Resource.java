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
import org.civilian.resource.pathparam.PathParam;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.PathScanner;
import org.civilian.util.PathScanner.Mark;


/**
 * Resource represents a resource of a web application, addressable by an URL.
 * Controller classes are used to handle requests for resource and dynamically generate a response. 
 * There are several options how to construct the resource tree of an application:
 * <ol>
 * <li>Scan the application for controller classes at startup.
 * <li>Scan the application at build time and generate a special class which constructs the resource tree
 * <li>Build the resource by hand (not recommended);
 * </ol> 
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
	
	
	private synchronized void addChild(Resource resource)
	{
		Resource[] children = ArrayUtil.addLast(children_, resource);
		Arrays.sort(children, COMPARATOR);
		children_ = children;
	}
	
	
	/**
	 * @return if this resource is the root resource.
	 */
	public boolean isRoot()
	{
		return parent_ == null;
	}

	
	/**
	 * @return the root resource.
	 */
	public Resource getRoot()
	{
		Resource resource = this;
		while(resource.parent_ != null)
			resource = resource.parent_;
		return resource;
	}

	
	/**
	 * @return the parent resource or null if this is the root resource
	 */
	public Resource getParent()
	{
		return parent_;
	}
	
	
	/**
	 * Returns the segment by which this resource extends
	 * the parent resource. Returns null if the resource extends 
	 * by a path-param. Returns "" if the resource is the root.
	 * @return the segment
	 */
	public String getSegment()
	{
		return segment_;
	}
	

	/**
	 * Returns the PathParam by which this resource extends the parent resource
	 * @return the PathParam or null, if the resource is the root or extends by a segment.
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
	 * @return the number of resources in the subtree starting with this Resource.
	 */
	public int size()
	{
		int count = 1;
		for (int i=getChildCount() - 1; i>=0; i--)
			count += getChild(i).size();
		return count;
	}
	
	
	/**
	 * @return the number of child resources.
	 */
	public int getChildCount()
	{
		return children_.length;
	}

	
	/**
	 * @param i the child index
	 * @return the i-th child resource.
	 */
	public Resource getChild(int i)
	{
		return children_[i];
	}


	/**
	 * @return the children 
	 */
	public Resource[] getChildren()
	{
		return children_.clone();
	}
	
	
	/**
	 * Associates the resource with some data object.
	 * @param data the data object
	 * @return this
	 */
	public Resource setData(Object data)
	{
		data_ = data;
		return this;
	}

	
	/**
	 * Return the data previously set with {@link #setData(Object)}.
	 * @return the data or null
	 */
	public Object getData()
	{
		return data_;
	}

	
	/**
	 * Return the data previously set with {@link #setData(Object)}, casted to type T if not null.
	 * @param type class
	 * @param <T> the class type
	 * @return the casted data or null
	 */
	public <T> T getData(Class<T> type)
	{
		Object data = getData(); // forward to direct accessor to help build mocks
		return data != null ? Check.isA(data, type) : null;
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
	 * Prints the resource tree starting with this resource.
	 * @param out a PrintStream
	 */
	public void print(PrintStream out)
	{
		print(new PrintWriter(out, true));
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
			out.print(" ");
		if (data_ != null)
		{
			out.print(" d=");
			out.print(data_);
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
	 * Returns a route representation of this resource.
	 */
	@Override public String toString()
	{
		return route_.toString();
	}
	
	
	private final Resource parent_;
	private final String segment_;
	private final PathParam<?> pathParam_;
	private final Route route_;
	private Object data_;
	private Resource[] children_ = EMPTY;
	private static Resource[] EMPTY = new Resource[0];
	private static ResComparator COMPARATOR = new ResComparator(); 
}
