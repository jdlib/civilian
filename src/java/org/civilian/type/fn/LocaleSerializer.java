package org.civilian.type.fn;


import java.text.ParseException;
import java.util.Locale;
import org.civilian.text.DateFormat;
import org.civilian.text.NumberFormat;
import org.civilian.text.NumberStyle;
import org.civilian.type.DateType;
import org.civilian.type.Type;
import org.civilian.type.DateTimeType;
import org.civilian.type.TimeType;
import static org.civilian.type.TypeLib.*;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


public class LocaleSerializer extends TypeSerializer
{
	public LocaleSerializer(Locale locale)
	{
		this(locale, null, null);
	}
	
	
	public LocaleSerializer(Locale locale, TypeFormatter formatter, TypeParser parser)
	{
		super(formatter, parser);
		
		// init locale data
		locale_			= Check.notNull(locale, "locale");
		dateFormat_ 	= new DateFormat(locale);
		numberFormat_ 	= new NumberFormat(locale);
		
		// init formatter
		formatter_.use(Object::toString)			.on(BYTE).on(CHARACTER);
		formatter_.use(this::formatNatural)			.on(BIGINTEGER).on(INTEGER).on(LONG).on(SHORT);
		formatter_.use(this::formatDecimal)			.on(BIGDECIMAL).on(DOUBLE).on(FLOAT);
		formatter_.use(Object::toString)			.on(STRING).on(BOOLEAN);
		formatter_.use(this::formatDate)			.on(Type.Category.DATE);
		formatter_.use(this::formatDateTime)		.on(Type.Category.DATETIME);
		formatter_.use(this::formatTime)			.on(Type.Category.TIME);
		
		// init parser
		parser_.use(numberFormat_::parseBigDecimal)	.on(BIGDECIMAL);	
		parser_.use(numberFormat_::parseBigInteger)	.on(BIGINTEGER);	
		parser_.use(Boolean::valueOf)				.on(BOOLEAN);	
		parser_.use(Byte::valueOf)					.on(BYTE);	
		parser_.use(TypeParser.CHAR_FUNCTION)		.on(CHARACTER);
		parser_.use(numberFormat_::parseFloat)		.on(FLOAT);	
		parser_.use(numberFormat_::parseDouble)		.on(DOUBLE);	
		parser_.use(numberFormat_::parseInteger)	.on(INTEGER);	
		parser_.use(numberFormat_::parseLong)		.on(LONG);	
		parser_.use(numberFormat_::parseShort)		.on(SHORT);	
		parser_.use(TypeParser.STRING_FUNCTION)		.on(STRING);
		parser_.use(this::parseDate)				.on(Type.Category.DATE);
		parser_.use(this::parseDateTime)			.on(Type.Category.DATETIME);
		parser_.use(this::parseTime)				.on(Type.Category.TIME);
	}
	
	
	public Locale getLocale()
	{
		return locale_;
	}


	public DateFormat getDateFormat()
	{
		return dateFormat_;
	}

	
	public NumberFormat getNumberFormat()
	{
		return numberFormat_;
	}

	
	private String formatDecimal(Type<? extends Number> type, Number value, Object style)
	{
		return numberFormat_.formatDecimal(value, numberStyle(style), null).toString();
	}

	
	private String formatNatural(Type<? extends Number> type, Number value, Object style)
	{
		return numberFormat_.formatNatural(value, numberStyle(style), null).toString();
	}

	
	private <T> String formatDate(Type<?> type, T value, Object style)
	{
		@SuppressWarnings("unchecked")
		DateType<T> dateType = (DateType<T>)type;
		int year	= dateType.getYear(value);
		int month	= dateType.getMonth(value);
		int day		= dateType.getDay(value);
		return dateFormat_.format(year, month, day);
	}

	
	private <T> String formatDateTime(Type<?> type, T value, Object style)
	{
		@SuppressWarnings("unchecked")
		DateTimeType<T> dateType = (DateTimeType<T>)type;
		int year	= dateType.getYear(value);
		int month	= dateType.getMonth(value);
		int day		= dateType.getDay(value);
		int hour	= dateType.getHour(value);
		int minute	= dateType.getMinute(value);
		int second	= dateType.getSecond(value);
		
		return dateFormat_.format(year, month, day) + ' ' + formatTime(hour, minute, second);
	}

	
	private <T> String formatTime(Type<?> type, T value, Object style)
	{
		@SuppressWarnings("unchecked")
		TimeType<T> dateType = (TimeType<T>)type;
		int hour	= dateType.getHour(value);
		int minute	= dateType.getMinute(value);
		int second	= dateType.getSecond(value);
		
		return formatTime(hour, minute, second);
	}

	
	private <T> String formatTime(int hour, int minute, int second)
	{
		return StringUtil.fillLeft(hour, 2) + ':' +
			StringUtil.fillLeft(minute, 2) + ':' + 
			StringUtil.fillLeft(second, 2);
	}
	
	
	private NumberStyle numberStyle(Object style)
	{
		return style instanceof NumberStyle ? (NumberStyle)style : NumberStyle.DEFAULT;
	}

	
	private <T> T parseDate(Type<T> type, String s) throws ParseException
	{
		return dateFormat_.parse((DateType<T>)type, s);
	}
	
	
	private <T> T parseDateTime(Type<T> type, String s) throws ParseException
	{
		DateTimeType<T> dtType = (DateTimeType<T>)type;

		int n = s.indexOf(' ');
		int[] t = null;
		if (n == -1)
			t = new int[3];
		else
		{
			t = parseTimeParts(s.substring(n + 1));
			s = s.substring(0, n);
		}
		
		int[] time = t;
		
		return dateFormat_.parse((y, m,d) -> {
			return dtType.create(y, m, d, time[0], time[1], time[2]);
		}, s);
	}
	
	
	private <T> T parseTime(Type<T> type, String s) throws ParseException
	{
		TimeType<T> timeType = (TimeType<T>)type;
		int[] parts = parseTimeParts(s);
		return timeType.create(parts[0], parts[1], parts[2]);
	}
	

	/**
	 * Extract a DateTime from a String.
	 * @exception ParseException thrown if parsing fails
	 */
	private int[] parseTimeParts(String s) throws ParseException
	{
		int length = s.length();
		if (length >= 4)
		{
			int colon = s.indexOf(':');
			if ((colon > 0) && (colon <= 2) && (colon < length))
			{
				int hour   = Integer.parseInt(s.substring(0, colon));
				int minute = length <= 5 ?
					Integer.parseInt(s.substring(colon + 1)) :
					Integer.parseInt(s.substring(colon + 1, colon + 3));
				if (length <= 5) 
					return new int[] { hour, minute, 0 };
				else if ((length >= 8) && (s.charAt(5) == ':'))
				{
					int second = Integer.parseInt(s.substring(6, 8));
					return new int[] { hour, minute, second };
				}
			}
		}
		throw new ParseException("invalid time: " + s, 0);
	}

	
	private Locale locale_;
	private DateFormat dateFormat_;
	private NumberFormat numberFormat_;
}


