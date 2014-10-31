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
 package org.civilian.samples.crm.web.root.customers.id;


import org.civilian.samples.crm.db.entity.Customer;
import org.civilian.samples.crm.web.CrmPathParams;
import org.civilian.samples.crm.web.root.customers.CustomersController;


public abstract class CustomerController extends CustomersController
{
	protected Customer getCustomer() throws Exception
	{
		Integer customerId = getRequest().getPathParam(CrmPathParams.CUSTOMERID);
		return customerId != null ? getCrmApp().getCustomerService().getCustomer(customerId) : null;
	}
}
