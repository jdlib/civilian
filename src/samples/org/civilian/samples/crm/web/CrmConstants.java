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
 package org.civilian.samples.crm.web;


import org.civilian.asset.AssetList;


public interface CrmConstants
{
	public static final String ATTR_USER = "user";
	public static final String LOGIN_PATH_PARAM = "path";
	

	// CSS_ASSETS contains all css-assets by the crm application
	// PageTemplate uses it to print the stylesheet links
	public static final AssetList CSS_ASSETS = new AssetList
	(
		// this is a css list:
		AssetList.CSS_TYPE,	
		// items:
		"css/lib/bootstrap.css", 
		"css/lib/toastr.css",
		"css/lib/flick/jquery-ui-1.10.3.custom.min.css",
		"css/samples.css",
		"css/crm.css"
	); 
}
