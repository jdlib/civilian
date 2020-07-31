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
 package org.civilian.samples.test;


import java.io.File;
import org.civilian.Application;
import org.civilian.content.ContentType;
import org.civilian.samples.crm.web.CrmApp;
import org.civilian.samples.crm.web.CrmResources;
import org.civilian.server.test.TestRequest;
import org.civilian.server.test.TestResponse;
import org.civilian.server.test.TestServer;


public class SamplesTest extends Assert
{
	/**
	 * Command line interface. Pass the web-directory as argument.
	 */
	public static void main(String[] args) throws Exception
	{
		File webDir = new File(args[0]);
		
		SamplesTest test = new SamplesTest();
		test.run(webDir);
	}
	
	
	public void run(File webDir) throws Exception
	{
		TestServer server = new TestServer(webDir);
		try
		{
			// the following would read civilian.ini and initialize all apps:
			// server.init();
			
			runCrmTest(server);
		}
		finally
		{
			server.close();
		}
	}
	
	
	private void runCrmTest(TestServer server) throws Exception
	{
		// explicitly initialize the CRM app
		
		CrmApp crmApp = new CrmApp();
		server.addApp(crmApp, "crm", "/");
		
		assertEquals(Application.Status.RUNNING, crmApp.getStatus());
		assertEquals("/", crmApp.getPath().print());
		
		TestRequest request 	= new TestRequest(crmApp);
		TestResponse response	= request.getTestResponse();	
		
		// access /customers and verify redirect to login page
		request.setPath(CrmResources.root.customers);
		request.setAcceptedContentTypes(ContentType.TEXT_HTML);
		assertEquals("/customers", request.getPath().print());
		request.run();
		assertEquals(302, response.getStatus());
		assertEquals("/login?path=%2Fcustomers", response.getHeaders().get("Location"));
		
		// access /login and post a login as ajax: assert that we have a session
		request.setMethod("POST");
		request.setPath(CrmResources.root.login);
		request.setAcceptedContentTypes(ContentType.APPLICATION_JSON);
		request.setParameter("name", "user").setParameter("password", "!user").setParameter("language", "en");
		request.run();
		assertEquals(200, response.getStatus());
		assertNotNull(request.getSession(false));
		
		// access /customers again
		request.setMethod("GET");
		request.setPath(CrmResources.root.customers);
		request.setAcceptedContentTypes(ContentType.TEXT_HTML);
		request.run();
		assertEquals(200, response.getStatus());
		assertEquals(ContentType.TEXT_HTML, response.getContentType());

		System.out.println("done");
	}
}
