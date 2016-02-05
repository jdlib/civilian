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


import org.civilian.type.DateType;
import org.civilian.type.TypeLib;


/**
 * A type implementation for java.sql.Date.
 * @see TypeLib#DATE_JAVA_SQL
 */
public class DateJavaSqlType extends DateType<java.sql.Date>
{
	@Override public Class<java.sql.Date> getJavaType()
	{
		return java.sql.Date.class;
	}


	@SuppressWarnings("deprecation")
	@Override public java.sql.Date create(int year, int month, int day)
	{
		return new java.sql.Date(year - 1900, month - 1, day);
	}


	@Override public java.sql.Date createToday()
	{
		return new java.sql.Date(System.currentTimeMillis());
	}


	@SuppressWarnings("deprecation")
	@Override public int getYear(java.sql.Date date)
	{
		return date.getYear() + 1900;
	}


	@SuppressWarnings("deprecation")
	@Override public int getMonth(java.sql.Date date)
	{
		return date.getMonth() + 1;
	}


	@SuppressWarnings("deprecation")
	@Override public int getDay(java.sql.Date date)
	{
		return date.getDate();
	}
}
