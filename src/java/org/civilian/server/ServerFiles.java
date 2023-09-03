package org.civilian.server;


import java.io.File;
import java.io.IOException;
import org.civilian.resource.Path;
import org.civilian.util.Check;
import org.civilian.util.FileType;
import org.civilian.util.Settings;


/**
 * ServerFiles gives access to real files associated with a Server. 
 */
public abstract class ServerFiles 
{
	/**
	 * Returns the real path corresponding to the given relative request path.
	 * @param path a path relative to the Server path.
	 * @return the real path or null if it does not map to a real path. 
	 */
	public abstract String getRealPath(String path);
	
	
	/**
	 * Returns the real path corresponding to the given request path.
	 * @param path a path relative to the server path
	 * @return the real path or null if it does not map to a real path. 
	 */
	public String getRealPath(Path path)
	{
		return getRealPath(path.toString()); 
	}


	/**
	 * Returns the file corresponding to the given virtual path.
	 * @param path the path
	 * @param fileType the expected fileType or null  
	 * @return the file
	 * @throws IllegalArgumentException thrown if the path cannot be mapped to a real path
	 */
	public File getRealFile(String path, FileType fileType) throws IllegalArgumentException
	{
		String realPath = getRealPath(path);
		File file = realPath != null ? new File(realPath) : null;
		if (fileType != null)
			fileType.check(file, "getRealFile " + path);
		return file;
	}


	/**
	 * @return the file corresponding to the servers's root directory in the local file system.  
	 */
	public File getRootDir()
	{
		return getRealFile("", FileType.EXISTENT_DIR);
	}
	


	/**
	 * Returns the real path of a resource located in a server specific config
	 * directory. For a servlet environment, the config directory is the WEB-INF directory
	 * of the web application. For that a call getConfigPath("myconfig.ini"), would return
	 * a file path whose name ends with "/WEB-INF/myconfig.ini".
	 * @param name a name
	 * @return the real path   
	 */
	public abstract String getConfigPath(String name);

	
	/**
	 * Returns a File for a config path.
	 * @param name a name below the {@link #getConfigPath(String) config path}.
	 * @param fileType the expected fileType or null
	 * @return the file  
	 */
	public File getConfigFile(String name, FileType fileType)
	{
		return getRealFile(getConfigPath(name), fileType);
	}


	/**
	 * Reads a config or properties file into a settings object.
	 * @param configName the name of the config file.
	 * @return the settings object 
	 * @throws IOException if the file does not exist or cannot be read.
	 */
	public Settings readConfigSettings(String configName) throws IOException
	{
		Check.notNull(configName, "configName");
		File configFile = getConfigFile(configName, FileType.EXISTENT_FILE);
		
		Settings settings = new Settings();
		settings.read(configFile);
		return settings;
	}
}
