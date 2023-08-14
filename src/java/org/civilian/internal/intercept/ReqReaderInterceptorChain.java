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
package org.civilian.internal.intercept;


import java.io.IOException;
import java.io.Reader;

import org.civilian.request.Request;
import org.civilian.request.RequestReaderInterceptor;


public class ReqReaderInterceptorChain implements RequestReaderInterceptor
{
	public static Reader intercept(Request request, Reader in, RequestReaderInterceptor interceptor) throws IOException
	{
		if (interceptor != null)
		{
			in = interceptor.intercept(request, in);
			if (in == null)
				throw new IllegalArgumentException("interceptor " + interceptor + " returned a null Reader");
		}
		return in;
	}
	
	
	public ReqReaderInterceptorChain(RequestReaderInterceptor i1, RequestReaderInterceptor i2)
	{
		i1_ = i1;
		i2_ = i2;
	}
	
	
	@Override public Reader intercept(Request request, Reader reader) throws IOException
	{
		reader = intercept(request, reader, i1_);
		reader = intercept(request, reader, i2_);
		return reader;
	}
	
	
	private RequestReaderInterceptor i1_;
	private RequestReaderInterceptor i2_;
}
