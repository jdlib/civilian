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
package org.civilian.internal.controller.arg;


import org.civilian.Request;
import org.civilian.internal.controller.arg.StringMethodArg;


public class TestStringArg extends StringMethodArg
{
	private void checkExcception() throws Exception
	{
		if (exception != null)
			throw exception;
	}
	
	
	@Override public String getValue(Request request) throws Exception
	{
		checkExcception();
		return value;
	}
	

	@Override public String[] getValues(Request request) throws Exception
	{
		checkExcception();
		return values;
	}
	
	
	@Override public String toString()
	{
		return "TestArg";
	}
	
	
	public Exception exception;
	public String value;
	public String[] values;
}
