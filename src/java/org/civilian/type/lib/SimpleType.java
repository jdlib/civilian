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
package org.civilian.type.lib;


import org.civilian.type.Type;
import org.civilian.type.TypeSerializer;


/**
 * A partial Type implementation with useful defaults for simple types.
 */
public abstract class SimpleType<T> implements Type<T>
{
	/**
	 * Returns true.
	 */
	@Override public boolean isSimpleType()
	{
		return true;
	}

	
	@Override public abstract Class<T> getJavaType();
	

	/**
	 * Returns null.
	 */
	@Override public Class<T> getJavaPrimitiveType()
	{
		return null;
	}
	
	
	/**
	 * Calls {@link #format(TypeSerializer, Object, Object)} with a null style parameter.
	 */
	@Override public String format(TypeSerializer serializer, T value)
	{
		return format(serializer, value, null);
	}
}
