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
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
 

/**
 * A collection of utility function for I/O access.
 */
public abstract class IoUtil
{
	/**
	 * Silently closes a Closeable.
	 * @param in a Closeable
	 * @return the exception or null if no exception thrown
	 */
	public static IOException close(Closeable in)
	{
		try
		{
			if (in != null)
				in.close();
			return null;
		}
		catch(IOException e)
		{
			return e;
		}
	}

	
	/**
	 * Copies all file to another file.
	 * @param src a File
	 * @param dest a File
	 * @throws IOException if an I/O error occurs
	 */
	public static void copyFile(File src, File dest) throws IOException 
	{
        dest.createNewFile();

	    try (FileInputStream in = new FileInputStream(src);
	    	FileOutputStream out = new FileOutputStream(dest))
	    {
	    	FileChannel srcChannel  = in.getChannel();
	    	FileChannel destChannel = out.getChannel();
	        destChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    }
	}	


	/**
	 * Reads all data from the reader and returns it as an array of lines.
	 * @param in a reader
	 * @param trim true, if the lines should be trimmed.
	 * @return the lines
	 * @throws IOException if an I/O error occurs
	 */
	public static String[] readLines(Reader in, boolean trim) throws IOException
	{
		ArrayList<String> list = new ArrayList<>(); 
		readLines(in, trim, list);
		return list.toArray(new String[list.size()]);
	}

	
	/**
	 * Reads all data from the reader and fills it in a list.
	 * @param in a reader
	 * @param trim true, if the lines should be trimmed.
	 * @param list receives the lines
	 * @throws IOException if an I/O error occurs
	 */
	public static void readLines(Reader in, boolean trim, List<String> list) throws IOException
	{
		BufferedReader reader = new BufferedReader(in);
		String line = null;
		while((line = reader.readLine()) != null)
		{
			if (trim)
				line = line.trim();
			if (!trim || (line.length() > 0))
				list.add(line);
		}
	}

	
	/**
	 * Reads all data from the Reader and returns it as string.
	 * @param in a reader
	 * @return the result
	 * @throws IOException if an I/O error occurs
	 */
	public static String readString(Reader in) throws IOException
	{
		StringWriter out = new StringWriter();
		in.transferTo(out);
		return out.toString();
	}
	
	
	/**
	 * Reads data from the Reader and stores it in a char array.
	 * @param in a reader
	 * @param buffer a buffer
	 * @param start the start index in the buffer
	 * @param length the number of bytes ro read
	 * @return the total number of chars read
	 * @throws IOException if an I/O error occurs
	 */
	public static int read(Reader in, char[] buffer, int start, int length) throws IOException
	{
		int totalRead = 0;
		int toRead = length;
		int read;
		while ((totalRead < length) && ((read = in.read(buffer, start, toRead)) != -1))
		{
			totalRead += read;
			toRead -= read;
			start += read;
		}
		return totalRead;
	}
	
	
	/**
	 * Removes any extension from the file name.
	 * @param file a file
	 * @return the file name without extension 
	 */
	public static String cutExtension(File file)
	{
		return cutExtension(file.getName());
	}

	
	/**
	 * Removes any extension from the file name.
	 * @param name a file name
	 * @return the name without the extension 
	 */
	public static String cutExtension(String name)
	{
		int p = name.indexOf('.');
		return p == -1 ? name : name.substring(0, p);
	}

	
	/**
	 * Extracts the extension, i.e. the part after the first '.'
	 * from the filename.
	 * @param file a file
	 * @return the file extension or null 
	 */
	public static String getExtension(File file)
	{
		return getExtension(file.getName());
	}


	/**
	 * Extracts the extension, i.e. the part after the first '.'
	 * from the filename.
	 * @param name a file name
	 * @return the file extension or null 
	 */
	public static String getExtension(String name)
	{
		int p = name.indexOf('.');
		return p == -1 ? null : name.substring(p + 1);
	}
	
	
	/**
	 * Norms a extension. If the provided extension is null
	 * or empty, then null is returned. Else the extension 
	 * is returned, with any leading dot removed.
	 * @param extension the extension
	 * @return the normed extension
	 */
	public static String normExtension(String extension)
	{
		if ((extension == null) || (extension.length() == 0))
			return null;
		else
			return StringUtil.cutLeft(extension, ".");
	}
	

	/**
	 * Deletes a file or a recursively deletes a directory.
	 * @param file a file
	 * @throws IOException if a file or directory cannot be deleted.
	 */
	public static void delete(File file) throws IOException 
	{
		if (file.isDirectory()) 
		{
			File[] files = file.listFiles();
			if (files != null)
			{
				for (File f : files)
					delete(f);
			}
		}
		if (!file.delete())
			throw new IOException("cannot delete " + file);
	}	


	/**
	 * Makes sure that a directory exists.
	 * @param dir a directory
	 * @throws IOException if a file or directory cannot be deleted.
	 */
	public static void mkdirs(File dir) throws IOException 
	{
		if (!dir.exists() && !dir.mkdirs()) 
			throw new IOException("cannot create " + dir);
	}	
}
