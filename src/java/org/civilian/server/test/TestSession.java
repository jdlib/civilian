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
package org.civilian.server.test;


import java.util.Enumeration;
import java.util.HashMap;
import org.civilian.request.Session;
import org.civilian.util.Check;
import org.civilian.util.Iterators;


/**
 * TestSession is a {@link Session} implementation to be used in a test environment.
 */
public class TestSession implements Session
{
	public TestSession()
	{
		creationTime_ 	= System.currentTimeMillis();
		id_ 			= String.valueOf(creationTime_);
	}
	
	
	@Override public long getCreationTime()
	{
		return creationTime_;
	}
	

	@Override public String getId()
	{
		return id_;
	}

	
	public TestSession setId(String id)
	{
		id_ = Check.notNull(id, "id");
		return this;
	}

	
	@Override public long getLastAccessedTime()
	{
		return lastAccessedTime_;
	}
	

	@Override public void setMaxInactiveInterval(int seconds)
	{
		maxInactiveInterval_ = Check.greaterEquals(seconds, 1, "seconds");
	}

	
	@Override public int getMaxInactiveInterval()
	{
		return maxInactiveInterval_;
	}

	
	@Override public Object getAttribute(String name)
	{
		return attributes_.get(name);
	}
	

	@Override public Enumeration<String> getAttributeNames()
	{
		return Iterators.asEnumeration(attributes_.keySet().iterator());
	}
	

	@Override public void setAttribute(String name, Object value)
	{
		attributes_.put(name, value);
	}

	
	@Override public void removeAttribute(String name)
	{
		attributes_.remove(name);
	}
	

	@Override public void invalidate()
	{
		id_ = null;
	}
	

	@Override public boolean isNew()
	{
		return isNew_;
	}


	public TestSession setNew(boolean isNew)
	{
		isNew_ = isNew;
		return this;
	}


	@Override public <T> T unwrap(Class<T> implClass)
	{
		return null;
	}
	
	
	private long creationTime_;
	private long lastAccessedTime_;
	private int maxInactiveInterval_ = 30*60;
	private String id_;
	private boolean isNew_;
	private HashMap<String,Object> attributes_ = new HashMap<>();
}
