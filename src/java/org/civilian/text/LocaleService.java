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


import java.util.Locale;
import org.civilian.Application;
import org.civilian.Request;
import org.civilian.Response;
import org.civilian.provider.LocaleServiceProvider;
import org.civilian.text.msg.MsgBundle;
import org.civilian.text.msg.MsgBundleProvider;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.LocaleSerializer;
import org.civilian.util.Check;


/**
 * LocaleService provides localization support for a certain locale.<p>
 * It contains a MsgBundle for that locale and a serializer 
 * which can be used to format/parse objects like numbers and dates into/from 
 * locale dependent string representations.<p>
 * LocaleService objects are created and can be obtained from the {@link LocaleServiceList}.<p>
 * {@link Request} and {@link Response} are both associated with a locale
 * and therefore provide LocaleService objects, initialized
 * from the request preferences.
 * @see Application#getLocaleServices()
 * @see Request#getLocaleService()
 * @see Response#getLocaleService()
 */
public class LocaleService implements LocaleServiceProvider, MsgBundleProvider
{
	/**
	 * A LocaleService instance for the system locale, using the default type lib,
	 * and empty msg bundle and the system locale serializer.
	 */
	public static final LocaleService SYSTEM_LOCALE_LOCALESERVICE = new LocaleService(
		LocaleSerializer.SYSTEM_LOCALE_SERIALIZER.getLocale(),
		null,
		null,
		LocaleSerializer.SYSTEM_LOCALE_SERIALIZER);
		
		
	/**
	 * Creates a new LocaleService object with an empty MsgBundle.
	 * @param locale a locale 
	 */
	public LocaleService(Locale locale)
	{
		this(locale, null, null, null);
	}
	

	/**
	 * Creates a new LocaleService object.
	 * @param locale a locale 
	 * @param msgBundle a MsgBundle. Will be converted into an empty bundle if null
	 * @param serializer a LocaleSerializer suitable for the locale. If null a new serializer will be created
	 */
	public LocaleService(Locale locale, TypeLib typeLib, MsgBundle msgBundle, LocaleSerializer serializer)
	{
		locale_ 		= Check.notNull(locale, "locale");
		typeLib_		= typeLib != null ? typeLib : TypeLib.getDefaultTypeLib();
		msgBundle_		= msgBundle != null ? msgBundle : MsgBundle.empty(locale);
		serializer_		= serializer != null ? serializer : new LocaleSerializer(locale);
		localeString_	= locale.toString();
	}
	
	
	/**
	 * Implements LocaleServiceProvider and returns this.
	 */
	@Override public LocaleService getLocaleService()
    {
	    return this;
    }

	
	/**
	 * Returns the locale.
	 */
	public Locale getLocale()
	{
		return locale_;
	}
	
	
	/**
	 * Returns the the type library.
	 */
	public TypeLib getTypeLib()
	{
		return typeLib_;
	}
	

	/**
	 * Returns the LocaleSerializer.
	 */
	public LocaleSerializer getSerializer()
	{
		return serializer_;
	}

	
	/**
	 * Returns the MsgBundle.
	 */
	@Override public MsgBundle getMsgBundle()
	{
		return msgBundle_;
	}

	
	/**
	 * Set the MsgBundle used by the service. 
	 */
	public void setMsgBundle(MsgBundle msgBundle)
	{
		msgBundle_ = Check.notNull(msgBundle, "msgBundle");
	}
	
	
	/**
	 * Returns the data previously set by setData().
	 * @see #setData
	 */
	public Object getData()
	{
		return data_;
	}
	
	
	/**
	 * Associates arbitrary data with the LocaleService object.
	 */
	public void setData(Object data)
	{
		data_ = data;
	}

	
	/**
	 * Returns true iif the other object is a LocaleService for
	 * the same locale.
	 */
	@Override public boolean equals(Object other)
	{
		return (other instanceof LocaleService) && 
			localeString_.equals(((LocaleService)other).localeString_); 
	}

	
	/**
	 * Returns a hash code.
	 */
	@Override public int hashCode()
	{
		return localeString_.hashCode();
	}
	
	
	/**
	 * Returns the string representation of the Locale.
	 */
	@Override public String toString()
	{
		return localeString_;
	}

	
	private MsgBundle msgBundle_;
	private final TypeLib typeLib_;
	private final Locale locale_;
	private final LocaleSerializer serializer_;
	private final String localeString_;
	private Object data_;
}
