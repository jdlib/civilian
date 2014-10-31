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


import org.civilian.CivTest;
import org.junit.Test;


public class ResponseWriterTest extends CivTest 
{
	@Test public void testPrintable()
	{
		out.print((Object)null);
		out.assertOut("null");
		
		out.print(new ResponseWriter.Printable() {
			@Override public void print(ResponseWriter out) throws Exception
			{
				out.print("hallo");
			}
		});
		out.assertOut("hallo");

		final Exception cause = new Exception();
		try
		{
			out.print(new ResponseWriter.Printable() {
				@Override public void print(ResponseWriter out) throws Exception
				{
					throw cause;
				}
			});
			fail();
		}
		catch(IllegalStateException e)
		{
			assertSame(cause, e.getCause());
		}
	}
	
	
	private static TestResponseWriter out = TestResponseWriter.create("ISO-8859-1");
}
