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
 package org.civilian.samples.error;


import org.civilian.application.AppConfig;
import org.civilian.application.Application;


public class ErrorApp extends Application
{
	@Override protected void init(AppConfig config) throws Exception
	{
		// uncomment entry initerr in the app config in civilian.ini
		// to see this error
		if (config.getSettings().getBoolean("initerr"))
			throw new IllegalStateException("an error in my initialization method happened");
	}
	

	@Override protected void close() throws Exception
	{
	}
}
