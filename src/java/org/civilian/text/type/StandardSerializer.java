package org.civilian.text.type;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import org.civilian.type.DateType;
import org.civilian.type.Type;
import org.civilian.text.Style;
import org.civilian.type.DateTimeType;
import org.civilian.type.TimeType;
import org.civilian.util.StringUtil;
import static org.civilian.type.TypeLib.*;


public class StandardSerializer extends TypeSerializer
{
	public static final StandardSerializer INSTANCE = new StandardSerializer();
	
	
	public StandardSerializer()
	{
		useFormatter((t,v,h) -> v.toString())	.byDefault();
		useFormatter(this::formatDate)			.on(Type.Category.DATE);
		useFormatter(this::formatTime)			.on(Type.Category.TIME);
		useFormatter(this::formatDateTime)		.on(Type.Category.DATETIME);
		useFormatter(this::formatKey)			.on(Type.Category.KEY);
		useFormatter(this::formatDiscrete)		.on(Type.Category.DISCRETE);
		useFormatter(this::formatEnum)			.on(Type.Category.ENUM);
		
		useParser(this::parseDate)				.on(Type.Category.DATE);
		useParser(this::parseTime)				.on(Type.Category.TIME);
		useParser(this::parseDateTime)			.on(Type.Category.DATETIME);
		useParser(this::parseKey)				.on(Type.Category.KEY);
		useParser(this::parseDiscrete)			.on(Type.Category.DISCRETE);
		useParser(this::parseEnum)				.on(Type.Category.ENUM);
		useSimpleParser(BigInteger::new)		.on(BIGINTEGER);
		useSimpleParser(BigDecimal::new)		.on(BIGDECIMAL);
		useSimpleParser(Boolean::valueOf)		.on(BOOLEAN);
		useSimpleParser(Byte::valueOf)			.on(BYTE);
		useParser(this::parseCharacter)			.on(CHARACTER);
		useSimpleParser(Double::valueOf)		.on(DOUBLE);
		useSimpleParser(Float::valueOf)			.on(FLOAT);
		useSimpleParser(Integer::valueOf)		.on(INTEGER);
		useSimpleParser(Long::valueOf)			.on(LONG);
		useSimpleParser(Short::valueOf)			.on(SHORT);
		useParser(PARSE_STRING)					.on(STRING);
	}
	
	
	private <T> String formatDate(Type<? extends T> type, T date, Style style)
	{
		@SuppressWarnings("unchecked")
		DateType<T> dt = (DateType<T>)type;
		return formatDate(dt.getYear(date), dt.getMonth(date), dt.getDay(date), new StringBuilder()).toString();
	}
	
	
	private StringBuilder formatDate(int year, int month, int day, StringBuilder s)
	{
		if (year < 0)
		{
			s.append('-');
			s.append(StringUtil.fillLeft(-year, 4));
		}
		else
			s.append(StringUtil.fillLeft(year, 4));
		s.append(StringUtil.fillLeft(month, 2));
		s.append(StringUtil.fillLeft(day, 2));
		return s;
	}


	private <T> String formatTime(Type<? extends T> type, T time, Style style)
	{
		@SuppressWarnings("unchecked")
		TimeType<T> tt = (TimeType<T>)type;
		return formatTime(tt.getHour(time), tt.getMinute(time), tt.getSecond(time), new StringBuilder()).toString();
	}
	
	
	private StringBuilder formatTime(int hour, int minute, int second, StringBuilder s)
	{
		s.append(StringUtil.fillLeft(hour, 2));
		s.append(StringUtil.fillLeft(minute, 2));
		s.append(StringUtil.fillLeft(second, 2));
		return s;
	}


	private <T> String formatDateTime(Type<? extends T> type, T dt, Style style)
	{
		@SuppressWarnings("unchecked")
		DateTimeType<T> t = (DateTimeType<T>)type;
		StringBuilder s = new StringBuilder();
		formatDate(t.getYear(dt), t.getMonth(dt), t.getDay(dt), s);
		formatTime(t.getHour(dt), t.getMinute(dt), t.getSecond(dt), s);
		return s.toString();
	}


	private <T> T parseDate(Type<T> type, String s) throws Exception
	{
		DateTimeParser p = new DateTimeParser(s);
		int year 	= p.year();
		int month 	= p.int2();
		int day     = p.int2();
		return ((DateType<T>)type).create(year, month, day);
	}


	private <T> T parseTime(Type<T> type, String s) throws Exception
	{
		DateTimeParser p = new DateTimeParser(s);
		int hour 	= p.int2();
		int minute 	= p.int2();
		int second  = p.int2();
		return ((TimeType<T>)type).create(hour, minute, second);
	}


	private <T> T parseDateTime(Type<T> type, String s) throws Exception
	{
		DateTimeParser p = new DateTimeParser(s);
		int year 	= p.year();
		int month 	= p.int2();
		int day     = p.int2();
		int hour 	= p.int2();
		int minute 	= p.int2();
		int second  = p.int2();
		return ((DateTimeType<T>)type).create(year, month, day, hour, minute, second);
	}
	
	
	private static class DateTimeParser
	{
		public DateTimeParser(String s)
		{
			string_ = s;
		}
		
		
		public int year() throws Exception
		{
			boolean negative = false;
			if (hasMore(1) && (string_.charAt(offset_) == '-'))
			{
				negative = true;
				offset_++;
			}
			int year = nextInt(4);
			return negative ? -year : year;
		}
		
		public int int2() throws Exception
		{
			return nextInt(2);
		}
		
		
		private int nextInt(int length) throws Exception
		{
			if (!hasMore(length))
				throw new ParseException(string_, offset_);
			int n = Integer.parseInt(string_.substring(offset_, offset_ + length));
			offset_ += length;
			return n;
		}
		
		
		public boolean hasMore(int i)
		{
			return offset_ + i <= string_.length(); 
		}

		
		private String string_;
		private int offset_;
	}
}


