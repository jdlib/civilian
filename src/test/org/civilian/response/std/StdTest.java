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
package org.civilian.response.std;


import static org.mockito.Mockito.*;
import java.util.ArrayList;
import org.junit.BeforeClass;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.content.ContentTypeList;
import org.civilian.request.Request;
import org.civilian.request.RequestHeaders;
import org.civilian.resource.Path;
import org.civilian.response.Response;
import org.civilian.template.TestTemplateWriter;


public class StdTest extends CivTest
{
	@BeforeClass public static void beforeClass()
	{
		request = mock(Request.class);
		headers = mock(RequestHeaders.class);
		out = TestTemplateWriter.create();
		when(out.response.getRequest()).thenReturn(request);
		when(out.response.getResponse()).thenReturn(out.response);
		when(request.getResponse()).thenReturn(out.response);
		when(request.getHeaders()).thenReturn(headers);
	}
	
	
	@Test public void testNotFound() throws Exception
	{
		assertNull(new NotFoundResponseHandler(false).send(request.getResponse()));
		verify(out.response).setStatus(Response.Status.NOT_FOUND);

		ArrayList<String> list = new ArrayList<>();
		list.add("x");
		when(request.getAcceptedContentTypes()).thenReturn(new ContentTypeList(ContentType.TEXT_HTML));
		assertNull(new NotFoundResponseHandler(true).send(request.getResponse()));
	}
	
	
	// right now just for coverage
	@Test public void testError() throws Exception
	{
		when(request.getAcceptedContentTypes()).thenReturn(new ContentTypeList());
		assertNull(sendErrorResponse(false));

		assertNull(sendErrorResponse(true));

		when(request.getAcceptedContentTypes()).thenReturn(new ContentTypeList(ContentType.TEXT_HTML));
		assertNull(sendErrorResponse(true));
	}
	
	
	private Exception sendErrorResponse(boolean develop)
	{
		ErrorResponseHandler errResponse = new ErrorResponseHandler(develop, 
			Response.Status.INTERNAL_SERVER_ERROR, 
			"some error", 
			new RuntimeException("unexpected"));
		return errResponse.send(request.getResponse());
	}
	
	
	/**
	 * Runs the template for coverage.
	 */
	@Test public void testTemplates() throws Exception
	{
		TestTemplateWriter out = TestTemplateWriter.create();
		
		Request request  = mock(Request.class);
		when(out.response.getRequest()).thenReturn(request);
		when(request.getPath()).thenReturn(Path.ROOT);
		when(request.getUrl()).thenReturn("");
		
		ErrorTemplate errTemplate = new ErrorTemplate(request, 400, "test", new IllegalArgumentException());
		errTemplate.print(out);
		
		NotFoundTemplate nfTemplate = new NotFoundTemplate(request);
		nfTemplate.print(out);
	}

	
	private static TestTemplateWriter out;
	private static Request request;
	private static RequestHeaders headers;
}
