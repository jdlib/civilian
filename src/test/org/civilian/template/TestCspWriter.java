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
package org.civilian.template;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import org.civilian.application.Application;
import org.civilian.response.Response;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.service.LocaleService;
import org.junit.Assert;


public class TestCspWriter extends CspWriter
{
	public static TestCspWriter create()
	{
		return create("UTF-8");
	}
	
	
	public static TestCspWriter create(String encoding)
	{
		Application app = mock(Application.class);
		
		LocaleService service = new LocaleService(Locale.US, null, MsgBundle.empty(Locale.ENGLISH), null); 
		Response response = mock(Response.class);
		when(response.getOwner()).thenReturn(app);
		when(response.getLocaleService()).thenReturn(service);
		when(response.getCharEncoding()).thenReturn(encoding);
		when(response.getResponse()).thenReturn(response);
		
		StringWriter stringOut = new StringWriter();
		StringBuffer buffer = stringOut.getBuffer();
		TestCspWriter out = new TestCspWriter(stringOut, response);
		out.app 		= app;
		out.response 	= response;
		out.service		= service;
		out.buffer		= buffer;
	
		try
		{
			when(response.getContentWriter()).thenReturn(out);
		} 
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return out;
	}
	
	
	public TestCspWriter(Writer writer, Response response)
	{
		super(writer);
		getData().add(response);
	}


	public void assertOut(String expected)
	{
		String actual = buffer.toString();
		buffer.setLength(0);
		Assert.assertEquals(expected, actual);
	}
	
	
	public void assertOutNormed(String expected)
	{
		expected = expected.replace('\'', '"');
		expected = expected.replace("#\"", "'");
		assertOut(expected);
	}
	
	
	public StringBuffer buffer;
	public Response response;
	public LocaleService service;
	public Application app;
}
