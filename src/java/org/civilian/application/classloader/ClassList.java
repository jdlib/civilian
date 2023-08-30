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
package org.civilian.application.classloader;


import java.util.ArrayList;
import java.util.List;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.StringUtil;


/**
 * ClassList defines a list of classes, either by specifying
 * them directly, or by defining match criteria for class or
 * package names. 
 */
public class ClassList
{
	/**
	 * @return the size of the list.
	 */
	public int size()
	{
		return list_.size();
	}
	

	/**
	 * Adds a Item to the list.
	 * @param item the item
	 * @return this
	 */
	private ClassList add(Item item)
	{
		if (!list_.contains(item))
			list_.add(item);
		return this;
	}

	
	/**
	 * Adds an item to the lists.
	 * The passed name can be 
	 * <ul>
	 * <li>either a qualified class name, therefore including that class.
	 * <li>or a string ending with '*' or '.', therefore including
	 * 		all classes whose qualified name starts with that package
	 * </ul>
	 * @param name the name defining the item
	 * @return this
	 */
	public ClassList add(String name)
	{
		Check.notNull(name, "name");
		
		if (name.endsWith("*") || name.endsWith("."))
		{
			name = StringUtil.cutRight(name, "*");
			add(new StartsWith(name));
		}
		else
		{
			String simpleName = ClassUtil.cutPackageName(name);
			if ((simpleName.length() > 0) && Character.isUpperCase(simpleName.charAt(0)))
				add(new ExactMatch(name));
			else
				add(new StartsWith(name));
		}
		return this;
	}
			
	
	/**
	 * Adds multiple names.
	 * @param names the names
	 * @see #add(String)
	 * @return this
	 */
	public ClassList add(String... names)
	{
		for (String name : names)
			add(name);
		return this;
	}
	
	
	/**
	 * Adds a class.
	 * @param c the class
	 * @return this
	 */
	public ClassList addClass(Class<?> c)
	{
		return add(new ExactMatch(c.getName()));
	}

	
	/**
	 * Adds a package. All class in or below that package are included.
	 * @param c a class defining a package
	 * @return this
	 */
	public ClassList addPackage(Class<?> c)
	{
		return add(new StartsWith(ClassUtil.getPackageName(c) + '.'));
	}

	
	/**
	 * @param name a name
	 * @return if the name is in the list..
	 */
	public boolean contains(String name)
	{
		if (name != null)
		{
			for (Item item : list_)
			{
				if (item.match(name))
					return true;
			}
		}
		return false;
	}

	
	/**
	 * @return a string representation of the list.
	 */
	@Override public String toString()
	{
		return list_.toString();
	}
	
	
	private abstract static class Item
	{
		public abstract boolean match(String name);


		@Override public abstract String toString();
		
		
		@Override public abstract int hashCode();

		
		@Override public abstract boolean equals(Object other);
	}
	

	private static class ExactMatch extends Item
	{
		public ExactMatch(String name)
		{
			name_ = name;
		}
		
		
		@Override public boolean match(String name)
		{
			return name_.equals(name);
		}
		

		@Override public boolean equals(Object other)
		{
			return (other instanceof ExactMatch) && ((ExactMatch)other).name_.equals(name_);
		}
		
		
		@Override public int hashCode()
		{
			return name_.hashCode();
		}

		
		@Override public String toString()
		{
			return "equals:" + name_;
		}
		
		
		private final String name_;
	}
	

	private static class StartsWith extends Item
	{
		public StartsWith(String name)
		{
			name_ = name;
		}
		
		
		@Override public boolean match(String name)
		{
			return name.startsWith(name_);
		}
		
		
		@Override public boolean equals(Object other)
		{
			return (other instanceof StartsWith) && ((StartsWith)other).name_.equals(name_);
		}
		
		
		@Override public int hashCode()
		{
			return name_.hashCode();
		}

		
		@Override public String toString()
		{
			return "startsWith:" + name_;
		}
		
		
		private final String name_;
	}

	
	private final List<Item> list_ = new ArrayList<>(); 
}
