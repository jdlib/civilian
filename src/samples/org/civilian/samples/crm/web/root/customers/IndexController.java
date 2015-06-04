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
 package org.civilian.samples.crm.web.root.customers;


import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.template.MultiPanelTemplate;
import org.civilian.samples.crm.web.template.ModuleTemplate;
import org.civilian.template.Template;


/**
 * Serves the customer module.
 * Entry points:
 * GET HTML /customers
 */
public class IndexController extends CustomersController
{
	@Override protected Template getContentTemplate() throws Exception
	{
		return new ModuleTemplate(new MultiPanelTemplate(Message.Customer));
	}


	@Override public boolean isModuleRoot()
	{
		return true;
	}
}
