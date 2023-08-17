package org.civilian.application.factory;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.civilian.ConfigKeys;
import org.civilian.application.AppConfig;
import org.civilian.application.Application;
import org.civilian.application.DefaultApp;
import org.civilian.application.admin.AdminApp;
import org.civilian.util.Check;
import org.civilian.util.ClassUtil;
import org.civilian.util.Settings;


/**
 * AppFactory is used during server start to load all apps defined in civilian.ini.
 */
public class AppFactory 
{
	/**
	 * Returns a list of Data objects which contain the applications and additional data defined in civilian.ini
	 * @param appClassLoader the classloader to load apps
	 * @param settings the settings file
	 * @param develop does the server run in develop mode?
	 * @return the list
	 * @throws Exception thrown if app loading fails
	 */
	public static List<Data> load(ClassLoader appClassLoader, Settings settings, boolean develop) throws Exception
	{
		AppFactory start = new AppFactory(appClassLoader, develop);
		
		start.createCustomApps(settings);
		start.createAdminApp(new Settings(settings, ConfigKeys.ADMINAPP_PREFIX));
		
		return start.dataList_;
	}
	
	
	private AppFactory(ClassLoader classLoader, boolean develop)
	{
		classLoader_ 	= classLoader != null ? classLoader : getClass().getClassLoader();
		develop_ 		= develop;
	}
	
	
	// creates and initializes applications
	private void createCustomApps(Settings settings) throws Exception
	{
		for (String id : findCustomAppIds(settings))
		{
			String appPrefix = ConfigKeys.APP_PREFIX + id + '.';
			Settings appSettings = new Settings(settings, appPrefix);
			if (AppConfig.isEnabled(appSettings, true, develop_))
				createCustomApp(appSettings, id, appPrefix);
		}
	}
	
	/**
	 * Creates the application for a certain id.
	 * Even if creation fails and we cannot instantiate the application object
	 * we push a DefaultApp object, which - when invoked - will present
	 * an error message.
	 */
	protected void createCustomApp(Settings settings, String id, String appPrefix)
		throws Exception
	{
		String path = settings.get(ConfigKeys.PATH, "");
		
		Application app;
		if (settings.contains(ConfigKeys.CLASS))
			app = createApplication(settings.get(ConfigKeys.CLASS));
		else if (settings.contains(ConfigKeys.PACKAGE))
			app = new DefaultApp(settings.get(ConfigKeys.PACKAGE));
		else
		{
			String msg = "application '" + id + "' must either define an application class (key '" + appPrefix + ConfigKeys.CLASS + 
				"') or set the base package for controller classes (key '" + appPrefix + ConfigKeys.PACKAGE + "')";
			throw new IllegalStateException(msg);
		}
		
		dataList_.add(new Data(app, id, settings, path));
	}
	
	
	private List<String> findCustomAppIds(Settings settings)
	{
		// test if the app ids are explicitly listed
		if (settings.contains(ConfigKeys.APPLICATIONS, false))
			return settings.getList(ConfigKeys.APPLICATIONS);
		
		// else collect them
		List<String> ids = new ArrayList<>(); 
		for (Iterator<String> keys = settings.keys(); keys.hasNext(); )
		{
			String key = keys.next();
			// test the mandatory keys
			if (key.startsWith(ConfigKeys.APP_PREFIX) && 
				(key.endsWith(ConfigKeys.CLASS) || key.endsWith(ConfigKeys.PACKAGE)))
			{
				int p = key.indexOf('.', ConfigKeys.APP_PREFIX.length());
				if (p != -1)
				{
					String id = key.substring(ConfigKeys.APP_PREFIX.length(), p);
					if (!ids.contains(id))
						ids.add(id);
				}
			}
		}
		Collections.sort(ids);
		return ids;
	}
	

	private void createAdminApp(Settings appSettings) throws Exception
	{
		if (AppConfig.isEnabled(appSettings, develop_, develop_))
		{
			String path 	= appSettings.get(ConfigKeys.PATH, ConfigKeys.ADMIN_PATH_DEFAULT);
			dataList_.add(new Data(new AdminApp(), "civadmin", appSettings, path));
		}
	}
	
	
	private Application createApplication(String className) throws Exception
	{
		return ClassUtil.createObject(className, Application.class, classLoader_);
	}
	
	
	/**
	 * Holds the data of an application defined in the Civilian config, 
	 * needed to create and add the application.
	 */
	public static class Data
	{
		public Data(Application app, String id, Settings settings, String path)
		{
			this.app			= Check.notNull(app, "app"); 
			this.id				= Check.notNull(id, "id");
			this.relativePath	= path;
			this.settings 		= settings;
		}
		
		
		public final String id;
		public final Application app;
		public final String relativePath;
		public final Settings settings;
	}
	
	
	private final ClassLoader classLoader_;
	private final boolean develop_;
	private final List<Data> dataList_ = new ArrayList<>();
}
