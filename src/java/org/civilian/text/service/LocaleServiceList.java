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
package org.civilian.text.service;


import java.util.HashMap;
import java.util.Locale;
import org.civilian.text.keys.KeyList;
import org.civilian.text.keys.KeyListBuilder;
import org.civilian.text.keys.KeySerializers;
import org.civilian.text.msg.EmptyMsgBundleFactory;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleFactory;
import org.civilian.text.type.LocaleSerializer;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;


/**
 * LocaleServiceList provides LocaleService objects for Locales.
 * The list possesses a non empty list of LocaleServices for "supported" locales, defined by the 
 * application setup. (A non localized application will just use a single
 * supported locale). The first of these supported locales is defined as the default locale.
 */
public class LocaleServiceList
{
	/**
	 * Creates a new LocaleServiceList.
	 * @param typeLib a type library
	 * @param msgBundleFactory a factory to create localized MsgBundles or null, if the service
	 * 		should only provide empty message bundles 
	 * @param allowUnsupportedLocales true if the service also constructs LocaleServices for unsupported
	 * 		locales or false, if it falls back to a supported locale.
	 * @param supportedLocales the list of supported locales. Must at least contain one entry.
	 */
	public LocaleServiceList(TypeLib typeLib, MsgBundleFactory msgBundleFactory, boolean allowUnsupportedLocales, Locale... supportedLocales)
	{
		typeLib_					= typeLib != null ? typeLib : TypeLib.getDefaultTypeLib();
		allowUnsupportedLocales_	= allowUnsupportedLocales;
		msgBundleFactory_			= msgBundleFactory != null ? msgBundleFactory : new EmptyMsgBundleFactory();
		supportedLocales_			= normLocales(supportedLocales);
		supportedServices_			= new LocaleService[supportedLocales_.length];	

		KeyListBuilder<LocaleService> klBuilder = new KeyListBuilder<>(); 
		klBuilder.setSerializer(KeySerializers.TO_STRING);
		for (int i=0; i<supportedLocales_.length; i++)
		{
			Locale locale 			= supportedLocales_[i];
			LocaleService service	= supportedServices_[i] = createService(locale);
			klBuilder.add(service, locale.getDisplayName(locale));
		}
		defaultService_	= supportedServices_[0];
		serviceKeys_ 	= klBuilder.end();
		
		if (allowUnsupportedLocales)
			localeMap_ = new AllowUnsupportedLocaleMap();
		else if (supportedLocales_.length == 1)
			localeMap_ = new FixedLocaleMap();
		else
			localeMap_ = new FallbackLocaleMap();
	}
	
	
	private static Locale[] normLocales(Locale[] locales)
	{
		if ((locales != null && locales.length > 0))
		{
			for (Locale locale : locales)
				Check.notNull(locale, "locale");
			return locales;
		}
		else
			return new Locale[] { Locale.ENGLISH };
	}
	
	
	/**
	 * @return the the type library of this LocaleServiceList.
	 */
	public TypeLib getTypeLib()
	{
		return typeLib_;
	}
	

	/**
	 * @return the MsgBundleFactory of this LocaleServiceList.
	 */
	public MsgBundleFactory getMsgBundleFactory()
	{
		return msgBundleFactory_;
	}
	
	
	/**
	 * @return the number of supported locales. 
	 */
	public int size()
	{
		return supportedLocales_.length;
	}

	
	/**
	 * @return the i-th supported locale.
	 * @param i the locale index 
	 */
	public Locale getLocale(int i)
	{
		return supportedLocales_[i];
	}

	
	/**
	 * @return the first supported locale. 
	 */
	public Locale getDefaultLocale()
	{
		return getDefaultService().getLocale();
	}
	

