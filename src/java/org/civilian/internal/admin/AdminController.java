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
package org.civilian.internal.admin;


import org.civilian.Application;
import org.civilian.Controller;
import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.content.ContentType;
import org.civilian.resource.Resource;
import org.civilian.template.Template;


/**
 * Controller base class of the admin application.
 */
public abstract class AdminController extends Controller implements AdminResources
{
	@Get @Produces(ContentType.Strings.TEXT_HTML)
	public void getHtml() throws Exception
	{
		Resource appResource = getRequest().getResource();
		if (appResource != root.$appId.resources)
			appResource = root.$appId.settings;
		getResponse().writeTemplate(new PageTemplate(getContentTemplate(), (AdminApp)getApplication(), getViewedApp(), appResource));
	}
	

	protected abstract Template getContentTemplate();
	
	
	protected abstract Application getViewedApp();
}
