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
	 * @return the unique session id.
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
	 * @return the time in seconds between client requests before the servlet container will invalidate the session.
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
	public default <T> T getAttrOrCreate(Class<T> c) throws InstantiationException, IllegalAccessException
	{
		return getAttrOrCreate(c, c.getName());
	}

	
	/**
	 * Returns a session attribute
	 * If no attribute is bound, a new object is created.
	 * @param c the attribute class
	 * @param name the attribute name
	 * @param <T> the attribute type
	 */
	@SuppressWarnings("deprecation")
	public default <T> T getAttrOrCreate(Class<T> c, String name) throws InstantiationException, IllegalAccessException
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
	 * @return an enumeration of all names of attributes bound to the session. 
	 */
	public Enumeration<String> getAttributeNames();
	
	
	/**
	 * Sets a session attribute. 
	 * @param name a name
	 * @param value a value
	 */
	public void setAttribute(String name, Object value);
	
	
	/**
	 * Removes an attribute.
	 * @param name a name
	 */
	public void removeAttribute(String name);
	
	
	/**
	 * Invalidates the session.
	 * Any objects bound to it is removed. 
	 */
	public void invalidate();
	
	
	/**
	 * @return if this session is new and the client does not yet know about it.
	 */
	public boolean isNew();

	
	/**
	 * Returns the underlying implementation of the sessopm which has the given class
	 * or null, if the implementation has a different class.
	 * In a servlet environment Session wraps a HttpSession.
	 * @param implClass a implementation class
	 * @param <T> the implementation type
	 * @return the implementation or null
	 */
	public <T> T unwrap(Class<T> implClass);
	

	/**
	 * Session.Optional represents a Session or null.
	 */
	public static class Optional
	{
		/**
		 * Creates a new Optional.
		 * @param session the session or null
		 */
		public Optional(Session session)
		{
			session_ = session;
		}
		
		
		/**
		 * Returns a session attribute.
		 * @param name the attribute name
		 * @return the attribute value or null if the name is not bound to a value or the session is null.
		 */
		public Object getAttribute(String name)
		{
			return session_ != null ? session_.getAttribute(name) : null;
		}
		
		
		/**
		 * Removes an attribute.
		 * @param name an attribute name
		 * @return this 
		 */
		public Optional removeAttribute(String name)
		{
			if (session_ != null)
				session_.removeAttribute(name);
			return this;
		}
		
		
		/**
		 * Invalidates the session if it exists.
		 * @return did the session exists.
		 */
		public boolean invalidate()
		{
			if (session_ != null)
			{
				session_.invalidate();
				return true;
			}
			else
				return false;
		}
		
		
		/**
		 * @return if the session exists.
		 */
		public boolean isPresent()
		{
			return session_ != null;
		}
		
		
		private final Session session_;
	}
}
