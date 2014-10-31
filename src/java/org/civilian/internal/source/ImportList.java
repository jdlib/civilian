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
package org.civilian.internal.source;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


/**
 * ImportList represents a list of import statements
 * of a Java class.
 */
public class ImportList
{
	/**
	 * Creates a new ImportList.
	 */
	public ImportList()
	{
		imports_ = new ArrayList<>();
	}
	
	
	/**
	 * Clears the list
	 */
	public void clear()
	{
		imports_.clear();
	}

	
	/**
	 * Returns the list size.
	 */
	public int size()
	{
		return imports_.size();
	}
	
	
	/**
	 * Returns the i-th statement.
	 */
	public String get(int i)
	{
		return imports_.get(i);
	}
	
	
	/**
	 * Adds the class name to the list.
	 */
	public void add(Class<?> c)
	{
		add(c.getName());
	}

	
	/**
	 * Adds the class name to the list.
	 */
	public void add(String className)
	{
		if (imports_.indexOf(className) == -1)
			imports_.add(className);
	}
	
	
	/**
	 * Returns the import statement as array.
	 */
	public String[] toArray(String appPackage)
	{
		String imports[] = new String[imports_.size()];
		imports_.toArray(imports);
		Arrays.sort(imports, new PackageComparator(appPackage));
		return imports;
	}
	

	/**
	 * Prints the import list. 
	 */
	public boolean write(PrintWriter out)
	{
		return write(out, "");
	}
	
	
	/**
	 * Prints the import list. 
	 */
	public boolean write(PrintWriter out, String appPackage)
	{
		String imports_[] = toArray(appPackage);
		int size = imports_.length;
		if (size > 0)
		{
			for (int i=0; i<size; i++)
			{
				out.print("import ");
				out.print(imports_[i]);
				out.println(";");
			}
			return true;
		}
		else
			return false;
	}

	
	@SuppressWarnings("serial")
	private static class PackageComparator implements Comparator<String>, Serializable
	{
		public PackageComparator(String appPackage)
		{
			appPackage_ = appPackage;
		}
		
		
		@Override
		public int compare(String s1, String s2)
		{
			int p1 = getPriority(s1);
			int p2 = getPriority(s2);
			if (p1 != p2)
				return p1 - p2;
			else
				return s1.compareTo(s2);
		}
		
		
		private int getPriority(String s)
		{
			if (s.startsWith("java."))
				return 1;
			else if (s.startsWith("javax."))
				return 2;
			else if (s.startsWith(appPackage_))
				return 4;
			else
				return 3;
		}
		
		
		private String appPackage_;
	}


	private ArrayList<String> imports_;
}
