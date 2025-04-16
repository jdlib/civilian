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


import jakarta.servlet.http.Cookie;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.controller.method.arg.reqparam.CookieParamObjectArg;
import org.civilian.controller.method.arg.reqparam.CookieParamValueArg;
import org.civilian.controller.method.arg.reqparam.HeaderParamValueArg;
import org.civilian.controller.method.arg.reqparam.MatrixParamValueArg;
import org.civilian.controller.method.arg.reqparam.ParameterValueArg;
import org.civilian.controller.method.arg.reqparam.PathParamArg;
import org.civilian.controller.method.arg.reqparam.ReqParamValueArg;
import org.civilian.request.CookieList;
import org.civilian.request.Request;
import org.civilian.request.RequestHeaders;
import org.civilian.response.Response;
import org.civilian.testcase1.Test1PathParams;


public class ReqParamArgTest extends CivTest
{
	@Test public void test() throws Exception
	{
		Request request 		= mock(Request.class);
		RequestHeaders headers 	= mock(RequestHeaders.class);
		Response response		= mock(Response.class);
		Cookie cookie 			= mock(Cookie.class);
		CookieList cookies 		= new CookieList(cookie);
		when(request.getHeaders()).thenReturn(headers);
		when(request.getCookies()).thenReturn(cookies);
		when(cookie.getName()).thenReturn("c");
		when(cookie.getValue()).thenReturn("cv");
		
		//--------------------------
		// parameters
		
		ReqParamValueArg q = new ParameterValueArg("q"); 
		q.getValue(request, response);
		verify(request).getParam("q");

		q.getValues(request, response);
		verify(request).getParams("q");
		
		assertEquals("Parameter \"q\"", q.toString());
		
		
		//--------------------------
		// matrix param
		
		ReqParamValueArg m = new MatrixParamValueArg("m"); 
		m.getValue(request, response);
		verify(request).getMatrixParam("m");

		m.getValues(request, response);
		verify(request).getMatrixParams("m");
		
		assertEquals("MatrixParam \"m\"", m.toString());

		//--------------------------
		// header param

		ReqParamValueArg h = new HeaderParamValueArg("h");
		h.getValue(request, response);
		verify(headers).get("h");

		h.getValues(request, response);
		verify(headers).getAll("h");
		
		assertEquals("HeaderParam \"h\"", h.toString());

	
		//--------------------------
		// cookie param value

		ReqParamValueArg c = new CookieParamValueArg("c");
		ReqParamValueArg x = new CookieParamValueArg("x");
		assertEquals("cv", c.getValue(request, response));
		assertNull(x.getValue(request, response));

		assertArrayEquals2(c.getValues(request, response), "cv");
		assertArrayEquals2(x.getValues(request, response));
		
		assertEquals("CookieParam \"c\"", c.toString());
		
		
		//--------------------------
		// cookie object value

		CookieParamObjectArg co = new CookieParamObjectArg("c");
		assertEquals(cookie, co.getValue(request, response));
		assertEquals("CookieParam \"c\"", co.toString());

		//---------------------------
		// path param
		
		PathParamArg<String> arg = new PathParamArg<>(Test1PathParams.BETA, null);
		assertEquals("PathParam \"beta\"", arg.toString());
		
		assertNull(arg.getValue(request, response));
		verify(request).getPathParam(Test1PathParams.BETA);
		
		when(request.getPathParam(Test1PathParams.BETA)).thenReturn("b");
		assertEquals("b", arg.getValue(request, response));
		verify(request, times(2)).getPathParam(Test1PathParams.BETA);
	}
}
