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
package org.civilian.content;


import org.civilian.CivTest;
import org.junit.After;
import org.junit.Test;


public class CompressionSchemeTest extends CivTest
{
	@After public void after()
	{
		CompressionScheme.setPreferred(CompressionScheme.GZIP);
	}
	
	
	@Test public void testMatch()
	{
		CompressionScheme.setPreferred(CompressionScheme.DEFLATE);

		// "": returns the identity scheme
		assertMatch(CompressionScheme.IDENTITY, "");
		
		// "*" returns the preferred scheme
		assertMatch(CompressionScheme.DEFLATE, "*");
		
		// "gzip, deflate": returns the preferred scheme if contained in the accepted
		assertMatch(CompressionScheme.DEFLATE, "gzip, deflate");

		// "compress": recognizes single schemes
		assertMatch(CompressionScheme.COMPRESS, "compress");

		// "dummy1, gzip, compress": returns the first recognized scheme 
		assertMatch(CompressionScheme.GZIP, "dummy1, gzip, compress");

		// returns null if nothing matches 
		assertMatch(CompressionScheme.IDENTITY, "dummy1, dummy2");
		
		assertMatch(CompressionScheme.GZIP, "identity; i=x;q=0.5, gzip;q=1.0, *;q=0");
		
		assertMatch(CompressionScheme.DEFLATE, "dummy;q=1.0, deflate;q=0.5");
		
		assertMatch(CompressionScheme.DEFLATE, "dummy;q=1.0, *;q=0.5, gzip;q=0.1");
		
		assertMatch(CompressionScheme.IDENTITY, "dummy");
		
		assertMatch(CompressionScheme.IDENTITY, "dummy; q=1");
		
		assertMatch(null, "dummy; q=1, *;q=0");
		
		assertMatch(null, "dummy; q=1, identity;q=0");

		// runtime exception
		assertMatch(CompressionScheme.IDENTITY, "dummy; q=a");
}
	
	
	private void assertMatch(CompressionScheme scheme, String accept)
	{
		assertEquals(scheme, CompressionScheme.match(accept));
	}
}
