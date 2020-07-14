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


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.civilian.Application;
import org.civilian.Request;
import org.civilian.Resource;
import org.civilian.asset.AssetConfig;
import org.civilian.asset.AssetLocation;
import org.civilian.asset.AssetServices;
import org.civilian.content.ContentSerializer;
import org.civilian.content.ContentType;
import org.civilian.content.JaxbXmlSerializer;
import org.civilian.controller.ControllerFactory;
import org.civilian.controller.classloader.ReloadConfig;
import org.civilian.provider.ResponseProvider;
import org.civilian.resource.ExtensionMapping;
import org.civilian.resource.Url;
import org.civilian.text.LocaleServiceList;
import org.civilian.text.msg.MsgBundleFactories;
import org.civilian.text.msg.MsgBundleFactory;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;
import org.civilian.util.IoUtil;
import org.civilian.util.Settings;


/**
 * AppConfig is used during initialization of an Application.
 * It is passed to the {@link Application#init(AppConfig)} method.
 * Derived application classes can use the AppConfig object to configure
 * the application if not already done with settings in the Civilian config file.
 */
public class AppConfig
{
	/**
	 * Implements an extended enabled test: The value of a config entry representing
	 * an enabled-flag may have the values "true", "false", "develop", "production".
	 * The method evaluates the string and returns the boolean value of enabled flag. 
	 * @param configEntry the value of a config entry
	 * @param defaultValue the default value if configEntry is null (= not specified) 
	 * @param develop the develop flag.
	 */
	public static boolean isEnabled(String configEntry, boolean defaultValue, boolean develop)
	{
		if (configEntry == null)
			return defaultValue;
		
		if ("true".equals(configEntry))
			return true;
		else if ("false".equals(configEntry))
			return false;
		else if ("develop".equals(configEntry))
			return develop;
		else if ("production".equals(configEntry))
			return !develop;
		else
			throw new IllegalArgumentException("invalid enabled-value: '" + configEntry + "'");
	}
	
	
	public static boolean isEnabled(Settings settings, boolean defaultValue, boolean develop)
	{
		return isEnabled(settings.get(ConfigKeys.ENABLED, null), defaultValue, develop);
	}

	
	/**
	 * Creates a new AppConfig object. This is done by the application during initialization
	 * when it calls Application#init(AppConfig). 
	 */
	public AppConfig(Application app, Settings settings)
	{
		app_					= app;
		settings_ 				= settings == null ? new Settings() : settings;
		async_					= settings_.getBoolean(ConfigKeys.ASYNC, false); 
		connect_				= settings_.getBoolean(ConfigKeys.CONNECT, true); 
		encoding_				= settings_.get(ConfigKeys.ENCODING, ConfigKeys.ENCODING_DEFAULT);
		typeLib_ 			= new TypeLib();
		extensionMapping_		= app.getResourceConfig().getExtensionMapping();
		defaultResExtension_	= IoUtil.normExtension(settings_.get(ConfigKeys.EXTENSION, null));

		// these init calls are safe
		initLocales();
		initUploadConfig();
		initReloadConfig();
		initAssetConfig();
	}
	
	
	private void initLocales()
	{
		String[] localeTags = settings_.getList(ConfigKeys.LOCALES);
		if (localeTags.length > 0)
		{
			supportedLocales_ = new Locale[localeTags.length];
			for (int i=0; i<localeTags.length; i++)
			{
				// Locale.forLanguageTag does only accept tags in the form 'de-CH' not 'de_CH'
				String tag = localeTags[i].replace('_', '-');
				supportedLocales_[i] = Locale.forLanguageTag(tag);
			}
		}
		else
			supportedLocales_ = new Locale[] { Locale.getDefault() };
	}


	private void initAssetConfig()
	{
		assetConfig_ = new AssetConfig();
		assetConfig_.setContentTypeLookup(app_.getContext().getContentTypeLookup());
		
	}
	
	private void initUploadConfig()
	{
		uploadConfig_ = new UploadConfig(new Settings(settings_, ConfigKeys.UPLOAD_PREFIX));
	}
	
	
	private void initReloadConfig()
	{
		if (app_.develop() && settings_.getBoolean(ConfigKeys.DEV_CLASSRELOAD, false))
		{
			Settings settings = new Settings(settings_, ConfigKeys.DEV_CLASSRELOAD + '.');
			reloadConfig_ = new ReloadConfig();
			reloadConfig_.includes().add(app_.getControllerConfig().getRootPackage());
			reloadConfig_.excludes().add(settings.getList(ConfigKeys.EXCLUDE));
			reloadConfig_.includes().add(settings.getList(ConfigKeys.INCLUDE));
		}	
	}
	
	
	/**
	 * Initializes all settings of the AppConfig which may throw an Exception.
	 */
	public void init() throws Exception
	{
		initAssetLocations();
		initText();
	}
	

