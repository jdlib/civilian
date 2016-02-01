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


import org.civilian.type.TypeLib;
import org.civilian.type.TypeSerializer;
import org.civilian.type.TypeVisitor;


/**
 * A type implementation for java.lang.Short.
 * @see TypeLib#SHORT
 */
public class ShortType extends SimpleType<Short>
{
	@Override public Class<Short> getJavaType()
	{
		return Short.class;
	}
	
	
	@Override public Class<Short> getJavaPrimitiveType()
	{
		return short.class;
	}

	
	@Override public String format(TypeSerializer serializer, Short value, Object style)
	{
		return serializer.formatShort(value, style);
	}

	
	@Override public Short parse(TypeSerializer serializer, String s) throws Exception
	{
		return serializer.parseShort(s);
	}
	
	
	@Override public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E
	{
		return visitor.visitShort(param);
	}
}
