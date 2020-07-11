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
package org.civilian.internal.pathparam;


import org.civilian.resource.PathParam;
import org.civilian.type.Type;
import org.civilian.type.fn.StandardSerializer;
import org.civilian.util.Check;


/**
 * A PathParam base class, which uses a Type object.
 */
public abstract class TypeBasedPathParam<T> extends PathParam<T>
{
	/**
	 * Creates a new TypeBasedPathParam. 
	 * @param name the name of the path parameter
	 * @param type the type of the path parameter values
	 */
	public TypeBasedPathParam(String name, Type<T> type)
	{
		super(name);
		type_ = Check.notNull(type, "type");
	}


	/**
	 * Returns the type of path parameter values.
	 */
	@Override public Class<T> getType()
	{
		return type_.getJavaType();
	}

	
	protected T parse(String s)
	{
		try
		{
			return s != null ? StandardSerializer.INSTANCE.parse(type_, s) : null;
		}
		catch(Exception e)
		{
			return null;
		}
	}


	protected String format(T value)
	{
		return StandardSerializer.INSTANCE.format(type_, value);
	}


	protected final Type<T> type_;
}
