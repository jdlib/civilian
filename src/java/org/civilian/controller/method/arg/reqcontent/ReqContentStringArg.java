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
package org.civilian.controller.method.arg.reqcontent;


import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.util.IoUtil;


/**
 * ReqContentStringArg returns the content of the request as string.
 */
public class ReqContentStringArg extends RequestContentArg 
{
	@Override public Object getValue(Request request, Response response) throws Exception
	{
		return IoUtil.readString(request.getContentReader());
	}
}
