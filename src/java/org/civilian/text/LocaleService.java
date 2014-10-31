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
package org.civilian.text;


import java.util.HashMap;
import java.util.Locale;
import org.civilian.text.keys.KeyList;
import org.civilian.text.keys.KeyListBuilder;
import org.civilian.text.keys.serialize.KeySerializers;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleFactory;
import org.civilian.type.lib.LocaleSerializer;
import org.civilian.util.Check;


/**
 * LocaleService provides LocaleData objects for Locales.<p>
 * The LocaleService possesses a non empty list of "supported" locales, defined by the 
 * application setup. (A non localized application will just use a single
 * supported locale). The first of these supported locales is termed the default locale.
 * The application will usually provide MsgBundles for supported locales.<p>
 * Each Request and Response is associated with a locale and therefore possesses a LocaleData,
 * which is obtained from the LocaleService.<p>
 * Since the locale requested by a client must not be supported,
 * the locale service needs a policy how to handle this situation:
 * <ul>
 * <li>restricted: the locale service tries to find a similar supported locale or falls back
 * 	   to the default locale. (default policy)
 * <li>unrestricted: the locale service constructs LocaleData for unsupported locales
 * 	   which will not be cached and therefore have a small performance penalty.
 * </ul>	       
 */
public class LocaleService
{
	/**
	 * Creates a new LocaleService
	 * @param msgBundleFactory a factory for MsgBundles. May be null
	 * @param allowUnsupportedLocales true if the service also constructs LocaleData for unsupported
	 * 		locales or false, if it falls back to a supported locale.
	 * @param supportedLocales the list of supported locales. Must at least contain one entry.
	 */
	public LocaleService(MsgBundleFactory msgBundleFactory, boolean allowUnsupportedLocales, Locale... supportedLocales)
	{
		msgBundleFactory_	= msgBundleFactory;
		supportedLocales_	= Check.notEmpty(supportedLocales, "supportedLocales");
		defaultLocale_ 		= supportedLocales_[0];
		supportedData_		= new LocaleData[supportedLocales_.length];	

		KeyListBuilder<LocaleData> klBuilder = new KeyListBuilder<>(); 
		klBuilder.setSerializer(KeySerializers.TO_STRING);
		for (int i=0; i<supportedLocales_.length; i++)
		{
			Locale locale = supportedLocales_[i];
			LocaleData data = supportedData_[i] = createData(locale, true);
			if (i == 0)
				defaultData_ = data;
			klBuilder.add(data, locale.getDisplayName(locale));
		}
		localeDataKeys_ = klBuilder.end();
		
		if (allowUnsupportedLocales)
			localeMap_ = new AllowUnsupportedLocaleMap();
		else if (supportedLocales_.length == 1)
			localeMap_ = new FixedLocaleMap();
		else
			localeMap_ = new FallbackLocaleMap();
	}
	
	
	/**
	 * Returns the number of supported locales. 
	 */
	public int getLocaleCount()
	{
		return supportedLocales_.length;
	}

	
	/**
	 * Returns the i-th supported locale. 
	 */
	public Locale getLocale(int i)
	{
		return supportedLocales_[i];
	}

	
	/**
	 * Returns the first supported locale. 
	 */
	public Locale getDefaultLocale()
	{
		return defaultLocale_;
	}
	

	/**
	 * Tests if the given locale is supported. 
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
	 */
	public Locale normLocale(Locale locale)
	{
		if ((locale != null) && (getLocaleCount() > 1))
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
		return defaultLocale_;
	}
	
	
	/**
	 * Returns the LocaleData of the default locale.
	 */
	public LocaleData getDefaultLocaleData()
	{
		return defaultData_;
	}

	
	/**
	 * Returns the LocaleData for a locale. 
	 * If the locale is not supported, it depends on the policy of
	 * the locale service what LocaleData is returned:
	 * If unsupported locales are allowed, a LocaleData for the locale
	 * is constructed and returned. Such a LocaleData is not cached and
	 * therefore has a small performance penalty.
	 * If unsupported locales are not allowed, a LocaleData for fallback locale
	 * is returned.
	 */
	public LocaleData getLocaleData(Locale locale)
	{
		Check.notNull(locale, "locale");
		return localeMap_.getData(locale);
	}

	
	public LocaleData getLocaleData(String locale)
	{
		Check.notNull(locale, "locale");
		for (LocaleData localeData : supportedData_)
		{
			if (localeData.toString().equals(locale))
				return localeData;
		}
		
		return getDefaultLocaleData();
	}

	
	/**
	 * Returns the LocaleData for the i-th locale.
	 */ 
	public LocaleData getLocaleData(int i)
	{
		return supportedData_[i];
	}

	
	/**
	 * Returns a KeyList for the defined LocaleData objects.
	 */ 
	public KeyList<LocaleData> getLocaleDataKeys()
	{
		return localeDataKeys_;
	}

	
	private LocaleData createData(Locale locale, boolean cached)
	{
		MsgBundle msgBundle = null;
		if (msgBundleFactory_ != null)
			msgBundle = msgBundleFactory_.getMsgBundle(locale);
		if (msgBundle == null)
			msgBundle = MsgBundle.empty(locale);
		
		LocaleSerializer serializer = new LocaleSerializer(locale, cached);
		return new LocaleData(locale, msgBundle, serializer);
	}
	
	/**
	 * A LocaleMap maps locales to LocaleData.
	 */
	private abstract class LocaleMap
	{
		public abstract LocaleData getData(Locale locale);
	}
	
	/**
	 * A LocaleMap with a single entry.
	 */
	private class FixedLocaleMap extends LocaleMap
	{
		@Override public LocaleData getData(Locale locale)
		{
			return defaultData_;
		}
	}

	/**
	 * A base class for LocaleMaps with multiple entries and a default entry.
	 */
	private abstract class MultiLocaleMap extends LocaleMap
	{
		public MultiLocaleMap()
		{
			for (Locale locale : supportedLocales_)
			{
				LocaleData data = createData(locale, true);
				map_.put(locale, data);
			}
			defaultData_ = map_.get(supportedLocales_[0]);
		}

		HashMap<Locale,LocaleData> map_ = new HashMap<>();
	}
	
	/**
	 * A LocaleMaps with multiple entries which returns a LocaleData
	 * from its supported locale-list for unknown locales
	 */
	private class FallbackLocaleMap extends MultiLocaleMap
	{
		@Override public LocaleData getData(Locale locale)
		{
			LocaleData data = map_.get(locale);
			if (data == null)
			{
				data = map_.get(normLocale(locale));
				if (data == null)
					data = defaultData_; // should not happen
			}
			return data;
		}
	}

	/**
	 * A LocaleMaps with multiple entries which creates uncached
	 * LocaleDatas when it encounters an unsupported locale.
	 */
	private class AllowUnsupportedLocaleMap extends MultiLocaleMap
	{
		@Override public LocaleData getData(Locale locale)
		{
			LocaleData data = map_.get(locale);
			if (data == null)
				data = createData(locale, false /*no cache*/);
			return data;
		}
	}


	private Locale defaultLocale_;
	private Locale[] supportedLocales_;
	private LocaleData defaultData_;
	private LocaleData[] supportedData_;
	private MsgBundleFactory msgBundleFactory_;
	private LocaleMap localeMap_;
	private KeyList<LocaleData> localeDataKeys_;
}
