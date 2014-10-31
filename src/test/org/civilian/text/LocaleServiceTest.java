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
package org.civilian.text;


import java.util.Locale;
import org.civilian.CivTest;
import org.junit.Test;


public class LocaleServiceTest extends CivTest
{
	@Test public void testSupportedLocales()
	{
		LocaleService service = new LocaleService(null, false, Locale.ENGLISH, Locale.GERMAN);
		assertEquals(2, service.getLocaleCount());
		assertSame(Locale.ENGLISH, service.getDefaultLocale());
		assertSame(Locale.GERMAN, service.getLocale(1));
		assertTrue(service.isSupported(Locale.ENGLISH));
		assertTrue(service.isSupported(Locale.GERMAN));
		assertFalse(service.isSupported(Locale.FRENCH));
		
		// norm locale
		assertSame(Locale.ENGLISH, service.normLocale(null));
		assertSame(Locale.ENGLISH, service.normLocale(Locale.ENGLISH));
		assertSame(Locale.ENGLISH, service.normLocale(Locale.ITALIAN));
		assertSame(Locale.ENGLISH, service.normLocale(Locale.US));
		
		// locale data
		assertSame(Locale.ENGLISH, service.getDefaultLocaleData().getLocale());
		assertSame(Locale.GERMAN, service.getLocaleData(1).getLocale());
		assertSame(Locale.GERMAN, service.getLocaleData(Locale.GERMAN).getLocale());
		assertSame(Locale.ENGLISH, service.getLocaleData(Locale.ITALIAN).getLocale());
	}
	
	
	@Test public void testSingleLocale()
	{
		LocaleService service = new LocaleService(null, false, Locale.FRENCH);
		assertSame(Locale.FRENCH, service.normLocale(Locale.ITALIAN));
	}
	


	@Test public void testUnsupportedLocales()
	{
		LocaleService service = new LocaleService(null, true, Locale.FRENCH, Locale.CHINESE);
		assertSame(Locale.FRENCH, service.getDefaultLocale());
	}
	
}
