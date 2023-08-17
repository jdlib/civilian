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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;

import org.civilian.application.AppConfig;
import org.civilian.application.Application;


/**
 * A ContentSerializer is a service to read and write the content
 * of a request or response. You configure ContentSerializers
 * during application setup.
 * @see AppConfig#getContentSerializers()
 * @see Application#init(AppConfig) 
 */
public abstract class ContentSerializer
{
	/**
	 * Reads the content from the given reader and constructs a object of
	 * a certain type.
	 * @param type the type of the constructed object
	 * @param reader a reader 
	 */
	public <T> T read(Class<T> type, Reader reader) throws Exception
	{
		return read(type, null, reader);
	}

	
	/**
	 * Reads the content from the given reader and constructs a object of
	 * a certain type.
	 * @param type the type of the constructed object
	 * @param genericType the generic type of the constructed object or null if not known
	 * @param reader a reader 
	 */
	public abstract <T> T read(Class<T> type, Type genericType, Reader reader) throws Exception; 

	
	/**
	 * Constructs a object of a certain type, given a string representation.
	 * @param type the type of the constructed object
	 * @param s a string 
	 */
	public <T> T read(Class<T> type, String s) throws Exception
	{
		return read(type, null, s); 
	}

	
	/**
	 * Constructs a object of a certain type, given a string representation.
	 * @param type the type of the constructed object
	 * @param genericType the generic type of the constructed object
	 * @param s a string 
	 */
	public <T> T read(Class<T> type, Type genericType, String s) throws Exception
	{
		return read(type, genericType, new StringReader(s != null ? s : "")); 
	}

	
	/**
	 * Writes the value to the writer. 
	 */
	public abstract void write(Object value, Writer writer) throws Exception; 


	/**
	 * Converts the value to a string. 
	 */
	public String write(Object value) throws Exception
	{
		StringWriter s = new StringWriter();
		write(value, s);
		return s.toString();
	}

	
	/**
	 * Asks the ContentSerializer to build a diagnostic message for an exception.
	 * The exception was previously thrown by a call to a read method.
	 * The message should be diagnostic and should be allowed to be presented 
	 * to a client (e.g. in a 400 bad request response). 
	 * @param e an exception thrown by the ContentSerializer.
	 * @return a diagnostic message or null if the exception was not due to
	 * 		a syntactic error but any other runtime error (like IOExceptions).
	 * 		The default implementation returns null.
	 */
	public String describeReadError(Exception e)
	{
		return null;
	}
	
	
	/**
	 * Returns the internal implementation object which has the given class.
	 */
	public abstract <T> T unwrap(Class<T> implClass);
	
	
	@Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
