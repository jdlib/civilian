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


import java.io.IOException;
import org.civilian.annotation.Get;
import org.civilian.annotation.Post;
import org.civilian.request.Session;


public class LogoutController extends CrmController
{
	@Get @Post public void process() throws IOException
	{
		getApplication().disableAutoLogin();
		
		Session session = getRequest().getSession(false);
		if (session != null)
			session.invalidate();
		getResponse().redirect().to(root.login);
	}
}
