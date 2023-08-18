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
package org.civilian.processor;


import java.io.IOException;
import java.io.OutputStream;

import org.civilian.Logs;
import org.civilian.content.CompressionScheme;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.response.ResponseHeaders;
import org.civilian.response.ResponseInterceptor;
import org.civilian.util.http.HeaderNames;


/**
 * Compressor is a processor which compresses response content.
 * Put it into the processor chain before any processors which will write response content.
 * Compressor uses the "Accept-Encoding" header to determine the compression scheme.
 * It can use all schemes which are available via the {@link CompressionScheme} class.
 */
public class Compressor extends Processor
{
	/**
	 * An constant to signal that no compression should be applied.
	 * If a Compression object was installed as interceptor on a Response,
	 * it will automatically wrap the response OutputStream into a compressing stream.
	 * To abandon this behavior set this attribute on the request (with some non-null value). 
	 */
	public static final String NO_COMPRESSION = "compressor-none";
	
	
	@Override public String getInfo() 
	{
		return getClass().getSimpleName();
	}

	
	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		String accepted = request.getHeaders().get(HeaderNames.ACCEPT_ENCODING);
		if (accepted != null)
			addInterceptor(request, accepted);
			
		return chain.next(request);
	}

	
	protected void addInterceptor(Request request, String accepted)
	{
		CompressionScheme scheme = CompressionScheme.match(accepted);
		if (scheme != null)
		{
			if (!scheme.isIdentity())
			{
				Response response = request.getResponse();
				response.getHeaders().add(HeaderNames.VARY, "Accept-Encoding");
				response.addInterceptor().forStream(new Interceptor(scheme));
			}
		}
		else if (Logs.PROCESSOR.isWarnEnabled())
		{
			Logs.PROCESSOR.warn("unhandled response compression schemes " + accepted);
		}
	}
	
	
	private static class Interceptor implements ResponseInterceptor<OutputStream>
	{
		public Interceptor(CompressionScheme scheme)
		{
			scheme_ = scheme;
		}


		@Override public ResponseInterceptor<OutputStream> prepareIntercept(Response response)
		{
			if (response.getRequest().getAttribute(NO_COMPRESSION) != null)
				return null;

			ResponseHeaders headers = response.getHeaders();

			// do not apply compression if some other content-encoding was applied
			String encoding = headers.get(HeaderNames.CONTENT_ENCODING);
			if (encoding != null)
				return null;

			// clear content length
			response.setContentLength(-1);

			// set content-encoding
			headers.set(HeaderNames.CONTENT_ENCODING, scheme_.getName());
			
			// enhance etag if set
			String etag = headers.get(HeaderNames.ETAG);
			if (etag != null)
				headers.set(HeaderNames.ETAG, etag + '-' + scheme_.getName());
			
			return this;
		}


		@Override public OutputStream intercept(OutputStream out) throws IOException
		{
			return scheme_.wrap(out);
		}
		
		
		private final CompressionScheme scheme_;
	}
}

