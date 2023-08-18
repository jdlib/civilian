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


import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.response.ResponseHandler;
import org.civilian.util.Check;


/**
 * The ErrorProcessor is used as sole processor
 * of applications which threw an exception during
 * initialization. 
 */
public class ErrorProcessor extends Processor
{
	public ErrorProcessor(ResponseHandler handler)
	{
		handler_ = Check.notNull(handler, "handler");
	}
	
	
	@Override public String getInfo() 
	{
		return handler_.toString();
	}
	

	@Override public boolean process(Request request, Response response, ProcessorChain chain) throws Exception
	{
		handler_.send(response);
		return true;
	}


	private final ResponseHandler handler_;
}
