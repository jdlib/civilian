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


/**
 * FileType spans the matrix of possible expectations to the questions 
 * file.exists() and file.isDirectory(), with the possible answers of yes, no, don't care.   
 */
public enum FileType
{
	/**
	 * Represents any file or directory.
	 */
	ANY(false, null),

	/**
	 * Represents any file.
	 */
	FILE(false, Boolean.FALSE),

	/**
	 * Represents any directory.
	 */
	DIR(false, Boolean.TRUE),

	/**
	 * Represents any existent file or directory.
	 */
	EXISTENT(true, null),

	/**
	 * Represents any existent file.
	 */
	EXISTENT_FILE(true, Boolean.FALSE),

	/**
	 * Represents any existent directory.
	 */
	EXISTENT_DIR(true, Boolean.TRUE);
	
		
	FileType(boolean existent, Boolean isDirectory)
	{
		this.existent = existent;
		this.isDirectory = isDirectory;
	}
	
	
	/**
	 * Tests if the given file matches the file
	 * @param file a file
	 * @return does it match?
	 */
	public File check(File file)
	{
		return check(file, null);
	}
	
	
	/**
	 * Tests if the given file matches the file.
	 * @param file a file
	 * @param what describes the file purpose. Used to build error messages.
	 * @throws IllegalArgumentException thrown if the check fails
	 * @return does it match?
	 */
	public File check(File file, String what) throws IllegalArgumentException
	{
		Check.notNull(file, "file");
        if (existent && !file.exists())
        	checkFailed(file, what, "* does not exist");
        if ((isDirectory != null) && file.exists()) 
        {
        	boolean expectDir = isDirectory.booleanValue();
        	boolean isDir = file.isDirectory();
        	if (expectDir && !isDir)
        		checkFailed(file, what, "expected a directory but is a file: *");
        	if (!expectDir && isDir)
        		checkFailed(file, what, "expected a file but is a directory: *");
        }
        return file;
	}
	
	
	private void checkFailed(File file, String what, String message)
	{
		message = message.replace("*", "'" + file.toString() + "'");
		if (what != null)
			message = what + ": " + message; 
		throw new IllegalArgumentException(message);
	}
	
	
	public final boolean existent;
	public final Boolean isDirectory;
}
