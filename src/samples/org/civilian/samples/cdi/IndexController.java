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
 package org.civilian.samples.cdi;


import javax.inject.Inject;
import org.civilian.Controller;
import org.civilian.annotation.Get;
import org.civilian.samples.cdi.service.Service;


public class IndexController extends Controller
{
	@Inject private Service service;
	
	
	@Get public void get() throws Exception
	{
		getResponse().writeText("Testing CDI - got injected service instance:\n" + service);
	}
}
