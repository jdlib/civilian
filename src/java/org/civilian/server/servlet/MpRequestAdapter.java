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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.civilian.application.Application;
import org.civilian.request.Upload;
import org.civilian.request.Uploads;
import org.civilian.util.ArrayUtil;
import org.civilian.util.IoUtil;


/**
 * A RequestAdapter for multi part requests, i.e. with content type multipart/form-data.
 */
class MpRequestAdapter extends ServletRequestAdapter
{
	public MpRequestAdapter(Application app, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws Exception
	{
		super(app, servletRequest, servletResponse);
		
		// extract parameters from the request and put it into our parameter map
		parameters_ = new HashMap<>(servletRequest.getParameterMap());
		
		// now parse the parts of the request
		String encoding = servletRequest.getCharacterEncoding();
		if (encoding == null)
			encoding = app.getDefaultEncoding().name();

		Uploads uploads;
		try
		{
			Map<String,Upload[]> uploadMap = new HashMap<>();
			for (Part part : servletRequest_.getParts())
				readPart(part, encoding, uploadMap);
			uploads = Uploads.of(uploadMap);
		}
		catch(IllegalStateException e)
		{
			uploads = Uploads.of(e);
		}
		uploads_ = uploads;
	}
	
	
	private void readPart(Part part, String encoding, Map<String,Upload[]> uploadMap) throws IOException
	{
		PartInfo info = new PartInfo(part);
		if (info.isFormData)
		{
			String partName = part.getName();
			if (info.fileName != null)
			{
				addParam(partName, info.fileName);
				PartUpload partUpload = new PartUpload(part, info.fileName);
				addUpload(partName, partUpload, uploadMap);
			}
			else
				addParam(partName, readPartValue(part, encoding));
		}
	}
	
	
	private static void addUpload(String name, PartUpload partUpload, Map<String,Upload[]> uploadMap)
	{
		Upload[] uploads = uploadMap.get(name);
		if (uploads == null)
			uploads = new Upload[] { partUpload };
		else
			uploads = ArrayUtil.addLast(uploads, partUpload);
		
		uploadMap.put(name, uploads);
	}
	
	
	private String readPartValue(Part part, String encoding) throws IOException
	{
		char c[] = new char[(int)part.getSize()];
		try (InputStreamReader in = new InputStreamReader(part.getInputStream(), encoding))
		{
			int length = IoUtil.read(in, c, 0, c.length);
			return new String(c, 0, length);
		}
	}


	@Override public String getParam(String name)
	{
		String[] p = getParams(name);
		return p.length == 0 ? null : p[0];
	}


	@Override public String[] getParams(String name)
	{
		String[] p = parameters_.get(name);
		return p != null ? p : EMPTY_PARAMS;
	}
	
	
	@Override public Iterator<String> getParamNames()
	{
		return parameters_.keySet().iterator();
	}
	
	
	@Override public Map<String,String[]> getParamMap()
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
	
	
	@Override public Uploads getUploads()
	{
		return uploads_;
	}
	
	
	private final HashMap<String,String[]> parameters_; 
	private final Uploads uploads_; 
}
