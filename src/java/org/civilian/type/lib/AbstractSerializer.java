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


import org.civilian.type.TypeSerializer;


/**
 * A partial TypeSerializer implementation with useful defaults.
 */
public abstract class AbstractSerializer implements TypeSerializer
{
	@Override public String formatBoolean(Boolean value, Object style)
	{
		return value != null ? formatBoolean(value.booleanValue(), style) : formatNull();
	}
	 
	
	@Override public String formatByte(Byte value, Object style)
	{
		return value != null ? formatByte(value.byteValue(), style) : formatNull();
	}
	
	
	@Override public String formatCharacter(Character value, Object style)
	{
		return value != null ? formatChar(value.charValue(), style) : formatNull();
	}
	
	
	@Override public String formatDouble(Double value, Object style)
	{
		return value != null ? formatDouble(value.doubleValue(), style) : formatNull();
	}
	
	
	@Override public String formatFloat(Float value, Object style)
	{
		return value != null ? formatFloat(value.floatValue(), style) : formatNull();
	}
	
	
	@Override public String formatInteger(Integer value, Object style)
	{
		return value != null ? formatInt(value.intValue(), style) : formatNull();
	}
	
	
	@Override public String formatLong(Long value, Object style)
	{
		return value != null ? formatLong(value.longValue(), style) : formatNull();
	}

	
	@Override public String formatNull()
	{
		return "";
	}
	
	
	@Override public String formatShort(Short value, Object style)
	{
		return value != null ? formatShort(value.shortValue(), style) : formatNull();
	}

	
	protected static boolean isBlank(String s)
	{
		return s == null || s.length() == 0;
	}
}
