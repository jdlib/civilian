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
package org.civilian;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Defines constants for the keys of the civilian.ini.
 */
public interface ConfigKeys
{
	/**
	 * A key prefix of the Civilian config.
	 */
	public static final String ADMINAPP_PREFIX = "civ.admin.";
	

	/**
	 * Default value for the path of the admin app.
	 */
	public static final String ADMIN_PATH_DEFAULT   = "civadmin";
	
	
	/**
	 * A key of the Civilian config.
	 */
	public static final String APPLICATIONS	= "applications";

	
	/**
	 * A key prefix of the Civilian config.
	 */
	public static final String APP_PREFIX = "app.";
	
	
	/**
	 * A key of the Civilian config.
	 */
	public static final String ASSET_PREFIX	= "asset.";


	/**
	 * A key of the Civilian config.
	 */
	public static final String CLASS = "class";


	/**
	 * A key of the Civilian config.
	 */
	public static final String ASYNC = "async";


	/**
	 * A key of the Civilian config.
	 */
	public static final String CONTENT_TYPE = "contentType";
	
	
	/**
	 * A key of the Civilian config.
	 */
	public static final String DEV_CLASSRELOAD = "dev.classreload";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String DEVELOP = "develop";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String DIR = "dir";


	/**
	 * A key of the Civilian config.
	 */
	public static final String ENABLED = "enabled";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String ENCODING	= "encoding";

	
	/**
	 * Default value "UTF-8" for encodings.
	 */
	public static final Charset ENCODING_DEFAULT = StandardCharsets.UTF_8;


	/**
	 * A key of the Civilian config.
	 */
	public static final String EXCLUDE = "exclude";


	/**
	 * A key of the Civilian config.
	 */
	public static final String FILESIZETHRESHOLD = "fileSizeThreshold";


	/**
	 * A key of the Civilian config.
	 */
	public static final String INCLUDE = "include";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String IP = "ip";


	/**
	 * A key of the Civilian config.
	 */
	public static final String LOCALES = "locales";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String LOCATION = "location";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String MAXFILESIZE = "maxFileSize";
	
	
	/**
	 * A key of the Civilian config.
	 */
	public static final String MAXREQUESTSIZE = "maxRequestSize";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String PACKAGE = "package";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String PATH = "path";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String MESSAGES = "messages";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String TYPE = "type";

	
	/**
	 * A key of the Civilian config.
	 */
	public static final String UPLOAD_PREFIX = "upload.";
}
