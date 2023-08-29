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


import org.civilian.annotation.Get;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.util.NavItemList;


/**
 * Returns navigation items for the customer detail pages.
 */
public class NavigationController extends CustomersController
{
	@Get public void getNavigation() throws Exception
	{
		NavItemList navItems = new NavItemList(this);
		navItems.add()
			.setLabel(msg(Message.MasterData))
			.setTemplateUrl(root.customers.$customerId.masterdata);

		for (int i=1; i<=5; i++)
		{
			navItems.add()
				.setLabel(msg(Message.Details) + i)
				.setTemplateUrl(root.customers.$customerId.details);
		}
		
		getResponse().writeJson(navItems);
	}
}
