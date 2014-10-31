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
package org.civilian.request;


import java.util.ArrayList;
import java.util.Collections;
import javax.servlet.http.Cookie;


/**
 * A list of cookies.
 */
public class CookieList extends ArrayList<Cookie>
{
	private static final long serialVersionUID = 1L;

	
	/**
	 * Creates an empty cookie list.
	 */
	public CookieList()
	{
	}
	
	
	/**
	 * Creates a cookie list filled with the given cookies.
	 */
	public CookieList(Cookie... cookies)
	{
		if ((cookies != null) && (cookies.length > 0))
			Collections.addAll(this, cookies);
	}
	
	
	/**
	 * Returns the cookie with the given name.
	 */
	public Cookie get(String name)
	{
		if (name != null)
		{
			for (Cookie c : this)
			{
				if ((c != null) && name.equals(c.getName()))
					return c;
			}
		}
		return null;
	}
}
