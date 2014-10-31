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
 * ServerInfo provides information about the server to which the request was sent
 * @see Request#getServerInfo()
 */
public abstract class ServerInfo
{
	/**
	 * Returns protocol and version used for the request.
	 * E.g. HTTP/1.1
	 */
	public abstract String getProtocol();

	
	/**
	 * Returns the protocol scheme used for the request.
	 * E.g. http, https, ftp.
	 */
	public abstract String getScheme();
	

	/**
	 * Returns the host name of the server.
	 */
	public abstract String getHost();
	
	
	/**
	 * Returns the port used for the request.
	 */
	public abstract int getPort();

	
	/**
	 * Returns a string representation of the ServerInfo.
	 */
	@Override public String toString()
	{
		String s = getScheme() + "://" + getHost();
		int port = getPort();
		if ((port > 0) && ((port != 80) || !"http".equals(getScheme())))
			s += ":" + port;
		return s;
	}
}
