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
package org.civilian.controller.method.arg;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.civilian.CivTest;
import org.civilian.controller.method.arg.misc.ResponseContentArg;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.response.ResponseContent;
import org.junit.Test;


public class RespContentArgTest extends CivTest
{
	private static abstract class Abstract
	{
	}

	
	public static class Explicit extends ResponseContent
	{
		@Override public void writeTo(Response response) throws Exception
		{
			called = true;
		}
		
		public boolean called;
	}

	
	public static class Implicit
	{
	}

	
	@Test public void test() throws Exception
	{
		Request request = mock(Request.class);
		Response response = mock(Response.class);
		when(response.isCommitted()).thenReturn(Boolean.FALSE);
		when(response.getContentAccess()).thenReturn(Response.ContentAccess.NONE);
		
		try
		{
			new ResponseContentArg(Abstract.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}

	
		ResponseContentArg arg = new ResponseContentArg(Explicit.class);
		Object value = arg.getValue(request, response);
		assertEquals(Explicit.class, value.getClass());
		arg.postProcess(request, response, value);
		assertTrue(((Explicit)value).called);

	
		arg = new ResponseContentArg(Implicit.class);
		value = arg.getValue(request, response);
		assertEquals(Implicit.class, value.getClass());
		arg.postProcess(request, response, value);
		verify(response).writeContent(value);
	}
}
