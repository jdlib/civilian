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
package org.civilian.controller.method.arg.conv;


import org.civilian.controller.method.arg.MethodArg;
import org.civilian.request.BadRequestException;
import org.civilian.request.Request;
import org.civilian.response.Response;
import org.civilian.util.Value;


/**
 * ValueArg returns a Value object containing the value or a parse exception.
 */
public class ValueArg extends MethodArg
{
	public ValueArg(MethodArg arg)
	{
		arg_ = arg;
	}
	
	
	@Override public Object getValue(Request request, Response response) throws Exception
	{
		try
		{
			return new Value<>(arg_.getValue(request, response));
		}
		catch(BadRequestException e)
		{
			return new Value<>(e.getCause(), e.getErrorValue());
		}
		catch(Exception e)
		{
			return new Value<>(e, null);
		}
	}


	@Override public String toString()
	{
		return arg_.toString();
	}
	
	
	private MethodArg arg_;
}
