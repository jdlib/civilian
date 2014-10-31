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


/**
 * A class for storing date time values.
 */
public class DateTime implements Comparable<DateTime>
{
	/**
	 * Creates a DateTime object for current date.
	 */
	public DateTime() 
	{
		this(System.currentTimeMillis());
	}


	/**
	 * Creates a DateTime object for the given system time.
	 */
	public DateTime(long time)
	{
		this(new java.util.Date(time));
	}


	/**
	 * Creates a DateTime object for the given system time.
	 */
	public DateTime(java.util.Date date)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		init(calendar);
	}
	

	/**
	 * Creates a DateTime object for the given calendar.
	 */
	public DateTime(Calendar calendar)
	{
		init(calendar);
	}
	
	
	/**
	 * Creates a DateTime object for the given date and time.
	 */
	public DateTime(Date date, Time time)
	{
		date_ = date;
		time_ = time;
	}
	
	
	/**
	 * Creates a DateTime object for the given date and time values.
	 */
	public DateTime(int year, int month, int day, int hours, int minutes, int seconds)
	{
		this(new Date(year, month, day), new Time(hours, minutes, seconds));
	}
	
	
	private void init(Calendar calendar)
	{
		date_ = new Date(calendar);
		time_ = new Time(calendar);
	}

	
	/**
	 * Returns the date part.
     */
	public Date getDate()
	{
		return date_;
	}
	
	
	/**
	 * Returns the day value (ranging from 1 to 31).
	 */
	public int getDay()
	{
		return date_.getDay();
	}


	/**
	 * Returns the month value (ranging from 1 to 12).
	 */
	public int getMonth()
	{
		return date_.getMonth();
	}


	/**
	 * Returns the year value.
     */
	public int getYear()
	{
		return date_.getYear();
	}

	
	/**
	 * Returns the time part.
     */
	public Time getTime()
	{
		return time_;
	}
	
	
	/**
	 * Returns the hours.
	 * @return a value between 0 and 23.
	 */
	public int getHours()
	{
		return time_ == null ? 0 : time_.getHours();
	}

	
	/**
	 * Returns the minutes.
	 * @return a value between 0 and 59.
	 */
	public int getMinutes()
	{
		return time_ == null ? 0 : time_.getMinutes();
	}


	/**
	 * Returns the seconds.
	 * @return a value between 0 and 59.
	 */
	public int getSeconds()
	{
		return time_ == null ? 0 : time_.getSeconds();
	}


	/**
	 * Tests if this date time is before the given date time.
	 */
	public boolean isBefore(DateTime dt)
	{
		return compareTo(dt) < 0;
	}


	/**
	 * Tests if this date time is after the given date time.
	 */
	public boolean isAfter(DateTime dt)
	{
		return compareTo(dt) > 0;
	}
	
	
	/**
	 * Compares with another DateTime.
	 */
	@Override public int compareTo(DateTime dt)
	{
		int n = getDate().compareTo(dt.getDate());
		if (n == 0)
			n = getTime().compareTo(dt.getTime());
		return n;
	}

	
	/**
	 * Extract a DateTime from a long.
	 * @see #toLong
	 */
	public static DateTime fromLong(long value)
	{
		int date = (int)(value / 1000000L);
		int time = (int)(value % 1000000L);
		return new DateTime(Date.fromInteger(date), Time.fromInteger(time));
	}

	
	/**
	 * Returns a long number which has the format YYYYMMDDhhmmss.
	 */
	public long toLong()
	{
		return (date_.toInteger()) * 1000000L + time_.toInteger();
	}
	
	
	/**
	 * Tests if the given DateTime object represents the same Datetime.
	 */
	@Override public boolean equals(Object object)
	{
		if (object instanceof DateTime)
		{
			DateTime dt = (DateTime)object;
			return dt.time_.equals(time_) && dt.date_.equals(date_);
		}
		else
			return false;
	}

	
	/**
	 * Returns a hash code for this DateIime.
	 */
	@Override public int hashCode()
	{
		return date_.hashCode() ^ time_.hashCode();
	}
	
	
	/**
	 * Returns a string version of the date time object
	 * in the format yyyyMMddhhmmss
	 */
	@Override public String toString()
	{
		return date_.toString() + ' ' + time_.toString();
	}

	
	private Date date_;
	private Time time_;
}