	private void initAssetLocations() throws Exception
	{
		Settings settings = new Settings(settings_, ConfigKeys.ASSET_PREFIX);
		for (AssetLocation loc : AssetServices.getLocations(app_, settings))
			assetConfig_.addLocation(loc);
	}

	
	private void initText() throws Exception
	{
		String tlKey = settings_.get(ConfigKeys.MESSAGES, null);
		if (tlKey != null)
			msgBundleFactory_ = MsgBundleFactories.createFactory(tlKey);
	}
	
	
	/**
	 * Returns the part of the application within the Civilian config file.
	 * The key prefix "app.&lt;app-id&gt;." is removed from the keys. 
	 */
	public Settings getSettings()
	{
		return settings_;
	}
	 

	/**
	 * Returns the current application version.
	 * The version is null by default.
	 * @see Application#getVersion()
	 */
	public String getVersion()
	{
		return version_;
	}


	/**
	 * Sets the application version.
	 * @see Application#getVersion()
	 */
	public void setVersion(String version)
	{
		version_ = version;
	}
	
	
	/**
	 * Returns the default application encoding.
	 * @see Application#getEncoding()
	 */
	public String getEncoding()
	{
		return encoding_;
	}


	/**
	 * Sets the default application encoding.
	 */
	public void setEncoding(String encoding)
	{
		encoding_ = Check.notNull(encoding, "encoding");
	}

	
	//----------------------------
	// locale/text
	//----------------------------

	
	/**
	 * Sets the list of supported locales.
	 */
	public void setSupportedLocales(Locale... supportedLocales)
	{
		supportedLocales_ = Check.notEmpty(supportedLocales, "supportedLocales");
	}
	

	/**
	 * Returns the list of supported locales.
	 */
	public Locale[] getSupportedLocales()
	{
		return supportedLocales_;
	}
	

	/**
	 * Returns false, if the {@link LocaleServiceList} will only provide LocaleService
	 * for supported locales (and fall back to a supported locale if a LocaleService for 
	 * any other Locale is requested), or true if LocaleServices will be returned
	 * for any requested locale. In the later case, unsupported LocaleServices will not be cached
     * (which adds a small performance penalty).
	 */
	public boolean allowUnsupportedLocales()
	{
		return allowUnsupportedLocales_;
	}
	
	
	/**
	 * Sets if the {@link LocaleServiceList} will provide LocaleServices
	 * even for locales which are not included in the list of supported locales. 
	 */
	public void setAllowUnsupportedLocales(boolean allow)
	{
		allowUnsupportedLocales_ = allow;
	}

	
	/**
	 * Returns the MsgBundleFactory used by the application to create
	 * locale dependent MsgBundle objects.
	 */
	public MsgBundleFactory getMsgBundleFactory()
	{
		return msgBundleFactory_;
	}

	
	/**
	 * Sets the MsgBundleFactory of the application.
	 */
	public void setMsgBundleFactory(MsgBundleFactory factory)
	{
		msgBundleFactory_ = factory;
	}
	
	
	//----------------------------
	// types
	//----------------------------

	
	/**
	 * Returns the type library: You can modify the type library, for instance to
	 * add new types if needed.
	 */
	public TypeLib getTypeLib()
	{
		return typeLib_;
	}

	
	/**
	 * Allows to replace the type library.
	 */
	public void setTypeLib(TypeLib library)
	{
		typeLib_ = Check.notNull(library, "library");
	}
	
	
	//----------------------------
	// assets
	//----------------------------

	
	/**
	 * Returns the AssetConfig. Use the AssetConfig to
	 * configure where your application assets are located. 
	 */
	public AssetConfig getAssetConfig()
	{
		return assetConfig_;
	}


	//----------------------------
	// reloading
	//----------------------------


	/**
	 * Returns the ReloadConfig. 
	 * @return the reload config or null if class reloading
	 * 		is not turned on. 
	 */
	public ReloadConfig getReloadConfig()
	{
		return reloadConfig_;
	}
	
	
	/**
	 * Sets the ReloadConfig. 
	 */
	public void setReloadConfig(ReloadConfig config)
	{
		reloadConfig_ = config;
	}

	
	//----------------------------
	// creating controllers
	//----------------------------


