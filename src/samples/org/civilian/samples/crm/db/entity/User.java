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


public class User
{
	public User()
	{
	}
	
	
	public User(Integer id)
	{
		id_ = id;
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

	
	public boolean isAdmin()
	{
		return isAdmin;
	}
	
	
	public void setIsAdmin(boolean isAdmin)
	{
		this.isAdmin = isAdmin;
	}

	
	public String getFirstName()
	{
		return firstName;
	}


	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}


	public String getEmail()
	{
		return email;
	}


	public void setEmail(String email)
	{
		this.email = email;
	}


	public String getPhone()
	{
		return phone;
	}


	public void setPhone(String phone)
	{
		this.phone = phone;
	}


	public String getPassword()
	{
		return password;
	}


	public void setPassword(String password)
	{
		this.password = password;
	}


	public String getLogin()
	{
		return login;
	}


	public void setLogin(String login)
	{
		this.login = login;
	}


	private Integer id_; 
	private String name; 
	private String firstName;
	private String email;
	private String login;
	private String password;
	private String phone;
	private boolean isAdmin;
}
