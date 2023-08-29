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
package org.civilian.samples.crm.web.root.users;


import org.civilian.response.Response;
import org.civilian.samples.crm.web.root.SecuredController;
import org.civilian.samples.crm.web.util.Script;


/**
 * Common base class for all user resources.
 */
public abstract class UsersController extends SecuredController
{
	/**
	 * All customer resources need the customer.js script.
	 */
	@Override public Script getScript()
	{
		return Script.USER;
	}


	@Override public String getModuleController()
	{
		return "UserController";
	}


	@Override protected final void checkCrmAccess() throws Exception
	{
		if (!getSessionUser().isAdmin)
			getResponse().sendError(Response.Status.FORBIDDEN);
	}
}
