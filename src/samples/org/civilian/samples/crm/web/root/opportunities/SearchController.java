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
package org.civilian.samples.crm.web.root.opportunities;


import org.civilian.annotation.Consumes;
import org.civilian.annotation.RequestContent;
import org.civilian.annotation.Get;
import org.civilian.annotation.Segment;
import org.civilian.annotation.Post;
import org.civilian.content.ContentType;
import org.civilian.response.protocol.NgReply;
import org.civilian.samples.crm.db.SearchParam;
import org.civilian.samples.crm.db.OpportunityService;
import org.civilian.samples.crm.text.Message;
import org.civilian.samples.crm.web.template.SearchTemplate;
import org.civilian.samples.crm.web.util.Client;
import org.civilian.template.Template;
import org.civilian.text.keys.KeyListBuilder;


/**
 * Maps to /opportunities/search
 */
public class SearchController extends OpportunitiesController
{
	@Override protected Template getContentTemplate() throws Exception
	{
		return new SearchTemplate(false); 
	}

	
	@Consumes(ContentType.Strings.APPLICATION_JSON) 
	@Post public void search(@RequestContent SearchParam[] params) throws Exception
	{
		getResponse().writeJson(getApplication().getOpportunityService().search(this, params));
	}
	
	
	// maps to /opportunities/search/filter
	@Segment("filter")
	@Consumes(ContentType.Strings.APPLICATION_JSON) 
	@Get public void getFilters(NgReply reply) throws Exception
	{
		KeyListBuilder<String> builder = new KeyListBuilder<>();
		builder.add(OpportunityService.FILTER_NAME, 		msg(Message.Name));
		builder.add(OpportunityService.FILTER_VOLUME,		msg(Message.Volume));
		builder.add(OpportunityService.FILTER_PROPABILITY,	msg(Message.Probability));
		reply.addScopeVariable(Client.SEARCH_FILTERS,	builder.end());
	}
}
