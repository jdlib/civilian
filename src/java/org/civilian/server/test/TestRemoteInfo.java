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
package org.civilian.server.test;


import org.civilian.request.RemoteInfo;
import org.civilian.util.Check;


/**
 * A test implementation of {@link RemoteInfo}.
 */
public class TestRemoteInfo extends RemoteInfo
{
	@Override public String getIp()
	{
		return ip_;
	}

	
	public TestRemoteInfo setIp(String ip)
	{
		ip_ = Check.notNull(ip, "ip");
		return this;
	}

	
	@Override public String getHost()
	{
		return host_ != null ? host_ : ip_;
	}

	
	public TestRemoteInfo setHost(String host)
	{
		host_ = host;
		return this;
	}

	
	@Override public String getUser()
	{
		return user_;
	}

	
	public TestRemoteInfo setUser(String user)
	{
		user_ = user;
		return this;
	}

	
	@Override public int getPort()
	{
		return port_;
	}
	

	public TestRemoteInfo setPort(int port)
	{
		port_ = port;
		return this;
	}

	
	public String user_;
	private String host_ = "localhost";
	private String ip_ = "127.0.0.1";
	private int port_ = 80;
}


