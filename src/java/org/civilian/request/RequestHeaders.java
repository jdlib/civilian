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
package org.civilian.request;


import java.util.Iterator;
import org.civilian.type.Type;
import org.civilian.util.Value;


/**
 * RequestHeaders provides access to the headers of a request.
 * @see Request#getHeaders()
 */
public interface RequestHeaders extends Iterable<String>
{
	/**
	 * Returns if the request contains a header with that name. 
	 * @param name the case insensitive header name
	 */
	public boolean contains(String name);
	
	
	/**
	 * Returns if the value of the header equals the given value.
	 * @param name the case insensitive header name
	 * @param value the comparison value
	 */
	public boolean is(String name, String value);

	
	/**
	 * Returns the value of a header, or null if the request does not 
	 * have such a header. 
	 * @param name the case insensitive header name
	 */
	public String get(String name);
	
	
	/**
	 * Returns the value of a header as Value object.
	 * If the parameter does not exist, the Value object has no value.
	 * If the conversion of the parameter to the requested type fails, the 
	 * Value object contains the error.
	 * @param name the case insensitive header name
	 * @param type the header type
	 */
	public <T> Value<T> get(String name, Type<T> type);


	/**
	 * Returns all the values of a request header as a String array.
	 * @param name the case insensitive header name
	 * @return the array. It has length 0 if the request does not contain headers with that name
	 */
	public String[] getAll(String name);

	
	/**
	 * Returns the value of a date header.
	 * @param name the case insensitive header name
	 * @return the number of milliseconds since January 1, 1970 GMT, or -1 if the 
	 * 		request does not contain such a header
	 * @throws IllegalArgumentException if the header cannot be converted to a date
	 */
	public long getDate(String name) throws IllegalArgumentException; 


	/**
	 * Returns the value of int header.
	 * @param name the case insensitive header name
	 * @return the header value converted to an integer or -1 if the request does not contain such a header
	 * @throws IllegalArgumentException if the header cannot be converted to a date
	 */
	public int getInt(String name) throws IllegalArgumentException;
	
	
	/**
	 * Returns an iterator for the header names contained in the request. 
	 */
	@Override public Iterator<String> iterator(); 
	
	
	/**
	 * Sets the header to the given values. 
	 */
	public void set(String name, String value);
	
	
	/**
	 * Adds a header. 
	 */
	public void add(String name, String value);
}
