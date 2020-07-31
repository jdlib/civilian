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


import org.civilian.request.LocalInfo;
import org.civilian.util.Check;


/**
 * TestLocalInfo is a {@link LocalInfo} implementation for a test environment.
 * @see TestRequest#getLocalInfo()
 */
public class TestLocalInfo extends LocalInfo
{
	@Override public String getIp()
	{
		return ip_;
	}

	
	public TestLocalInfo setIp(String ip)
	{
		ip_ = Check.notNull(ip, "ip");
		return this;
	}

	
	@Override public String getHost()
	{
		return host_ != null ? host_ : ip_;
	}

	
	public TestLocalInfo setHost(String host)
	{
		host_ = host;
		return this;
	}

	
	@Override public int getPort()
	{
		return port_;
	}
	

	public TestLocalInfo setPort(int port)
	{
		port_ = port;
		return this;
	}

	
	private String host_ = "localhost";
	private String ip_ = "127.0.0.1";
	private int port_ = 80;
}
