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


import java.math.BigDecimal;
import java.math.BigInteger;
import org.civilian.type.DateType;
import org.civilian.util.StringUtil;


/**
 * A TypeSerializer implementation which uses mostly the toString()
 * method to produce string representations of values.
 */
public class StandardSerializer extends AbstractSerializer
{
	public static final StandardSerializer INSTANCE = new StandardSerializer();
	
	
	@Override public String formatBigDecimal(BigDecimal value, Object style)
	{
		return formatDefault(value); 
	}

	
	@Override public String formatBigInteger(BigInteger value, Object style)
	{
		return formatDefault(value); 
	}


	@Override public String formatBoolean(boolean value, Object style)
	{
		return Boolean.toString(value);
	}

	
	@Override public String formatByte(byte value, Object style)
	{
		return Byte.toString(value);
	}

	
	@Override public String formatChar(char value, Object style)
	{
		return Character.toString(value);
	}


	@Override public String formatDate(int year, int month, int day, Object style)
	{
		return StringUtil.fillLeft(year, 4) + StringUtil.fillLeft(month, 2) + StringUtil.fillLeft(day, 2); 
	}


	@Override public String formatDouble(double value, Object style)
	{
		return Double.toString(value);
	}

	
	@Override public String formatFloat(float value, Object style)
	{
		return Float.toString(value);
	}
	
	
	@Override public String formatInt(int value, Object style)
	{
		return Integer.toString(value);
	}


	@Override public String formatLong(long value, Object style)
	{
		return Long.toString(value);
	}


	@Override public String formatString(String value, Object style)
	{
		return formatDefault(value); 
	}


	@Override public String formatShort(short value, Object style)
	{
		return Short.toString(value);
	}


	// we don't implement formatObject this way since JsonSerializer does an own formatObject implementation
	private String formatDefault(Object value)
	{
		return value != null ? value.toString() : formatNull();
	}
	
	
	@Override public BigDecimal parseBigDecimal(String s) throws Exception
	{
		return isBlank(s) ? null : new BigDecimal(s);
	}
	
	
	@Override public BigInteger parseBigInteger(String s) throws Exception
	{
		return isBlank(s) ? null : new BigInteger(s);
	}
	
	
	@Override public Boolean parseBoolean(String s) throws Exception
	{
		return isBlank(s) ? null : Boolean.valueOf(s);
	}
	
	
	@Override public Byte parseByte(String s) throws Exception
	{
		return isBlank(s) ? null : Byte.valueOf(s);
	}


	@Override public Character parseCharacter(String s) throws Exception
	{
		s = parseString(s); // strip off quotes
		return isBlank(s) ? null : Character.valueOf(s.charAt(0));
	}
	
	
	@Override public <T> T parseDate(String s, DateType<T> dateType) throws Exception
	{
		if (isBlank(s))
			return null;
		if (s.length() != 8)
			throw new IllegalArgumentException("date strings must be 8 characters long");
		try
		{
			int year  = Integer.parseInt(s.substring(0, 4)); 
			int month = Integer.parseInt(s.substring(4, 6)); 
			int day   = Integer.parseInt(s.substring(6));
			return dateType.create(year, month, day);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("not a valid date (yyyymmdd): " + s, e);
		}
	}
	
	
	@Override public Double parseDouble(String s) throws Exception
	{
		return isBlank(s) ? null : Double.valueOf(s);
	}

	
	@Override public Float parseFloat(String s)
	{
		return isBlank(s) ? null : Float.valueOf(s);
	}
	

	@Override public Integer parseInteger(String s) throws Exception
	{
		return isBlank(s) ? null : Integer.valueOf(s);
	}

	
	@Override public Long parseLong(String s) throws Exception
	{
		return isBlank(s) ? null : Long.valueOf(s);
	}
	

	@Override public Short parseShort(String s)
	{
		return isBlank(s) ? null : Short.valueOf(s);
	}
	
	
	@Override public String parseString(String s) throws Exception
	{
		return s;
	}
}
