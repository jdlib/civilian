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


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.CivTest;
import org.civilian.controller.method.arg.conv.ConvertingArg;
import org.civilian.request.Request;
import org.civilian.text.service.LocaleService;
import org.civilian.type.TypeLib;


public class ConvertingArgTest extends CivTest
{
	@Test public void test() throws Exception
	{
		TypeLib typeLib = new TypeLib();
		TestStringArg arg = new TestStringArg();
		
		//---------------------------------------
		// unsupported type
		try
		{
			ConvertingArg.create(arg, "a", false, typeLib, HashMap.class);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
		
		//---------------------------------------
		// supported types
		
		// create two ConvertingArgs with simple converter
		// one with a default value the other not
		MethodArg argSimple1 = ConvertingArg.create(arg, "1.0", false, typeLib, Double.class); // locale independent
		MethodArg argSimple2 = ConvertingArg.create(arg, null,  true,  typeLib, Double.class); // locale dependent

		// create two ConvertingArgs with collection converters
		// string... -> collection types
		MethodArg argColl1   = ConvertingArg.create(arg, "b",  false, typeLib, List.class);
		MethodArg argColl2   = ConvertingArg.create(arg, null, false, typeLib, List.class);

		// test toString
		assertEquals("TestArg", argSimple1.toString());
		assertEquals("TestArg", argSimple2.toString());
		assertEquals("TestArg", argColl1.toString());
		assertEquals("TestArg", argColl2.toString());
		
		
		Request request = mock(Request.class);
		when(request.getLocaleService()).thenReturn(new LocaleService(Locale.GERMAN));
	}
}
