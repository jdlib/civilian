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
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
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
	 * Returns the system dependent line separator.
	 */
	public static String getLineSeparator()
	{
		return System.getProperty("line.separator");
	}


	/**
	 * Returns the system dependent file separator.
	 */
	public static String getFileSeparator()
	{
		return System.getProperty("file.separator");
	}

	
	/**
	 * Returns the system dependent file separator char.
	 */
	public static char getFileSeparatorChar()
	{
		String sep = getFileSeparator();
		if (sep.length() != 1)
			throw new IllegalArgumentException("separator is not a char: " + sep);
		return sep.charAt(0);
	}

	
	/**
	 * Copies all data from an InputStream to an OutputStream.
	 * @exception IOException if an I/O error occurs
	 */
	public static void copy(InputStream src, OutputStream dest) throws IOException
	{
		byte buffer[] = new byte[2048];
		int count;
		while ((count = src.read(buffer)) != -1)
			dest.write(buffer, 0, count);
	}

	
	/**
	 * Copies all data from an InputStream to an OutputStream.
	 * @exception IOException if an I/O error occurs
	 */
	public static void copy(Reader src, Writer dest) throws IOException
	{
		char buffer[] = new char[2048];
		int count;
		while ((count = src.read(buffer)) != -1)
			dest.write(buffer, 0, count);
	}

	
	/**
	 * Copies all file to another file.
	 * @exception IOException if an I/O error occurs
	 */
	public static void copyFile(File src, File dest) throws IOException 
	{
        dest.createNewFile();

	    FileChannel srcChannel = null;
	    FileChannel destChannel = null;

	    try 
	    {
	        srcChannel  = new FileInputStream(src).getChannel();
	        destChannel = new FileOutputStream(dest).getChannel();
	        destChannel.transferFrom(srcChannel, 0, srcChannel.size());
	    }
	    finally
	    {
	    	close(srcChannel);
	    	close(destChannel);
	    }
	}	
	
	
	/**
	 * Copies all data from the InputStream into a byte array.
	 */
	public static byte[] readBytes(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}


	/**
	 * Reads all data from the reader and returns it as an array of lines.
	 * @param trim true, if the lines should be trimmed.
	 */
	public static String[] readLines(Reader in, boolean trim) throws IOException
	{
		ArrayList<String> list = new ArrayList<>(); 
		readLines(in, trim, list);
		return list.toArray(new String[list.size()]);
	}

	
	/**
	 * Reads all data from the reader and fills it in a list.
	 * @param trim true, if the lines should be trimmed.
	 */
	public static void readLines(Reader in, boolean trim, List<String> list) throws IOException
	{
		BufferedReader reader = new BufferedReader(in);
		if (list == null)
			list = new ArrayList<>();
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
	 */
	public static String readString(Reader in) throws IOException
	{
		StringWriter out = new StringWriter();
		copy(in, out);
		return out.toString();
	}
	
	
	/**
	 * Reads data from the Reader and stores it in a char array.
	 * @return the total number of chars read
	 */
	public static int read(Reader reader, char[] buffer, int start, int length) throws IOException
	{
		int totalRead = 0;
		int toRead = length;
		int read;
		while ((totalRead < length) && ((read = reader.read(buffer, start, toRead)) != -1))
		{
			totalRead += read;
			toRead -= read;
			start += read;
		}
		return totalRead;
	}
		
	
	/**
	 * Reads data from the InputStream and stores it in a byte array.
	 * @return the total number of bytes read
	 */
	public static int read(InputStream in, byte[] buffer, int start, int length) throws IOException
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
	 */
	public static String cutExtension(File file)
	{
		return cutExtension(file.getName());
	}

	
	/**
	 * Removes any extension from the file name. 
	 */
	public static String cutExtension(String name)
	{
		int p = name.indexOf('.');
		return p == -1 ? name : name.substring(0, p);
	}

	
	/**
	 * Extracts the extension, i.e. the part after the first '.'
	 * from the filename.
	 * @return the file extension or null 
	 */
	public static String getExtension(File file)
	{
		return getExtension(file.getName());
	}


	/**
	 * Extracts the extension, i.e. the part after the first '.'
	 * from the filename.
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
	 * @throws IOException if a file or directory cannot be deleted.
	 */
	public static void delete(File file) throws IOException 
	{
		if (file.isDirectory()) 
		{
		    for (File f : file.listFiles())
		    	delete(f);
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
