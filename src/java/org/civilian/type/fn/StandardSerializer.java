package org.civilian.type.fn;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import org.civilian.type.DateType;
import org.civilian.type.Type;
import org.civilian.type.DateTimeType;
import org.civilian.type.TimeType;
import org.civilian.util.StringUtil;
import static org.civilian.type.TypeLib.*;


public class StandardSerializer extends TypeSerializer
{
	public static final StandardSerializer INSTANCE = new StandardSerializer();
	
	
	public StandardSerializer()
	{
		formatter_.use((t,v,h) -> v.toString())	.byDefault();
		formatter_.use(this::formatDate)		.on(Type.Category.DATE);
		formatter_.use(this::formatTime)		.on(Type.Category.TIME);
		formatter_.use(this::formatDateTime)	.on(Type.Category.DATETIME);
		
		parser_.use(this::parseDate)			.on(Type.Category.DATE);
		parser_.use(this::parseTime)			.on(Type.Category.TIME);
		parser_.use(this::parseDateTime)		.on(Type.Category.DATETIME);
		parser_.use(BigInteger::new)			.on(BIGINTEGER);
		parser_.use(BigDecimal::new)			.on(BIGDECIMAL);
		parser_.use(Boolean::valueOf)			.on(BOOLEAN);
		parser_.use(Byte::valueOf)				.on(BYTE);
		parser_.use(TypeParser.CHAR_FUNCTION)	.on(CHARACTER);
		parser_.use(Double::valueOf)			.on(DOUBLE);
		parser_.use(Float::valueOf)				.on(FLOAT);
		parser_.use(Integer::valueOf)			.on(INTEGER);
		parser_.use(Long::valueOf)				.on(LONG);
		parser_.use(Short::valueOf)				.on(SHORT);
		parser_.use(TypeParser.STRING_FUNCTION)	.on(STRING);
	}
	
	
	private <T> String formatDate(Type<? extends T> type, T date, Object hint)
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


	private <T> String formatTime(Type<? extends T> type, T time, Object hint)
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


	private <T> String formatDateTime(Type<? extends T> type, T dt, Object hint)
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
