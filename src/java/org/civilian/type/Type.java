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
package org.civilian.type;


import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;
import org.civilian.util.Check;


/**
 * Type represents a data type.
 */
public abstract class Type<T>
{
	/**
	 * Category categorizes types.
	 */
	public static class Category
	{
		private static AtomicInteger nextOrdinal_ = new AtomicInteger();
		public static final Category SIMPLE 	= new Category("SIMPLE");
		public static final Category DATE		= new Category("DATE");
		public static final Category TIME		= new Category("TIME");
		public static final Category DATETIME	= new Category("DATETIME");
		public static final Category AUTO		= new Category("AUTO");
		public static final Category LIST		= new Category("LIST");
		public static final Category DISCRETE	= new Category("DISCRETE");
		
		
		public Category(String name)
		{
			name_ 	 = Check.notNull(name, "name");
			ordinal_ = nextOrdinal_.getAndIncrement();
		}
		
		
		/**
		 * Returns the unique ordinal id of the category. It may
		 * change during different VM runs.
		 * @return the ordinal. The ordinal is a value &gt;= 0.  
		 */
		public final int ordinal()
		{
			return ordinal_;
		}
		
		
		/**
		 * @return the category name.
		 */
		public final String name()
		{
			return name_;
		}

		
		@Override public String toString()
		{
			return name();
		}

		
		/**
		 * @return the number of existing categories.
		 */
		public static int count()
		{
			return nextOrdinal_.get();
		}
		
		
		private final int ordinal_;
		private final String name_;
	}

	
	public Type(Category category)
	{
		category_ 	= Check.notNull(category, "category");
		ordinal_ 	= nextOrdinal_.getAndIncrement();
	}
	
	
	/**
	 * Returns the unique ordinal id of the type instance. It may
	 * change during different VM runs.
	 * @return the ordinal. The ordinal is a value &gt;= 0.  
	 */
	public final int ordinal()
	{
		return ordinal_;
	}
	
	
	/**
	 * @return the type category.
	 */
	public final Category category()
	{
		return category_;
	}

	
	/**
	 * @return true if the Type is a simple type (i.e. not an array or list type).
	 * The default implementation returns true.
	 */
	public boolean isSimpleType()
	{
		return true;
	}

	
	/**
	 * @return the associated Java type.
	 */
	public abstract Class<T> getJavaType();
	

	/**
	 * @return the associated primitive Java type, or null if does not have one.
	 * The default implementation returns null.
	 */
	public Class<T> getJavaPrimitiveType()
	{
		return null;
	}
	

	/**
	 * @param length a length
	 * @return an array of length n.
	 */
	@SuppressWarnings("unchecked")
	public T[] createArray(int length)
	{
		return (T[])Array.newInstance(getJavaType(), length);
	}


	/**
	 * @return the simple name of the type class.
	 */
	@Override public String toString()
	{
		return getClass().getSimpleName();
	}


	private final int ordinal_;
	private final Category category_;
	private static final AtomicInteger nextOrdinal_ = new AtomicInteger();
}
 