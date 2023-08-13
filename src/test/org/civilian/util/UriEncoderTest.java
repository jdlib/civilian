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


import org.junit.Test;
import org.civilian.CivTest;


public class UriEncoderTest extends CivTest
{
	@Test public void test()
	{
		assertEncode("abc", "abc");
		assertEncode("a c", "a%20c");
		assertEncode("a%c", "a%25c");
		
		String alpha = "abcdefghijklmnopqrstuvwxyz";
		String unreserverd = alpha + alpha.toUpperCase() + "0123456789._-~";
		assertEncode(unreserverd, unreserverd);
	}
	
	
	private void assertEncode(String in, String expectedOut)
	{
		String actualOut = UriEncoder.encodeString(in);
		assertEquals(expectedOut, actualOut); 
	}
}
