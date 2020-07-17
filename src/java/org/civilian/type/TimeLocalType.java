package org.civilian.type;


import java.time.LocalTime;


public class TimeLocalType extends TimeType<LocalTime>
{
	public static final TimeLocalType INSTANCE = new TimeLocalType();
	
	
	private TimeLocalType()
	{
	}
	
	
	@Override public LocalTime create(int hour, int minute, int second)
	{
		return LocalTime.of(hour, minute, second);
	}
	

	@Override public LocalTime now()
	{
		return LocalTime.now();
	}
	

	@Override public int getHour(LocalTime value)
	{
		return value.getHour();
	}
	

	@Override public int getMinute(LocalTime value)
	{
		return value.getMinute();
	}
	

	@Override public int getSecond(LocalTime value)
	{
		return value.getSecond();
	}
	

	@Override public Class<LocalTime> getJavaType()
	{
		return LocalTime.class;
	}
}
