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


import org.civilian.util.Date;


/**
 * A type implementation for org.civilian.util.Date.
 * @see TypeLib#DATE_CIVILIAN
 */
public class DateCivilianType extends DateType<Date>
{
	public static final DateCivilianType INSTANCE = new DateCivilianType();
	
	
	private DateCivilianType()
	{
	}
	
	
	@Override public Class<Date> getJavaType()
	{
		return Date.class;
	}
	
	
	@Override public Date create(int year, int month, int day)
	{
		return new Date(year, month, day);
	}


	@Override public Date createToday()
	{
		return new Date();
	}


	@Override public int getYear(Date date)
	{
		return date.getYear();
	}


	@Override public int getMonth(Date date)
	{
		return date.getMonth();
	}


	@Override public int getDay(Date date)
	{
		return date.getDay();
	}
}
