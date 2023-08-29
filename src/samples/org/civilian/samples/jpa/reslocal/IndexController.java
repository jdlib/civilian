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


import org.civilian.annotation.Get;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.samples.jpa.shared.model.Person;
import org.civilian.samples.jpa.shared.web.IndexForm;
import org.civilian.samples.jpa.shared.web.IndexTemplate;
import org.civilian.samples.jpa.reslocal.model.PersonService;


public class IndexController extends ResLocalController
{
	@Get @Post @Produces("text/html")
	public void render() throws Exception
	{
		IndexForm form = new IndexForm(this);
		String message = null;
		
		if (form.ok.isClicked() && form.read())
		{
			Person person 	= PersonService.query(form.code.getValue());
	        message 		= person != null ? "Hello '" + person.getName() + "'" : "(not found)"; 
		}

		renderPage(new IndexTemplate(form, message, CreateController.class));
	}
}
