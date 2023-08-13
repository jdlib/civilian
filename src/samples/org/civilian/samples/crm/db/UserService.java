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


import org.civilian.samples.crm.db.entity.User;
import org.civilian.samples.crm.text.Message;
import org.civilian.text.msg.MsgBundleProvider;


public class UserService
{
	public static final String FILTER_NAME 		= "name";
	public static final String FILTER_FIRSTNAME	= "firstName";
	public static final String FILTER_EMAIL	 	= "email";
	public static final String FILTER_PHONE	 	= "phone";
	
	private static final User USER  = new User(Integer.valueOf(123));
	private static final User POWERUSER = new User(Integer.valueOf(456));
	private static final User ADMIN = new User(Integer.valueOf(789));
	private static final User[] USERS = new User[] { USER, POWERUSER, ADMIN };
	static
	{
		USER.setName("User");
		USER.setFirstName("Adam");
		USER.setEmail("user@company.com");
		USER.setPhone("001 002 123");
		USER.setLogin("user");
		USER.setPassword("!user");
		
		POWERUSER.setName("Poweruser");
		POWERUSER.setFirstName("Berta");
		POWERUSER.setEmail("poweruser@company.com");
		POWERUSER.setPhone("001 002 456");
		POWERUSER.setLogin("poweruser");
		POWERUSER.setPassword("!poweruser");
		
		ADMIN.setName("Admin");
		ADMIN.setFirstName("Carl");
		ADMIN.setEmail("admin@company.com");
		ADMIN.setPhone("001 002 789");
		ADMIN.setLogin("admin");
		ADMIN.setPassword("!admin");
		ADMIN.setIsAdmin(true);
	}
	
		
	public User authenticate(String login, String password)
	{
		if ((login != null) && (password != null))
		{
			for (User user : USERS)
			{
				if (login.equals(user.getLogin()) && password.equals(user.getPassword()))
					return user;
			}
		}
		return null;
	}
	
	
	public SearchResult search(MsgBundleProvider mp, SearchParam... params)
	{
		SearchResult result = new SearchResult
		(
			mp.msg(Message.Name),
			mp.msg(Message.FirstName),
			mp.msg(Message.Email),
			mp.msg(Message.Phone)
		);

		for (User user : USERS)
			result.addRow(user.getId(), user.getName(), user.getFirstName(), user.getEmail(), user.getPhone());
		
		return result;
	}


	public static User getUser(Integer id)
	{
		for (User user : USERS)
		{
			if (user.getId().equals(id))
				return user;
		}
		return null;
	}
}
