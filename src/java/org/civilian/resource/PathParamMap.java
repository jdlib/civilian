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


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;
import org.civilian.Application;


/**
 * PathParamMap holds the {@link PathParam PathParams} of an {@link Application}.
 * @see ControllerConfig#getPathParams()
 * @see Application#getControllerConfig()
 */
public class PathParamMap implements Iterable<PathParam<?>>
{
	public static final PathParamMap EMPTY = new PathParamMap().seal();
	
	
	public PathParamMap()
	{
	}
	
	
	public PathParamMap(Class<?> constantsClass)
	{
		constantsClass_ = constantsClass;
	}
	
	
	@Override public Iterator<PathParam<?>> iterator()
	{
		return pathParams_.values().iterator();
	}


	public PathParam<?> get(String name)
	{
		return pathParams_.get(name);
	}
	
	
	public <T,P extends PathParam<T>> P add(P param)
	{
		if (sealed_)
			throw new IllegalStateException("map sealed");
		pathParams_.put(param.getName(), param);
		return param;
	}
	
	
	public <T,P extends PathParam<T>> P addAndSeal(P param)
	{
		add(param);
		seal();
		return param;
	}

	
	/**
	 * Seals the map: You can't add further parameters to it.
	 */
	public PathParamMap seal()
	{
		if (!sealed_)
		{
			sealed_ = true;
			pathParams_ = Collections.unmodifiableMap(pathParams_);
		}
		return this;
	}
	
	
	/**
	 * Returns if the map is sealed. 
	 */
	public boolean isSealed()
	{
		return sealed_;
	}
	
	
	/**
	 * Returns the class which defines for constants for the path parameters
	 * in this map.
	 */
	public Class<?> getConstantsClass()
	{
		return constantsClass_;
	}
	

	/**
	 * Returns the java field in the maps constant class which
	 * holds the given path param.
	 */
	public Field getConstantField(PathParam<?> pathParam)
	{
		if (constantsClass_ != null)
		{
			for (Field field : constantsClass_.getDeclaredFields())
			{
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) && 
					Modifier.isPublic(mod) &&
					PathParam.class.isAssignableFrom(field.getType()))
				{
					try
					{
						if (pathParam == field.get(null))
							return field;
					}
					catch (Exception e)
					{
						throw new IllegalStateException("cannot access field " + field, e);
					}
				}
			}
		}
		return null;
	}
	

	public String getConstant(PathParam<?> pathParam)
	{
		Field field = getConstantField(pathParam);
		if (field != null)
			return constantsClass_.getName() + '.' + field.getName();
		throw new IllegalArgumentException("no constant for path parameter '" + pathParam + "' defined in " + constantsClass_);
	}
	

	/**
	 * Returns a copy of the map.
	 */
	public Map<String,PathParam<?>> toMap()
	{
		return new HashMap<>(pathParams_); 
	}
		
	
	private boolean sealed_;
	private Class<?> constantsClass_;
	private Map<String,PathParam<?>> pathParams_ = new HashMap<>(); 
}
