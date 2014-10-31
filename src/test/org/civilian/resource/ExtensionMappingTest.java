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
package org.civilian.resource;


import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.junit.Test;


public class ExtensionMappingTest extends CivTest
{
	@Test public void test()
	{
		ExtensionMapping mapping = new ExtensionMapping();
		mapping.addLocale("fr", Locale.FRENCH);
		mapping.addContentType("html", ContentType.TEXT_HTML);
		
		assertSame(Locale.FRENCH, mapping.getLocale("fr"));
		assertNull(mapping.getLocale("de"));
		assertSame(Locale.FRENCH, mapping.extractLocale("fr"));
		assertSame(Locale.FRENCH, mapping.extractLocale("html.fr"));
		assertNull(mapping.extractLocale("de"));
		assertNull(mapping.extractLocale(null));

		assertSame(ContentType.TEXT_HTML, mapping.getContentType("html"));
		assertNull(mapping.getContentType("txt"));
		assertSame(ContentType.TEXT_HTML, mapping.extractContentType("html"));
		assertSame(ContentType.TEXT_HTML, mapping.extractContentType("html.fr"));
		assertNull(mapping.extractContentType("txt"));
		assertNull(mapping.extractContentType(null));
	}
}
