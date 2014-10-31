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
		CompressionScheme.setPreferred(CompressionScheme.DEFAULT_GZIP_SCHEME);
	}
	
	
	@Test public void testMatch()
	{
		CompressionScheme.setPreferred(CompressionScheme.DEFAULT_DEFLATE_SCHEME);

		// "": returns the identity scheme
		assertMatch(CompressionScheme.DEFAULT_IDENTITY_SCHEME, "");
		
		// "*" returns the preferred scheme
		assertMatch(CompressionScheme.DEFAULT_DEFLATE_SCHEME, "*");
		
		// "gzip, deflate": returns the preferred scheme if contained in the accepted
		assertMatch(CompressionScheme.DEFAULT_DEFLATE_SCHEME, "gzip, deflate");

		// "compress": recognizes single schemes
		assertMatch(CompressionScheme.DEFAULT_COMPRESS_SCHEME, "compress");

		// "dummy1, gzip, compress": returns the first recognized scheme 
		assertMatch(CompressionScheme.DEFAULT_GZIP_SCHEME, "dummy1, gzip, compress");

		// returns null if nothing matches 
		assertMatch(CompressionScheme.DEFAULT_IDENTITY_SCHEME, "dummy1, dummy2");
		
		assertMatch(CompressionScheme.DEFAULT_GZIP_SCHEME, "identity; i=x;q=0.5, gzip;q=1.0, *;q=0");
		
		assertMatch(CompressionScheme.DEFAULT_DEFLATE_SCHEME, "dummy;q=1.0, deflate;q=0.5");
		
		assertMatch(CompressionScheme.DEFAULT_DEFLATE_SCHEME, "dummy;q=1.0, *;q=0.5, gzip;q=0.1");
		
		assertMatch(CompressionScheme.DEFAULT_IDENTITY_SCHEME, "dummy");
		
		assertMatch(CompressionScheme.DEFAULT_IDENTITY_SCHEME, "dummy; q=1");
		
		assertMatch(null, "dummy; q=1, *;q=0");
		
		assertMatch(null, "dummy; q=1, identity;q=0");

		// runtime exception
		assertMatch(CompressionScheme.DEFAULT_IDENTITY_SCHEME, "dummy; q=a");
}
	
	
	private void assertMatch(CompressionScheme scheme, String accept)
	{
		assertEquals(scheme, CompressionScheme.match(accept));
	}
}
