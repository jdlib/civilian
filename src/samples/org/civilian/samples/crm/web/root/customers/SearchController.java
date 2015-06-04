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


import org.civilian.annotation.Consumes;
import org.civilian.annotation.RequestContent;
import org.civilian.annotation.Get;
import org.civilian.annotation.Path;
import org.civilian.annotation.Post;
import org.civilian.content.ContentType;
import org.civilian.response.protocol.NgReply;
import org.civilian.samples.crm.db.SearchParam;
import org.civilian.samples.crm.db.CustomerService;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.template.SearchTemplate;
import org.civilian.samples.crm.web.util.Client;
import org.civilian.template.Template;
import org.civilian.text.keys.KeyListBuilder;


/**
 * Maps to /customers/search and /customers/search/filter
 */
public class SearchController extends CustomersController
{
	@Override protected Template getContentTemplate() throws Exception
	{
		return new SearchTemplate(false); 
	}

	
	@Consumes(ContentType.Strings.APPLICATION_JSON) 
	@Post public void search(@RequestContent SearchParam[] params) throws Exception
	{
		getResponse().writeJson(getCrmApp().getCustomerService().search(this, params));
	}
	
	
	// maps to /customers/search/filter
	@Path("filter")
	@Consumes(ContentType.Strings.APPLICATION_JSON) 
	@Get public void getFilters(NgReply reply) throws Exception
	{
		KeyListBuilder<String> builder = new KeyListBuilder<>();
		builder.add(CustomerService.FILTER_NAME, 		msg(Message.Name));
		builder.add(CustomerService.FILTER_NUMBER,		msg(Message.Number));
		builder.add(CustomerService.FILTER_CITY,		msg(Message.City));
		builder.add(CustomerService.FILTER_ZIP,			msg(Message.Zip));
		reply.addScopeVariable(Client.SEARCH_FILTERS,	builder.end());
	}
}
