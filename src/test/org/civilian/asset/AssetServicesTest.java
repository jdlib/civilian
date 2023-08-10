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
package org.civilian.asset;


import java.io.File;
import java.util.List;
import org.civilian.CivTest;
import org.civilian.content.ContentType;
import org.civilian.server.test.TestApp;
import org.civilian.server.test.TestServer;
import org.civilian.util.Settings;
import org.junit.Test;


public class AssetServicesTest extends CivTest
{
	@Test public void test() throws Exception
	{
		TestServer server = new TestServer();
		TestApp app = new TestApp();
		server.addApp(app, "app", "", null);
		
		File dir = new File(".").getAbsoluteFile();
		
		Settings settings = new Settings();
		settings.set("location.0", 				"dir:" + dir.getAbsolutePath());
		settings.set("location.0.encoding", 	"ISO-8859-1");
		settings.set("location.1", 				"civres");
		settings.set("location.3", 				"res:assets -> cp");
		settings.set("location.3.encoding", 	"UTF-8");
		settings.set("location.3.contentType",	"text/css");
		
		List<AssetLocation> locations = AssetServices.getLocations(settings, server, app.getPath());
		assertEquals(3, locations.size());
		
		AssetLocation loc = locations.get(0);
		assertEquals("", loc.getRelativePath().toString());
		assertEquals("ISO-8859-1", loc.getCharEncoding());

		loc = locations.get(1);
		assertEquals(null, loc.getCharEncoding());
		assertEquals("/civilian", loc.getRelativePath().toString());

		loc = locations.get(2);
		assertEquals("/cp", loc.getRelativePath().toString());
		assertEquals(ContentType.TEXT_CSS, loc.getContentType());
		assertEquals("/cp -> res:assets, UTF-8", loc.getInfo());
	}
}
