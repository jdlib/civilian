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
 package org.civilian.samples.crm.web.root.contacts.id;


import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.content.ContentType;
import org.civilian.response.protocol.NgReply;
import org.civilian.samples.crm.db.entity.Contact;
import org.civilian.samples.crm.web.util.Client;
import org.civilian.template.Template;


public class IndexController extends ContactController
{
	/**
	 * Returns a Contact object as JSON
	 */
	@Get @Produces(ContentType.Strings.APPLICATION_JSON)
	public void getData(NgReply reply) throws Exception
	{
		Contact contact = getContact();
		if (contact != null)
			reply.addScopeVariable(Client.OBJECT, contact);
	}


	@Override protected Template getContentTemplate() throws Exception
	{
		return new IndexTemplate();
	}
}
