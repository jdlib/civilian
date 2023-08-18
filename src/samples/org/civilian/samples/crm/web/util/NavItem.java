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
 package org.civilian.samples.crm.web.util;


import org.civilian.resource.Resource;
import org.civilian.resource.Url;
import org.civilian.response.Response;
import org.civilian.response.ResponseProvider;


/**
 * NavItem holds navigation information used by the client.
 * It has representational elements:
 * <ul>
 * <li>a label, localized to the client language
 * <li>an image url, if an image will be displayed with the item
 * <li>an indent level, if the item is presented in a level navigation (e.g. in a tree)
 * </ul>   
 * and asset information:
 * <ul>
 * <li>script urls, if the module associated with the item, needs script assests
 * <li>a template url, if the module associated with the item, needs this html template
 * </ul>
 */
public class NavItem
{
	private static final Integer DEFAULT_ID = Integer.valueOf(0);
	
	
	public NavItem(ResponseProvider rp)
	{
		response_ = rp.getResponse();
	}
	
	
	public NavItem setLabel(String text)
	{
		label = text;
		return this;
	}
	

	public NavItem setTemplateUrl(Resource resource)
	{
		Url url = new Url(response_, resource);
		for (int i=0; i<url.getPathParamCount(); i++)
			url.setPathParam(i, DEFAULT_ID);
		this.templateUrl = url.toString(); 
		return this;
	}

	
	public NavItem setScriptUrls(String scriptUrls)
	{
		this.scriptUrls = scriptUrls;
		return this;
	}
	
	
	public NavItem setLevel(int level)
	{
		this.level = Integer.valueOf(level);
		return this;
	}
	
	
	public String label;
	public String scriptUrls;		// as required by civilian.load(item)
	public String templateUrl;		// as required by civilian.load(item)
	public Integer level;
	transient Response response_; 
}
