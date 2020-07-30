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


/**
 * A type implementation for Time types.
 */
public abstract class TimeType<T> extends Type<T>
{
	public TimeType()
	{
		super(Category.TIME); 
	}


	public T create(int hour, int minute)
	{
		return create(hour, minute, 0);
	}
	
	
	public abstract T create(int hour, int minute, int second);


	public abstract T now();
	

	public abstract int getHour(T value);

	
	public abstract int getMinute(T value);

	
	public abstract int getSecond(T value);
}
