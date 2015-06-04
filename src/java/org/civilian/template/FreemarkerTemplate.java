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
package org.civilian.template;


import java.io.IOException;


/**
 * A Template implementation which is based on a Freemarker template.
 */
public class FreemarkerTemplate extends org.civilian.template.Template
{
	/**
	 * Creates a new FreemarkerTemplate. 	 
	 * @param template the freemarker tempate
	 * @param model the model
	 */
	public FreemarkerTemplate(freemarker.template.Template template, Object model)
	{
		template_ 	= template;
		model_ 		= model;
	}
	
	
	@Override protected void print() throws IOException, freemarker.template.TemplateException
	{
		template_.process(model_, out);  
	}

	
	private Object model_;
	private freemarker.template.Template template_;
}
