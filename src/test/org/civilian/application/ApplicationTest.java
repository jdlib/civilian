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


import java.io.IOException;
import org.junit.Test;
import org.civilian.CivTest;
import org.civilian.ConfigKeys;
import org.civilian.content.ContentType;
import org.civilian.resource.PathParamMap;
import org.civilian.server.test.TestApp;
import org.civilian.server.test.TestRequest;
import org.civilian.server.test.TestServer;
import org.civilian.util.ClassUtil;
import org.civilian.util.Settings;


public class ApplicationTest extends CivTest
{
	private static class NoArgsApp extends Application
	{
		@Override protected void init(AppConfig config) throws Exception
		{
		}

		@Override protected void close() throws Exception
		{
		}
	}
	
	
	private static class RelPackageApp extends Application
	{
		public RelPackageApp()
		{
			super(null, ".root");
		}
		
		@Override protected void init(AppConfig config) throws Exception
		{
		}

		@Override protected void close() throws Exception
		{
		}
	}

	
	private static class ErrorApp extends Application
	{
		@Override protected void init(AppConfig config) throws Exception
		{
			throw new IOException("io");
		}

		@Override protected void close() throws Exception
		{
		}
	}
	
	
	@Test public void testCreate()
	{
		String pname = ClassUtil.getPackageName(getClass());
		
		Application app = new NoArgsApp();
		assertEquals(pname, app.getControllerConfig().getRootPackage());
		assertSame(PathParamMap.EMPTY, app.getControllerConfig().getPathParams());

		app = new RelPackageApp();
		assertEquals(pname + ".root", app.getControllerConfig().getRootPackage());
		assertSame(PathParamMap.EMPTY, app.getControllerConfig().getPathParams());
	}


	@Test public void testAccessors()
	{
		Application app = new NoArgsApp();
		assertSame(app, app.getApplication());
		assertNull(app.getVersion());
		assertFalse(app.ignoreError(null));
		assertEquals("app '?'", app.toString());

		assertNull(app.getAttribute("x"));
		app.setAttribute("x", "y");
		assertEquals("y", app.getAttribute("x"));
		assertEquals("x", app.getAttributeNames().next());
		assertNull(app.getContentSerializer(ContentType.APPLICATION_JSON));
	}
	
	
	@Test public void testInit() throws Exception
	{
		TestServer server = new TestServer();
		server.setDevelop(true);
		
		ErrorApp errorApp = new ErrorApp();
		server.addApp(errorApp, "err", "err", null);
		assertEquals(Application.Status.ERROR, errorApp.getStatus());

		TestApp testApp = new TestApp();
		Settings settings = new Settings();
		settings.set(ConfigKeys.DEV_CLASSRELOAD, true);
		server.addApp(testApp, "test", "test", settings);
		assertEquals(Application.Status.RUNNING, testApp.getStatus());
		assertTrue(testApp.getControllerService().isReloading());
		assertNotNull(testApp.getProcessors());
		assertNotNull(testApp.getRootResource());
		assertNull(testApp.getContentSerializer(ContentType.APPLICATION_EXCEL));
		assertNotNull(testApp.getContentSerializer(ContentType.APPLICATION_JSON));
		assertNotNull(testApp.getContentSerializer(ContentType.TEXT_PLAIN));
	}
	
	
	@Test public void testProcess() throws Exception
	{
		TestApp app = new TestApp();
		TestRequest request = new TestRequest(app);
		
		app.process(request);
	}
}
