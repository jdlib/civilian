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


import java.io.IOException;
import org.civilian.util.Check;


class ResponseInterceptorChain<T> implements ResponseInterceptor<T>
{
	public static <T> T intercept(T out, ResponseInterceptor<T> interceptor) 
		throws IOException
	{
		if (interceptor != null)
		{
			out = interceptor.intercept(out);
			if (out == null)
				throw new IllegalArgumentException("interceptor " + interceptor + " returned null");
		}
		return out;
	}
	
	
	public static <T> ResponseInterceptor<T> of(ResponseInterceptor<T> i1, ResponseInterceptor<T> i2)
	{
		if (i1 == null)
			return i2;
		else if (i2 == null)
			return i1;
		else
			return new ResponseInterceptorChain<>(i1, i2);
	}

	
	private ResponseInterceptorChain(ResponseInterceptor<T> i1, ResponseInterceptor<T> i2)
	{
		i1_ = Check.notNull(i1, "i1");
		i2_ = Check.notNull(i2, "i2");
	}
	
	
	@Override public ResponseInterceptor<T> prepareIntercept(Response response)
	{
		ResponseInterceptor<T> preppedI1 = i1_.prepareIntercept(response);
		ResponseInterceptor<T> preppedI2 = i2_.prepareIntercept(response);

		if ((preppedI1 == i1_) && (preppedI2 == i2_))
			return this;
		else if ((preppedI1 != null) && (preppedI2 != null))
			return new ResponseInterceptorChain<>(preppedI1, preppedI2);
		else if (preppedI1 != null)
			return preppedI1;
		else
			return preppedI2;
	}

	
	@Override public T intercept(T out) throws IOException
	{
		out = intercept(out, i1_);
		out = intercept(out, i2_);
		return out;
	}
	
	
	private final ResponseInterceptor<T> i1_;
	private final ResponseInterceptor<T> i2_;
}
