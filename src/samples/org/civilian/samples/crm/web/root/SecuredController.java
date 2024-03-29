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
package org.civilian.samples.crm.web.root;


import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.content.ContentType;
import org.civilian.request.Request;
import org.civilian.request.Session;
import org.civilian.resource.Path;
import org.civilian.resource.Url;
import org.civilian.response.Response;
import org.civilian.samples.crm.web.CrmConstants;
import org.civilian.samples.crm.web.SessionUser;
import org.civilian.samples.crm.web.template.ControllerTemplate;
import org.civilian.samples.crm.web.util.Script;
import org.civilian.template.Template;
import org.civilian.util.http.HeaderNames;


/**
 * A base class for all resources behind the login-wall.
 */
public abstract class SecuredController extends CrmController
{
	/**
	 * We check if we have a session and a logged-in user. 
	 * If not, we stop processing:
	 * - for an ajax request (recognized via X-Requested-With header), we send a 401 error
	 * - else we redirect to the login page,
	 */
	@Override protected final void checkAccess() throws Exception
	{
		Request request 	= getRequest();
		Response response 	= getResponse();
		Session session 	= request.getSession(false /*do not create*/);
		if (session != null)
			sessionUser_ = (SessionUser)session.getAttribute(CrmConstants.ATTR_USER);
		
		if (sessionUser_ == null)
		{
			if (request.getHeaders().is(HeaderNames.X_REQUESTED_WITH, "XMLHttpRequest"))
				response.sendError(Response.Status.UNAUTHORIZED);
			else
			{
				// remember the current request path: on successful login, the login resource
				// will redirect to that path again
				Url loginUrl = response.url().to(root.login);
				loginUrl.queryParams().add(CrmConstants.LOGIN_PATH_PARAM, request.getPath().toString());
				getResponse().redirect().to(loginUrl);
			}
		}
		else
		{
			// check access successful: initialize the locale to the locale specified at login
			request.setLocaleService(sessionUser_.localeService);
			response.setLocaleService(sessionUser_.localeService);
			checkCrmAccess();
		}
	}
	
	
	/**
	 * Allow derived resources to check if the user has enough
	 * rights to access the resource. The default implementation is empty.
	 */
	protected void checkCrmAccess() throws Exception
	{
	}

	
	/**
	 * All resources in the CRM application may produce html output, either
	 * a whole page (normal get-request) or a part (angular ajax request to load
	 * a page). This base class handles the logic, derived classed only need
	 * to return a Template object for their specific content.
	 * see {@link #getContentTemplate()}. 
	 */
	@Produces({ ContentType.Strings.TEXT_HTML, ContentType.Strings.TEXT_X_TEMPLATE })
	@Get public void render() throws Exception
	{
		Template template = getContentTemplate();
		if (template == null)
		{
			// this controller cannot produce html output
			getResponse().sendError(Response.Status.NOT_ACCEPTABLE);
		}
		else
		{
			boolean templateRequested = ContentType.TEXT_X_TEMPLATE.hasValue(getResponse().getContentType()); 
			String moduleController   = getModuleController();
			if ((moduleController != null) && (!templateRequested || isModuleRoot()))
				template = new ControllerTemplate(template, moduleController);
			if (!templateRequested)
			{
				// wrap the template in a complete page
				Path reloadPath = develop() ? getApplication().getServer().getPath() : null;
				template = new PageTemplate(getResponse(), template, getScript(), sessionUser_, showMenuBar(), reloadPath, getApplication().getAssetService().getPath());
			}
			getResponse().writeContent(template);
		}
	}


	/**
	 * Returns the content template of the resource.
	 * The default implementation returns null.
	 * Derived resource classes should return a Template object if they
	 * want to serve a HTML pages for GET-request with Accept:html.
	 */
	protected Template getContentTemplate() throws Exception
	{
		return null; 
	}


	protected Script getScript()
	{
		return Script.CRM;
	}

	
	/**
	 * Returns the currently logged in user.
	 */
	public SessionUser getSessionUser()
	{
		return sessionUser_;
	}
	
	
	/**
	 * Does the HTML representation of the resource show the main navigation
	 * menu-bar? 
	 */
	protected boolean showMenuBar()
	{
		return false;
	}
	
	
	public abstract String getModuleController();
	
	
	public boolean isModuleRoot()
	{
		return false;
	}

	
	private SessionUser sessionUser_;
}
