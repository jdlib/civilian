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


import org.civilian.request.ServerInfo;
import org.civilian.util.Check;


/**
 * A test implementation of {@link ServerInfo}.
 */
public class TestServerInfo extends ServerInfo
{
	@Override public String getProtocol()
	{
		return protocol_;
	}
	

	public TestServerInfo setProtocol(String protocol)
	{
		protocol_ = Check.notNull(protocol, "protocol");
		return this;
	}

	
	@Override public String getScheme()
	{
		return scheme_;
	}
	

	public TestServerInfo setScheme(String scheme)
	{
		scheme_ = Check.notNull(scheme, "scheme");
		return this;
	}

	
	@Override public String getHost()
	{
		return host_;
	}
	

	public TestServerInfo setHost(String host)
	{
		host_ = Check.notNull(host, "host");
		return this;
	}

	
	@Override public int getPort()
	{
		return port_;
	}
	
	
	public TestServerInfo setPort(int port)
	{
		port_ = port;
		return this;
	}

	
	private String protocol_ = "HTTP/1.1";
	private String host_ = "localhost";
	private String scheme_ = "http";
	private int port_ = 80;
}


