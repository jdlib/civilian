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
import java.io.Reader;
import org.civilian.Request;


/**
 * A RequestReaderInterceptor can be used to wrap the Reader a Request.
 * @see Request#addInterceptor(RequestReaderInterceptor)
 */
public interface RequestReaderInterceptor
{
	/**
	 * Called when the Reader of request content is obtained
	 * for the first time. 
	 * @param request the request
	 * @param reader the Reader
	 * @return a Reader which should be used to read request content.
	 */
	public Reader intercept(Request request, Reader reader) throws IOException; 
}
