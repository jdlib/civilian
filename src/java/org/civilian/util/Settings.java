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
package org.civilian.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.function.Function;


/**
 * A class like java.util.Properties but with more useful functionality.
 */
public class Settings
{
	/**
	 * Creates an empty settings object.
	 */
	public Settings()
	{
		props_ = new Properties();
	}
	
	
	/**
	 * Creates an settings object based on the given properties.
	 * @param properties a properties object. If null a new properties object is created.
	 */
	public Settings(Properties properties)
	{
		props_ = properties != null ? properties : new Properties();
	}

	
	/**
	 * Creates a settings object which wraps another settings object.
	 * @param wrapped the wrapped settings.
	 * @param prefix a prefix which os automatically preprended
	 * 		to every key:
	 * 		Example:
	 * 		<code>
	 * 		Settings s1 = ...;<br>
	 * 		assert s1.hasKey("one.two")<br>
	 * 		Settings s2 = new Settings2(s1, "one.");<br>
	 * 		assert s1.getKey("one.two") == s2.getKey("two")
	 * 		</code>
	 */
	public Settings(Settings wrapped, String prefix)
	{
		wrapped_ = Check.notNull(wrapped, "wrapped");
		prefix_	 = Check.notNull(prefix, "prefix");
		if (wrapped.prefix_ != null)
			prefix_ = wrapped.prefix_ + prefix;   
		props_	 = wrapped.props_;
	}
	
	
	/**
	 * Reads the settings from the input stream.
	 * @param in the InputStream
	 * @throws IOException if an I/O error occurs
	 */
	public void read(InputStream in) throws IOException
	{
		props_.load(in);
	}

	
	/**
	 * Reads the Settings from the reader.
	 * @param reader a reader
	 * @throws IOException if an I/O error occurs
	 */
	public void read(Reader reader) throws IOException
	{
		if (wrapped_ != null)
			wrapped_.read(reader);
		else
			props_.load(reader);
	}

	
	/**
	 * Reads the Settings from the file and sets
	 * the {@link #getSource() source}.
	 * @param file a file
	 * @throws IOException if an I/O error occurs
	 */
	public void read(File file) throws IOException
	{
		setSource(file);
		try(FileInputStream in = new FileInputStream(file))
		{
			read(in);
		}
	}

	
	/**
	 * @return the source from which the settings were read.
	 * @see #setSource(String)
	 */
	public String getSource()
	{
		return wrapped_ != null ? wrapped_.getSource() : source_;
	}
	
	
	/**
	 * Sets the source from which the settings were read.
	 * If the settings throws exception, then the source is included
	 * in the exception message
	 * @param source the source
	 */
	public void setSource(String source)
	{
		if (wrapped_ != null)
			wrapped_.setSource(source);
		else
			source_ = source;
	}
	
	
	/**
	 * Sets the source from which the Settings was read.
	 * If the Settings throws an exception, then the source is included
	 * in the exception message
	 * @param source the source
	 */
	public void setSource(File source)
	{
		setSource(source.getAbsolutePath())	;
	}

	
	/**
	 * Adjusts a key which is used as key into the properties object.
	 * @param key the key
	 */
	private String adjust(String key)
	{
		Check.notNull(key, "key");
		// add prefix if the Settings wraps another settings object
		return prefix_ == null ? key : prefix_ + key;
	}
	

	/**
	 * @param key the key
	 * @return the raw value for the key contained in the properties object.
	 */
	private String rawValue(String key)
	{
		key = adjust(key);
		return props_.getProperty(key);
	}
	

	/**
	 * @param key the key
	 * @return the normed value for the key contained in the properties object.
	 * If the value is not null, it is trimmed and if empty normed to null.
	 */
	private String value(String key)
	{
		String value = rawValue(key);
		if (value != null)
		{
			value = value.trim();
			if (value.length() == 0)
				value = null;
		}
		return value;
	}
	
	
	/**
	 * @return if the settings contains a non-empty value for the key.
	 * @param key the key
	 */
	public boolean contains(String key)
	{
		return contains(key, true);
	}
	
	
	/**
	 * @return if the settings contain an entry for the key.
	 * @param key the key
	 * @param notEmpty if true then the entry must not be empty
	 */
	public boolean contains(String key, boolean notEmpty)
	{
		String v = notEmpty ? value(key) : rawValue(key);
		return v != null; 
	}

	
	/**
	 * @return the value of a key.
	 * @param key the key
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public String get(String key) throws IllegalArgumentException
	{
		String value = value(key);
		if (value == null)
			throwKeyException(key, " is not set");
		return value;
	}
	
	
	/**
	 * Returns the value of a key.
	 * If the key is not mapped to any value, the default value is returned.
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the value
	 */
	public String get(String key, String defaultValue)
	{
		String value = value(key);
		return value != null ? value : defaultValue; 
	}
	

