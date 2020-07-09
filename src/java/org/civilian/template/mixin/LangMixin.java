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
package org.civilian.template.mixin;


import org.civilian.provider.LocaleServiceProvider;
import org.civilian.provider.MsgBundleProvider;
import org.civilian.template.TemplateWriter;
import org.civilian.text.LocaleService;
import org.civilian.text.NumberStyle;
import org.civilian.text.msg.MsgBundle;
import org.civilian.type.DateType;
import org.civilian.type.TypeLib;
import org.civilian.type.fn.TypeSerializer;
import org.civilian.type.fn.LocaleSerializer;
import org.civilian.util.Check;


/**
 * LangMixin is a template mixin which defines utility methods for localization.
 * It contains a TypeSerializer to format values and a MsgBundle to translate message ids.
 * <p>
 * The LangMixin tries to initialize the TypeSerializer and MsgBundle from a 
 * {@link LocaleServiceProvider} in the TemplateWriter {@link TemplateWriter#getAttribute(Class) context}.
 * If the TemplateWriter was created by a Response, the Response acts as the LocaleServiceProvider,
 * and therefore the mixin uses the TypeSerializer and MsgBundle of the response.  
 * Else the mixin defaults to {@link LocaleSerializer#SYSTEM_LOCALE_SERIALIZER} and an empty message bundle.
 * <p>
 * Independent of the initialization you may explicitly set TypeSerializer and MsgBundle. 
 */
public class LangMixin implements MsgBundleProvider, LocaleServiceProvider
{
	/**
	 * Creates a new LangMixin object.
	 */
	public LangMixin(TemplateWriter out)
	{
		Check.notNull(out, "out");
		
		LocaleServiceProvider lsp = out.getAttribute(LocaleServiceProvider.class);
		init(lsp != null ? lsp.getLocaleService() : LocaleService.SYSTEM_LOCALE_LOCALESERVICE); 
	}
	
	
	/**
	 * Initializes the mixin to use the LocaleSerivce.
	 */
	public void init(LocaleService service)
	{
		ls_ = Check.notNull(service, "service");
	}

	
	//-----------------------------
	// accessors
	//-----------------------------

	
	/**
	 * Returns the LocaleService used by the mixin.
	 */
	@Override public LocaleService getLocaleService()
	{
		return ls_;
	}

	
	/**
	 * Returns the TypeSerializer used by the mixin.
	 */
	public TypeSerializer getSerializer()
	{
		return ls_.getSerializer();
	}
	
	
	/**
	 * Returns the MsgBundle used by the mixin.
	 */
	@Override public MsgBundle getMsgBundle()
	{
		return ls_.getMsgBundle();
	}
		
	
	//------------------------
	// format methods
	//------------------------

	
	/**
	 * Formats a java.util.Date. If the date is null, then "" is returned.
	 * @return a locale dependent date string.
	 */
	public String format(java.util.Date date)
	{
		return format(date, "");
	}
	
	
	/**
	 * Formats a java.util.Date.
	 * @param defaultValue the defaultValue is returned if the date is null. 
	 * @return a locale dependent date string.
	 */
	public String format(java.util.Date date, String defaultValue)
	{
		return format(TypeLib.DATE_JAVA_UTIL, date, defaultValue);
	}
	
	
	/**
	 * Formats a java.util.Calendar. If the calendar is null, then "" is returned.
	 * @return a locale dependent date string.
	 */
	public String format(java.util.Calendar calendar)
	{
		return format(calendar, "");
	}
	
	
	/**
	 * Formats a java.util.Calendar.
	 * @param defaultValue the defaultValue is returned if the date is null. 
	 * @return a locale dependent date string.
	 */
	public String format(java.util.Calendar calendar, String defaultValue)
	{
		return format(TypeLib.DATE_CALENDAR, calendar, defaultValue);
	}
	
	
	/**
	 * Formats a org.civilian.util.Date.
	 * If the date is null, then "" is returned.
	 * @return a locale dependent date string.
	 */
	public String format(org.civilian.util.Date date)
	{
		return format(date, "");
	}
	
	
	/**
	 * Formats a org.civilian.util.Date.
	 * @param defaultValue the defaultValue is returned if the date is null. 
	 * @return a locale dependent date string.
	 */
	public String format(org.civilian.util.Date date, String defaultValue)
	{
		return format(TypeLib.DATE_CIVILIAN, date, defaultValue);
	}

	
	/**
	 * Formats a Date object.
	 * @param type the DateType which describes the date class
	 * @param date the date value
	 * @param defaultValue the defaultValue is returned if the date value is null. 
	 * @return a locale dependent date string.
	 */
	public <T> String format(DateType<T> type, T date, String defaultValue)
	{
		return date != null ? getSerializer().format(type, date) : defaultValue;
	}

	
	/**
	 * Formats a int value.
	 * @param n a int value
	 * @return a locale dependent string representation of the integer.
	 */
	public String format(int n)
	{
		return format(n, null);
	}
	

	/**
	 * Formats a int value.
	 * @param n a int value
	 * @param style an optional style object. Use a {@link NumberStyle} variant
	 * 		if you want to tweak locale dependent formatting
	 * @return a locale dependent string representation of the integer.
	 */
	public String format(int n, Object style)
	{
		return getSerializer().format(TypeLib.INTEGER, Integer.valueOf(n), style);
	}
	

	/**
	 * Formats a Integer value.
	 * @param value a Integer value
	 * @param defaultValue the defaultValue is returned if the Integer is null. 
	 * @return a locale dependent string representation of the integer.
	 */
	public String format(Integer value, String defaultValue)
	{
		return value != null ? format(value.intValue()) : defaultValue;
	}

	
	/**
	 * Formats a long value.
	 * @param value a long value
	 * @return a locale dependent string representation of the long value.
	 */
	public String format(long value)
	{
		return format(value, null);
	}

	
	/**
	 * Formats a long value.
	 * @param value a long value
	 * @param style an optional style object. Use a {@link NumberStyle} variant
	 * 		if you want to tweak locale dependent formatting
	 * @return a locale dependent string representation of the long value.
	 */
	public String format(long value, Object style)
	{
		return getSerializer().format(TypeLib.LONG, Long.valueOf(value), style);
	}
	

	/**
	 * Formats a Long.
	 * @param value a Long
	 * @param defaultValue the defaultValue is returned if the Long is null. 
	 * @return a locale dependent string representation of the long value.
	 */
	public String format(Long value, String defaultValue)
	{
		return value != null ? format(value.longValue()) : defaultValue;
	}
	
	
	/**
	 * Formats a double value.
	 * @param value a double value
	 * @return a locale dependent string representation of the double value.
	 */
	public String format(double value)
	{
		return format(value, null);
	}

	
	/**
	 * Formats a double value.
	 * @param value a double value
	 * @param style an optional style object. Use a {@link NumberStyle} variant
	 * 		if you want to tweak locale dependent formatting
	 * @return a locale dependent string representation of the double value.
	 */
	public String format(double value, Object style)
	{
		return getSerializer().format(TypeLib.DOUBLE, Double.valueOf(value), style);
	}

	
	/**
	 * Formats a Double.
	 * @param value a Double
	 * @param defaultValue the defaultValue is returned if the Double is null. 
	 * @return a locale dependent string representation of the double value.
	 */
	public String format(Double value, String defaultValue)
	{
		return value != null ? format(value.doubleValue()) : defaultValue;
	}
	

	private LocaleService ls_;
}
