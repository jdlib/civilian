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
package org.civilian.util.classpath;


import java.net.URL;
import org.junit.Test;
import org.civilian.CivTest;


public class VfsTest extends CivTest
{
	@SuppressWarnings("deprecation")
	@Test public void test() throws Exception
	{
		assertNotNull(Vfs.create());
		
		assertFalse(VfsProtocol.INSTANCE.accept(new URL("http://test.com")));
		VfsProtocol.INSTANCE.scan(null, null);
	}
}
