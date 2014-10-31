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
package org.civilian.content;


import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;


/**
 * A ContentSerializer for string objects.
 */
public class TextSerializer extends ContentSerializer
{
	/**
	 * Returns the content as string.    
	 */
	@SuppressWarnings("unchecked")
	@Override public <T> T read(Class<T> type, Type genericType, Reader reader) throws Exception
	{
		Check.notNull(type, "type");
		Check.notNull(reader, "reader");

		if (type != String.class)
			throw new IllegalArgumentException("can only read strings");
		
		return (T)IoUtil.readString(reader);
	}


	/**
	 * Converts the object to a string and writes it to the writer. 
	 */
	@Override public void write(Object object, Writer writer) throws Exception
	{
		if (object != null)
			writer.write(object.toString());
	}


	/**
	 * Returns null.
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}
}
