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
import java.io.InputStream;
import org.civilian.Request;
import org.civilian.request.RequestStreamInterceptor;


public class ReqStreamInterceptorChain implements RequestStreamInterceptor
{
	public static InputStream intercept(Request request, InputStream in, RequestStreamInterceptor interceptor) throws IOException
	{
		if (interceptor != null)
		{
			in = interceptor.intercept(request, in);
			if (in == null)
				throw new IllegalArgumentException("interceptor " + interceptor + " returned a null InputStream");
		}
		return in;
	}
	
	
	public ReqStreamInterceptorChain(RequestStreamInterceptor i1, RequestStreamInterceptor i2)
	{
		i1_ = i1;
		i2_ = i2;
	}
	
	
	@Override public InputStream intercept(Request request, InputStream in) throws IOException
	{
		in = intercept(request, in, i1_);
		in = intercept(request, in, i2_);
		return in;
	}
	
	
	private RequestStreamInterceptor i1_;
	private RequestStreamInterceptor i2_;
}
