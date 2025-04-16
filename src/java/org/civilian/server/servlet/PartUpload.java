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
package org.civilian.server.servlet;


import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.http.Part;
import org.civilian.request.Upload;


/**
 * Helper class to presents servlet parts as Civilian uploads.
 */
class PartUpload extends Upload
{
	public PartUpload(Part part, String fileName)
	{
		part_ = part;
		fileName_ = fileName;
	}
	
	
	@Override public void delete() throws IOException
	{
		part_.delete();
	}

	
	@Override public long length()
	{
		return part_.getSize(); 
	}
	
	
	@Override public String getContentType()
	{
		return part_.getContentType();
	}
	

	@Override public InputStream getInputStream() throws IOException
	{
		return part_.getInputStream();
	}

	
	@Override public String getName()
	{
		return part_.getName();
	}
	

	@Override public String getFileName()
	{
		return fileName_;
	}

	
	@Override public String toString()
	{
		return "Upload:" + getName() + ":" + fileName_;
	}
	
	
	private final Part part_;
	private final String fileName_;
}
