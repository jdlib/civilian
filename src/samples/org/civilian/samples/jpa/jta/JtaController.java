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
 package org.civilian.samples.jpa.jta;


import org.civilian.Controller;
import org.civilian.samples.jpa.shared.web.PageTemplate;
import org.civilian.template.Template;


/**
 * Common controller base class for all controllers of JtaApp.
 */
public abstract class JtaController extends Controller
{
	public static final JtaResources.Root root = JtaResources.root;
	
	
	/**
	 * Helper method to render a HTML page.
	 * @param content a template for the page content, which will be wrapped
	 * 		into a PageTemplate.
	 */
	protected void renderPage(Template content) throws Exception
	{
		PageTemplate pt = new PageTemplate(content);
		getResponse().writeTemplate(pt);
	}
}
