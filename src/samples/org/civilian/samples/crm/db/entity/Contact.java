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


public class Contact
{
	public Contact()
	{
	}
	
	
	public Contact(Integer id)
	{
		id_ 		= id;
		int n		= id.intValue();
		name 		= "Name" + n;
		firstName	= "FirstName" + n;
		customer	= "Customer" + n;
		phone		= "344 566 " + n;
	}
	
	
	public Integer getId()
	{
		return id_;
	}


	public String getName()
	{
		return name;
	}	
	
	
	public void setName(String name)
	{
		this.name = name;
	}	

	
	public String getFirstName()
	{
		return firstName;
	}


	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}


	public String getPhone()
	{
		return phone;
	}


	public void setPhone(String phone)
	{
		this.phone = phone;
	}


	public String getCustomer()
	{
		return customer;
	}


	public void setCustomer(String customer)
	{
		this.customer = customer;
	}


	private Integer id_; 
	private String name; 
	private String firstName;
	private String customer;
	private String phone;
}
