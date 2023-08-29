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


import org.civilian.controller.Controller;
import org.civilian.samples.crm.web.CrmApp;
import org.civilian.samples.crm.web.CrmResources;


/**
 * Common base class for all CRM controllers.
 */
public abstract class CrmController extends Controller implements CrmResources 
{
	/**
	 * Casts the application to the CrmApplication class.
	 */
	@Override public CrmApp getApplication()
	{
		return (CrmApp)super.getApplication();
	}
	
	
	/**
	 * Does the HTML representation of the resource show the main navigation
	 * menu-bar? 
	 */
	protected boolean showMenuBar()
	{
		return false;
	}
}
