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


import org.civilian.samples.crm.web.template.ModuleTemplate;
import org.civilian.samples.crm.web.root.users.id.IndexTemplate;
import org.civilian.template.Template;


public class IndexController extends UsersController
{
	@Override protected Template getContentTemplate() throws Exception
	{
		return new ModuleTemplate(new IndexTemplate());
	}


	@Override public boolean isModuleRoot()
	{
		return true;
	}
}
