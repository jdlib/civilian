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


import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Test;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.civilian.CivTest;
import org.civilian.content.JaxbXmlSerializer;
import org.civilian.content.GsonJsonSerializer;


public class ContentSerializerTest extends CivTest
{
	@Test public void testGsonJsonSerializer() throws Exception
	{
		GsonJsonSerializer reader = new GsonJsonSerializer();

		// read write to a target type
		assertEquals(Boolean.TRUE, reader.read(Boolean.class, "true"));
		assertEquals("false", reader.write(Boolean.FALSE));
		
		// read write to a JsonElement
		JsonElement one = reader.read(JsonElement.class, "1");
		assertEquals(new JsonPrimitive(Integer.valueOf(1)), one);
		assertEquals("1", reader.write(one));
	}
	
	
	@Test public void testJaxbReader() throws Exception
	{
		JAXBContext context = JAXBContext.newInstance(Demo.class);
		JaxbXmlSerializer reader = new JaxbXmlSerializer(context);
		assertTrue(reader.read(Demo.class, "<demo/>") instanceof Demo);
	}
	
	
	@XmlRootElement
	private static class Demo
	{
	}
}
