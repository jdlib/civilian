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
package org.civilian.text.service;


import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.text.service.LocaleServiceList;
import org.junit.Test;


public class LocaleServiceTest extends CivTest
{
	@Test public void testSupportedLocales()
	{
		LocaleServiceList services = new LocaleServiceList(null, null, false, Locale.ENGLISH, Locale.GERMAN);
		assertEquals(2, services.size());
		assertSame(Locale.ENGLISH, services.getDefaultLocale());
		assertSame(Locale.GERMAN, services.getLocale(1));
		assertTrue(services.isSupported(Locale.ENGLISH));
		assertTrue(services.isSupported(Locale.GERMAN));
		assertFalse(services.isSupported(Locale.FRENCH));
		
		// norm locale
		assertSame(Locale.ENGLISH, services.normLocale(null));
		assertSame(Locale.ENGLISH, services.normLocale(Locale.ENGLISH));
		assertSame(Locale.ENGLISH, services.normLocale(Locale.ITALIAN));
		assertSame(Locale.ENGLISH, services.normLocale(Locale.US));
		
		// locale data
		assertSame(Locale.ENGLISH, services.getDefaultService().getLocale());
		assertSame(Locale.GERMAN, services.getService(1).getLocale());
		assertSame(Locale.GERMAN, services.getService(Locale.GERMAN).getLocale());
		assertSame(Locale.ENGLISH, services.getService(Locale.ITALIAN).getLocale());
	}
	
	
	@Test public void testSingleLocale()
	{
		LocaleServiceList services = new LocaleServiceList(null, null, false, Locale.FRENCH);
		assertSame(Locale.FRENCH, services.normLocale(Locale.ITALIAN));
	}
	


	@Test public void testUnsupportedLocales()
	{
		LocaleServiceList services = new LocaleServiceList(null, null, true, Locale.FRENCH, Locale.CHINESE);
		assertSame(Locale.FRENCH, services.getDefaultLocale());
	}
	
}
