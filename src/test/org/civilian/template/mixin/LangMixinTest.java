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
package org.civilian.template.mixin;


import org.junit.Test;
import java.time.LocalDate;
import org.civilian.CivTest;
import org.civilian.template.TestTemplateWriter;


public class LangMixinTest extends CivTest
{
	@Test public void testFormat()
	{
		TestTemplateWriter out = TestTemplateWriter.create("ISO-8859-1");
		LangMixin lang = new LangMixin(out);
		
		// dates
		LocalDate date = LocalDate.of(2012, 01, 31);
		assertEquals("01/31/2012", lang.format(date));
		assertEquals("01/31/2012", lang.format(date, "a"));
		assertEquals("a", lang.format((LocalDate)null, "a"));
		
		// int
		assertEquals("1,234", lang.format(1234));
		assertEquals("2,345", lang.format(new Integer(2345), "a"));
		assertEquals("a", lang.format((Integer)null, "a"));

		// long
		assertEquals("1,234", lang.format(1234L));
		assertEquals("2,345", lang.format(new Long(2345), "a"));
		assertEquals("a", lang.format((Long)null, "a"));

		// double
		assertEquals("1,234.50", lang.format(1234.5));
		assertEquals("2,345.60", lang.format(new Double(2345.6), "a"));
		assertEquals("a", lang.format((Double)null, "a"));
	}
	


	@Test public void testAccessors()
	{
		TestTemplateWriter out = TestTemplateWriter.create("ISO-8859-1");
		LangMixin locale = new LangMixin(out);

		assertEquals("?key", locale.msg("key"));
		assertEquals("?key", locale.msg("key", "param"));
	}
}