	/**
	 * @return if LocaleServices for unsupported locales
	 * are returned, or if the service falls back to a supported locale. 
	 */
	public boolean allowUnsupportedLocales()
	{
		return allowUnsupportedLocales_;
	}

	
	/**
	 * Tests if the given locale is supported.
	 * @param locale a locale 
	 * @return supported
	 */
	public boolean isSupported(Locale locale)
	{
		return toSupported(locale) != null;
	}
	
	
	private Locale toSupported(Locale locale)
	{
		for (Locale supported : supportedLocales_)
		{
			if (supported.equals(locale))
				return supported;
		}
		return null;
	}

	
	/**
	 * Returns a supported locale. If the locale is included
	 * in the supported locales it is returned.
	 * If its language matches the language of a supported locale
	 * that locale is returned. Else the default locale is returned.
	 * @param locale a locale
	 * @return the normed locale 
	 */
	public Locale normLocale(Locale locale)
	{
		if ((locale != null) && (size() > 1))
		{
			Locale supported = toSupported(locale);
			if (supported != null)
				return supported;
			
			// fall back to first locale with same language
			String language = locale.getLanguage();
			for (Locale sl : supportedLocales_)
			{
				if (sl.getLanguage().equals(language))
					return sl;
			}
		}
		
		// fall back to default locale
		return getDefaultLocale();
	}
	
	
	/**
	 * @return the LocaleService of the default locale.
	 */
	public LocaleService getDefaultService()
	{
		return defaultService_;
	}

	
	/**
	 * Returns the LocaleService for a locale. 
	 * If the locale is not supported, it depends on the policy of
	 * the locale service what LocaleService is returned:
	 * If unsupported locales are allowed, a LocaleService for the locale
	 * is constructed and returned. Such a LocaleService is not cached and
	 * therefore has a small performance penalty.
	 * If unsupported locales are not allowed, a LocaleService for fallback locale
	 * is returned.
	 * @param locale a locale
	 * @return the service
	 */
	public LocaleService getService(Locale locale)
	{
		LocaleService ls = null;
		if (locale != null)
			ls = localeMap_.getService(locale);
		return ls != null ? ls : getDefaultService();
	}

	
	public LocaleService getService(String locale)
	{
		if (locale != null)
		{
			for (LocaleService service : supportedServices_)
			{
				if (service.toString().equals(locale))
					return service;
			}
		}
		return getDefaultService();
	}

	
	/**
	 * @return the LocaleService for the i-th locale.
	 * @param i the service index
	 */ 
	public LocaleService getService(int i)
	{
		return supportedServices_[i];
	}

	
	/**
	 * @return a KeyList for the defined LocaleService objects.
	 */ 
	public KeyList<LocaleService> getServiceKeys()
	{
		return serviceKeys_;
	}

	
	private LocaleService createService(Locale locale)
	{
		MsgBundle msgBundle = msgBundleFactory_.getMsgBundle(locale);
		LocaleSerializer first = supportedServices_[0] != null ? supportedServices_[0].getSerializer() : null; 
		LocaleSerializer serializer = new LocaleSerializer(locale, first);
		return new LocaleService(locale, typeLib_, msgBundle, serializer);
	}
	
	
	/**
	 * Clears the cache of the MsgBundleFactory and reloads the MsgBundles of all services.
	 */
	public void reloadServiceMsgBundles()
	{
		msgBundleFactory_.clearCache();
		for (LocaleService service : supportedServices_)
			service.setMsgBundle(msgBundleFactory_.getMsgBundle(service.getLocale()));
	}
	
	
	/**
	 * A LocaleMap maps locales to LocaleServices.
	 */
	private abstract class LocaleMap
	{
		public abstract LocaleService getService(Locale locale);
	}
	
	
	/**
	 * A LocaleMap with a single entry.
	 */
	private class FixedLocaleMap extends LocaleMap
	{
		@Override public LocaleService getService(Locale locale)
		{
			return defaultService_;
		}
	}
	

	/**
	 * A base class for LocaleMaps with multiple entries and a default entry.
	 */
	private abstract class MultiLocaleMap extends LocaleMap
	{
		public MultiLocaleMap()
		{
			for (LocaleService service : supportedServices_)
				map_.put(service.getLocale(), service);
		}
		

		protected LocaleService tryGet(Locale locale)
		{
			return map_.get(locale);
		}
		

		private HashMap<Locale,LocaleService> map_ = new HashMap<>();
	}
	
	
	/**
	 * A LocaleMap with multiple entries which returns a LocaleService
	 * from its supported locale-list for unknown locales
	 */
	private class FallbackLocaleMap extends MultiLocaleMap
	{
		@Override public LocaleService getService(Locale locale)
		{
			LocaleService service = tryGet(locale);
			if (service == null)
			{
				service = tryGet(normLocale(locale));
				if (service == null)
					service = defaultService_;
			}
			return service;
		}
	}

	/**
	 * A LocaleMap with multiple entries which creates uncached
	 * LocaleServices when it encounters an unsupported locale.
	 */
	private class AllowUnsupportedLocaleMap extends MultiLocaleMap
	{
		@Override public LocaleService getService(Locale locale)
		{
			LocaleService service = tryGet(locale);
			if (service == null)
				service = createService(locale);
			return service;
		}
	}


	private final boolean allowUnsupportedLocales_;
	private final TypeLib typeLib_;
	private final Locale[] supportedLocales_;
	private final LocaleService defaultService_;
	private final LocaleService[] supportedServices_;
	private final MsgBundleFactory msgBundleFactory_;
	private final LocaleMap localeMap_;
	private final KeyList<LocaleService> serviceKeys_;
}
