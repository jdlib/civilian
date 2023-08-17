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
package org.civilian.asset.service;


import org.civilian.asset.Asset;
import org.civilian.asset.TestAsset;
import org.civilian.resource.Path;


public class TestLocation extends AssetLocation
{
	public TestLocation(String relativePath, String assetPath)
	{
		this(relativePath, assetPath, null);
	}
	
	
	public TestLocation(String relativePath, String assetPath, Asset asset)
	{
		super(relativePath);
		assetPath_ 	= Path.norm(assetPath);
		asset_ 		= asset;
	}


	@Override protected Asset find(Path assetPath) throws Exception
	{
		findCalled_++;
		if (assetPath_.equals(assetPath.toString()))
			return asset_ != null ? asset_ : new TestAsset();
		else
			return null;
	}


	@Override protected String getInfoParam()
	{
		return "Test";
	}
	
	
	public int findCalled()
	{
		return findCalled_;
	}
		
	
	public void setAsset(Asset asset)
	{
		asset_ = asset;
	}
	
	
	private String assetPath_;
	private Asset asset_; 
	private int findCalled_;
}
