package org.civilian.type;


import java.time.LocalDateTime;


public class DateTimeLocalType extends DateTimeType<LocalDateTime>
{
	public static final DateTimeLocalType INSTANCE = new DateTimeLocalType();
	
	
	private DateTimeLocalType()
	{
	}
	
	
	@Override public LocalDateTime now()
	{
		return LocalDateTime.now();
	}


	@Override public LocalDateTime create(int year, int month, int day, int hour, int minute, int second)
	{
		return LocalDateTime.of(year, month, day, hour, minute, second);
	}
	
 
	@Override public int getYear(LocalDateTime value)
	{
		return value.getYear();
	}
	

	@Override public int getMonth(LocalDateTime value)
	{
		return value.getMonthValue();
	}
	

	@Override public int getDay(LocalDateTime value)
	{
		return value.getDayOfMonth();
	}
	

	@Override public int getHour(LocalDateTime value)
	{
		return value.getHour();
	}
	

	@Override public int getMinute(LocalDateTime value)
	{
		return value.getMinute();
	}


	@Override public int getSecond(LocalDateTime value)
	{
		return value.getSecond();
	}
	

	@Override public Class<LocalDateTime> getJavaType()
	{
		return LocalDateTime.class;
	}
}
