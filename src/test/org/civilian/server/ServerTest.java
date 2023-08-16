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
package org.civilian.server;


import java.io.File;
import org.civilian.CivTest;
import org.civilian.server.test.TestServer;
import org.junit.Test;


public class ServerTest extends CivTest
{
	@Test public void testAccessors()
	{
		File dir = new File(".").getAbsoluteFile();
		TestServer server = new TestServer(dir);
		assertEquals(0, server.getApplications().size());
		assertNull(server.getApplication("x"));
		assertEquals(dir, server.getRootDir());
	}
}
