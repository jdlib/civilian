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


import java.util.List;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.annotation.BeanParam;
import org.civilian.annotation.HeaderParam;
import org.civilian.controller.method.arg.factory.MethodArgFactory;
import org.civilian.controller.method.arg.misc.BeanParamArg;
import org.civilian.request.Request;
import org.civilian.request.RequestHeaders;
import org.civilian.resource.pathparam.PathParamMap;
import org.civilian.type.TypeLib;
import org.civilian.util.HttpHeaders;


public class BeanParamArgTest extends CivTest
{
	@Test public void test() throws Exception
	{
		MethodArgFactory factory = new MethodArgFactory(PathParamMap.EMPTY, new TypeLib());
		
		BeanParamArg arg 		= new BeanParamArg(factory, Bean.class);
		Request request 		= mock(Request.class);
		RequestHeaders headers	= mock(RequestHeaders.class);
		when(request.getHeaders()).thenReturn(headers);
		when(request.getParameter("name")).thenReturn("theName");
		when(headers.get(HttpHeaders.ACCEPT)).thenReturn("text/html");
		when(request.getParameters("values")).thenReturn(new String[] { "a", "b" });
		when(request.getParameter("x")).thenReturn("2");
		
		Object value = arg.getValue(request);
		assertNotNull(value);
		assertEquals(Bean.class, value.getClass());
		
		Bean bean = (Bean)value;
		assertEquals("theName", bean.name_);
		assertEquals("text/html", bean.acceptHeader);
		assertNotNull(bean.values);
		assertIterator(bean.values.iterator(), "a", "b");
		assertNotNull(bean.point_);
		assertEquals(2, bean.point_.x);
		assertEquals(0, bean.point_.y);
	}
	
	
	public static class Bean
	{
		// implicit @Parameter("name") for bean property setter 
		public void setName(String s)
		{
			name_ = s;
		}
			
		
		// not a valid bean property setter but annotated with a @BeanParam
		@BeanParam 
		public void initPoint(Point point)
		{
			point_ = point;
		}

		
		private String name_;
		
		private Point point_;
		
		@HeaderParam("Accept")
		public String acceptHeader;		 
		
		// implicit: @Parameter("values")
		public List<String> values;
	}


	public static class Point
	{
		// implicit: @Parameter("x")
		public int x = -1;
		// implicit: @Parameter("y")
		public int y = -1;
	}
}
