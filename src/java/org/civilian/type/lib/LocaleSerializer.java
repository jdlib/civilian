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
package org.civilian.type.lib;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Locale;
import org.civilian.text.DateFormat;
import org.civilian.text.NumberStyle;
import org.civilian.type.DateType;
import org.civilian.text.NumberFormat;
import org.civilian.util.Check;


/**
 * LocaleSerializer is a TypeSerializer which produces locale dependent
 * string representations when formatting a value.
 * If you a {@link NumberStyle} object to one of the number formatting
 * methods, it will be used to format the number. If the style parameter
 * is null for these methods, LocaleSerializer will fall back to
 * {@link NumberStyle#DEFAULT}.
 */
public class LocaleSerializer extends AbstractSerializer
{
	/**
	 * A LocaleSerializer instance for the system locale.
	 */
	public static final LocaleSerializer SYSTEM_LOCALE_SERIALIZER = new LocaleSerializer(Locale.getDefault());
	
	
	/**
	 * Calls {@link LocaleSerializer#LocaleSerializer(Locale, boolean)}
	 * with parameter cached set to true.
	 */
	public LocaleSerializer(Locale locale)
	{
		this(locale, true);
	}
	

	/**
	 * Creates a new LocaleSerializer.
	 * @param cached the LocaleSerializer uses a {@link DateFormat} instance internally
	 * 		to format dates. The cache parameter determines if this DateFormat
	 * 		is cached or not (see {@link DateFormat#getInstance(Locale, boolean)}
	 */
	public LocaleSerializer(Locale locale, boolean cached)
	{
		this(locale, NumberFormat.getInstance(locale, cached), DateFormat.getInstance(locale, cached));
	}
	

	public LocaleSerializer(Locale locale, NumberFormat numberFormat, DateFormat dateFormat)
	{
		numberFormat_ 	= Check.notNull(numberFormat, "numberFormat");
		dateFormat_		= Check.notNull(dateFormat, "dateFormat");
	}
	
	
	public Locale getLocale()
	{
		return numberFormat_.getLocale();
	}

	
	public NumberFormat getNumberFormat()
	{
		return numberFormat_;
	}

	
	public DateFormat getDateFormat()
	{
		return dateFormat_;
	}

	
	@Override public String formatBigDecimal(BigDecimal value, Object style)
	{
		return value != null ? 
			numberFormat_.formatDecimal(value, numberStyle(style), null).toString() :
			formatNull();
	}

	
	@Override public String formatBigInteger(BigInteger value, Object style)
	{
		return value != null ? 
			numberFormat_.formatNatural(value, numberStyle(style), null).toString() :
			formatNull();
	}


	@Override public String formatBoolean(boolean value, Object style)
	{
		return String.valueOf(value);
	}

	
	@Override public String formatByte(byte value, Object style)
	{
		return String.valueOf(value);
	}

	
	@Override public String formatChar(char value, Object style)
	{
		return String.valueOf(value);
	}


	@Override public String formatDate(int year, int month, int day, Object style)
	{
		return dateFormat_.format(year, month, day);
	}


	@Override public String formatDouble(double value, Object style)
	{
		return numberFormat_.formatDecimal(value, numberStyle(style));
	}

	
	@Override public String formatFloat(float value, Object style)
	{
		return formatDouble(value, style);
	}
	
	
	@Override public String formatInt(int value, Object style)
	{
		return formatLong(value, style);
	}


	@Override public String formatLong(long value, Object style)
	{
		return numberFormat_.formatNatural(value, numberStyle(style));
	}


	@Override public String formatShort(short value, Object style)
	{
		return formatLong(value, style);
	}


	@Override public String formatString(String value, Object style)
	{
		return value != null ? value : formatNull();
	}
	
	
	private NumberStyle numberStyle(Object style)
	{
		return style instanceof NumberStyle ? (NumberStyle)style : NumberStyle.DEFAULT;
	}


	@Override public BigDecimal parseBigDecimal(String s) throws ParseException
	{
		return numberFormat_.parseBigDecimal(s);
	}
	
	
	@Override public BigInteger parseBigInteger(String s) throws ParseException
	{
		return numberFormat_.parseBigInteger(s);
	}
	
	
	@Override public Boolean parseBoolean(String s)
	{
		return s == null ? null : Boolean.valueOf(s);
	}
	
	
	@Override public Byte parseByte(String s)
	{
		return isBlank(s) ? null : Byte.valueOf(s);
	}


	@Override public Character parseCharacter(String s)
	{
		return (s == null || s.length() == 0) ? null : Character.valueOf(s.charAt(0));
	}
	
	
	@Override public <T> T parseDate(String s, DateType<T> dateType) throws Exception
	{
		return isBlank(s) ? null : dateFormat_.parse(s, dateType);
	}
	
	
	@Override public Double parseDouble(String s) throws ParseException
	{
		return numberFormat_.parseDouble(s);
	}

	
	@Override public Float parseFloat(String s) throws ParseException
	{
		return numberFormat_.parseFloat(s);
	}
	

	@Override public Integer parseInteger(String s)
	{
		return numberFormat_.parseInteger(s);
	}

	
	@Override public Long parseLong(String s)
	{
		return numberFormat_.parseLong(s);
	}
	

	@Override public String parseString(String s)
	{
		return s;
	}

	
	@Override public Short parseShort(String s)
	{
		return numberFormat_.parseShort(s);
	}

	
	private DateFormat dateFormat_;
	private NumberFormat numberFormat_;
}
