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


/**
 * RemoteInfo provides info about the client or last proxy that sent the request.
 * @see Request#getRemoteInfo()
 */
public abstract class RemoteInfo
{
	/**
	 * Returns the IP address of the remote client.
	 */
	public abstract String getIp();
	
	
	/**
	 * Returns the fully qualified name of the remote client, or the IP address if
	 * the host name was not resolved..
	 */
	public abstract String getHost();

	
	/**
	 * Returns the source port of the remote client.
	 */
	public abstract int getPort();


	/**
	 * Returns the login name of the authenticated user which made the request.
	 * @return the login or null if not authenticated, or the client does not send
	 * 		the login in subsequent requests after authentication.  
	 */
	public abstract String getUser();

	
	@Override public String toString()
	{
		String s;
		if (getHost() != null)
			s = getHost();
		else if (getIp() != null)
			s = getIp();
		else
			s = "?";
		if (getUser() != null)
			s = getUser() + "@" + s;
		if (getPort() > 0)
			s += ":" + getPort();
		return s;
	}
}
