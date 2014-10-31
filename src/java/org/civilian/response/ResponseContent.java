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
package org.civilian.response;


import org.civilian.Response;


/**
 * ResponseContent is an object which can generate
 * content of a response.
 * If a parameter of a controller action method is derived
 * of ResponseContent, it will be handled as follows:
 * <ul>
 * <li>A new instance of the parameter type is instantiated and
 * 		injected into the parameter variable, when the method is called
 * <li>Once the method has finished, and the response was not commited,
 * 		the instantiated object is written to the response. 
 * </ul>	
 * The {@link org.civilian.annotation.ResponseContent} annotation offers
 * similar functionality.
 */
public abstract class ResponseContent
{
	/**
	 * Writes content to the response. This method is automatically called if you 
	 * inject a ResponseContent object in a controller action method.
	 * The default implementation calls {@link Response#writeContent(Object)} with this
	 * as argument. 
	 */
	public void writeTo(Response response) throws Exception
	{
		response.writeContent(this);
	}
}
