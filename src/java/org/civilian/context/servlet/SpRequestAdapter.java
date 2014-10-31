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
package org.civilian.context.servlet;


import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.civilian.Application;
import org.civilian.request.Upload;
import org.civilian.util.Iterators;


/**
 * A RequestAdapter for normal (not-multi-part) requests.
 */
class SpRequestAdapter extends ServletRequestAdapter
{
	public SpRequestAdapter(Application app, HttpServletRequest servletRequest)
	{
		super(app, servletRequest);
	}


	@Override public String getParameter(String name)
	{
		return servletRequest_.getParameter(name);
	}


	@Override public String[] getParameters(String name)
	{
		String[] p = servletRequest_.getParameterValues(name);
		return p != null ? p : EMPTY_PARAMS;
	}
	
	
	@Override public Iterator<String> getParameterNames()
	{
		return Iterators.asIterator(servletRequest_.getParameterNames());
	}
	
	
	@Override public Map<String,String[]> getParameterMap()
	{
		return servletRequest_.getParameterMap();
	}


	//----------------------------
	// uploads
	//----------------------------
	
	
	@Override public boolean hasUploads()
	{
		return false;
	}
	

	@Override public Upload getUpload(String name)
	{
		return null;
	}

	
	@Override public Upload[] getUploads(String name)
	{
		return new Upload[0];
	}

	
	@Override public Iterator<String> getUploadNames()
	{
		return Iterators.<String>empty();
	}


	/**
	 * Returns null.
	 */
	@Override public Exception getUploadError()
	{
		return null;
	}
}

