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
package org.civilian.samples.crm.web.root.users.id;


import org.civilian.samples.crm.db.UserService;
import org.civilian.samples.crm.db.entity.User;
import org.civilian.samples.crm.web.CrmPathParams;
import org.civilian.samples.crm.web.root.users.UsersController;


public abstract class UserController extends UsersController
{
	protected User getUser() throws Exception
	{
		Integer userId = getRequest().getPathParam(CrmPathParams.USERID);
		return userId != null ? UserService.getUser(userId) : null;
	}
}
