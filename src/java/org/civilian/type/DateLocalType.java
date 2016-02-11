package org.civilian.type;


import java.time.LocalDate;


/**
 * A type implementation for java.time.LocalDate.
 * @see TypeLib#DATE_CIVILIAN
 */
public class DateLocalType extends DateType<LocalDate>
{
	public static final DateLocalType INSTANCE = new DateLocalType();
	
	
	private DateLocalType()
	{
	}
	
	
	@Override public Class<LocalDate> getJavaType()
	{
		return LocalDate.class;
	}
	
	
	@Override public LocalDate create(int year, int month, int day)
	{
		return LocalDate.of(year, month, day);
	}


	@Override public LocalDate createToday()
	{
		return LocalDate.now();
	}


	@Override public int getYear(LocalDate date)
	{
		return date.getYear();
	}


	@Override public int getMonth(LocalDate date)
	{
		return date.getMonthValue();
	}


	@Override public int getDay(LocalDate date)
	{
		return date.getDayOfMonth();
	}
}
