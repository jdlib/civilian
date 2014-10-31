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
package org.civilian.util;


import java.util.Calendar;
import java.util.GregorianCalendar;
import org.civilian.text.DateFormat;


/**
 * A class for storing date values. A date consists of a year value, a month value 
 * (counting from 1 to 12), and a day value (counting from 1 to 31). 
 * Easier to use than java.util.Calendar and not so deprecated as java.util.Date.
 */
public class Date implements Cloneable, Comparable<Date>
{
	/**
	 * A week day constant for Sunday. Same as java.util.Calendar.SUNDAY
	 */
    public final static int WEEKDAY_SUNDAY 		= Calendar.SUNDAY;

	/**
	 * A week day constant for Monday. Same as java.util.Calendar.MONDAY
	 */
    public final static int WEEKDAY_MONDAY 		= Calendar.MONDAY;

	/**
	 * A week day constant for Tuesday. Same as java.util.Calendar.TUESDAY
	 */
    public final static int WEEKDAY_TUESDAY 	= Calendar.TUESDAY;

	/**
	 * A week day constant for Wednesday. Same as java.util.Calendar.WEDNESDAY
	 */
    public final static int WEEKDAY_WEDNESDAY 	= Calendar.WEDNESDAY;

	/**
	 * A week day constant for Thursday. Same as java.util.Calendar.THURSDAY
	 */
    public final static int WEEKDAY_THURSDAY 	= Calendar.THURSDAY;
    
	/**
	 * A week day constant for Friday. Same as java.util.Calendar.FRIDAY
	 */
    public final static int WEEKDAY_FRIDAY 		= Calendar.FRIDAY;

	/**
	 * A week day constant for Saturday. Same as java.util.Calendar.SATURDAY
	 */
    public final static int WEEKDAY_SATURDAY 	= Calendar.SATURDAY;	
    

	//--------------------
	// Creation
	//--------------------

	
	/**
	 * Returns a Date object for the current date.
	 */
	public static Date today()
	{
		return new Date();
	}


	/**
	 * Creates a Date object for the current date.
	 */
	public Date() 
	{
		this(System.currentTimeMillis());
	}


	/**
	 * Creates a Date object for the given system time.
	 */
	public Date(long time)
	{
		this(new java.util.Date(time));
	}


	/**
	 * Creates a new Date.
	 * @param year the year (the year 2001 is specified as 2001)
	 * @param month the month (counting from 1 to 12)
	 * @param day the day (counting from 1 to 31)
	 * @exception IllegalArgumentException thrown if the values represent
	 *		an invalid date
	 */
	public Date(int year, int month, int day) 
	{	
		if (!isValidDate(year, month, day))
			throw new IllegalArgumentException("invalid date arguments");
		init(year, month, day);
	}


	/**
	 * Creates a date from another date.
	 */
	public Date(Date date) 
	{
		init(date.year_, date.month_, date.day_);
	}


	/**
	 * Creates a date from a java.util.Date.
	 */
	@SuppressWarnings("deprecation")
	public Date(java.util.Date date)
	{
		init(date.getYear() + 1900, date.getMonth() + 1, date.getDate());
	}


	/**
	 * Creates a date from a Calendar.
	 */
	public Date(Calendar cal)
	{
		init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
	}


	private void init(int year, int month, int day)
	{
		year_ 	= year;
		month_	= month;
		day_  	= day;
	}
	
	
	//--------------------
	// Accessors
	//--------------------

	
	/**
	 * Returns the day value (ranging from 1 to 31).
	 */
	public int getDay()
	{
		return day_;
	}


	/**
	 * Returns the month value (ranging from 1 to 12).
	 */
	public int getMonth()
	{
		return month_;
	}


	/**
	 * Returns the year value.
     */
	public int getYear()
	{
		return year_;
	}

	
	/**
	 * Returns the value for the last day of the month of this date (ranging from
	 * 28 to 31).
	 */
	public int getLastDayOfMonth()
	{
		return getLastDayOfMonth(month_, year_);
	}


	/**
	 * Returns the last day of the month for the given month and year (ranging from 28 to 31).
	 * @param month a value between 1 and 12.
	 */
	public static int getLastDayOfMonth(int month, int year)
	{
		return isLeapYear(year) ? DAYS_IN_MONTH_LEAP[month-1] : DAYS_IN_MONTH_NOLEAP[month-1];
	}

	
	/**
	 * Tests if the year of this date is a leap year.
	 */
	public boolean isLeapYear()
	{
		return isLeapYear(getYear());
	}


	/**
	 * Tests if the given year is a leap year.
	 */
	public static boolean isLeapYear(int year)
	{
		final int gregorianCutoverYear = 1582;

		if (year >= gregorianCutoverYear) 
			return (year%4 == 0) && ((year%100 != 0) || (year%400 == 0)); // Gregorian
		else 
			return (year%4 == 0); // Julian
	}

	
	/**
	 * Returns the day of week (ranging from 1 to 7) of this date.
	 * Sunday = 1, Monday = 2, ..., Saturday = 7.
	 * The constants {@link #WEEKDAY_SUNDAY}, ... encode these values.
	 */
	public int getDayOfWeek()
	{
		return ((toJulianDayNumber() + 1) % 7) + 1;
	}
	
	
	//--------------------
	// Comparison
	//--------------------

	
	/**
	 * Tests if this date is equal to another date.
	 */
	@Override public boolean equals(Object date)
	{
		if ((date == null) || !(date instanceof Date))
			return false;

		Date d = (Date) date;
		return (d.day_  == day_) && (d.month_ == month_) && (d.year_  == year_);
	}


