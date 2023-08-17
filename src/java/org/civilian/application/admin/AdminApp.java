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
package org.civilian.application.admin;


import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.asset.service.AssetConfig;
import org.civilian.asset.service.CivResourceLocation;
import org.civilian.processor.IpFilter;
import org.civilian.processor.ProcessorConfig;


public class AdminApp extends Application
{
	public AdminApp()
	{
		super(AdminPathParams.MAP);
	}
	
	
	@Override protected void init(AppConfig config) throws Exception
	{
		config.setResourceRoot(AdminResources.root);
		
		AssetConfig assetConfig = config.getAssetConfig();
		assetConfig.clearLocations();
		assetConfig.addLocation(new CivResourceLocation());
	}
	
	
	/**
	 * If no explicit IpFilter is configured, we only allow access
	 * to localhost.
	 */
	@Override protected void initProcessors(ProcessorConfig config) throws Exception
	{
		if (config.indexOf(IpFilter.class) < 0)
		{
			IpFilter filter = new IpFilter(IpFilter.LOCALHOST);
			config.addFirst(filter);
		}
	}
	
	
	@Override protected void close() throws Exception
	{
	}
}
