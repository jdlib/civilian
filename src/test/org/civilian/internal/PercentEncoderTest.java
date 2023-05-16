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
package org.civilian.internal;


import org.civilian.CivTest;
import org.junit.Test;


public class PercentEncoderTest extends CivTest
{
	@Test public void testConvert()
	{
		assertConvert('a', 0x61);
		assertConvert(' ', 0x20);
		assertConvert('ä', -61, -92);
	}
	
	
	@Test public void testEscape()
	{
		assertEscape('a', "%61"); 
		assertEscape(' ', "%20"); 
		assertEscape('ä', "%C3%A4"); 
		assertEscape('%', "%25"); 
		assertEscape('\u1234', "%E1%88%B4"); 
	}

	
	private void assertEscape(char c, String result)
	{
		assertEquals(result, encoder_.escape(c));
	}
	
	
	private void assertConvert(char c, int... bytes)
	{
		assertEquals(bytes.length, encoder_.convert(c));
		for (int i=0; i<bytes.length; i++)
			assertEquals(bytes[i], encoder_.getResult(i));
	}

	
	private PercentEncoder encoder_ = new PercentEncoder();
}
