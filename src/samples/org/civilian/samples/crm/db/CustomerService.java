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
import org.civilian.text.msg.MsgBundleProvider;
import org.civilian.samples.crm.db.entity.Customer;
import org.civilian.samples.crm.text.Message;


public class CustomerService
{
	public static final String FILTER_NAME 		= "name";
	public static final String FILTER_NUMBER 	= "nr";
	public static final String FILTER_ZIP	 	= "zip";
	public static final String FILTER_CITY	 	= "city";
	
		
	public SearchResult search(MsgBundleProvider mp, SearchParam... params)
	{
		SearchResult result = new SearchResult
		(
			mp.msg(Message.Name),
			mp.msg(Message.Number),
			mp.msg(Message.City)
		);
		int size = new Random().nextInt(20) + 1;
		for (int i=0; i<size; i++)
		{
			Integer id		= Integer.valueOf(100 + i);
			String name 	= "Name" + (100 + i);
			String number 	= "Number" + i;
			String city 	= "City" + i;
			result.addRow(id, name, number, city);
		}
		return result;
	}


	public Customer getCustomer(Integer id)
	{
		return new Customer(id);
	}
}
