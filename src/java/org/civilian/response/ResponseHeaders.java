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
package org.civilian.response;


import java.util.Iterator;
import org.civilian.Response;


/**
 * ResponseHeaders allow to set and read response headers.
 * Given a response object, you can access the ResponseHeaders by {@link Response#getHeaders()} 
 * In a Servlet environment a response header equals a HTTP response header.
 */
public interface ResponseHeaders extends Iterable<String>
{
	/**
	 * Adds a header to the response.
	 * @param name the header name
	 * @param value the header value
	 * @return this 
	 */
	public ResponseHeaders add(String name, String value); 


	/**
	 * Adds a date header to the response.
	 * @param name the header name
	 * @param value the date specified in milliseconds since epoch 
	 * @return this 
	 */
	public ResponseHeaders addDate(String name, long value); 


	/**
	 * Adds a integer header to the response.
	 * @param name the header name
	 * @param value the value 
	 * @return this 
	 */
	public ResponseHeaders addInt(String name, int value); 


	/**
	 * Tests if a specific header was added.
	 */
	public boolean contains(String name);
	
	
	/**
	 * Returns an iterator of the header names.
	 */
	@Override public Iterator<String> iterator();
	
	
	/**
	 * Returns all the values of a response header as a String array.
	 * @return the array. It has length 0 if the request does not contain headers with that name
	 */
	public String[] getAll(String name); 
	
	
	/**
	 * Returns the first value of a specific header.
	 */
	public String get(String name); 


	/**
	 * Sets the values of a header. This will override any old value,
	 * if the header was already set,
	 * @param name the header name
	 * @param values the header values
	 * @return this 
	 */
	public ResponseHeaders set(String name, String... values); 


	/**
	 * Sets the value of a date header. This will override any old value,
	 * if the header was already set,
	 * @param name the header name
	 * @param value the date specified in milliseconds since the epoch 
	 * @return this 
	 */
	public ResponseHeaders setDate(String name, long value); 


	/**
	 * Sets the value of a int header. This will override any old value,
	 * if the header was already set,
	 * @param name the header name
	 * @param value the value 
	 * @return this 
	 */
	public ResponseHeaders setInt(String name, int value); 
}
