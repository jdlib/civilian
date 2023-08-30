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


import javax.inject.Inject;
import org.civilian.annotation.Get;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.samples.jpa.jta.model.PersonService;
import org.civilian.samples.jpa.shared.model.Person;
import org.civilian.samples.jpa.shared.web.CreateForm;
import org.civilian.samples.jpa.shared.web.CreateTemplate;


/**
 * A Controller for resource "/create".
 */
public class CreateController extends JtaController
{
	@Get @Post @Produces("text/html") 
	public void render() throws Exception
	{
		CreateForm form = new CreateForm(this);
		if (form.ok.isClicked() && form.read())
		{
			Person person = new Person();
			person.setCode(form.code.getValue());
			person.setFirstName(form.firstName.getValue());
			person.setLastName(form.lastName.getValue());
			
			personService.persist(person);
			getResponse().sendRedirect().to(root);
		}
		else
			renderPage(new CreateTemplate(form));
	}
	
	
	@Inject private PersonService personService; 
}
