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
package org.civilian.controller;


import java.sql.SQLException;
import org.civilian.CivTest;
import org.civilian.Controller;
import org.civilian.annotation.Get;
import org.civilian.server.test.TestApp;
import org.junit.BeforeClass;
import org.junit.Test;


public class ControllerTest extends CivTest
{
	public static class TestController extends Controller
	{
		@Get public void testAccessors() throws Exception
		{
			assertSame(app_, getApplication());
			assertSame(app_.getServer(), getServer());
			assertSame(getClass(), getControllerType().getControllerClass());
			assertTrue(isProcessing());
			assertNull(getException());
			assertEquals("?msg", msg("msg"));
			
			// can't invoke myself
			try
			{
				process(getRequest());
			}
			catch(IllegalStateException e)
			{
				assertEquals("already processing", e.getMessage());
			}
		}

	
		@Get public void testException() throws Exception
		{
			throw new SQLException("little bobby tables");
		}
	}


	@BeforeClass public static void beforeClass()
	{
		app_ = new TestApp();
		app_.init();
	}

	
	@Test public void testNonRequestAccess()
	{
		Controller controller = new TestController();
		try
		{
			assertEquals(app_.develop(), controller.develop());
		}
		catch (IllegalStateException e)
		{
			assertEquals("may not be called outside of process(Request)", e.getMessage());
		}
	}


	private static TestApp app_;
}
