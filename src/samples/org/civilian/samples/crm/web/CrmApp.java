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


import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.processor.ProcessorConfig;
import org.civilian.samples.crm.db.ContactService;
import org.civilian.samples.crm.db.CustomerService;
import org.civilian.samples.crm.db.OpportunityService;
import org.civilian.samples.crm.db.UserService;


public class CrmApp extends Application
{
	public CrmApp()
	{
		super(CrmPathParams.MAP, ".root" /* root controller package*/);
	}
	
	
	@Override protected void init(AppConfig config) throws Exception
	{
		config.setResourceRoot(CrmResources.root);
		autoLogin_ 				= develop() && config.getSettings().getBoolean("autologin", false);
		userService_			= new UserService();
		customerService_		= new CustomerService();
		contactService_			= new ContactService();
		opportunityService_		= new OpportunityService();
	}

	
	@Override protected void initProcessors(ProcessorConfig config) throws Exception
	{
		// to switch on gzip compression, uncomment the next line:
		// config.addBefore(ResourceDispatch.class, new Compressor());
	}

	
	public boolean doAutoLogin()
	{
		return autoLogin_;
	}
	

	public void disableAutoLogin()
	{
		autoLogin_ = false;
	}

	
	public UserService getUserService()
	{
		return userService_;
	}

	
	public CustomerService getCustomerService()
	{
		return customerService_;
	}

	
	public ContactService getContactService()
	{
		return contactService_;
	}

	
	public OpportunityService getOpportunityService()
	{
		return opportunityService_;
	}

	
	@Override protected void close() throws Exception
	{
	}
	
	
	private boolean autoLogin_;
	private UserService userService_;
	private CustomerService customerService_;
	private ContactService contactService_;
	private OpportunityService opportunityService_;
}
