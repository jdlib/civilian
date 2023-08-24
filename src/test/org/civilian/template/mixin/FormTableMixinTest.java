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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Locale;
import org.civilian.CivTest;
import org.civilian.form.Form;
import org.civilian.form.TextField;
import org.civilian.request.Request;
import org.civilian.template.TestTemplateWriter;
import org.civilian.text.service.LocaleService;
import org.junit.Test;


public class FormTableMixinTest extends CivTest
{
	@Test public void test()
	{
		Request request = mock(Request.class);
		TestTemplateWriter out = TestTemplateWriter.create();
		when(out.response.getRequest()).thenReturn(request);
		when(request.getLocaleService()).thenReturn(new LocaleService(Locale.ENGLISH));
		when(request.getRequest()).thenReturn(request);
		
		Form form = new Form(out.response);
		form.add(new TextField("name"), "Name");
		
		FormTableMixin formTable = new FormTableMixin(out); 
		formTable.print(form);
		
		String expected = 
			"<form method='POST' name='" + form.getName() + "'>\n" + 
			"<input type='hidden' value='' name='f1431434861'>\n" + 
			"<table>\n" +
			"<tr>\n" +
			"\t<td>Name</td>\n" + 
			"\t<td><input type='text' name='name' value=''></td>\n" + 
			"</tr>\n" + 
			"<tr>\n" + 
			"\t<td></td>\n" +
			"\t<td>\n" +
			"\t</td>\n" +
			"</tr>\n" + 
			"</table>\n" +
			"</form>\n" + 
			"<script>\n" + 
			"document.forms." + form.getName() + ".elements[#'name#'].focus();\n" +
			"</script>\n";
		out.assertOutNormed(expected);
	}
}
