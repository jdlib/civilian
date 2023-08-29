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
package org.civilian.samples.inject;


public class Registration
{
	public String getFirstName()
	{
		return firstName_;
	}
	
	
	public void setFirstName(String firstName)
	{
		firstName_ = firstName;
	}
	
	
	public String getLastName()
	{
		return lastName_;
	}
	
	
	public void setLastName(String lastName)
	{
		lastName_ = lastName;
	}
	
	
	public String getEmail()
	{
		return email_;
	}
	
	
	public void setEmail(String email)
	{
		email_ = email;
	}
	
	
	public int getAge()
	{
		return age_;
	}

	
	public void setAge(int age)
	{
		age_ = age;
	}


	private String firstName_;
	private String lastName_;
	private String email_;
	private int age_;
}
