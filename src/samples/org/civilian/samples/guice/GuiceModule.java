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
package org.civilian.samples.guice;


import org.civilian.samples.guice.service.Service;
import org.civilian.samples.guice.service.ServiceImpl;
import com.google.inject.AbstractModule;


public class GuiceModule extends AbstractModule
{
	@Override protected void configure()
	{
		bind(Service.class).to(ServiceImpl.class);
	}
}
