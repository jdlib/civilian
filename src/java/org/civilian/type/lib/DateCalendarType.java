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


import java.util.Calendar;
import java.util.GregorianCalendar;
import org.civilian.type.DateType;
import org.civilian.type.TypeLib;


/**
 * A type implementation for java.util.Calendar.
 * @see TypeLib#DATE_CALENDAR
 */
public class DateCalendarType extends DateType<Calendar>
{
	@Override public Class<Calendar> getJavaType()
	{
		return Calendar.class;
	}
	
	
	@Override public Calendar create(int year, int month, int day)
	{
		return new GregorianCalendar(year, month - 1, day);
	}

	
	@Override public Calendar createToday()
	{
		return new GregorianCalendar();
	}


	@Override public int getYear(Calendar cal)
	{
		return cal.get(Calendar.YEAR);
	}


	@Override public int getMonth(Calendar cal)
	{
		return cal.get(Calendar.MONTH) + 1;
	}


	@Override public int getDay(Calendar cal)
	{
		return cal.get(Calendar.DAY_OF_MONTH);
	}
}
