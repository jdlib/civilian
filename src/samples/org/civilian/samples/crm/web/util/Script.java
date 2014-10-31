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
 package org.civilian.samples.crm.web.util;


import org.civilian.resource.Path;


public class Script
{
	public static final Script CRM			= new Script("/js/crm/crm.js");
	public static final Script CONTACT		= new Script("/js/crm/contact.js", CRM);
	public static final Script CUSTOMER		= new Script("/js/crm/customer.js", CRM);
	public static final Script OPPORTUNITY	= new Script("/js/crm/opportunity.js", CRM);
	public static final Script USER			= new Script("/js/crm/user.js", CRM);
	
	
	public Script(String path, Script... dependsOn)
	{
		this.dependsOn 	= dependsOn;
		this.path = Path.norm(path);
	}
	
	
	public final String path;
	public final Script[] dependsOn;
}
