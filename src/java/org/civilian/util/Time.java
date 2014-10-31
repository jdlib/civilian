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
 * Time represents a time value, storing hours, minutes and seconds. 
 */
public class Time implements Cloneable, Comparable<Time>
{
	/**
	 * Returns the current system time.
	 */
	public static Time now() 
	{
		return new Time();
	}


	/**
	 * Creates a Time object for the current system time.
	 */
	public Time() 
	{
		this(System.currentTimeMillis());
	}


	/**
	 * Creates a Time object from a java.sqlTime.
	 */
	@SuppressWarnings("deprecation")
	public Time(java.sql.Time time) 
	{
		this(time.getHours(), time.getMinutes(), time.getSeconds());
	}

	
	/**
	 * Creates a new Time object.
	 * @param time the specified number of milliseconds since the standard base time 
	 *		known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
	 */
	public Time(long time)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new java.util.Date(time));
		init(calendar);
	}

	
	/**
	 * Creates a time object from a Calendar.
	 */
	public Time(Calendar cal)
	{
		init(cal);
	}

	
	/**
	 * Creates a new Time.
	 * @param hours a value between 0 and 23.
	 * @param minutes a value between 0 and 59.
	 * @param seconds a value between 0 and 59.
	 */
	public Time(int hours, int minutes, int seconds) 
	{	
		if (!isValidTime(hours, minutes, seconds))
			throw new IllegalArgumentException("invalid time arguments");
		init(hours, minutes, seconds);
	}


	/**
	 * Creates a Time object from another Time object.
	 */
	public Time(Time time) 
	{
		init(time.hours_, time.minutes_, time.seconds_);
	}


	private void init(int hours, int minutes, int seconds)
	{
		hours_ 		= hours;
		minutes_	= minutes;
		seconds_ 	= seconds;
	}


	private void init(Calendar cal)
	{
		init(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
	}
	

	/**
	 * Return sthe second.
	 * @return a value between 0 and 59.
	 */
	public int getSeconds()
	{
		return seconds_;
	}


	/**
	 * Return the minutes.
	 * @return a value between 0 and 59.
	 */
	public int getMinutes()
	{
		return minutes_;
	}


	/**
	 * Return the hours.
	 * @return a value between 0 and 23.
	 */
	public int getHours()
	{
		return hours_;
	}


	/**
	 * Test if the given Time object represents the same time.
	 */
	@Override public boolean equals(Object time)
	{
		if ((time == null) || !(time instanceof Time))
			return false;

		Time t = (Time) time;
		return (t.seconds_  == seconds_) &&	(t.minutes_ == minutes_) && (t.hours_  == hours_);
	}


	/**
	 * Returns a hashcode for this Time.
	 */
	@Override public int hashCode()
	{
		return toInteger();
	}

	
	/**
	 * Tests if this time is before the given time.
	 */
	public boolean isBefore(Time time)
	{
		return compareTo(time) < 0;
	}


	/**
	 * Tests if this time is after the given time.
	 */
	public boolean isAfter(Time time)
	{
		return compareTo(time) > 0;
	}


	/**
	 * Compares with another time.
	 */
	@Override public int compareTo(Time t)
	{
		return toInteger() - t.toInteger();
	}

	
	/**
	 * Tests if the given values represent a valid time.
	 */
	public static boolean isValidTime(int hours, int minutes, int seconds)
	{
		return isValid(hours, 24) && isValid(minutes, 60) &&  isValid(seconds, 60);
	}
	
	
	private static boolean isValid(int v, int max)
	{
		return (v >= 0) && (v < max);
	}


	/**
	 * Returns a copy of this Time.
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
	 * Return a string representation of this date in the form hh:mm:ss. 
	 * This serves for debug purpose only and does not address any locale issues.
	 */
	@Override public String toString()
	{
		return StringUtil.fillLeft(hours_, 2) + ':' +  StringUtil.fillLeft(minutes_, 2) + ':' + StringUtil.fillLeft(seconds_, 2);
	}


	/**
	 * Extracts a Time from an integer, assuming a value with the pattern hhmmss.
	 * @see #toInteger
	 */
	public static Time fromInteger(int value)
	{
		int hour = value / 10000;
		value %= 10000;
		int minute = value / 100;
		int second = value % 100;
		
		return new Time(hour, minute, second);
	}

	
	/**
	 * Returns an integer representation of this time in the format hhmmss.
	 */
	public int toInteger()
	{
		return hours_ * 10000 + minutes_ * 100 + seconds_;
	}
	
	
	/**
	 * Returns the number of seconds since midnight.
	 */
	public int toSeconds()
	{
		return hours_ * 3600 + minutes_ * 60 + seconds_;
	}


	private int hours_;
	private int minutes_;
	private int seconds_;
}
