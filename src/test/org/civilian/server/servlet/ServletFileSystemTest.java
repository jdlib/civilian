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
package org.civilian.server.servlet;


import org.civilian.CivTest;
import org.civilian.server.servlet.ServletUtil;
import org.junit.Test;


public class ServletFileSystemTest extends CivTest
{
	@Test public void testInhibited()
	{
		assertInhibited("WEB-INF", true);
		assertInhibited("META-INF", true);
		assertInhibited("/awEb-InF/c", true);
		assertInhibited("/amEtA-InF/c", true);
		assertInhibited("/a/b/c", false);
		assertInhibited("/a/WEB-In/c", false);
	}


	private void assertInhibited(String path, boolean prohibited)
	{
		if (ServletUtil.isProhibitedPath(path) != prohibited)
			fail(path);
	}
}
