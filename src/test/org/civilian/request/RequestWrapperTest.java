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
package org.civilian.request;


import org.civilian.CivTest;
import org.junit.Test;


public class RequestWrapperTest extends CivTest
{
	@Test public void test()  throws Exception
	{
		WrapperProxy<Request> requestWp = new WrapperProxy<>(Request.class); 
		RequestWrapper requestWrapper 	= new RequestWrapper(requestWp.getProxy());
		assertSame(requestWrapper, requestWrapper.getRequest());
		requestWp.testForwardInvocation(requestWrapper);
	}
}
