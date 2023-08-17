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
package org.civilian.application;


import org.civilian.ConfigKeys;
import org.civilian.util.Check;
import org.civilian.util.Settings;


/**
 * UploadConfig specifies how an application handles upload requests.
 * If the application decides to accept uploaded files,
 * the UploadConfig determines the maximal size of the total request,
 * the uploaded file, the threshold size when to save uploaded
 * files temporarily to a local storage, and the location of that local storage.
 * The Upload object can be either configured in the Civilian config file
 * or during application setup.
 * @see AppConfig#setUploadConfig(UploadConfig)
 * @see AppConfig#getUploadConfig()
 */
public class UploadConfig
{
	public static final String DEFAULT_TEMP_DIRECTORY 	= "";
	public static final long DEFAULT_MAXREQUESTSIZE 	= -1L;
	public static final long DEFAULT_MAXFILESIZE 		= -1L;
	public static final int DEFAULT_FILESIZETHRESHOLD	= 0;
	public static final boolean DEFAULT_ENABLED			= false;
	

	/**
	 * Creates a new UploadConfig initialized from the given config.
	 */
	public UploadConfig(Settings settings)
	{
		enabled_			= settings.getBoolean(ConfigKeys.ENABLED,		DEFAULT_ENABLED);
		maxRequestSize_		= settings.getLong(ConfigKeys.MAXREQUESTSIZE,	DEFAULT_MAXREQUESTSIZE);
		maxFileSize_		= settings.getLong(ConfigKeys.MAXFILESIZE, 		DEFAULT_MAXFILESIZE);
		tempDirectory_ 		= settings.get(ConfigKeys.DIR,					DEFAULT_TEMP_DIRECTORY);
		fileSizeThreshold_	= settings.getInt(ConfigKeys.FILESIZETHRESHOLD,	DEFAULT_FILESIZETHRESHOLD);
	}
	
	
	/**
	 * Creates a new UploadConfig with default values.
	 * @param enabled are uploads enabled?
	 */
	public UploadConfig(boolean enabled)
	{
		this(enabled, DEFAULT_MAXREQUESTSIZE, DEFAULT_MAXFILESIZE, DEFAULT_TEMP_DIRECTORY, DEFAULT_FILESIZETHRESHOLD);
	}


	/**
	 * Creates a new UploadConfig with default values.
	 * @param enabled are uploads enabled?
	 * @param maxRequestSize the maximum byte size allowed for upload requests, or -1L if request size is not limited.
	 * @param maxFileSize the maximum byte size allowed for single files within an upload request, or -1L if file size is not limited.
	 * @param tempDirectory sets the directory where files will be stored temporarily when their length 
	 * 		exceeds the size threshold. 
	 * @param fileSizeThreshold the file size threshold or 0 if the threshold is not limited. If an uploaded file size exceeds the threshold, 
	 * 		it will be written to the temp directory.
	 */
	public UploadConfig(boolean enabled, long maxRequestSize, long maxFileSize, String tempDirectory, int fileSizeThreshold)
	{
		enabled_			= enabled;
		tempDirectory_		= Check.notNull(tempDirectory, "tempDirectory");
		maxRequestSize_ 	= maxRequestSize >= 0 ? maxRequestSize : DEFAULT_MAXREQUESTSIZE;
		maxFileSize_ 		= maxFileSize >= 0 ? maxFileSize : DEFAULT_MAXFILESIZE;
		fileSizeThreshold_	= Math.max(fileSizeThreshold,  0);
	}
	
	
	/**
	 * Returns if uploads are enabled.
	 */
	public boolean isEnabled()
	{
		return enabled_;
	}

	
	/**
	 * Returns the maximum byte size allowed for upload requests, 
	 * or -1 if the request size is unlimited. 
	 */
	public long getMaxRequestSize()
	{
		return maxRequestSize_;
	}


	/**
	 * Returns the maximum byte size allowed for single files within an upload request, 
	 * or -1 if it is not limited.
	 */
	public long getMaxFileSize()
	{
		return maxFileSize_;
	}

	
	/**
	 * Returns the size threshold. If an uploaded file size exceeds the threshold, it 
	 * will be written to the temp directory. 
	 */
	public int getFileSizeThreshold()
	{
		return fileSizeThreshold_;
	}

	
	/**
	 * Returns the directory where files will be stored temporarily when their length 
	 * exceeds the size threshold. 
	 */
	public String getTempDirectory()
	{
		return tempDirectory_;
	}


	private boolean enabled_;
	private String tempDirectory_;
	private long maxRequestSize_;
	private long maxFileSize_;
	private int fileSizeThreshold_;
}	
