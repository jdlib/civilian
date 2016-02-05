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
package org.civilian.type.fn;


import java.text.ParseException;
import org.civilian.type.Type;


/**
 * A TypeSerializer represents a certain schema of parsing and formatting
 * values from or to a string.
 */
// TODO docs
public abstract class TypeSerializer
{
	protected TypeSerializer()
	{
		this(null, null);
	}
	
	
	protected TypeSerializer(TypeFormatter formatter, TypeParser parser)
	{
		formatter_ = formatter != null ? formatter : new TypeFormatter(null, this);
		parser_	   = parser    != null ? parser    : new TypeParser(null, this);
	}

	
	public TypeFormatter getFormatter()
	{
		return formatter_;
	}
	
	
	public <T> String format(Type<T> type, T value)
	{
		return formatter_.format(type, value);
	}
	
	
	public <T> String format(Type<T> type, T value, Object hint)
	{
		return formatter_.format(type, value, hint);
	}
	
	
	public TypeParser getParser()
	{
		return parser_;
	}
	
	
	public <T> T parse(Type<T> type, String s) throws ParseException
	{
		return parser_.parse(type, s);
	}
	
	
	protected <T> T parseNull(Type<T> type) throws Exception
	{
		return null;
	}

	
	protected final TypeFormatter formatter_;
	protected final TypeParser parser_;
}
