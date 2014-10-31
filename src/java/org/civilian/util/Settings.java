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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.civilian.type.Type;
import org.civilian.type.lib.StandardSerializer;


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
	 */
	public void read(InputStream in) throws IOException
	{
		props_.load(in);
	}

	
	/**
	 * Reads the Settings from the reader.
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
	 * Returns the source from which the settings were read.
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
	 */
	public void setSource(File source)
	{
		setSource(source.getAbsolutePath())	;
	}

	
	/**
	 * Adjusts a key which is used as key into the properties object.
	 */
	private String adjust(String key)
	{
		Check.notNull(key, "key");
		// add prefix if the Settings wraps another settings object
		return prefix_ == null ? key : prefix_ + key;
	}
	

	/**
	 * Returns the raw value for the key contained in the properties object.
	 */
	private String rawValue(String key)
	{
		key = adjust(key);
		return props_.getProperty(key);
	}
	

	/**
	 * Returns the normed value for the key contained in the properties object.
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
	 * Returns if the settings contains a non-empty value for the key.
	 */
	public boolean contains(String key)
	{
		return contains(key, true);
	}
	
	
	/**
	 * Returns if the settings contain an entry for the key.
	 * @param notEmpty if true then the entry must not be empty
	 */
	public boolean contains(String key, boolean notEmpty)
	{
		String v = notEmpty ? value(key) : rawValue(key);
		return v != null; 
	}

	
	/**
	 * Returns the value of a key.
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
	 */
	public String get(String key, String defaultValue)
	{
		String value = value(key);
		return value != null ? value : defaultValue; 
	}
	

	/**
	 * Returns true if the value to which the key is mapped is "true", false
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
	 */
	public boolean getBoolean(String key, boolean defaultValue)
	{
		String v = get(key, null);
		return (v != null) ? Boolean.valueOf(v).booleanValue() : defaultValue;
	}

	
	/**
	 * Returns the value of a key converted to an integer.
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public int getInt(String key)
	{
		String s = get(key);
		return Integer.parseInt(s);
	}

	
	/**
	 * Returns the value of a key converted to an integer.
	 * If the key is not mapped to any value, the default value is returned.
	 */
	public int getInt(String key, int defaultValue)
	{
		String s = get(key, null);
		return (s != null) ? Integer.parseInt(s) : defaultValue;
	}

	
	/**
	 * Returns the value of a key converted to a long.
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public long getLong(String key)
	{
		String s = get(key);
		return Long.parseLong(s); 
	}

	
	/**
	 * Returns the value of a key converted to a long.
	 * If the key is not mapped to any value, the default value is returned.
	 */
	public long getLong(String key, long defaultValue)
	{
		String s = get(key, null);
		return (s != null) ? Long.parseLong(s) : defaultValue;
	}

	
	/**
	 * Returns the value of a key converted to a double.
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public double getDouble(String key)
	{
		String s = get(key);
		return Double.parseDouble(s); 
	}

	
	/**
	 * Returns the value of a key converted to a double.
	 * If the key is not mapped to any value, the default value is returned.
	 */
	public double getDouble(String key, double defaultValue)
	{
		String s = get(key, null);
		return (s != null) ? Double.parseDouble(s) : defaultValue;
	}

		
	/**
	 * Returns the value of a key converted to a type.
	 * @exception IllegalArgumentException thrown if the key is not mapped to 
	 *		any value.
	 */
	public <T> T getValue(String key, Type<T> type) throws Exception
	{
		String s = get(key);
		return type.parse(StandardSerializer.INSTANCE, s); 
	}

	
	/**
	 * Returns the value of a key converted to an integer.
	 * If the key is not mapped to any value, the default value is returned.
	 */
	public <T> T getValue(String key, Type<T> type, T defaultValue) throws Exception
	{
		String s = get(key, null);
		return (s != null) ? type.parse(StandardSerializer.INSTANCE, s) : defaultValue; 
	}

	
	/**
 	 * Returns the value of a key, separated by commas, 
	 * splitted into a string array.
	 * @param key the key
	 */	
	public String[] getList(String key)
	{
		return getList(key, ",");
	}
	
	
	/**
	 * Returns the value of a key splitted into a string array.
	 * @param key the key
	 * @param delimiter the delimiter for separating the substrings
	 */	
	public String[] getList(String key, String delimiter)
	{
		ArrayList<String> list = new ArrayList<>();
		getList(key, delimiter, list);
		return list.toArray(new String[list.size()]);
	}
	
	
	/**
	 * Fills the value of a key splitted into a strings
	 * into the list.
	 * @param key the key
	 * @param delimiter the delimiter for separating the substrings
	 */	
	public int getList(String key, String delimiter, List<String> list)
	{
		String s = get(key, null);
		if (s != null)
		{
			StringTokenizer st = new StringTokenizer(s, delimiter);
			while(st.hasMoreElements())
				list.add(st.nextToken().trim());
		}
		return list.size();
	}

	
	/**
	 * Assumes that the value for the given key is a class name
	 * and returns a new instance for that class.
	 * @param key
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass. 
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
	 * @param key
	 * @param superClass a superclass of the class. The method checks that the class 
	 * 		is derived from the superclass. 
	 */
	public <T> T getObject(String key, Class<T> superClass, ClassLoader loader, T defaultValue) 
		throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		String className = get(key, null);
		return (className == null) ? defaultValue : ClassUtil.createObject(className, superClass, loader);
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

	
	public <T> void set(String key, Type<T> type, T value)
	{
		String s = value != null ? type.format(StandardSerializer.INSTANCE, value) : null;
		set(key, s);
	}

	
	/**
	 * Sets the value of a key.
	 */
	public void set(String key, int value)
	{
		set(key, String.valueOf(value));
	}

	
	/**
	 * Sets the value of a key.
	 */
	public void set(String key, boolean value)
	{
		set(key, String.valueOf(value));
	}

	
	/**
	 * Sets the value of a key.
	 */
	public void set(String key, long value)
	{
		set(key, String.valueOf(value));
	}
	
	
	/**
	 * Removes a key.
	 */
	public void remove(String key)
	{
		set(key, null);
	}

	
	/**
	 * Returns a Settings object with the given prefix. 
	 * @see Settings#Settings(Settings, String)
	 */
	public Settings wrap(String prefix)
	{
		return new Settings(this, prefix);
	}

	
	/**
	 * Calls wrap(prefix + "."). 
	 */
	public Settings wrap(int prefix)
	{
		return wrap(prefix + ".");
	}

	
	/**
	 * Returns an iterator of the keys.
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
