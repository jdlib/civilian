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
package org.civilian.internal.classpath;


import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.util.ClassUtil;


public class ClassPathScanTest extends CivTest
{
	@Test public void test() throws Exception
	{
		assertFalse(ClassPathScan.equinoxDetected());
		
		String packageName = ClassUtil.getPackageName(getClass());
		
		ClassPathScan scan = new ClassPathScan(packageName);
		assertTrue(scan.collect().size() > 0);
	}
}
