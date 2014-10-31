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
import java.io.InputStream;
import org.civilian.Request;


/**
 * A RequestStreamInterceptor can be used to wrap the InputStream of a Request.
 * @see Request#addInterceptor(RequestStreamInterceptor)
 */
public interface RequestStreamInterceptor
{
	/**
	 * Called when the InputStream or Reader of request content is obtained
	 * for the first time. 
	 * @param request the request
	 * @param in the InputStream
	 * @return an InputStream which should be used to read request content.
	 */
	public InputStream intercept(Request request, InputStream in) throws IOException; 
}
