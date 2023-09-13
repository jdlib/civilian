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


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * An iterator for command line arguments.
 * Any argument prefixed by '@' is treated as argument file: Its content
 * is read, interpreted as arguments and inserted into the argument list. 
 */
public class Arguments
{
	/**
	 * Creates a Arguments object.
	 * @param args the string arguments to the main method
	 */
	public Arguments(String... args)
	{
    	this(null, args);
    }
    
    
    /**
	 * Creates a Arguments object.
     * @param loader if not null then it is used to open argument files
     * @param args the arguments
     */
    public Arguments(ResourceLoader loader, String... args)
    {
    	loader_	= loader;
    	addArgs(args, null);
	}
   
    
    private ArrayList<String> addArgs(String[] args, ArrayList<String> files)
    {
    	int len = args == null ? 0 : args.length;
    	
    	if (args_ == null)
    		args_ = new ArrayList<>(len);
    	else
    		args_.ensureCapacity(args_.size() + len);
    			
		for (int i=0; i<len; i++)
		{
			String arg = args[i];
			
			// ignore null and empty args
			if ((arg != null) && (arg.length() > 0))
			{
				if ((arg.charAt(0) == '@') && (arg.length() > 1))
					files = insertArgumentFile(arg.substring(1), files);
				else 
					args_.add(arg);
			}
		}
		
		return files;
    }

	
	private ArrayList<String> insertArgumentFile(String file, ArrayList<String> files)
	{
		if (files == null)
			files = new ArrayList<>();
		if (files.contains(file))
			throw new IllegalArgumentException("cyclic inclusion of argument file: " + file);
		files.add(file);
		
		try
		{
			try(InputStream in = loader_ == null ?  
				new FileInputStream(file) :
				loader_.required().getResourceAsStream(file))
			{
	            String args[] = readArguments(in);
	            files = addArgs(args, files);
	            return files;
			}
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("cannot read argument file " + file, e);
		}
	}


    private String[] readArguments(InputStream in) throws IOException
    {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    	String line;
    	ArrayList<String> args = new ArrayList<>();
    	while((line = reader.readLine()) != null)
    	{
    		line = line.trim();
    		if (!line.startsWith("#"))
    		{
	    		StringTokenizer st = new StringTokenizer(line);
	    		while(st.hasMoreTokens())
	    			args.add(st.nextToken());
    		}
    	}
    	return args.toArray(new String[args.size()]);
    }
    
 
    /**
	 * @return the current argument.
	 */
	public String get()
	{
		return hasMore() ? args_.get(pos_) : null;
	}

	
	/**
	 * @return the next argument string and proceeds to the next argument.
	 */
	public String next()
	{
		return next(null);
	}


	/**
	 * @return the next argument string or throws an IllegalArgumentException if there
	 * are no more arguments left.
	 * @param what describes the parameter. Used when a IllegalArgumentException is thrown.
	 */
	public String next(String what)
	{
		if (!hasMore())
		{
			String message = what != null ? "missing argument: " + what : "no arguments left"; 
			throw new IllegalArgumentException(message);
		}
		return args_.get(pos_++);
	}


    /**
     * @return the next argument string as int.
 	 * @param what describes the parameter. Used when a IllegalArgumentException is thrown.
     * @throws IllegalArgumentException if there are no more arguments left.
     */
    public int nextInt(String what)
    {
        String s = next(what);
        try
		{
        	return Integer.parseInt(s);
		}
        catch(NumberFormatException e)
		{
    		throw createConvertException(what, "'" + s + "' is not a integer", e);
		}
    } 
    
    
    /**
     * @return the next argument string as boolean.
	 * @param what describes the parameter. Used when a IllegalArgumentException is thrown.
     * @throws IllegalArgumentException if there are no more arguments left.
     */
    public boolean nextBoolean(String what)
    {
        String s = next(what);
       	return Boolean.parseBoolean(s);
    } 

    
    /**
     * @return the next argument string as File.
 	 * @param what describes the parameter. Used when a IllegalArgumentException is thrown.
     * @throws IllegalArgumentException if there are no more arguments left.
     */
    public File nextFile(String what)
    {
    	return nextFile(what, null);
    }
    
    
    /**
     * @return the next argument string as File.
     * @param what describes the parameter. Used when a IllegalArgumentException is thrown.
     * @param fileType the expected file type.
     * @throws IllegalArgumentException if there are no more arguments left or the file
     * 		does not match the expected file type
     */
    public File nextFile(String what, FileType fileType)
    {
        File file = new File(next(what));
        if (fileType != null)
        	fileType.check(file, what);
        return file;
    } 

    
    /**
     * Interprets the next argument string as class name and returns a
     * new instance of that class.
     * @param what describes the parameter. Used when a IllegalArgumentException is thrown.
     * @param superClass a super class of the class.
     * @param <T> the superclass type
     * @return the new instance
     */
    public <T> T nextObject(String what, Class<T> superClass)
    {
    	String className = next(what);
    	try
    	{
    		return ClassUtil.createObject(className, superClass, null);
    	}
    	catch(Exception e)
    	{
    		throw createConvertException(what, e.getMessage(), e);
    	}
    } 

   
    /**
	 * @param s a string 
	 * @return if the next argument matches the string. If true, 
	 * the string is consumed.
	 */
	public boolean consume(String s)
	{
		if (hasMore() && args_.get(pos_).equals(s))
		{
			pos_++;
			return true;
		}
		else
			return false;
	}


	/**
	 * @param s a string
	 * Replaces the current argument with the string.
	 */
	public void replace(String s)
	{
		if (hasMore())
			args_.set(pos_, s);
	}
	
	
	/**
	 * @param s a string
	 * @return if the next argument starts with the given string.
	 */
	public boolean startsWith(String s)
	{
		return hasMore() && args_.get(pos_).startsWith(s);
	}


	/**
	 * @return if there are more arguments left.
	 */
	public boolean hasMore()
	{
		return pos_ < args_.size();
	}


	/**
	 * @param count the count
	 * @return if there are at least count more arguments.
	 */
	public boolean hasMore(int count)
	{
		return pos_ + count <= args_.size();
	}
	
	
	/**
	 * @return the remaining arguments.
	 */
	public List<String> getRestArgs()
	{
		return args_.subList(pos_, args_.size());
	}
	
	
	private IllegalArgumentException createConvertException(String what, String message, Exception cause)
	{
		if (what != null)
			message = "reading " + what + ": " + message;
		return new IllegalArgumentException(message, cause);
	}


	private ArrayList<String> args_;
	private int pos_;
    private ResourceLoader loader_;
}
