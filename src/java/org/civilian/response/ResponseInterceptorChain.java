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
import java.io.Writer;

import org.civilian.util.Check;


class RespWriterInterceptorChain implements ResponseWriterInterceptor
{
	public static Writer intercept(Writer writer, ResponseWriterInterceptor interceptor) 
		throws IOException
	{
		if (interceptor != null)
		{
			writer = interceptor.intercept(writer);
			if (writer == null)
				throw new IllegalArgumentException("interceptor " + interceptor + " returned a null OutputStream");
		}
		return writer;
	}
	
	
	public RespWriterInterceptorChain(ResponseWriterInterceptor i1, ResponseWriterInterceptor i2)
	{
		i1_ = Check.notNull(i1, "i1");
		i2_ = Check.notNull(i2, "i2");
	}
	
	
	@Override public ResponseWriterInterceptor prepareWriterIntercept(Response response)
	{
		ResponseWriterInterceptor preppedI1_ = i1_.prepareWriterIntercept(response);
		ResponseWriterInterceptor preppedI2_ = i2_.prepareWriterIntercept(response);

		if ((preppedI1_ == i1_) && (preppedI2_ == i2_))
			return this;
		else if ((preppedI1_ != null) && (preppedI2_ != null))
			return new RespWriterInterceptorChain(preppedI1_, preppedI2_);
		else if (preppedI1_ != null)
			return preppedI1_;
		else
			return preppedI2_;
	}

	
	@Override public Writer intercept(Writer writer) throws IOException
	{
		writer = intercept(writer, i1_);
		writer = intercept(writer, i2_);
		return writer;
	}
	
	
	private ResponseWriterInterceptor i1_;
	private ResponseWriterInterceptor i2_;
}
