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
package org.civilian.internal.controller.arg;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.controller.method.arg.MethodArg;
import org.civilian.controller.method.arg.reqcontent.ReqContenBytesArg;
import org.civilian.controller.method.arg.reqcontent.ReqContentArgs;
import org.civilian.controller.method.arg.reqcontent.ReqContentGenericArg;
import org.civilian.controller.method.arg.reqcontent.ReqContentInputStreamArg;
import org.civilian.controller.method.arg.reqcontent.ReqContentReaderArg;
import org.civilian.controller.method.arg.reqcontent.ReqContentStringArg;
import org.civilian.request.Request;


public class ReqContentArgTest extends CivTest
{
	@Test public void test() throws Exception
	{
		Request request = mock(Request.class);
		MethodArg arg;
		
		StringReader reader	= new StringReader("123");
		ByteArrayInputStream in = new ByteArrayInputStream("abc".getBytes()); 
		
		// reader based
		when(request.getContentReader()).thenReturn(reader);
		
		arg = create(Reader.class);
		assertTrue(arg instanceof ReqContentReaderArg);
		assertEquals(reader, arg.getValue(request));

		arg = create(String.class);
		assertTrue(arg instanceof ReqContentStringArg);
		assertEquals("123", arg.getValue(request));
		
		// inputstream based
		when(request.getContentStream()).thenReturn(in);

		arg = create(InputStream.class);
		assertTrue(arg instanceof ReqContentInputStreamArg);
		assertEquals(in, arg.getValue(request));

		arg = create((new byte[0]).getClass());
		assertTrue(arg instanceof ReqContenBytesArg);
		assertArrayEquals("abc".getBytes(), (byte[])arg.getValue(request));
		
		arg = create(Integer.class);
		assertTrue(arg instanceof ReqContentGenericArg);
		when(request.readContent(Integer.class, Integer.class)).thenReturn(Integer.valueOf(456));
		assertEquals(Integer.valueOf(456), arg.getValue(request));
	}
	
	
	
	private static MethodArg create(Class<?> c)
	{
		return ReqContentArgs.create(c, c);
	}
}
