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
 package org.civilian.samples.jpa.reslocal;


import org.civilian.controller.Controller;
import org.civilian.samples.jpa.reslocal.model.JpaContext;
import org.civilian.samples.jpa.shared.web.PageTemplate;
import org.civilian.template.Template;


/**
 * Base class for all controllers of the Jpa-ResourceLocal sample.
 */
public abstract class ResLocalController extends Controller
{
	/**
	 * Returns the ResLocalApp.
	 */
	@Override public ResLocalApp getApplication()
	{
		return (ResLocalApp)super.getApplication();
	}
	
	
	/**
	 * Closes the entity manager used by the controller after
	 * a request was processed. If processing threw an exception, any
	 * current transaction is rolled back, else it is committed. 
	 */
	@Override protected final void exit()
	{
		JpaContext.closeEm(getException() == null);
	}
	
	
	/**
	 * Helper method to render a HTML page response.
	 * @param content a template for the page content
	 */
	protected void renderPage(Template content) throws Exception
	{
		PageTemplate pt = new PageTemplate(content);
		getResponse().writeContent(pt);
	}
}
