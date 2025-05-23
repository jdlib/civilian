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


import java.util.Iterator;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.civilian.application.Application;
import org.civilian.request.Uploads;
import org.civilian.util.Iterators;


/**
 * A RequestAdapter for normal (not-multi-part) requests.
 */
class SpRequestAdapter extends ServletRequestAdapter
{
	public SpRequestAdapter(Application app, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
	{
		super(app, servletRequest, servletResponse);
	}


	@Override public String getParam(String name)
	{
		return servletRequest_.getParameter(name);
	}


	@Override public String[] getParams(String name)
	{
		String[] p = servletRequest_.getParameterValues(name);
		return p != null ? p : EMPTY_PARAMS;
	}
	
	
	@Override public Iterator<String> getParamNames()
	{
		return Iterators.asIterator(servletRequest_.getParameterNames());
	}
	
	
	@Override public Map<String,String[]> getParamMap()
	{
		return servletRequest_.getParameterMap();
	}


	//----------------------------
	// uploads
	//----------------------------
	
	
	@Override public Uploads getUploads()
	{
		return Uploads.EMPTY;
	}
}

