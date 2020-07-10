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
import org.civilian.context.test.TestApp;


public class AppConfigTest extends CivTest
{
	@Test public void testEnabled()
	{
		assertTrue(AppConfig.isEnabled((String)null, true, false));
		assertTrue(AppConfig.isEnabled("true", false, false));
		assertFalse(AppConfig.isEnabled("false", true, true));
		assertFalse(AppConfig.isEnabled("develop", true, false));
		assertTrue(AppConfig.isEnabled("develop", true, true));
		assertFalse(AppConfig.isEnabled("production", true, true));
		assertTrue(AppConfig.isEnabled("production", true, false));
		
		try
		{
			AppConfig.isEnabled("x", true, false);
			fail();
		}
		catch(IllegalArgumentException e)
		{
		}
	}


	@Test public void testConfig() throws Exception
	{
		TestApp app = new TestApp();
		AppConfig config = new AppConfig(app, null);
		
		assertNotNull(config.getSettings());
		assertNull(config.getVersion());
		
		config.setAllowUnsupportedLocales(true);
		assertTrue(config.allowUnsupportedLocales());
		
		config.setTypeLib(config.getTypeLib());
		config.setUploadConfig(config.getUploadConfig());
		
		assertNotNull(config.getExtensionMapping());
		
		config.setDefaultResExtension(".html");
		assertEquals("html", config.getDefaultResExtension());
	}
}
