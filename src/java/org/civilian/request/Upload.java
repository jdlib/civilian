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
package org.civilian.request;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * Upload represents an uploaded file.
 * @see Request#getUploads()
 */
public abstract class Upload
{
	/**
	 * An empty Upload array.
	 */
	public static final Upload[] EMPTY_LIST = new Upload[0];
	
		
	/**
	 * Deletes the upload file including any temporary files.
	 * @throws IOException if an I/O error occurs
	 */
	public abstract void delete() throws IOException;

	
	/**
	 * @return the length of the uploaded file.
	 */
	public abstract long length();

	
	/**
	 * @return the content type of the uploaded file.
	 */
	public abstract String getContentType();

	
	/**
	 * @return an input stream to the uploaded data.
	 * @throws IOException if an I/O error occurs
	 */
	public abstract InputStream getInputStream() throws IOException;

	
	/**
	 * Writes the upload to the specified file.
	 * @param file a file
	 * @throws IOException if an I/O error occurs
	 */
	public void write(File file) throws IOException
	{
		try (FileOutputStream out = new FileOutputStream(file))
		{
			getInputStream().transferTo(out);
		}
	}

	
	/**
	 * @return the name of the upload parameter. 
	 */
	public abstract String getName();


	/**
	 * @return the filename of the upload, indicating the original
	 * filename of the uploaded file.
	 */
	public abstract String getFileName();
}
