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
package org.civilian.processor;


import java.util.ArrayList;
import java.util.regex.Pattern;
import org.civilian.Processor;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.internal.Logs;
import org.civilian.util.Check;


/**
 * IpFilter is a Processor which stops requests, if their remote ip address
 * is not contained in a whitelist of allowed ips.
 */
public class IpFilter extends Processor
{
	public static final String LOCALHOST = "localhost";
	
	
	public IpFilter(String... allowedIps)
	{
		// build tests
		ArrayList<IpTest> tests = new ArrayList<>();
		for (int i=0; i<allowedIps.length; i++)
		{
			String allowedIp = Check.notNull(allowedIps[i], "ip");
			if (allowedIp.equals("all"))
			{
				tests.clear();
				break;
			}
			else if (allowedIp.equals(LOCALHOST))
			{
				tests.add(new LiteralTest("127.0.0.1"));
				tests.add(new LiteralTest("0:0:0:0:0:0:0:1"));
			}
			else if (allowedIp.contains("*"))
				tests.add(new WildcardTest(allowedIp)); 
			else
				tests.add(new LiteralTest(allowedIp));
		}

		if (tests.size() > 0)
			tests.toArray(ipTests_ = new IpTest[tests.size()]);
		else
			ipTests_ = null;
		
		// build info
		StringBuilder sb = new StringBuilder("Allowed IPs: ");
		if (ipTests_ != null)
		{
			for (int i = 0; i<ipTests_.length; i++)
			{
				if (i > 0)
					sb.append(", ");
				sb.append(ipTests_[i]);
			}
		}
		else
			sb.append("all");
		
		info_ = sb.toString();
	}
	
	
	@Override public String getInfo() 
	{
		return info_;
	}
	
	
	public int size()
	{
		return ipTests_.length;
	}
	
	
	public IpTest getIpTest(int i)
	{
		return ipTests_[i]; 
	}
	
	
	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		// if no ip tests are defined, we allow all ips
		if (ipTests_ != null)
		{
			String remoteIp = request.getRemoteInfo().getIp();
			if (!isAllowedIp(remoteIp))
			{
				if (Logs.PROCESSOR.isDebugEnabled())
					Logs.PROCESSOR.debug("forbidden ip " + remoteIp);
				request.getResponse().sendError(Response.Status.FORBIDDEN);
				return true; // we have processed the request, no further processing
			}
		}
		return chain.next(request); // ip allowed: continue processing 
	}
	
	
	public boolean isAllowedIp(String ip)
	{
		if (ipTests_ == null)
			return true;
		
		if (ip != null)
		{
			for (int i=0; i<ipTests_.length; i++)
			{
				if (ipTests_[i].isAllowed(ip))
					return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Implements a test if a request with a certain IP should be allowed.
	 */
	public static abstract class IpTest
	{
		public abstract boolean isAllowed(String ip);

		@Override public abstract String toString();
	}

	
	private static class LiteralTest extends IpTest
	{
		public LiteralTest(String ip)
		{
			ip_ = ip;
		}
		
		@Override public boolean isAllowed(String ip)
		{
			return ip_.equals(ip);
		}

		@Override public String toString()
		{
			return ip_;
		}
		
		private String ip_;
	}

	
	private static class WildcardTest extends IpTest
	{
		public WildcardTest(String pattern)
		{
			ipPattern_ = Pattern.compile(pattern.replace(".", "\\.").replace("*", "([0-9]{1,3})"));
			display_   = pattern;
		}

		@Override public boolean isAllowed(String ip)
		{
			return ipPattern_.matcher(ip).matches();
		}

		@Override public String toString()
		{
			return display_;
		}
		
		private Pattern ipPattern_;
		private String display_;
	}
	
	
	private final IpTest[] ipTests_;
	private final String info_;
}
