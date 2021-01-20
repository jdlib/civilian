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
import org.civilian.Processor;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.content.CompressionScheme;
import org.civilian.internal.Logs;
import org.civilian.response.ResponseHeaders;
import org.civilian.response.ResponseStreamInterceptor;


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
	
	
	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		String accepted = request.getHeaders().get("Accept-Encoding");
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
				response.getHeaders().add("Vary", "Accept-Encoding");
				response.addInterceptor(new Interceptor(scheme));
			}
		}
		else if (Logs.PROCESSOR.isWarnEnabled())
		{
			Logs.PROCESSOR.warn("unhandled response compression schemes " + accepted);
		}
	}
	
	
	private static class Interceptor implements ResponseStreamInterceptor
	{
		public Interceptor(CompressionScheme scheme)
		{
			scheme_ = scheme;
		}


		@Override public ResponseStreamInterceptor prepareStreamIntercept(Response response)
		{
			if (response.getRequest().getAttribute(NO_COMPRESSION) != null)
				return null;

			ResponseHeaders headers = response.getHeaders();

			// do not apply compression if some other content-encoding was applied
			String encoding = headers.get("Content-Encoding");
			if (encoding != null)
				return null;

			// clear content length
			response.setContentLength(-1);

			// set content-encoding
			headers.set("Content-Encoding", scheme_.getName());
			
			// enhance etag if set
			String etag = headers.get("Etag");
			if (etag != null)
				headers.set("Etag", etag + '-' + scheme_.getName());
			
			return this;
		}


		@Override public OutputStream intercept(OutputStream out) throws IOException
		{
			return scheme_.wrap(out);
		}
		
		
		private final CompressionScheme scheme_;
	}
}

