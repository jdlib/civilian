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
import java.io.OutputStream;

import org.civilian.response.Response;
import org.civilian.response.ResponseStreamInterceptor;
import org.civilian.util.Check;


public class RespStreamInterceptorChain implements ResponseStreamInterceptor
{
	public static OutputStream intercept(OutputStream out, ResponseStreamInterceptor interceptor) 
		throws IOException
	{
		if (interceptor != null)
		{
			out = interceptor.intercept(out);
			if (out == null)
				throw new IllegalArgumentException("interceptor " + interceptor + " returned a null OutputStream");
		}
		return out;
	}
	
	
	public RespStreamInterceptorChain(ResponseStreamInterceptor i1, ResponseStreamInterceptor i2)
	{
		i1_ = Check.notNull(i1, "i1");
		i2_ = Check.notNull(i2, "i2");
	}
	
	
	@Override public ResponseStreamInterceptor prepareStreamIntercept(Response response)
	{
		ResponseStreamInterceptor preppedI1_ = i1_.prepareStreamIntercept(response);
		ResponseStreamInterceptor preppedI2_ = i2_.prepareStreamIntercept(response);

		if ((preppedI1_ == i1_) && (preppedI2_ == i2_))
			return this;
		else if ((preppedI1_ != null) && (preppedI2_ != null))
			return new RespStreamInterceptorChain(preppedI1_, preppedI2_);
		else if (preppedI1_ != null)
			return preppedI1_;
		else
			return preppedI2_;
	}

	
	@Override public OutputStream intercept(OutputStream out) throws IOException
	{
		out = intercept(out, i1_);
		out = intercept(out, i2_);
		return out;
	}
	
	
	private ResponseStreamInterceptor i1_;
	private ResponseStreamInterceptor i2_;
}
