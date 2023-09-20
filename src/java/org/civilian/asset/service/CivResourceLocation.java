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


import java.nio.charset.StandardCharsets;
import org.civilian.asset.Asset;
import org.civilian.asset.BytesAsset;
import org.civilian.content.ContentType;
import org.civilian.resource.Path;


/**
 * CivAssetsLocation is a specialized ClassPathLocation
 * which serves assets included in the Civilian jar file.
 * It also serves a virtual asset with relative path "/angular/civ-init.js",
 * (={@link #ANGULAR_INIT_SCRIPT}) for initialization
 * of the Civilian's angular support.
 */
public class CivResourceLocation extends JavaResourceLocation
{
	/**
	 * The name of the header which is sent for Ajax requests sent
	 * by CivRequest (as defined in civilian.js).
	 */
	public static final String CIVILIAN_HEADER = "X-Civilian";
	
	public static final String DEFAULT_PATH = "civilian";
	public static final String ANGULAR_INIT_SCRIPT  = "/angular/civ-init.js";
	
	
	/**
	 * Creates a new CivAssetsLocation object,
	 * for the location path "/civilian".
	 */
	public CivResourceLocation()
	{
		this(DEFAULT_PATH);
	}
	
	
	/**
	 * Creates a new CivAssetsLocation object.
	 * @param path the path
	 */
	public CivResourceLocation(String path)
	{
		this(path, null, false);
	}


	public CivResourceLocation(String path, String appPath, boolean develop)
	{
		super(path != null ? path : DEFAULT_PATH, "civassets" /* the directory of the assets in civilian.jar*/);
		
		// civassets are encoded in UTF-8
		setEncoding(StandardCharsets.UTF_8); 

		if (appPath != null)
		{
			StringBuilder s = new StringBuilder();
			s.append("civilian.appPath = new civilian.Path('").append(appPath).append("');\n");
			s.append("civilian.basePath = civilian.Path.createBasePath();\n");
			if (develop)
				s.append("civilian.develop = true;");
			initScript_ = new BytesAsset(getEncoding(), s.toString());
			initScript_.setContentType(ContentType.APPLICATION_JAVASCRIPT);
		}
	}

	
	/**
	 * Returns the requested asset or builds it if the path is {@link #ANGULAR_INIT_SCRIPT} 
	 */
	@Override protected Asset find(Path assetPath) throws Exception
	{
		if (assetPath.getValue().equals(ANGULAR_INIT_SCRIPT))
			return initScript_;
		else
			return super.find(assetPath);
	}


	private Asset initScript_;
}
