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
package org.civilian.context.test;


import java.security.Principal;
import org.civilian.request.RequestSecurity;
import org.civilian.util.ArrayUtil;
import org.civilian.util.Check;


/**
 * TestSecurity is a {@link RequestSecurity} implementation for a
 * test environment.
 * @see TestRequest#getSecurity()
 */
public class TestSecurity implements RequestSecurity
{
	@Override public boolean isSecure()
	{
		return isSecure_;
	}
	
	
	public TestSecurity setSecure(boolean secure)
	{
		isSecure_ = secure;
		return this;
	}

	
	@Override public boolean authenticate() throws Exception
	{
		return authenticateResult_;
	}
	
	
	public TestSecurity setAuthenticate(boolean result) throws Exception
	{
		authenticateResult_ = result;
		return this;
	}

	
	@Override public String getAuthType()
	{
		return authType_;
	}
	
	
	public TestSecurity setAuthType(String authType)
	{
		authType_ = authType;
		return this;
	}

	
	@Override public Principal getUserPrincipal()
	{
		return userPrincipal_;
	}
	
	
	public TestSecurity setUserPrincipal(Principal principal)
	{
		userPrincipal_ = principal;
		return this;
	}
	
	
	@Override public boolean isUserInRole(String role)
	{
		return ArrayUtil.contains(userRoles_, role);
	}
	
 	
	public TestSecurity setUserRoles(String... roles)
	{
		userRoles_ = roles;
		return this;
	}

	
	@Override public void login(String username, String password) throws Exception
	{
	}
	
	
	@Override public void logout() throws Exception
	{
	}
	
	
	@Override public String getRequestedSessionId()
	{
		return requestedSessionId_;
	}
	
	
	public TestSecurity setRequestedSessionId(String id)
	{
		requestedSessionId_ = id;
		return this;
	}
	
	
	@Override public SessionIdSource getRequestedSessionIdSource() 
	{
		return requestedSessionIdSource_;
	}
	
	
	public TestSecurity setRequestedSessionIdSource(SessionIdSource source) 
	{
		requestedSessionIdSource_ = Check.notNull(source, "source");
		return this;
	}
	
	
	@Override public boolean isRequestedSessionIdValid()
	{
		return requestedSessionIdValid_;
	}
	
	
	public TestSecurity setRequestedSessionIdValid(boolean valid)
	{
		requestedSessionIdValid_ = valid;
		return this;
	}

	
	private boolean isSecure_;
	private boolean authenticateResult_;
	private String authType_;
	private Principal userPrincipal_;
	private String[] userRoles_;
	private String requestedSessionId_;
	private SessionIdSource requestedSessionIdSource_;
	private boolean requestedSessionIdValid_;
}
