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


/**
 * A RequestInterceptor can be used to wrap the Reader or InputStream of a Request.
 * @see Request#addInterceptor()
 * @param T either a Reader or InputStream
 */
public interface RequestInterceptor<T>
{
	/**
	 * Called when the Reader of request content is obtained
	 * for the first time. 
	 * @param request the request
	 * @param the input (either a Reader or InputStream)
	 * @return a Reader which should be used to read request content.
	 * @throws IOException if an error occurs
	 */
	public T intercept(Request request, T in) throws IOException; 
}
