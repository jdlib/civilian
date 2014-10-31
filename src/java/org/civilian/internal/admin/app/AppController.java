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
package org.civilian.internal.admin.app;


import org.civilian.Application;
import org.civilian.internal.admin.AdminController;
import org.civilian.internal.admin.AdminPathParams;


public abstract class AppController extends AdminController
{
	@Override protected void init() throws Exception
	{
		String appId = getRequest().getPathParam(AdminPathParams.APPID);
		viewedApp_ 	 = getContext().getApplication(appId);
		if (viewedApp_ == null)
			getResponse().sendRedirect(root);
	}
	
	
	@Override protected Application getViewedApp()
	{
		return viewedApp_;
	}

	
	protected Application viewedApp_;
}
