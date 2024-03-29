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


import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.civilian.ConfigKeys;
import org.civilian.asset.Asset;
import org.civilian.content.ContentType;
import org.civilian.resource.Path;
import org.civilian.server.Server;
import org.civilian.util.Settings;


/**
 * AssetServices is a factory class to create AssetLocations
 * and other AssetService implementations.
 */
public abstract class AssetServices
{
	/**
	 * A key used civilian.ini for a directory based AssetLocation.
	 */
	public static final String DIR_LOCATION_KEY	= "dir";

	
	/**
	 * A key used in civilian.ini for a class-path based AssetLocation
	 * which serves Java resources.
	 */
	public static final String JAVARES_LOCATION_KEY = "res";

	
	/**
	 * A key used in civilian.ini for an AssetLocation which serves Java resource from civilian.jar
	 */
	public static final String CIVRES_LOCATION_KEY = "civres";

	
	/**
	 * The default path below the asset root.
	 */ 
	public static final String DEFAULT_PATH	= "";
	
		
	
	/**
	 * Returns the AssetLocations of an application.
	 * @param appSettings the application settings 
	 * @param server the server
	 * @param appPath the path to the application
	 * @throws Exception if an exception occurs
	 * @return the location list
	 */
	public static List<AssetLocation> getLocations(Settings appSettings, Server server, Path appPath)
		throws Exception
	{
		List<AssetLocation> list = new ArrayList<>();

		int counter = -1;
		int missed = 0;
		while(missed < 5)
		{
			// keys are "location", "location.0", "location.1", etc.  
			String key = ConfigKeys.LOCATION + (counter < 0 ? "" : '.' + String.valueOf(counter));
			counter++;
			String def = appSettings.get(key, null);
			if (def == null)
				++missed;
			else
			{
				Settings locSettings	= new Settings(appSettings, key + '.');
				AssetLocation location	= getLocation(def, locSettings, server, appPath);
				list.add(location);
			}
		}
		
		return list;
	}
	
	
	/**
	 * Returns the AssetLocation of an application, defined by an entry in civilian.ini.
	 * The definition has the form type (':' param)? ('-&gt;' path)?
	 * @param definition an entry app.&lt;appid&gt;.asset.location[.&lt;n&gt;]
	 * @param locSettings the location settings
	 * @param server the server
	 * @param appPath the application path
	 * @return the location
	 * @throws Exception if an exception occurs
	 */
	public static AssetLocation getLocation(String definition, Settings locSettings, Server server, Path appPath)
		throws Exception
	{
		String relPath;
		String type = definition;
		String param = null;
		
		// extract the path
		int p = definition.lastIndexOf("->");
		if (p < 0)
			relPath = locSettings.get(ConfigKeys.PATH, null);
		else
		{
			relPath = definition.substring(p + 2).trim();
			type 	= definition.substring(0, p).trim();
		}
		
		// extract the param
		p = type.indexOf(':');
		if (p >= 0)
		{
			param = type.substring(p + 1).trim();
			type  = type.substring(0, p).trim();
		}
		
			
		AssetLocation location;
		if (DIR_LOCATION_KEY.equals(type))
		{
			location = getDirectoryLocation(relPath, server.getServerFiles().getRootDir(), param != null ? param : "");
		}
		else if (JAVARES_LOCATION_KEY.equals(type))
		{
			// type must be of form "res:<prefix>"
			if (param == null)
				throw new IllegalArgumentException("asset location '" + definition + "' has no resource prefix");
			location = getJavaResourceLocation(relPath, param);
		}
		else if (CIVRES_LOCATION_KEY.equals(type))
		{
			location = getCivResourceLocation(relPath, appPath.toString(), server.develop());
		}
		else
			throw new IllegalArgumentException("invalid asset location type '" + type + "'");
		
		String contentType = locSettings.get(ConfigKeys.CONTENT_TYPE, null);
		if (contentType != null)
			location.setContentType(ContentType.getContentType(contentType));
			
		Charset charset = locSettings.getCharset(ConfigKeys.ENCODING, null);
		location.setEncoding(charset);
		
		return location;
	}

	
	/**
	 * Returns an AssetLocation whose assets
	 * are files in the local file-system. The location
	 * is defined by a root directory. Asset paths are translated into 
     * file paths relative to that root directory.
	 * @param path the path of the AssetLocation below the asset-root. 
	 * @param dir the root directory. 
	 * @return the location
	 */
	public static AssetLocation getDirectoryLocation(String path, File dir)
	{
		return new DirectoryLocation(path, dir);
	}

	
	/**
	 * Returns a new AssetLocation for asset files in the local file-system.
	 * @param path the path of the AssetLocation below the asset-root. 
	 * @param serverRootDir the server root directory 
	 * @param directory a directory. If null, then the server root directory is used. If relative
	 * 		then the (server directory)/directory is used. Else if absolute the directory itself is used.
	 * @return the location
	 */
	public static AssetLocation getDirectoryLocation(String path, File serverRootDir, String directory)
	{
		File result;
		
		if (directory == null)
			result = serverRootDir;
		else
		{
			result = new File(directory);
			if (!result.exists())
				result = new File(serverRootDir, directory);
		}		
		
		return getDirectoryLocation(path, result);
	}

	
	/**
	 * Returns an AssetLocation which returns Java resources from the class-path as Assets.
	 * @param path the path of the AssetLocation below the asset root. 
	 * @param prefix the prefix path of the resources.
	 * 		Must not be empty for security reasons. Asset paths are translated 
	 * 		into resource names by prepending the prefix to the asset path.
	 * @return the location
	 */
	public static AssetLocation getJavaResourceLocation(String path, String prefix)
	{
		return new JavaResourceLocation(path, prefix);
	}
	

	/**
	 * Returns an AssetLocation which serves Java resources from civilian.jar.
	 * @param path the path of the AssetLocation below the asset root.
	 * 		If null, the path "civilian" is used.
	 * @param appPath the path to the application
	 * @param develop the develop flag
	 * @return the location
	 */
	public static AssetLocation getCivResourceLocation(String path, String appPath, boolean develop)
	{
		return new CivResourceLocation(path, appPath, develop);
	}
	
	
	/**
	 * Adds caching capabilities to an AssetService. Assets returned by the AssetService
	 * will be cached and returned on subsequent requests, if the Asset is still valid.   
	 * @param service a AssetService implementation
	 * @param maxMemSize if the size of an Asset is smaller than maxMemSize
	 * 		than its contents will also be held in memory which will speed up request processing
	 * 		of those assets. 
	 * @return the new caching AssetService
	 * @see Asset#isValid()
	 */
	public static AssetService makeCaching(AssetService service, int maxMemSize)
	{
		return new AssetCache(service, maxMemSize);
	}


	/**
	 * Returns an AssetService which combines the given services.
	 * If no services are provided then the returned AssetService will not be able to serve assets.
	 * (see {@link AssetService#hasAssets()}).<br>
	 * @param prefixPath a path to be prepended to the paths of the given services
	 * @param services a list of services.
	 * @return the combined service
	 */
	public static AssetService combine(Path prefixPath, AssetService... services)
	{
		return services.length == 1 ?
			services[0] :
			new CombinedAssetService(prefixPath, services);
	}
}
