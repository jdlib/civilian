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


import org.civilian.CivTest;
import org.civilian.Version;
import org.civilian.util.IoUtil;
import org.junit.Test;


public class VersionTest extends CivTest
{
	@Test public void testMain()
	{
		try(SysOut out = new SysOut())
		{
			Version.main(new String[] {});
			
			assertEquals(Version.VALUE + IoUtil.getLineSeparator(), out.toString());

			out.reset();
			Version.main(new String[] { "-v"});
			assertEquals("civilian.version = " + Version.VALUE + IoUtil.getLineSeparator(), out.toString());
		}
	}
}
