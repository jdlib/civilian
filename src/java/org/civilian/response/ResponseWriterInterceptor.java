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
import org.civilian.Response;


/**
 * A ResponseWriterInterceptor can be used to wrap the Writer of a Response.
 * @see Response#addInterceptor(ResponseWriterInterceptor)
 */
public interface ResponseWriterInterceptor
{
	/**
	 * Called when the Writer of response content is about to be obtained
	 * for the first time. The ResponseWriterInterceptor may modify the Response
	 * (e.g. set headers) 
	 * @param response the response
	 * @return an interceptor whose intercept()-method will be called afterwards, or null
	 * 		if the interceptor decided to not intercept. 
	 */
	public ResponseWriterInterceptor prepareWriterIntercept(Response response);
	
	
	/**
	 * Called when the Writer of response content is obtained
	 * for the first time. The intercept-method should allow to be called multiple
	 * times. If {@link Response#resetBuffer()} is called, the previous Writer 
	 * is discarded and created by a new Writer. 
	 * @param writer the Writer
	 * @return an Writer which should be used to write response content.
	 */
	public Writer intercept(Writer writer) throws IOException; 
}
