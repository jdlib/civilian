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


import java.security.Principal;


/**
 * RequestSecurity provides access to security related
 * aspects of a Request.
 * @see Request#getSecurity()
 */
public interface RequestSecurity
{
	/**
	 * An enum to specify how the client 
	 * included a session id into the request.
	 */
	public enum SessionIdSource
	{
		/**
		 * A session id was transferred as cookie.
		 */
		FROM_COOKIE,
		
		/**
		 * A session id was embedded into the URL.
		 */
		FROM_URL,
		
		/**
		 * No session id was submitted.
		 */
		NONE
	};
	
	
	/**
	 * Was the request made using a secure channel, e.g. HTTPS?
	 */
	public boolean isSecure();
	
	
	/**
	 * Use the server specific mechanism to authenticate the user making the request.
	 * @return true, if authentication was successful, false else. In the last case, the response
	 * 		has been modified to send an appropriate message to the client 
	 */
	public boolean authenticate() throws Exception;
	

	/**
	 * Returns the authentication scheme used to protect the application requests.
	 * In a Servlet environment, the schemes BASIC_AUTH, FORM_AUTH, CLIENT_CERT_AUTH, DIGEST_AUTH are used.
	 * @return the scheme or null requests are not protected by a server mechanism.
	 */
	public String getAuthType();
	
	
	/**
	 * Returns a Principal object with the name of the current authenticated user, or null if the user has has not been authenticated
	 */
	public Principal getUserPrincipal();
	
	
	/**
	 * Returns if the authenticated user has the specified role, as defined by server mechanisms. 
	 */
	public boolean isUserInRole(String role);
	
 	
	/**
	 * Asks the server to validate the provided user name and password. 
	 * @throws Exception if validation fails.
	 */
	public void login(String username, String password) throws Exception;
	
	
	/**
	 * Sets the authenticated user to null.
	 */
	public void logout() throws Exception;
	
	
	/**
	 * Returns the session id specified by the client.
	 */
	public String getRequestedSessionId();
	
	
	/**
	 * Returns the source of the request session id.
	 */
	public SessionIdSource getRequestedSessionIdSource(); 
	
	
	/**
	 * Returns if the requested session id is valid.
	 */
	public boolean isRequestedSessionIdValid();
}
