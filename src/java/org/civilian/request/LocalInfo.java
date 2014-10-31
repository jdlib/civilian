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


import org.civilian.Request;


/**
 * Provides information about the IP interface on which a request was received. 
 * @see Request#getLocalInfo()
 */
public abstract class LocalInfo
{
	/**
	 * Returns the address of the IP interface on which the request was received.
	 */
	public abstract String getIp();
	

	/**
	 * Returns the host name of the IP interface on which the request was received.
	 */
	public abstract String getHost();

	
	/**
	 * Returns the port of the IP interface on which the request was received.
	 */
	public abstract int getPort();
	

	/**
	 * Returns a descriptive info string of the LocalInfo.
	 */
	@Override public String toString()
	{
		String s;
		if (getHost() != null)
			s = getHost();
		else if (getIp() != null)
			s = getIp();
		else
			s = "?";
		if (getPort() > 0)
			s += ":" + getPort();
		return s;
	}
}
