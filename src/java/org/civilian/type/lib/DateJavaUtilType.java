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
import org.civilian.type.TypeSerializer;
import org.civilian.type.TypeVisitor;


/**
 * A type implementation for java.util.Date.
 * @see TypeLib#DATE_JAVA_UTIL
 */
public class DateJavaUtilType extends DateType<java.util.Date>
{
	@Override public Class<java.util.Date> getJavaType()
	{
		return java.util.Date.class;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override public String format(TypeSerializer serializer, java.util.Date date, Object style)
	{
		return date == null ? 
			serializer.formatNull() : 
			serializer.formatDate(date.getYear() + 1900, date.getMonth() + 1, date.getDate(), style);  
	}

	
	@Override public java.util.Date parse(TypeSerializer serializer, String s) throws Exception
	{
		return serializer.parseDate(s, this);
	}
	
	
	@Override public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E
	{
		return visitor.visitDate(param, this);
	}


	@SuppressWarnings("deprecation")
	@Override public java.util.Date create(int year, int month, int day)
	{
		return new java.util.Date(year - 1900, month - 1, day);
	}


	@Override public java.util.Date createToday()
	{
		return new java.util.Date();
	}


	@SuppressWarnings("deprecation")
	@Override public int getYear(java.util.Date date)
	{
		return date.getYear() + 1900;
	}


	@SuppressWarnings("deprecation")
	@Override public int getMonth(java.util.Date date)
	{
		return date.getMonth() + 1;
	}


	@SuppressWarnings("deprecation")
	@Override public int getDay(java.util.Date date)
	{
		return date.getDate();
	}
}
