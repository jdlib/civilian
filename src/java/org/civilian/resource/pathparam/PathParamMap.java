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
package org.civilian.resource.pathparam;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;
import org.civilian.application.Application;
import org.civilian.controller.ControllerConfig;
import org.civilian.util.Check;


/**
 * PathParamMap holds the {@link PathParam PathParams} of an {@link Application}.
 * @see ControllerConfig#getPathParams()
 * @see Application#getControllerConfig()
 */
public class PathParamMap implements Iterable<PathParam<?>>
{
	public static final PathParamMap EMPTY = new PathParamMap();
	
	
	public PathParamMap()
	{
		map_ = Map.of();
		constantsClass_ = null;
	}

	
	public PathParamMap(PathParam<?>... params)
	{
		this(null, params);
	}
	
	
	public PathParamMap(Class<?> constantsClass, PathParam<?>... params)
	{
		constantsClass_ = constantsClass;
		Map<String,PathParam<?>> map = new HashMap<>();
		for (PathParam<?> param : params)
			map.put(Check.notNull(param, "param").getName(), param);
		map_ = Collections.unmodifiableMap(map);
	}
	
	
	@Override public Iterator<PathParam<?>> iterator()
	{
		return map_.values().iterator();
	}


	public PathParam<?> get(String name)
	{
		return map_.get(name);
	}
	
	
	/**
	 * @return the class which defines for constants for the path parameters
	 * in this map.
	 */
	public Class<?> getConstantsClass()
	{
		return constantsClass_;
	}
	

	/**
	 * @param pathParam a pathParam 
	 * @return the java field in the maps constant class which
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
	 * @return a copy of the map.
	 */
	public Map<String,PathParam<?>> toMap()
	{
		return map_; 
	}
		
	
	private final Map<String,PathParam<?>> map_; 
	private final Class<?> constantsClass_;
}
