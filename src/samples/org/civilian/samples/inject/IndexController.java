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
 package org.civilian.samples.inject;


import org.civilian.Controller;
import org.civilian.Response;
import org.civilian.annotation.BeanParam;
import org.civilian.annotation.Get;
import org.civilian.annotation.HeaderParam;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.annotation.Parameter;


/**
 * Controller for resource "/".
 */
public class IndexController extends Controller
{
	@Get @Produces("text/html") public void get() throws Exception
	{
		getResponse().writeTemplate(new IndexTemplate());
	}
	
	
	/**
	 * Demonstrates use of the customer annotation @RemoteIp
	 * which injects the ip of the caller.
	 */
	@Post @Produces("text/html") public void post(@RemoteIp String remoteIp, 
		@HeaderParam("Accept") String acceptHeader,
		@Parameter("age") int age,
		@BeanParam Registration registration,
		Response response)
		throws Exception
	{
		response.writeTemplate(new IndexTemplate(remoteIp, acceptHeader, registration));
	}
}