	/**
	 * @param key the key
	 * @return true if the value to which the key is mapped is "true", false
	 * otherwise.
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public boolean getBoolean(String key) throws IllegalArgumentException
	{
		return Boolean.valueOf(get(key)).booleanValue();
	}
	
	
	/**
	 * Returns true if the value to which the key is mapped is "true", false
	 * otherwise.
	 * If the key is not mapped to any value, the default value is returned.
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the value
	 */
	public boolean getBoolean(String key, boolean defaultValue)
	{
		String v = get(key, null);
		return (v != null) ? Boolean.valueOf(v).booleanValue() : defaultValue;
	}
	
	
	/**
	 * @return the value of a key.
	 * If the key is not mapped to any value, the default value is returned.
	 * @param key the key
	 * @param defaultValue the default value
	 */
	public Charset getCharset(String key, Charset defaultValue)
	{
		return getValue(key, Charset::forName, defaultValue);
 	}

	
	/**
	 * @return the value of a key converted to an integer.
	 * @param key the key
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public int getInt(String key)
	{
		String s = get(key);
		return Integer.parseInt(s);
	}

	
	/**
	 * @return the value of a key converted to an integer.
	 * If the key is not mapped to any value, the default value is returned.
	 * @param key the key
	 * @param defaultValue the default value
	 */
	public int getInt(String key, int defaultValue)
	{
		String s = get(key, null);
		return (s != null) ? Integer.parseInt(s) : defaultValue;
	}

	
	/**
	 * @return the value of a key converted to a long.
	 * @param key the key
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public long getLong(String key)
	{
		String s = get(key);
		return Long.parseLong(s); 
	}

	
	/**
	 * @return the value of a key converted to a long.
	 * If the key is not mapped to any value, the default value is returned.
	 * @param key the key
	 * @param defaultValue the default value
	 */
	public long getLong(String key, long defaultValue)
	{
		String s = get(key, null);
		return (s != null) ? Long.parseLong(s) : defaultValue;
	}

	
	/**
	 * @return the value of a key converted to a double.
	 * @param key the key
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public double getDouble(String key)
	{
		String s = get(key);
		return Double.parseDouble(s); 
	}

	
	/**
	 * @return the value of a key converted to a double.
	 * If the key is not mapped to any value, the default value is returned.
	 * @param key the key
	 * @param defaultValue the default value
	 */
	public double getDouble(String key, double defaultValue)
	{
		String s = get(key, null);
		return (s != null) ? Double.parseDouble(s) : defaultValue;
	}

		
	/**
 	 * @return the value of a key, separated by commas, splitted into a string array.
	 * @param key the key
	 */	
	public String[] getArray(String key)
	{
		return getArray(key, ",");
	}
	
	
	/**
	 * @return the value of a key splitted into a string array.
	 * @param key the key
	 * @param delimiter the delimiter for separating the substrings
	 */	
	public String[] getArray(String key, String delimiter)
	{
		List<String> list = getList(key, delimiter);
		return list.toArray(new String[list.size()]);
	}
	
	
	/**
 	 * @return the value of a key, separated by commas, splitted into a string list.
	 * @param key the key
	 */	
	public List<String> getList(String key)
	{
		return getList(key, ",");
	}
	
	
	/**
	 * @return the value of a key splitted into a string list.
	 * @param key the key
	 * @param delimiter the delimiter for separating the substrings
	 */	
	public List<String> getList(String key, String delimiter)
	{
		List<String> list = new ArrayList<>();
		getList(key, delimiter, list);
		return list;
	}

