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
import org.civilian.util.Date;


/**
 * A type implementation for org.civilian.util.Date.
 * @see TypeLib#DATE_CIVILIAN
 */
public class DateCivilianType extends SimpleType<Date> implements DateType<Date>
{
	@Override public Class<Date> getJavaType()
	{
		return Date.class;
	}
	
	
	@Override public String format(TypeSerializer serializer, Date date, Object style)
	{
		return date == null ? 
			serializer.formatNull() : 
			serializer.formatDate(date.getYear(), date.getMonth(), date.getDay(), style);  
	}

	
	@Override public Date parse(TypeSerializer serializer, String s) throws Exception
	{
		return serializer.parseDate(s, this);
	}
	
	
	@Override public <R,P,E extends Exception> R accept(TypeVisitor<R,P,E> visitor, P param) throws E
	{
		return visitor.visitDate(param, this);
	}


	@Override public Date createDate(int year, int month, int day)
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