	/**
	 * Returns the ControllerFactory. 
	 * @return the factory or null if no controller factory is used. 
	 */
	public ControllerFactory getControllerFactory()
	{
		return controllerFactory_;
	}
	
	
	/**
	 * Sets the ControllerFactory used for creating controller objects. 
	 */
	public void setControllerFactory(ControllerFactory factory)
	{
		controllerFactory_ = factory;
	}

	
	//----------------------------
	// upload
	//----------------------------

	
	/**
	 * Returns the current UploadConfig. 
	 */
	public UploadConfig getUploadConfig()
	{
		return uploadConfig_;
	}
	
	
	/**
	 * Sets the UploadConfig. 
	 */
	public void setUploadConfig(UploadConfig uploadConfig)
	{
		uploadConfig_ = Check.notNull(uploadConfig, "uploadConfig");
	}

	
	//----------------------------
	// resources
	//----------------------------

	
	/**
	 * Returns the root resource. By default the root resource
	 * is null. If you don't explicitly set the root resource
	 * the application will build the resource 
	 * tree after initialization by scanning the classpath for
	 * controller classes.  
	 */
	public Resource getRootResource()
	{
		return rootResource_;
	}
	
	
	/**
	 * Sets the root resource of the application. If the resource
	 * tree is not explicitly set, the application will  build the resource 
	 * tree after initialization by scanning the classpath for
	 * controller classes. If you have generated a class defining
	 * constants for all resource you may use its root resource to
	 * and avoid runtime scanning for controllers.
	 */
	public void setResourceRoot(Resource root)
	{
		Check.notNull(root, "root");
		if (!root.isRoot())
			throw new IllegalArgumentException("not a root: " + root);
		rootResource_ = root;
	}

	
	/**
	 * Returns the default extension used for resource urls
	 * when a url is built using {@link Url#Url(ResponseProvider, Resource)}.
	 * @return the extension starting with a dot, or null if
	 * 		no extension should be appended to the url.
	 */
	public String getDefaultResExtension()
	{
		return defaultResExtension_;
	}

	
	/**
	 * Sets the default resource extension.
	 */
	public void setDefaultResExtension(String extension)
	{
		defaultResExtension_ = IoUtil.normExtension(extension);
	}
	
	
	/**
	 * Returns the ExtensionMapping object to configure extenson mappings.
	 * If a client is a not able to send Accept headers, extension mappings
	 * can be used to derive accept preferences from the URL extension.
	 */
	public ExtensionMapping getExtensionMapping()
	{
		return extensionMapping_;
	}
	

	//----------------------------
	// connect
	//----------------------------

	
	/**
	 * Returns the connect-value.
	 * @see #setConnect(boolean)
	 */
	public boolean getConnect()
	{
		return connect_;
	}
	

	/**
	 * Sets if the application should be connected to receive requests? In case of 
	 * of a servlet environment this means that civilian dynamically registers  
	 * a servlet to route application requests to the application. Default is true.
	 */
	public void setConnect(boolean connect)
	{
		connect_ = connect;
	}

	
	/**
	 * Returns the async-value.
	 * @see #setAsync(boolean)
	 */
	public boolean getAsync()
	{
		return async_;
	}
	

	/**
	 * Sets if the application should be support asynchronous requests processing?
	 * @see Request#startAsync()
	 */
	public void setAsync(boolean async)
	{
		async_ = async;
	}

	
	//----------------------------
	// content
	//----------------------------
	

	/**
	 * Returns the ContentSerializers supported by the application.
	 * You may add own implementations to support conversion of all
	 * content types needed by your application.<br>
	 * If you want to implement serialization of XML using JAXB, simply define 
	 * a suitable JAXBContext, create a {@link JaxbXmlSerializer} and add it to the map.<br>
	 */
	public Map<String,ContentSerializer> getContentSerializers()
	{
		return contentSerializers_;
	}

	
	/**
	 * Registers a ContentSerializer for a ContentType.
	 */
	public void registerContentSerializer(ContentType contentType, ContentSerializer serializer)
	{
		Check.notNull(contentType, "contentType");
		registerContentSerializer(contentType.getValue(), serializer);
	}

	
	/**
	 * Registers a ContentSerializer for a ContentType.
	 */
	public void registerContentSerializer(String contentType, ContentSerializer serializer)
	{
		Check.notNull(contentType, "contentType");
		Check.notNull(serializer, "serializer");
		contentSerializers_.put(contentType, serializer);
	}

	
	/**
	 * Returns the ContentSerializer registered for a ContentType.
	 */
	public ContentSerializer getContentSerializer(ContentType contentType)
	{
		return contentSerializers_.get(contentType.getValue());
	}

	
	private Application app_;
	private String encoding_;
	private String version_;
	private Settings settings_;
	private Locale[] supportedLocales_;
	private boolean allowUnsupportedLocales_;
	private MsgBundleFactory msgBundleFactory_;
	private TypeLib typeLib_;
	private AssetConfig assetConfig_;
	private UploadConfig uploadConfig_;
	private Resource rootResource_;
	private String defaultResExtension_;
	private ExtensionMapping extensionMapping_;
	private ReloadConfig reloadConfig_;
	private boolean connect_;
	private boolean async_;
	private ControllerFactory controllerFactory_;
	private Map<String,ContentSerializer> contentSerializers_ = new HashMap<>();
}
