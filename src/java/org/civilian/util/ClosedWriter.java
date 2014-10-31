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


import java.io.Writer;
import java.io.IOException;


/**
 * ClosedWriter is a Writer that helps to implement efficient 
 * proxy Writer classes. A proxy writer (like PrintWriter, FilterWriter, etc.) uses 
 * another writer to which he writes. For correctness he has to check 
 * every write if the underlying writer was not closed before.
 * Instead of checking this, the output writer can be exchanged with a 
 * ClosedWriter instead.
 */
public class ClosedWriter extends Writer
{
	public static final ClosedWriter INSTANCE = new ClosedWriter();
		
	
	private ClosedWriter()
	{
	}
	
	
	/**
	 * Does nothing.
	 */
	@Override public void close()
	{
	}
	
	
	/**
	 * Throws an exception.
	 */
	@Override public void flush() throws IOException
	{
	    throw closedException();
	}
	
	
	/**
	 * Throws an exception.
	 */
	@Override public void write(char c[], int off, int len) throws IOException
	{
	    throw closedException();
	}
	

	private IOException closedException() throws IOException
	{
	    return new IOException("Writer is closed");
	}
}
