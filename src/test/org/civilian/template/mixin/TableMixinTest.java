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


import org.civilian.CivTest;
import org.civilian.template.TestTemplateWriter;
import org.junit.Test;


public class TableMixinTest extends CivTest
{
	@Test public void testTableColumn()
	{
		TableMixin.Column col = new TableMixin.Column();

		col.setPercentWidth(5);
		assertEquals("5%", col.getWidth());
		
		col.setWidth(200);
		assertEquals("200px", col.getWidth());
	}
	
	
	@Test public void testTableModel()
	{
		TableMixin.Model model = new TableMixin.Model(5);
		assertEquals(5, model.getColumnCount());
		model.setColumnAttrs("align", "right");
		assertArrayEquals2(model.getColumn(0).getAttrs(), "align", "right"); 
	}
	

	@Test public void testTableModelDef1()
	{
		TableMixin.Model model = new TableMixin.Model("[]10[90,class=fill][50%,class='fill top'][100,class=fill,align=right]30[][80]");
		assertEquals(8, model.getColumnCount());
		
		int next = 0;
		TableMixin.Column col;
		
		col = model.getColumn(next++);
		assertNull(col.getWidth());
		assertNull(col.getAttrs());
		assertFalse(col.isGap());

		col = model.getColumn(next++);
		assertTrue(col.isGap());
		assertEquals("10px", col.getWidth());

		col = model.getColumn(next++);
		assertEquals("90px", col.getWidth());
		assertArrayEquals2(col.getAttrs(), "class", "fill");
		assertFalse(col.isGap());

		col = model.getColumn(next++);
		assertEquals("50%", col.getWidth());
		assertArrayEquals2(col.getAttrs(), "class", "fill top");
		assertFalse(col.isGap());

		col = model.getColumn(next++);
		assertEquals("100px", col.getWidth());
		assertArrayEquals2(col.getAttrs(), "class", "fill", "align", "right");
		assertFalse(col.isGap());

		col = model.getColumn(next++);
		assertTrue(col.isGap());
		assertEquals("30px", col.getWidth());

		col = model.getColumn(next++);
		assertNull(col.getWidth());
		assertNull(col.getAttrs());
		assertFalse(col.isGap());

		col = model.getColumn(next++);
		assertEquals("80px", col.getWidth());
		assertNull(col.getAttrs());
		assertFalse(col.isGap());
	}
	

	@Test public void testTableModelDef2()
	{
		TableMixin.Model model = new TableMixin.Model("[][]");
		assertEquals(2, model.getColumnCount());
		
		int next = 0;
		TableMixin.Column col;
		for (int i=0; i<2; i++)
		{
			col = model.getColumn(next++);
			assertNull(col.getWidth());
			assertNull(col.getAttrs());
			assertFalse(col.isGap());
		}
	}
	
	
	@Test public void testTableModelDef3()
	{
		try
		{
			new TableMixin.Model("[align]");
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expected '=' (7): '[align]", e.getMessage());
		}
	}
	
	
	@Test public void testTableWriter1()
	{
		TestTemplateWriter out = TestTemplateWriter.create();
		
		TableMixin table = new TableMixin(out);

		table.columns("[][5%][200][][]");
		assertEquals(5, table.columns());
		table.defaultRowAttrs("valign", "top");
		
		table.startTable("a", "b");
		out.assertOutNormed("<table a='b'>\n");
		
		table.startCell();
		out.assertOutNormed("<colgroup>\n" +
			"\t<col>\n" +
			"\t<col width='5%'>\n" +
			"\t<col width='200px'>\n" + 
			"\t<col>\n" +
			"\t<col>\n" +
			"</colgroup>\n" + 
			"<tr valign='top'>\n\t<td>");
	}


	@Test public void testTableWriterRowspan()
	{
		TestTemplateWriter out = TestTemplateWriter.create();
		TableMixin table = new TableMixin(out);

		table.columns("[][class='x']");
		assertEquals(2, table.columns());
		table.startTable();
		table.rowspan(2).startCell();
		out.print("a");
		table.endCell();
		table.startCell();
		out.print("b");
		table.endCell();
		table.startCell();
		out.print("c");
		table.endCell();
		table.endTable();
		out.assertOutNormed("<table>\n" +
			"<tr>\n" +
			"\t<td rowspan='2'>a</td>\n" +
			"\t<td class='x'>b</td>\n" +
			"</tr>\n" +
			"<tr>\n" +
			"\t<td class='x'>c</td>\n" +
			"</tr>\n" +
			"</table>\n");
	}
}
