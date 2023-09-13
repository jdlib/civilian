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
package org.civilian.type;


import org.civilian.text.DateFormat;


/**
 * A Type extension for Dates.
 */
public abstract class DateType<T> extends Type<T> implements DateFormat.Factory<T>
{
	/**
	 * Creates a new DateType.
	 */
	public DateType()
	{
		super(Category.DATE);
	}
	
	
	/**
	 * Creates a Date object for given year, month and day values. 
	 * @param year the year
	 * @param month the month (1-12)
	 * @param day the day (1-31)
	 */
	@Override public abstract T create(int year, int month, int day);

	
	/**
	 * Creates a Date object for current date.
	 * @return the date
	 */
	public abstract T createToday();


	/**
	 * Extracts the year from a date object.
	 * @param date a date
	 * @return the year
	 */
	public abstract int getYear(T date);


	/**
	 * Extracts the month from a date object.
	 * @param date a date
	 * @return the month (1-12)
	 */
	public abstract int getMonth(T date);


	/**
	 * Extracts the day from a date object.
	 * @param date a date
	 * @return the day (1-31)
	 */
	public abstract int getDay(T date);
}
