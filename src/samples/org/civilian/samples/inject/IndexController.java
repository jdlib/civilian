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


import org.civilian.annotation.BeanParam;
import org.civilian.annotation.Get;
import org.civilian.annotation.HeaderParam;
import org.civilian.annotation.Post;
import org.civilian.annotation.Produces;
import org.civilian.controller.Controller;


/**
 * Controller for resource "/".
 */
public class IndexController extends Controller
{
	@Get @Produces("text/html") public Object get() throws Exception
	{
		return new IndexTemplate();
	}
	
	
	/**
	 * Demonstrates use of the customer annotation @RemoteIp
	 * which injects the ip of the caller.
	 */
	@Post @Produces("text/html") public Object post(
		@RemoteIp String remoteIp, 
		@HeaderParam("Accept") String acceptHeader,
		@BeanParam Registration registration)
		throws Exception
	{
		return new IndexTemplate(remoteIp, acceptHeader, registration);
	}
}
