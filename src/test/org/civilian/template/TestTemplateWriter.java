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
import org.civilian.Application;
import org.civilian.Response;
import org.civilian.template.TemplateWriter;
import org.civilian.text.LocaleService;
import org.civilian.text.msg.MsgBundle;
import org.junit.Assert;


public class TestTemplateWriter extends TemplateWriter
{
	static
	{
		TemplateWriter.setDefaultLineSeparator("\n");
	}
	
	
	public static TestTemplateWriter create()
	{
		return create("UTF-8");
	}
	
	
	public static TestTemplateWriter create(String encoding)
	{
		Application app = mock(Application.class);
		
		LocaleService service = new LocaleService(Locale.US, MsgBundle.empty(Locale.ENGLISH), null); 
		Response response = mock(Response.class);
		when(response.getApplication()).thenReturn(app);
		when(response.getLocaleService()).thenReturn(service);
		when(response.getContentEncoding()).thenReturn(encoding);
		when(response.getResponse()).thenReturn(response);
		
		StringWriter stringOut = new StringWriter();
		StringBuffer buffer = stringOut.getBuffer();
		TestTemplateWriter out = new TestTemplateWriter(stringOut, response);
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
	
	
	public TestTemplateWriter(Writer writer, Response response)
	{
		super(writer);
		addContext(response);
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
