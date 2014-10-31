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
package org.civilian.response.protocol;


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.response.protocol.Message;


public class MessageTest extends CivTest
{
	@Test public void test()
	{
		Message message = new Message();
		assertTrue(message.isEmpty());
		
		assertEquals("info", message.getType());
		assertNull(message.getTitle());
		assertNull(message.getText());
		
		message.asSuccess().setText("text").setTitle("title");
		assertEquals("success", message.getType());
		assertEquals("title", message.getTitle());
		assertEquals("text", message.getText());
		assertFalse(message.isEmpty());
		
		message.asError();
		assertEquals("error", message.getType());

		message.asInfo();
		assertEquals("info", message.getType());

		message.asWarning();
		assertEquals("warning", message.getType());
		
	}
}
