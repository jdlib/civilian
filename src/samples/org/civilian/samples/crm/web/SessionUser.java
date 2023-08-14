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


import org.civilian.samples.crm.db.entity.User;
import org.civilian.text.service.LocaleService;


public class SessionUser
{
	public SessionUser(User user, LocaleService localeService)
	{
		this(user.getName(), user.isAdmin(), localeService);
	}
	
	
	public SessionUser(String name, boolean isAdmin, LocaleService localeService)
	{
		this.name 			= name;
		this.localeService 	= localeService;
		this.isAdmin		= isAdmin;
	}
	
	
	public final boolean isAdmin;
	public final String name;
	public final LocaleService localeService;
}
