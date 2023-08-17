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
package org.civilian.controller.method.arg;


import java.lang.reflect.Method;
import java.util.List;
import org.civilian.CivTest;
import org.civilian.annotation.Parameter;
import org.civilian.controller.method.arg.conv.ValueArg;
import org.civilian.controller.method.arg.factory.MethodArgFactory;
import org.civilian.request.BadRequestException;
import org.civilian.resource.PathParamMap;
import org.civilian.util.Value;
import org.junit.Test;


public class ValueArgTest extends CivTest
{
	@SuppressWarnings("rawtypes")
	public void run(@Parameter("simple-notype") Value simpleNotype, 
		@Parameter("simple") Value<Integer> simple,
		@Parameter("list-notype") Value<List> listNotype,
		@Parameter("list") Value<List<String>> list)
	{
	}
	
	
	@Test public void testCreate()
	{
		Method method = findMethod(getClass(), "run");
		MethodArgFactory factory = new MethodArgFactory(PathParamMap.EMPTY, TYPELIB);
		MethodArg[] args = factory.createParamArgs(method);
		assertEquals(4, args.length);
	}
	

	@SuppressWarnings("unchecked")
	@Test public void testRun() throws Exception
	{
		TestStringArg arg 	= new TestStringArg();
		ValueArg varg		= new ValueArg(arg); 
		
		arg.value = "1";
		
		Value<String> v	= (Value<String>)varg.getValue(null);
		assertEquals("1", v.getValue());
		assertNull(v.getError());
		assertNull(v.getErrorValue());
		
		Exception ex = arg.exception = new RuntimeException();
		v	= (Value<String>)varg.getValue(null);
		assertNull(v.getValue());
		assertEquals(arg.exception, v.getError());
		assertNull(v.getErrorValue());
		
		arg.exception = new BadRequestException("hello", ex).setErrorValue("12.34");
		v	= (Value<String>)varg.getValue(null);
		assertNull(v.getValue());
		assertEquals("12.34", v.getErrorValue());
	}
}
