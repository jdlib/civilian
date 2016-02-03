/*
 * Copyright (C) 2016 Civilian Framework.
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


public abstract class DateTimeType<T> extends Type<T>
{
	public DateTimeType()
	{
		super(Category.DATETIME);
	}
	
	
	public abstract T now();
	
			
	public abstract T create(java.sql.Timestamp ts);


	public abstract T create(int year, int month, int day, int hour, int minute, int second);

	
	public abstract int getYear(T date);


	public abstract int getMonth(T date);


	public abstract int getDay(T date);

	
	public abstract int getHour(T time);

	
	public abstract int getMinute(T time);

	
	public abstract int getSecond(T time);
}
