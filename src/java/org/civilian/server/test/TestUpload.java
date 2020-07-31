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
package org.civilian.server.test;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.civilian.request.Upload;


/**
 * TestUpload is a Upload implementation for test purposes. 
 */
public class TestUpload extends Upload
{
	/**
	 * Creates an TestUpload.
	 */
	public TestUpload(String name, String fileName)
	{
		name_ = name;
		fileName_ = fileName;
		setEmpty();
	}
	
	
	@Override public void delete() throws IOException
	{
	}
	

	@Override public long length()
	{
		return length_;
	}
	

	public Upload setLength(long length)
	{
		length_ = length;
		return this;
	}

	
	@Override public String getContentType()
	{
		return contentType_;
	}

	
	public TestUpload setContentType(String type)
	{
		contentType_ = type;
		return this;
	}

	
	@Override public InputStream getInputStream() throws IOException
	{
		return inputStream_;
	}

	
	public Upload setInputStream(InputStream in)
	{
		inputStream_ = in;
		return this;
	}
	
	
	public Upload setContent(String s)
	{
		return setContent(s.getBytes());
	}

	
	public Upload setContent(byte[] bytes)
	{
		setInputStream(new ByteArrayInputStream(bytes));
		setLength(bytes.length);
		return this;
	}

	
	public Upload setEmpty()
	{
		return setContent(new byte[0]);
	}

	
	@Override public String getName()
	{
		return name_;
	}
	

	@Override public String getFileName()
	{
		return fileName_;
	}

	
	private InputStream inputStream_;
	private String contentType_;
	private long length_;
	private String name_;
	private String fileName_;
}
