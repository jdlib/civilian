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
package org.civilian.asset;


import org.junit.Test;
import static org.mockito.Mockito.*;
import org.civilian.resource.Path;
import org.civilian.template.TestTemplateWriter;
import org.civilian.template.mixin.HtmlMixin;
import org.civilian.CivTest;


public class AssetListTest extends CivTest
{
	@Test public void test() throws Exception
	{
		AssetList list = new AssetList(AssetList.JS_TYPE, "js/test1.js", "js/test2.js");
		assertEquals(2, list.size());
		assertEquals("js/test1.js", list.getPath(0));
		assertSame(AssetList.JS_TYPE, list.getType());
		
		list.setProductionPaths("js/prod.js");
		assertArrayEquals2(list.getProductionPaths(), "js/prod.js");
		
		TestTemplateWriter out = TestTemplateWriter.create();
		when(out.app.getPath()).thenReturn(new Path("/assets"));
		list.print(out);
		out.assertOutNormed("<script src='/assets/js/prod.js'></script>\n");
	}


	@Test public void testTypes() throws Exception
	{
		HtmlMixin html = mock(HtmlMixin.class);
		
		AssetList.CSS_TYPE.printRef("x", html);
		verify(html).linkCss("x");

		AssetList.JS_TYPE.printRef("y", html);
		verify(html).script("y");
	}
}
