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
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.civilian.Application;
import org.civilian.request.Upload;
import org.civilian.util.ArrayUtil;
import org.civilian.util.IoUtil;


/**
 * A RequestAdapter for multi part requests, i.e. with content type multipart/form-data.
 */
class MpRequestAdapter extends ServletRequestAdapter
{
	public MpRequestAdapter(Application app, HttpServletRequest servletRequest) throws Exception
	{
		super(app, servletRequest);
		
		try
		{
			// extract parameters from the request and put it into our parameter map
			parameters_ = new HashMap<>(servletRequest.getParameterMap());
			
			// now parse the parts of the request
			String encoding = servletRequest.getCharacterEncoding();
			if (encoding == null)
				encoding = app.getDefaultCharEncoding();
			for (Part part : servletRequest_.getParts())
				readPart(part, encoding);
		}
		catch(IllegalStateException e)
		{
			uploadError_ = e;
		}
	}
	
	
	private void readPart(Part part, String encoding) throws IOException
	{
		PartInfo info = new PartInfo(part);
		if (info.isFormData)
		{
			String partName = part.getName();
			if (info.fileName != null)
			{
				addParam(partName, info.fileName);
				PartUpload upload = new PartUpload(part, info.fileName);
				addUpload(partName, upload);
			}
			else
				addParam(partName, readPartValue(part, encoding));
		}
	}
	
	
	private void addUpload(String name, PartUpload upload)
	{
		Upload[] uploads = uploads_.get(name);
		if (uploads == null)
			uploads = new Upload[] { upload };
		else
			uploads = ArrayUtil.addLast(uploads, upload);
		
		uploads_.put(name, uploads);
	}
	
	
	private String readPartValue(Part part, String encoding) throws IOException
	{
		char c[] = new char[(int)part.getSize()];
		InputStreamReader in = new InputStreamReader(part.getInputStream(), encoding);
		try
		{
			int length = IoUtil.read(in, c, 0, c.length);
			return new String(c, 0, length);
		}
		finally
		{
			in.close();
		}
	}


	@Override public String getParameter(String name)
	{
		String[] p = getParameters(name);
		return p.length == 0 ? null : p[0];
	}


	@Override public String[] getParameters(String name)
	{
		String[] p = parameters_.get(name);
		return p != null ? p : EMPTY_PARAMS;
	}
	
	
	@Override public Iterator<String> getParameterNames()
	{
		return parameters_.keySet().iterator();
	}
	
	
	@Override public Map<String,String[]> getParameterMap()
	{
		return new HashMap<>(parameters_); 
	}
	
	
	private void addParam(String name, String value)
	{
		String[] v = parameters_.get(name);
		if (v == null)
			v = new String[] { value };
		else
			v = ArrayUtil.addLast(v, value);
		parameters_.put(name, v);
	}
	
	
	//----------------------------
	// uploads
	//----------------------------
	
	
	@Override public Upload getUpload(String name)
	{
		Upload[] uploads = uploads_.get(name);
		return uploads != null ? uploads[0] : null;
	}

	
	@Override public Upload[] getUploads(String name)
	{
		Upload[] uploads = uploads_.get(name);
		return uploads != null ? uploads.clone() : Upload.EMPTY_LIST;
	}

	
	@Override public Iterator<String> getUploadNames()
	{
		return uploads_.keySet().iterator();
	}
	

	@Override public boolean hasUploads()
	{
		return uploads_.size() > 0;
	}
	
	
	@Override public Exception getUploadError()
	{
		return uploadError_;
	}
	

	private Exception uploadError_;
	private HashMap<String,String[]> parameters_ = new HashMap<>(); 
	private HashMap<String,Upload[]> uploads_ = new HashMap<>(); 
}
