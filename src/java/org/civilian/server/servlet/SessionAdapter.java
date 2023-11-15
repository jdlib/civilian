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
package org.civilian.server.servlet;


import java.util.Iterator;
import javax.servlet.http.HttpSession;
import org.civilian.request.Session;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Iterators;


/**
 * Presents a HttpSession as Session.
 */
public class SessionAdapter implements Session
{
	public SessionAdapter(HttpSession session)
	{
		session_ = Check.notNull(session, "session");
	}
	
	
	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public long getCreationTime()
	{
		return session_.getCreationTime();
	}
	

	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public String getId()
	{
		return session_.getId();
	}

	
	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public long getLastAccessedTime()
	{
		return session_.getLastAccessedTime();
	}


	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public void setMaxInactiveInterval(int interval)
	{
		session_.setMaxInactiveInterval(interval);
	}

	
	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public int getMaxInactiveInterval()
	{
		return session_.getMaxInactiveInterval();
	}
	

	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public Object getAttribute(String name)
	{
		return session_.getAttribute(name);
	}

	
	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public Iterator<String> getAttributeNames()
	{
		return Iterators.asIterator(session_.getAttributeNames());
	}
	

	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public void setAttribute(String name, Object value)
	{
		session_.setAttribute(name, value);
	}
	

	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public void removeAttribute(String name)
	{
		session_.removeAttribute(name);
	}

	
	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public void invalidate()
	{
		session_.invalidate();
	}


	/**
	 * Forwards to the HttpSession. 
	 */
	@Override public boolean isNew()
	{
		return session_.isNew();
	}

	
	/**
	 * Returns the HttpSession if you pass in HttpSession.class. 
	 */
	@Override public <T> T unwrap(Class<T> implClass)
	{
		return ClassUtil.unwrap(session_, implClass);
	}
	
	
	private HttpSession session_;
}
