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
package org.civilian.resource;


import java.io.PrintWriter;
import java.io.Serializable;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


/**
 * Path represents a path string. It uses the '/' character
 * to separate path segments.<br>
 * Wrapping a path string in 
 * an object ensures that its string representation is normed
 * and operations on objects creates again a valid path.<br>
 * The root path is represented as "".
 * Any other path always starts with a "/" but never ends with a "/".
 * Therefore any concatenation of path strings would again result in a
 * valid path string.<br>
 * If you print a path object, you must ensure that the root path
 * is printed as "/". You can use {@link #print()} for that purpose.
 */
public class Path implements CharSequence, Serializable, Comparable<Path>
{
	private static final long serialVersionUID = 5312565042029867855L;
	
	
	/**
	 * A constant for the root path.
	 */
	public static final Path ROOT = new Path(null);
	private static final String ROOT_VALUE = "";
	
	
	/**
	 * Tests if the path starts with the given prefix and 
	 * either equals the path or ends at a segment boundary.
	 */
	public static boolean startsWith(String path, String prefix)
	{
		Check.notNull(path, "path");
		if (path.startsWith(prefix))
		{
			int prefixLen = prefix.length();
			if ((path.length() == prefixLen) ||
				((path.length() > prefixLen) && (path.charAt(prefixLen) == '/')))
				return true;
		}
		return false;
	}

	
	/**
	 * Norms a string which represents a path with segments
	 * separated by '/'.
	 * A trailing '/' is removed from the path and a leading
	 * '/' is added to any non-root path, whereas the root path
	 * is represented as the string "".
	 */
	public static String norm(String path)
	{
		if ((path == null) || (path.length() == 0) || path.equals("/"))
			return ROOT_VALUE;
		else
		{
			path = StringUtil.cutRight(path, "/");
			path = StringUtil.haveLeft(path, "/");
			return !path.equals("/") ? path : ROOT_VALUE;
		}
	}
	
	
	/**
	 * Creates a new Path from the path string.
	 */
	public Path(String value)
	{
		value_ = norm(value);
	}
	
	
	/**
	 * Creates a new Path from an already normed string.
	 */
	private Path(Void dummy, String value)
	{
		value_ = value;
	}
	
	
	/**
	 * Returns the path string.
	 * If this path is the root path, then "" is returned.
	 */
	public String getValue()
	{
		return value_;
	}
	
	
	/**
	 * Returns a printable version of the path.
	 * If this path is the root path, "/" is returned. 
	 */
	public String print()
	{
		return isRoot() ? "/" : value_;
	}

	
	/**
	 * Prints this path appended by the subPath
	 * @param out a PrintWriter 
	 * @param subPath a subpath which may or may not conform to the path formatting conventions of path.
	 */
	public void print(PrintWriter out, String subPath)
	{
		if ((subPath == null) || (subPath.length() == 0) || subPath.equals("/"))
			out.print(print());
		else if (subPath.charAt(0) == '/')
		{
			if (!isRoot())
				out.print(value_);
			out.print(subPath);
		}
		else
		{
			out.print(print());
			if (!isRoot())
				out.print('/');
			out.print(subPath);
		}
	}
	

	/**
	 * Returns the length of the path string.
	 */
	@Override public int length()
	{
		return value_.length();
	}


	/**
	 * Returns the character at the given index.
	 * Implements the CharSequence interface.
	 */
	@Override public char charAt(int index)
	{
		return value_.charAt(index);
	}


	/**
	 * Returns a character subsequence from the path string.
	 * Implements the CharSequence interface.
	 */
	@Override public CharSequence subSequence(int start, int end)
	{
		return value_.subSequence(start, end);
	}

	
	/**
	 * Returns if this path is the root path.
	 */
	public boolean isRoot()
	{
		return value_.length() == 0;
	}
	
	
	/**
	 * Returns a path which corresponds to this path + the given path.
	 */
	public Path add(Path path)
	{
		if ((path == null) || path.isRoot())
			return this;
		else if (isRoot()) 
			return path;
		else
			return new Path(null, value_ + path.toString());
	}
	
	
	/**
	 * Returns a path which corresponds to this path + the given path.
	 */
	public Path add(String path)
	{
		String normed = norm(path);
		if (normed == ROOT_VALUE)
			return this;
		else if (isRoot())
			return new Path(null, normed);
		else
			return new Path(null, value_ + normed);
	}


	/**
	 * Adds the path to the StringBuilder.
	 */
	public void addTo(StringBuilder s)
	{
		int length = s.length(); 
		if (isRoot())
		{
			if (length == 0)
				s.append('/');
		}
		else
		{
			if ((length == 0) || (s.charAt(length - 1) != '/'))
				s.append(value_);
			else
				s.append(value_, 1, value_.length());
		}
	}
	
	
	/**
	 * Tests if the path starts with the prefix and 
	 * either equals the path or ends at a segment boundary.
	 */
	public boolean startsWith(String prefix)
	{
		return startsWith(value_, prefix);
	}
	
	
	/**
	 * Tests if the path starts with the other path.
	 */
	public boolean startsWith(Path path)
	{
		Check.notNull(path, "path");
		return startsWith(path.value_);
	}
	
	
	/**
	 * Tests if this path starts with the path.
	 * If true a new path with the start path removed
	 * is returned else null is returned. 
	 */
	public Path cutStart(Path path)
	{
		Check.notNull(path, "path");
		if (path.isRoot())
			return this;
		else if (!startsWith(path))
			return null;
		else if (length() > path.length())
			return new Path(null, value_.substring(path.length()));
		else
			return Path.ROOT;
	}
	
	
	/**
	 * Returns the extension of the last segment in the path.
	 * @return null if the path has no extension or the part after first '.' in the last segment
	 * 		E.g. If the path is "/user/view.en.html" then "en.html" is returned
	 */
	public String getExtension()
	{
		int pSlash	= value_.lastIndexOf('/');
		int pExt  	= value_.indexOf('.', pSlash + 1);
		return pExt < 0 ? null : value_.substring(pExt + 1);  
	}
	
	
	public String getLastSegment()
	{
		int pSlash	= value_.lastIndexOf('/');
		return pSlash < 0 ? "" : value_.substring(pSlash + 1);
	}

	
	/**
	 * Compares the path values.
	 */
	@Override public int compareTo(Path other)
	{
		return value_.compareTo(other.value_);
	}


	/**
	 * Returns true iif the other object is a Path and
	 * has the same path value. 
	 */
	@Override public boolean equals(Object other)
	{
		return (other instanceof Path) && ((Path)other).value_.equals(value_);
	}
	
	
	/**
	 * Returns the hash code of the path value. 
	 */
	@Override public int hashCode()
	{
		return value_.hashCode();
	}
	
	
	/**
	 * Returns the path string.
	 */
	@Override public String toString()
	{
		return value_;
	}
	
	
	private final String value_;
}