	/**
	 * Fills the value of a key splitted into a strings
	 * into the list.
	 * @param key the key
	 * @param delimiter the delimiter for separating the substrings
	 * @param list a list 
	 */	
	public void getList(String key, String delimiter, List<String> list)
	{
		String s = get(key, null);
		if (s != null)
		{
			StringTokenizer st = new StringTokenizer(s, delimiter);
			while(st.hasMoreElements())
				list.add(st.nextToken().trim());
		}
	}

	
	/**
	 * Assumes that the value for the given key is a class name
	 * and returns a new instance for that class.
	 * @param key the key
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass.
	 * @param loader a ClassLoader 
	 * @throws ClassNotFoundException if the class was not found 
	 * @throws InstantiationException if the instance could not be created 
	 * @throws IllegalAccessException if the instance could not be created
	 * @return the object 
	 * @param <T> the object type
	 */
	public <T> T getObject(String key, Class<T> superClass, ClassLoader loader) 
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		return ClassUtil.createObject(get(key), superClass, loader);
	}
	
	
	/**
	 * Assumes that the value for the given key is a class name
	 * and returns a new instance of that class. If the Settings does
	 * not contain the key, then it returns the defaultValue.
	 * @param key the key
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass.
	 * @param loader a ClassLoader 
	 * @param defaultValue the default value
	 * @throws ClassNotFoundException if the class was not found 
	 * @throws InstantiationException if the instance could not be created 
	 * @throws IllegalAccessException if the instance could not be created
	 * @return the object
	 * @param <T> the object type
	 */
	public <T> T getObject(String key, Class<T> superClass, ClassLoader loader, T defaultValue) 
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		String className = get(key, null);
		return (className == null) ? defaultValue : ClassUtil.createObject(className, superClass, loader);
	}
	
	
	/**
	 * Gets the string value for a key. If not null asks the factory
	 * to convert it into the target value, else returns the default value.
	 * @param key the key
	 * @param factory can translate a string into the target value
	 * @param defaultValue returned if the string value is null
	 * @return the result  
	 * @param <T> the object type
	 */
	public <T> T getValue(String key, Function<String,T> factory, T defaultValue)
	{
		String s = get(key, null);
		return s != null ? factory.apply(s) : defaultValue;
	}
	
	
	/**
	 * Sets the value of a key.
	 * @param key the key 
	 * @param value the value. If null, the key will be removed.
	 * 		If not null, its toString() method will be called to 
	 * 		convert it into a string.
	 */
	public void set(String key, Object value)
	{
		key = adjust(key);
		if (value == null)
			props_.remove(key);
		else
			props_.setProperty(key, value.toString());
	}

	
	/**
	 * Sets the value of a key.
	 * @param key the key
	 * @param value the value
	 */
	public void set(String key, int value)
	{
		set(key, String.valueOf(value));
	}

	
	/**
	 * Sets the value of a key.
	 * @param key the key
	 * @param value the value
	 */
	public void set(String key, boolean value)
	{
		set(key, String.valueOf(value));
	}

	
	/**
	 * Sets the long value of a key.
	 * @param key the key
	 * @param value the value
	 */
	public void set(String key, long value)
	{
		set(key, String.valueOf(value));
	}
	
	
	/**
	 * Removes a key.
	 * @param key the key
	 */
	public void remove(String key)
	{
		set(key, null);
	}

	
	/**
	 * @return a Settings object with the given prefix.
	 * @param prefix the prefix 
	 * @see Settings#Settings(Settings, String)
	 */
	public Settings wrap(String prefix)
	{
		return new Settings(this, prefix);
	}

	
	/**
	 * Calls wrap(prefix + ".").
	 * @param prefix the prefix 
	 * @return the wrapped settings
	 */
	public Settings wrap(int prefix)
	{
		return wrap(prefix + ".");
	}

	
	/**
	 * @return an iterator of the keys.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Iterator<String> keys()
	{
		return Iterators.asIterator((Enumeration)props_.keys());
	}
	
	
	private void throwKeyException(String key, String message)
	{
		String text = "settings key '" + adjust(key) + "' ";
		if (source_ != null)
			text += "in '" + source_ + "' ";
		text += message;
		throw new IllegalArgumentException(text);
	}

	
	@Override public String toString()
	{
		return "Settings[src=" + source_ + "]";
	}

	
	private String source_;
	private String prefix_ = "";
	private Settings wrapped_;
	private Properties props_;
}
