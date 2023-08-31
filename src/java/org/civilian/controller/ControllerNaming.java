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


import org.civilian.util.StringUtil;


/**
 * ControllerNaming implements  naming conventions for
 * controller classes. It is used during a resource scan:
 * When the classpath is searched for potential controller
 * classes it provides a naming convention to identify for controller classes.
 * (see {@link #isControllerClass(String)}).
 * When packages and controller classes are mapped to resources
 * it is also used to convert package and class names into resource
 * segments.    
 */
public class ControllerNaming
{
	/**
	 * By default each controller implementation class is required to have the suffix
	 * "Controller".
	 */
	public static final String DEFAULT_CONTROLLER_SUFFIX = "Controller";
	

	/**
	 * By default controller segments named "index" are ignored.
	 */
	public static final String DEFAULT_INDEX_SEGMENT = "index";

	
	/**
	 * Creates a new ControllerNaming, with controller suffix {@value #DEFAULT_CONTROLLER_SUFFIX}
	 * and index segment {@link #DEFAULT_INDEX_SEGMENT}. 
	 */
	public ControllerNaming()
	{
		this(DEFAULT_CONTROLLER_SUFFIX, DEFAULT_INDEX_SEGMENT);
	}
	
	
	/**
	 * Creates a new ControllerNaming. 
	 * @param controllerSuffix the suffix of controller classes
	 * @param indexSegment the name of the index segment.
	 */
	public ControllerNaming(String controllerSuffix, String indexSegment)
	{
		controllerSuffix_ = controllerSuffix;
		indexSegment_	  = indexSegment;
	}

	
	/**
	 * Tests if a class is a potential controller class.
	 * The default implementation returns true if the name ends with "Controller".
	 * This naming convention is used to limit the potential controller
	 * classes which are introspected in a second step.
	 * {@link #DEFAULT_CONTROLLER_SUFFIX "Controller"}
	 * @param simpleClassName the simple name of a class
	 * @return is controller class
	 */
	public boolean isControllerClass(String simpleClassName)
	{
		return simpleClassName.endsWith(controllerSuffix_);
	}
	

	/**
	 * Converts a package part into a resource segment.
	 * The default implementation converts the part to lower case.
	 * @param packagePart the packagePart
	 * @return the segment 
	 */
	public String packagePart2Segment(String packagePart)
	{
		return packagePart.toLowerCase();
	}

	
	/**
	 * Converts the simple name of a controller class into a resource segment.
	 * If null is returned then the controller class should be mapped
	 * to the resource corresponding to its package.
	 * The default implementation cuts the "Controller"-suffix and
	 * converts the name to lower case. If the obtained segment equals
	 * "index", it is also set to null, mapping the controller class
	 * to its package.
	 * @param simpleClassName a simple class name
	 * @return the segment
	 */
	public String className2Segment(String simpleClassName)
	{
		String segment = StringUtil.cutRight(simpleClassName, DEFAULT_CONTROLLER_SUFFIX).toLowerCase();
		if (segment.equals(indexSegment_))
			segment = null;
		return segment;
	}
	
	
	private final String controllerSuffix_;
	private final String indexSegment_;
}


