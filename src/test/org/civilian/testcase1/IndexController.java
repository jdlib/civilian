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
package org.civilian.testcase1;


import org.civilian.annotation.Consumes;
import org.civilian.annotation.Get;
import org.civilian.annotation.Produces;
import org.civilian.annotation.Parameter;


public class IndexController extends Test1Controller
{
	@Consumes("application/json") 
	@Produces("application/json") 
	@Get public void getJson(@Parameter("what") String what)
	{
	}
	
	
	@Produces("text/html") public void ignoredNoMethodAnnotation()
	{
	}


	@Get public static void ignoredStatic()
	{
	}


	@Get public String ignoredNotVoid()
	{
		return null;
	}
}
