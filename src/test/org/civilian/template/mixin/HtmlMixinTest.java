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


import static org.mockito.Mockito.when;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.resource.Path;
import org.civilian.template.TestTemplateWriter;
import org.junit.Test;


public class HtmlMixinTest extends CivTest
{
	@Test public void test()
	{
		TestTemplateWriter out = TestTemplateWriter.create("ISO-8859-1");
		when(out.app.getPath()).thenReturn(new Path("assets"));
		HtmlMixin html = new HtmlMixin(out);

		html.linkCss("test.css");
		out.assertOut("<link rel=\"stylesheet\" type=\"text/css\" href=\"/assets/test.css\">\n");
		
		html.metaContentType();
		out.assertOut("");
		when(out.response.getContentTypeAndEncoding()).thenReturn(ContentType.TEXT_PLAIN.getValue());
		html.metaContentType();
		out.assertOut("<meta http-equiv=\"Content-Type\" content=\"text/plain\">\n");
		
		html.script("test.js");
		out.assertOut("<script src=\"/assets/test.js\"></script>\n");

		html.script("test.js", "type", "template");
		out.assertOut("<script src=\"/assets/test.js\" type=\"template\"></script>\n");
		
		html.attr("size", 5);
		out.assertOut(" size=\"5\"");

		html.attr("name", "alpha");
		out.assertOut(" name=\"alpha\"");

		html.attr("name", "");
		out.assertOut(" name=\"\"");

		html.attr("name", null);
		out.assertOut(" name=\"\"");

		html.attr("name", "<>");
		out.assertOut(" name=\"&lt;&gt;\"");

		html.attr("name", "<>", false);
		out.assertOut(" name=\"<>\"");
		
		html.text("sometext");
		out.assertOut("sometext");

		html.text(null);
		out.assertOut("");

		html.text("\"&<>\n\r");
		out.assertOut("&quot;&amp;&lt;&gt;\n\r");

		html.attrValue("\"&<>\n\r");
		out.assertOut("&quot;&amp;&lt;&gt;&#10;");
		
		html.jsString(null, true);
		out.assertOut("null");
		
		html.jsString("js", true);
		out.assertOut("'js'");

		html.jsString("\"'\t\n\r\\", false);
		out.assertOut("\"\\'\\t\\n\\r\\\\");
	}
}
