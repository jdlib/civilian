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


import org.civilian.provider.MessageProvider;
import org.civilian.response.ResponseWriter;
import org.civilian.text.NumberStyle;
import org.civilian.text.msg.MsgBundle;
import org.civilian.type.DateType;
import org.civilian.type.TypeLib;
import org.civilian.util.Check;


/**
 * LangMixin is a template mixin which defines utility methods for localization.
 * It uses the {@link ResponseWriter#getTypeSerializer() TypeSerializer}
 * and {@link ResponseWriter#getMsgBundle() MsgBundle} of the ResponseWriter to generate 
 * locale dependent output.
 */
public class LangMixin implements MessageProvider
{
	/**
	 * Creates a new L10n object.
	 */
	public LangMixin(ResponseWriter out)
	{
		this.out = Check.notNull(out, "out");
	}

	
	/**
	 * Returns the message text for the key using the ResponseWriters MsgBundle.
	 * @see MsgBundle#msg(CharSequence)
	 * @see ResponseWriter#getMsgBundle()
	 */
	@Override public String msg(Object key)
	{
		return out.getMsgBundle().msg(key);
	}
	
	
	/**
	 * Returns the message text for the key using the ResponseWriters MsgBundle
	 * and replaces the placeholders in the message with the given
	 * parameters.
	 * @see MsgBundle#msg(CharSequence, Object...)
	 * @see ResponseWriter#getMsgBundle()
	 */
	@Override public String msg(Object key, Object... params)
	{
		return out.getMsgBundle().msg(key, params);
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
	 * Formats a java.util.Date. If the date is null, then "" is returned.
	 * @return a locale dependent date string.
	 */
	public String format(java.util.Calendar date)
	{
		return format(date, "");
	}
	
	
	/**
	 * Formats a java.util.Date.
	 * @param defaultValue the defaultValue is returned if the date is null. 
	 * @return a locale dependent date string.
	 */
	public String format(java.util.Calendar date, String defaultValue)
	{
		return format(TypeLib.DATE_CALENDAR, date, defaultValue);
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
	 * Formats a org.civilian.util.Date.
	 * @param type the DateType which describes the date class
	 * @param date the date value
	 * @param defaultValue the defaultValue is returned if the date value is null. 
	 * @return a locale dependent date string.
	 */
	public <T> String format(DateType<T> type, T date, String defaultValue)
	{
		return date != null ? type.format(out.getTypeSerializer(), date) : defaultValue;
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
		return out.getTypeSerializer().formatInt(n, style);
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
		return out.getTypeSerializer().formatLong(value, style);
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
		return out.getTypeSerializer().formatDouble(value, style);
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
	

	private ResponseWriter out;
}
