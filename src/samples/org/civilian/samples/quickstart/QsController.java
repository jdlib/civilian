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
 package org.civilian.samples.quickstart;


import org.civilian.Controller;


/**
 * Common base class for all Qs controllers.
 */
public abstract class QsController extends Controller implements QsResources 
{
	/**
	 * Casts the application to the QsApp class.
	 */
	public QsApp getQsApp()
	{
		return (QsApp)getApplication();
	}
}
