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
package org.civilian.util;


import org.junit.Test;
import org.civilian.CivTest;


public class CharSetTest extends CivTest
{
	@Test public void test()
	{
		Charset charset;
		
		charset = Charset.getCharset("utf-8");
		assertTrue(charset.isPrintable((char)0));
		assertTrue(charset.isPrintable((char)128));
		assertTrue(charset.isPrintable((char)256));
		assertTrue(charset.isPrintable((char)8000));
		
		charset = Charset.getCharset("IS=-8859-1");
		assertTrue(charset.isPrintable((char)0));
		assertTrue(charset.isPrintable((char)128));
		assertTrue(charset.isPrintable((char)255));
		assertFalse(charset.isPrintable((char)256));
		assertFalse(charset.isPrintable((char)8000));
		
		charset = Charset.getCharset("ASCII");
		assertTrue(charset.isPrintable((char)0));
		assertTrue(charset.isPrintable((char)127));
		assertFalse(charset.isPrintable((char)128));
		assertFalse(charset.isPrintable((char)256));
		assertFalse(charset.isPrintable((char)8000));
	}
}
