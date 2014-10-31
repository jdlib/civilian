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
package org.civilian.application;


import org.junit.Test;
import org.civilian.CivTest;



public class UploadConfigTest extends CivTest
{
	@Test public void test()
	{
		UploadConfig config = new UploadConfig(true);
		assertTrue(config.isEnabled());
		assertEquals(UploadConfig.DEFAULT_MAXREQUESTSIZE,		config.getMaxRequestSize());
		assertEquals(UploadConfig.DEFAULT_MAXFILESIZE, 			config.getMaxFileSize());
		assertEquals(UploadConfig.DEFAULT_TEMP_DIRECTORY,		config.getTempDirectory());
		assertEquals(UploadConfig.DEFAULT_FILESIZETHRESHOLD,	config.getFileSizeThreshold());
	}
}
