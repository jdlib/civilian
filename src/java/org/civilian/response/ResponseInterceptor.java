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


/**
 * A ResponseInterceptor can be used to wrap the Writer or OutputStream of a Response.
 * @see Response#addInterceptor()
 * @param T either Writer or OutputStream
 */
public interface ResponseInterceptor<T>
{
	/**
	 * Called when the output of response content is about to be obtained
	 * for the first time. The ResponseInterceptor may modify the Response
	 * (e.g. set headers) 
	 * @param response the response
	 * @return an interceptor whose intercept()-method will be called afterwards, or null
	 * 		if the interceptor decided to not intercept. 
	 */
	public ResponseInterceptor<T> prepareIntercept(Response response);
	
	
	/**
	 * Called when the Writer of response content is obtained
	 * for the first time. The intercept-method should allow to be called multiple
	 * times. If {@link Response#resetBuffer()} is called, the previous Writer 
	 * is discarded and created by a new Writer. 
	 * @param out the Writer or OutputStream
	 * @return an Writer which should be used to write response content.
	 * @throws IOException if an IO error occurs
	 */
	public T intercept(T out) throws IOException; 
}
