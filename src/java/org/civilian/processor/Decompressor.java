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
import java.io.InputStream;

import org.civilian.Request;
import org.civilian.content.CompressionScheme;
import org.civilian.internal.Logs;
import org.civilian.request.RequestStreamInterceptor;
import org.civilian.util.HttpHeaders;


/**
 * Decompressor is a processor which decompresses request content.
 * Put it into the processor chain before any processors which want to read the content.
 * Decompressor uses the Content-encoding header to determine the compression scheme.
 * It recognizes and treats all schemes which are available via the {@link CompressionScheme} class.
 */
public class Decompressor extends Processor 
{
	@Override public String getInfo() 
	{
		return getClass().getSimpleName();
	}

	
	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		String scheme = request.getHeaders().get(HttpHeaders.CONTENT_ENCODING);
		if (scheme != null)
			addInterceptor(request, scheme);
		return chain.next(request);
	}
	
	
	private void addInterceptor(Request request, String schemeName)
	{
		CompressionScheme scheme = CompressionScheme.get(schemeName);
		
		if (scheme != null)
		{
			request.addInterceptor(new Interceptor(scheme));
		}
		else if (!CompressionScheme.Names.IDENTITY.equals(schemeName) && Logs.PROCESSOR.isWarnEnabled())
		{
			// identity should not be used in content-encoding but 
			// we don't log a warning if somebody sends it
			Logs.PROCESSOR.warn("unhandled request compression scheme " + schemeName);
		}
	}
	
	
	private static class Interceptor implements RequestStreamInterceptor
	{
		public Interceptor(CompressionScheme scheme)
		{
			scheme_ = scheme;
		}
		
		
		@Override public InputStream intercept(Request request, InputStream in) throws IOException
		{
			return scheme_.wrap(in);
		}
		
		
		private final CompressionScheme scheme_;
	}
}
