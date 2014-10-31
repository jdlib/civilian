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
 package org.civilian.samples.crm.db.entity;


public class Customer
{
	public Customer()
	{
	}
	
	
	public Customer(Integer id)
	{
		int n		= id.intValue();
		this.id 	= id;
		name 		= "Name" + n;
		number 		= "Number" + n;
		city 		= "City" + n;
		street 		= "Street" + n;
		zip 		= String.valueOf(80000 + n);
		homepage 	= "http://www.i" + n + ".com/";
	}

	
	public Integer id; 
	public String name; 
	public String number; 
	public String city; 
	public String street; 
	public String zip; 
	public String homepage; 
}
