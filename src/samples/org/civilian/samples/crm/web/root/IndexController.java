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
 package org.civilian.samples.crm.web.root;


import org.civilian.Template;


/**
 * Landing page of the CRM application.
 * Builds the main navigation menu-bar and shows the welcome page.
 */
public class IndexController extends SecuredController 
{
	/**
	 * Returns true.
	 */
	@Override protected boolean showMenuBar()
	{
		return true;
	}

	
	@Override protected Template getContentTemplate() throws Exception
	{
		return new IndexTemplate();
	}
	
	
	@Override public String getModuleController()
	{
		return null;
	}
}
