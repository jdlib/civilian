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


import java.util.Enumeration;


/**
 * Represents a session of a user. Wraps HttpSEssion in a servlet environment. 
 */
public interface Session
{
	/**
	 * Returns when the session was created
	 * @return the time measured in milliseconds since midnight January 1, 1970 GMT.
	 */
	public long getCreationTime();
	

	/**
	 * Returns the unique session id.
	 */
	public String getId();
	

	/**
	 * Returns the last time the client sent a request associated with this session
	 * @return the time as number of milliseconds since midnight January 1, 1970 GMT.
	 */
	public long getLastAccessedTime();
	
	
	/**
	 * Sets the time between client requests before the servlet container will invalidate the session.
	 * @param seconds the time in seconds
	 */
	public void setMaxInactiveInterval(int seconds);
	
	
	/**
	 * Returns the time in seconds between client requests before the servlet container will invalidate the session.
	 */
	public int getMaxInactiveInterval();
	

	/**
	 * Returns a session attribute.
	 * @param name the attribute name
	 * @return the attribute value or null if the name is not bound to a value.
	 */
	public Object getAttribute(String name);
	

	/**
	 * Returns a session attribute, stored under its class name.
	 * If no attribute is bound, a new object is created.
	 * @param c the attribute class
	 */
	public default <T> T getCreateAttr(Class<T> c) throws InstantiationException, IllegalAccessException
	{
		return getCreateAttr(c, c.getName());
	}

	
	/**
	 * Returns a session attribute
	 * If no attribute is bound, a new object is created.
	 * @param c the attribute class
	 * @param name the attribute name
	 */
	public default <T> T getCreateAttr(Class<T> c, String name) throws InstantiationException, IllegalAccessException
	{
		T value = c.cast(getAttribute(name));
		if (value == null)
		{
			value = c.newInstance();
			setAttribute(name, value);
		}
		return value;
	}

	
	/**
	 * Returns an enumeration of all names of attributes bound to the session. 
	 */
	public Enumeration<String> getAttributeNames();
	
	
	/**
	 * Sets a session attribute. 
	 */
	public void setAttribute(String name, Object value);
	
	
	/**
	 * Removes an attribute. 
	 */
	public void removeAttribute(String name);
	
	
	/**
	 * Invalidates the session.
	 * Any objects bound to it is removed. 
	 */
	public void invalidate();
	
	
	/**
	 * Returns if this session is new and the client does not yet know about it.
	 */
	public boolean isNew();

	
	/**
	 * Returns the underlying implementation of the sessopm which has the given class
	 * or null, if the implementation has a different class.
	 * In a servlet environment Session wraps a HttpSession.
	 */
	public <T> T unwrap(Class<T> implClass);
}
