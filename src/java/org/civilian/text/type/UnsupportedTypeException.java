/*
 * Copyright (C) 2016 Civilian Framework.
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
package org.civilian.text.type;


import org.civilian.type.Type;


@SuppressWarnings("serial")
public class UnsupportedTypeException extends RuntimeException
{
	public UnsupportedTypeException(Type<?> type)
	{
		super(type + " not supported");
		type_ = type;
	}
	
	
	public UnsupportedTypeException(String message, Type<?> type)
	{
		super(message + ": " + type + " not supported");
		type_ = type;
	}
	
	
	public UnsupportedTypeException(Object object, String message, Type<?> type)
	{
		this(object.getClass().getSimpleName() + '.' + message, type);
	}

	
	public Type<?> getType()
	{
		return type_;
	}
	
	
	private final Type<?> type_;
}
