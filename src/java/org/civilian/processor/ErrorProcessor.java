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


import org.civilian.Processor;
import org.civilian.Request;
import org.civilian.response.std.ErrorResponse;


/**
 * The ErrorProcessor is used as sole processor
 * of applications which threw an exception during
 * initialization. 
 */
public class ErrorProcessor extends Processor
{
	public ErrorProcessor(int statusCode, String message, Throwable error)
	{
		statusCode_	= statusCode;
		message_	= message;
		error_ 		= error;
		info_ 		= String.valueOf(statusCode);
		if (error != null)
			info_ += " " + error.getMessage();
		else if (message != null)
			info_ += " " + message;
	}
	

	@Override public boolean process(Request request, ProcessorChain chain) throws Exception
	{
		ErrorResponse er = new ErrorResponse();
		er.send(request.getResponse(), statusCode_, message_, error_);
		return true;
	}


	private String message_;
	private Throwable error_;
	private int statusCode_;
}
