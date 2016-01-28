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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.text.DateFormatSymbols;
import org.civilian.type.DateType;
import org.civilian.util.Check;
import org.civilian.util.Date;
import org.civilian.util.StringUtil;


/**
 * DateFormat provides locale dependent formatting of dates.
 */
public class DateFormat implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	
	public static final char SYMBOL_MONTH = 'M';
	public static final char SYMBOL_DAY   = 'd';
	public static final char SYMBOL_YEAR  = 'y';
	

	/**
	 * Creates a new DateFormat.
	 */
	public DateFormat(Locale locale)
	{
		locale_					= Check.notNull(locale, "locale");
		SimpleDateFormat sdf	= getSimpleDateFormat(locale);
		symbols_				= sdf.getDateFormatSymbols();
		String pattern			= sdf.toPattern();
		
		int len = pattern.length();
		int pos = 0;
		for (int i=0; i<len; i++)
		{
			char c = pattern.charAt(i);
			if (Character.isLetter(c))
			{
				switch(c)
				{
					case SYMBOL_DAY:
						dayPosition_ = pos;
						break;
					case SYMBOL_MONTH:
						monthPosition_ = pos;
						break;
					case SYMBOL_YEAR:
						yearPosition_ = pos;
						break;
				}
			}
			else
			{
				separatorSymbol_ = c;
				++pos;
			}
		}
	}

	
	private SimpleDateFormat getSimpleDateFormat(Locale locale)
	{
		try
		{
			return (SimpleDateFormat)java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, locale);
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException("cannot create a SimpleDateFormat for locale " + locale, e);
		}
	}
	
	
	@SuppressWarnings("unused")
	private SimpleDateFormat getSimpleTimeFormat(Locale locale)
	{
		try
		{
			return (SimpleDateFormat)java.text.DateFormat.getTimeInstance(java.text.DateFormat.MEDIUM, locale);
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException("cannot create a SimpleDateFormat for locale " + locale, e);
		}
	}

	
	/**
	 * Returns the locale of the date format.
	 */
	public Locale getLocale()
	{
		return locale_;
	}
	

	/**
	 * Returns the locale dependent symbol which separates date parts
	 * (e.g. '.' or '/').
	 */
	public char getSeparatorSymbol()
	{
		return separatorSymbol_;
	}


	/**
	 * Returns the position (0-2) of the day value in date string.
	 */
	public int getDayPosition()
	{
		return dayPosition_;
	}


	/**
	 * Returns the position (0-2) of the month value in date string.
	 */
	public int getMonthPosition()
	{
		return monthPosition_;
	}


	/**
	 * Returns the position (0-2) of the year value in date string.
	 */
	public int getYearPosition()
	{
		return yearPosition_;
	}

	
	/**
	 * Returns the name of a month
	 * @param month the month (ranging from 1 to 12)
	 */
	public String getMonthName(int month)
	{
		return getName(symbols_.getMonths(), month - 1);
	}
		
		
	/**
	 * Returns the short name of a month
	 * @param month the month (ranging from 1 to 12)
	 */
	public String getShortMonthName(int month)
	{
		return getName(symbols_.getShortMonths(), month - 1);
	}

	
	/**
	 * Returns the name of a weekday.
	 * @param weekday the weekday
	 * @see Date#WEEKDAY_SUNDAY
	 */
	public String getWeekdayName(int weekday)
	{
		return getName(symbols_.getWeekdays(), weekday);
	}
		
		
	/**
	 * Returns the short name of a weekday
	 * @param weekday the weekday 
	 * @see Date#WEEKDAY_SUNDAY
	 */
	public String getShortWeekdayName(int weekday)
	{
		return getName(symbols_.getShortWeekdays(), weekday);
	}

	
	private String getName(String names[], int index)
	{
		try
		{
			return names[index];
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			throw new IllegalArgumentException("invalid index " + index);
		}
	}
	
	
	//------------------------------
	// parse
	//------------------------------
	
	
	/**
	 * Parses a date from a string representation.
	 * @exception java.text.ParseException thrown if an parse error occurs
	 */
	public <T> T parse(String text, DateType<T> dateType) throws ParseException
	{
		int values[] = new int[6]; // 0-2: value 3-5: length of string part
		int errorPos = parseValues(text, values);
		if (errorPos >= 0)
			throw new ParseException("'" + text + "' has an invalid date format", errorPos);

		int day  	= values[dayPosition_];
		int month	= values[monthPosition_];
		int year 	= values[yearPosition_];
		if (values[yearPosition_ + 3] == 2)
			year += 2000;

		try
		{
			return dateType.createDate(year, month, day);
		}
		catch(IllegalArgumentException e)
		{
			throw new ParseException("'" + text + "' is an invalid date", 0);
		}
	}


	private int parseValues(String text, int values[])
	{
		StringBuilder s = new StringBuilder();
		int pos = 0;
		int len = text.length();

		for (int i=0; i<3; i++)
		{
			if (pos >= len)
				return pos;
			
			int p = pos;
			s.setLength(0);
			while (p < len)
			{
				char c = text.charAt(p++);
				if (c == separatorSymbol_)
					break;
				if (c != ' ')
					s.append(c);
			}
			try
			{
				values[i] = Integer.parseInt(s.toString());
				values[i + 3] = s.length();
			}
			catch(Exception e)
			{
				return pos;
			}
			pos = p;
		}
		
		return -1; // ok
	}


	//------------------------------
	// format
	//------------------------------
	

	/**
	 * Returns a string representation of a date.
	 */
	public String format(Date date)
	{
		return format(date.getYear(), date.getMonth(), date.getDay());
	}


	/**
	 * Returns a string representation of a date.
	 * @param date the date
	 * @param s a StringBuilder to hold the result
	 */
	public void format(Date date, StringBuilder s)
	{
		format(date.getYear(), date.getMonth(), date.getDay(), s);
	}


	/**
	 * Returns a string representation of a date.
	 * @param date the date
	 * @param s a StringBuilder to hold the result
	 * @param ignorePart exclude a date part from the result string
	 * @see #SYMBOL_MONTH
	 * @see #SYMBOL_DAY
	 * @see #SYMBOL_YEAR
	 */
	public void format(Date date, StringBuilder s, char ignorePart)
	{
		format(date.getYear(), date.getMonth(), date.getDay(), s, ignorePart);
	}


	/**
	 * Returns a string representation of a date.
	 * @param year the year (the year 2001 is specified as 2001)
	 * @param month the month (counting from 1 to 12)
	 * @param day the day (counting from 1 to 31)
	 */
	public String format(int year, int month, int day)
	{
		StringBuilder s = new StringBuilder(10);
		format(year, month, day, s, '@');
		return s.toString();
	}

	
	/**
	 * Returns a string representation of a date.
	 * @param year the year (the year 2001 is specified as 2001)
	 * @param month the month (counting from 1 to 12)
	 * @param day the day (counting from 1 to 31)
	 * @param s a StringBuilder to hold the result
	 */
	public void format(int year, int month, int day, StringBuilder s)
	{
		format(year, month, day, s, '@');
	}


	/**
	 * Returns a string representation of a date.
	 * @param year the year (the year 2001 is specified as 2001)
	 * @param month the month (counting from 1 to 12)
	 * @param day the day (counting from 1 to 31)
	 * @param s a StringBuilder to hold the result
	 * @param ignorePart exclude a date part from the result string
	 * @see #SYMBOL_MONTH
	 * @see #SYMBOL_DAY
	 * @see #SYMBOL_YEAR
	 */
	public void format(int year, int month, int day, StringBuilder s, char ignorePart)
	{
		Check.notNull(s, "StringBuilder");
		
		int parts = 0;
		for (int i=0; i<3; i++)
		{
			int val = -1;
			int len = 2;
			char symbol = '@';

			if (dayPosition_ == i)
			{
				val		= day;
				symbol	= SYMBOL_DAY;
			}
			else if (monthPosition_ == i)
			{
				val		= month;
				symbol	= SYMBOL_MONTH;
			}
			else if (yearPosition_ == i)
			{
				val		= year;
				symbol	= SYMBOL_YEAR;
				len		= 4;
			}

			if (ignorePart != symbol)
			{
				if ((parts > 0) && (parts <= 2))
					s.append(separatorSymbol_);
				s.append(StringUtil.fillLeft(String.valueOf(val), len, '0'));
				parts++;
			}
		}
	}


	private int dayPosition_;
	private int monthPosition_;
	private int yearPosition_;
	private char separatorSymbol_;
	private DateFormatSymbols symbols_;
	private final Locale locale_;
}
