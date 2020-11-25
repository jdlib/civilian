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
package org.civilian.internal;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.civilian.request.RequestHeaders;
import org.civilian.response.ResponseHeaders;
import org.civilian.type.Type;
import org.civilian.type.fn.StandardSerializer;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Iterators;
import org.civilian.util.Value;


/**
 * ParamList is a helper class to build parameter or header
 * lists used in request or response implementations.  
 */
public class ParamList implements RequestHeaders, ResponseHeaders 
{
	private static final String[] EMPTY_VALUES = new String[0];
	public static final ParamList EMPTY = new ParamList(false, Collections.<String, String[]>emptyMap() /*immutable*/);

	
	public ParamList()
	{
		this(false);
	}
	

	public ParamList(boolean caseInsensitiveNames)
	{
		this(caseInsensitiveNames, null);
	}

	
	public ParamList(boolean caseInsensitiveNames, Map<String,String[]> map)
	{
		caseInsensitiveNames_ = caseInsensitiveNames;
		map_ = map;
	}

	
	public void clear()
	{
		map_ = null;
	}
	
	
	@Override public boolean contains(String name)
	{
		return map_ != null ? get(name) != null : false;
	}
	
	
	@Override public boolean is(String name, String value)
	{
		return ClassUtil.equals(get(name), value);
	}


	@Override public String get(String name)
	{
		String[] v = map_ != null ? map_.get(normName(name)) : null;
		if (v == null)
			return getNext(name);
		else if (v.length > 0)
			return v[0];
		else
			return null;
	}
	
	
	/**
	 * Allows chaining of ParamLists.
	 * The default implementation returns null.
	 */
	protected String getNext(String name)
	{
		return null;
	}

	
	/**
	 * Allows chaining of ParamLists.
	 * The default implementation returns -1L.
	 */
	protected long getDateNext(String name)
	{
		return -1L;
	}

	
	@Override public String[] getAll(String name)
	{
		String[] v = map_ != null ? map_.get(normName(name)) : null;
		if (v == null)
			v = getNextAll(name);
		return v != null ? v : EMPTY_VALUES; 
	}


	/**
	 * Allows chaining of ParamLists.
	 * The default implementation returns null.
	 */
	protected String[] getNextAll(String name)
	{
		return null;
	}
	
	
	@Override public int getInt(String name)
	{
		String s = get(name);
		if (s == null)
			return -1;
		try
		{
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("not a int value '" + s + "'", e);
		}
	}
	
	
	@Override public long getDate(String name)
	{
		if (map_ != null)
		{
			String[] s = map_.get(normName(name));
			if ((s != null) && (s.length > 0))
			{
				String v = s[0];
				try
				{
					return Long.parseLong(v);
				}
				catch(NumberFormatException e)
				{
					throw new IllegalArgumentException("not a date value '" + v + "'", e);
				}
			}
		}
		return getDateNext(name);	
	}

	
	@Override public <T> Value<T> get(String name, Type<T> type)
	{
		return new Value<>(type, get(name), StandardSerializer.INSTANCE);
	}

	
	@Override public Iterator<String> iterator()
	{
		Iterator<String> thisNames = map_ != null ? map_.keySet().iterator() : null; 
		Iterator<String> nextNames = getNextNames();
		
		if ((thisNames != null) && (nextNames != null))
			return Iterators.unique(Iterators.join(thisNames, nextNames));
		else if (nextNames != null)
			return nextNames;
		else if (thisNames != null)
			return thisNames;
		else
			return Iterators.empty();
	}

	
	/**
	 * Allows chaining of ParamLists.
	 * The default implementation returns an empty array.
	 */
	protected Iterator<String> getNextNames()
	{
		return null;
	}
	
	
	@Override public ParamList set(String name, String value)
	{
		getMap().put(normName(name), new String[] { value });
		return this;
	}


	@Override public ParamList setDate(String name, long value)
	{
		return set(name, String.valueOf(value));
	}
	
	
	@Override public ParamList setInt(String name, int value)
	{
		return set(name, String.valueOf(value));
	}

	
	public void setNull(String name)
	{
		set(name, null);
	}
	
	
	@Override public ParamList add(String name, String value)
	{
		if (value != null)
		{
			name = normName(name);
			String[] v = getAll(name);
			getMap().put(name, ArrayUtil.addLast(v, value));
		}
		return this;
	}
	
	
	@Override public ParamList addDate(String name, long value)
	{
		add(name, String.valueOf(value));
		return this;
	}
	
	
	@Override public ParamList addInt(String name, int value)
	{
		return add(name, String.valueOf(value));
	}
	

	public Map<String, String[]> getMap()
	{
		if (map_ == null)
			map_ = new HashMap<>();
		return map_;
	}
	
	
	private String normName(String name)
	{
		Check.notNull(name, "name");
		return caseInsensitiveNames_ ? name.toLowerCase() : name;
	}
	

	private boolean caseInsensitiveNames_;
	private Map<String,String[]> map_;
}
