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
package org.civilian.request;


import java.io.IOException;


class RequestInterceptorChain<T> implements RequestInterceptor<T>
{
	public static <T> T intercept(Request request, T in, RequestInterceptor<T> interceptor) throws IOException
	{
		if (interceptor != null)
		{
			in = interceptor.intercept(request, in);
			if (in == null)
				throw new IllegalArgumentException("interceptor " + interceptor + " returned null");
		}
		return in;
	}
	
	
	public static <T> RequestInterceptor<T> of(RequestInterceptor<T> i1, RequestInterceptor<T> i2)
	{
		if (i1 == null)
			return i2;
		else if (i2 == null)
			return i1;
		else
			return new RequestInterceptorChain<>(i1, i2); 
	}

	
	private RequestInterceptorChain(RequestInterceptor<T> i1, RequestInterceptor<T> i2)
	{
		i1_ = i1;
		i2_ = i2;
	}
	
	
	@Override public T intercept(Request request, T in) throws IOException
	{
		in = intercept(request, in, i1_);
		in = intercept(request, in, i2_);
		return in;
	}
	
	
	private final RequestInterceptor<T> i1_;
	private final RequestInterceptor<T> i2_;
}
