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
 package org.civilian.samples.crm.db;


import java.util.Random;
import org.civilian.provider.MsgBundleProvider;
import org.civilian.samples.crm.db.entity.Opportunity;
import org.civilian.samples.crm.text.Message;


public class OpportunityService
{
	public static final String FILTER_NAME 			= "name";
	public static final String FILTER_VOLUME		= "volume";
	public static final String FILTER_PROPABILITY 	= "probability";
	
		
	public SearchResult search(MsgBundleProvider mp, SearchParam[] params)
	{
		SearchResult result = new SearchResult
		(
			mp.msg(Message.Name),
			mp.msg(Message.Volume),
			mp.msg(Message.Probability)
		);
		int size = new Random().nextInt(20) + 1;
		for (int i=0; i<size; i++)
		{
			Integer id		= Integer.valueOf(100 + i);
			String name 	= "Name" + (100 + i);
			int n			= i +1;
			Integer volume	= Integer.valueOf(n * 10000 + n * 100);
			Integer prob	= Integer.valueOf((n * 17) % 100);
			result.addRow(id, name, volume, prob);
		}
		return result;
	}


	public Opportunity getOpportunity(Integer id)
	{
		return new Opportunity(id);
	}
}