	/**
	 * Tests if this date is before the given date.
	 */
	public boolean isBefore(Date d)
	{
		return compareTo(d) < 0;
	}


	/**
	 * Tests if this date is after the given date.
	 */
	public boolean isAfter(Date d)
	{
		return compareTo(d) > 0;
	}
	
	
	/**
	 * Compares this date to another date.
	 */
	@Override public int compareTo(Date d)
	{
		return toInteger() - d.toInteger();
	}
	

	//--------------------
	// Arithmetic
	//--------------------

	
	/**
	 * Returns a new Date object representing this date  
	 * plus the given amount of years.
	 */
	public Date addYears(int years)
	{
		Date d = new Date(this);
		d.addYearsInternal(years);
		return d;
	}


	private void addYearsInternal(int years)
	{
		year_ += years;
		correctDayOfMonth();
	}


	/**
	 * Returns a new Date object representing this date plus the given amount of months.
	 */
	public Date addMonths(int months)
	{
		Date d = new Date(this);
		d.addMonthInternal(months);
		return d;
	}


	private void addMonthInternal(int months)
	{
		int month = month_ + months - 1;

        if (month >= 0)
		{
			year_ += (month / 12);
			month_ = (month % 12) + 1; 
        }
        else
		{
			year_ += ((month + 1) / 12) - 1;
			month %= 12;
			if (month < 0)
				month += 12;
			month_ = month + 1; 
        }
		correctDayOfMonth();
	}


	/**
	 * Returns a new Date object representing this date plus the given amount of days.
	 */
	public Date addDays(int days)
	{
		if (days == 0)
			return this;
		else
		{
			Calendar calendar = toCalendar();
			calendar.add(Calendar.DATE, days);
			return new Date(calendar);
		}
	}


	private void correctDayOfMonth()
	{
        int lastDay = getLastDayOfMonth();
		if (day_ >= lastDay)
			day_ = lastDay;
	}

	
	/**
	 * Calculates the difference in days between two dates.
	 */
	public int difference(Date date)
	{
		return toJulianDayNumber() - date.toJulianDayNumber();
	}
	
	
	//--------------------
	// Validation
	//--------------------

	
	/**
	 * Tests if the given values represent a valid date.
	 */
	public static boolean isValidDate(int year, int month, int day)
	{
		if ((month < 1) || (month > 12))
			return false;

		short monthBound[] = isLeapYear(year) ? DAYS_IN_MONTH_LEAP : DAYS_IN_MONTH_NOLEAP; 
        return (day >= 1) && (day <= monthBound[month - 1]);
	}
	

	//--------------------
	// Conversion
	//--------------------

	
	/**
	 * Returns a string representation of this date in the form yyyyMMdd.
	 * This serves for debug purpose only. To get a locale dependent string 
	 * use the DateFormat class.
	 * @see DateFormat
	 */
	@Override public String toString()
	{
		return StringUtil.fillLeft(String.valueOf(toInteger()), 8, '0');
	}


	/**
	 * Converts the date to an Calendar.
	 */
	public Calendar toCalendar()
	{
		return new GregorianCalendar(year_, month_ - 1, day_);
	}


	/**
	 * Converts the date to an java.util.Date.
	 */
   	public java.util.Date toJavaDate()
    {
    	return toCalendar().getTime();
    }


	/**
	 * Converts the date to an java.sql.Date.
	 */
   	public java.sql.Date toJavaSqlDate()
    {
    	return new java.sql.Date(toJavaDate().getTime());
    }

	
	/**
	 * Returns an integer representation of this date, in the form yyyymmdd
	 */
	public int toInteger()
	{
		return toInteger(year_, month_, day_);
	}


	/**
	 * Returns an integer representation of the given date values.
	 */
	public static int toInteger(int year, int month, int day) 
	{
		return 10000 * year + 100 * month + day;
	}

	
	/**
	 * Parses a date from an integer.
	 * @see #toInteger
	 */
	public static Date fromInteger(int value)
	{
		int year = value / 10000;
		value %= 10000;
		int month = value / 100;
		int day = value % 100;
		
		return new Date(year, month, day);
	}


	/**
	 * Converts a date to its Julian Day Number.
	 * The algorithm is described at http://www.capecod.net/~pbaum/date/date0.htm.
	 */
	public int toJulianDayNumber()
	{
		int z = (month_ < 3) ? year_ - 1 : year_;
		int f = JULIAN_DAY_MONTH_TABLE[month_ - 1];
		return day_ + f + 365*z  + 
			(int)Math.floor(z / 4d) - 
			(int)Math.floor(z / 100d) + 
			(int)Math.floor(z / 400d) + 1721119;
	}
	
	
	//--------------------
	// misc
	//--------------------
	
	
	/**
	 * Clones this date.
	 */
	@Override public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}


	/**
	 * Returns a hashcode for the date.
	 */
	@Override public int hashCode()
	{
		return toInteger();
	}

	
	private int day_;
	private int month_;
	private int year_;
	private static final short DAYS_IN_MONTH_NOLEAP[]	= {31,28,31,30,31,30,31,31,30,31,30,31,30,31};
	private static final short DAYS_IN_MONTH_LEAP[]  	= {31,29,31,30,31,30,31,31,30,31,30,31,30,31};
	private static final short JULIAN_DAY_MONTH_TABLE[] = {306, 337, 0, 31, 61, 92, 122, 153, 184, 214, 245, 275 };
}
