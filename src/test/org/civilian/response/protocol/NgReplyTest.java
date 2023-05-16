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


import org.civilian.CivTest;
import org.junit.Test;


public class NgReplyTest extends CivTest
{
	@Test public void testMsg()
	{
		// merely coverage
		NgReply reply = new NgReply();
		Message msg = reply.addMessage();
		reply.addMessage(msg);
		reply.addMessage("text");
		reply.addMessage("text", "title");
		assertEquals(4, reply.getMessageCount());
		reply.clearMessages();
		assertEquals(0, reply.getMessageCount());
	}


	@Test public void testToast()
	{
		// merely coverage
		NgReply reply = new NgReply();
		Message msg = reply.addToast();
		reply.addToast(msg);
		reply.addToast("text");
		reply.addToast("text", "title");
		assertEquals(4, reply.getToastCount());
		reply.clearToasts();
		assertEquals(0, reply.getToastCount());
	}


	@Test public void testAlerts()
	{
		// merely coverage
		NgReply reply = new NgReply();
		reply.addAlert("text1");
		reply.addAlert("text2");
		assertEquals(2, reply.getAlertCount());
		reply.clearAlerts();
		assertEquals(0, reply.getAlertCount());
	}


	@Test public void testLogs()
	{
		// merely coverage
		NgReply reply = new NgReply();
		reply.addLog("text1");
		reply.addLog("text2");
		assertEquals(2, reply.getLogCount());
		reply.clearLogs();
		assertEquals(0, reply.getLogCount());
	}


	@Test public void testVars()
	{
		// merely coverage
		NgReply reply = new NgReply();
		reply.addVariable("x", Boolean.TRUE);
		reply.addVariable("y", Boolean.FALSE);
		assertEquals(2, reply.getVariableCount());
		reply.clearVariables();
		assertEquals(0, reply.getVariableCount());
	}


	@Test public void testScopeVars()
	{
		// merely coverage
		NgReply reply = new NgReply();
		reply.addScopeVariable("x", Boolean.TRUE);
		reply.addScopeVariable("y", Boolean.FALSE);
		assertEquals(2, reply.getScopeVariableCount());
		reply.clearScopeVariables();
		assertEquals(0, reply.getScopeVariableCount());
	}


	@Test public void testClearAll()
	{
		NgReply reply = new NgReply();
		reply.addMessage();
		reply.addScopeVariable("x", Boolean.TRUE);
		assertEquals(1, reply.getMessageCount());
		assertEquals(1, reply.getScopeVariableCount());
		
		reply.clear();
		assertEquals(0, reply.getMessageCount());
		assertEquals(0, reply.getScopeVariableCount());
	}
}
