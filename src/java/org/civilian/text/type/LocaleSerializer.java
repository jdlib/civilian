package org.civilian.text.type;


import java.text.ParseException;
import java.util.Locale;
import org.civilian.text.DateFormat;
import org.civilian.text.NumberFormat;
import org.civilian.text.NumberStyle;
import org.civilian.text.Style;
import org.civilian.text.TimeStyle;
import org.civilian.type.DateType;
import org.civilian.type.Type;
import org.civilian.type.DateTimeType;
import org.civilian.type.TimeType;
import static org.civilian.type.TypeLib.*;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;


public class LocaleSerializer extends TypeSerializer
{
	/**
	 * A LocaleSerializer instance for the system locale.
	 */
	public static final LocaleSerializer SYSTEM_LOCALE_SERIALIZER = new LocaleSerializer(Locale.getDefault());

	
	
	public LocaleSerializer(Locale locale)
	{
		this(locale, null);
	}
	
	
	public LocaleSerializer(Locale locale, LocaleSerializer shared)
	{
		super(shared);
		
		// init locale data
		locale_			= Check.notNull(locale, "locale");
		dateFormat_ 	= new DateFormat(locale);
		numberFormat_ 	= new NumberFormat(locale);
		
		// init formatter
		useSimpleFormatter(Object::toString)			.on(BYTE).on(CHARACTER);
		useFormatter(this::formatNatural)				.on(BIGINTEGER).on(INTEGER).on(LONG).on(SHORT);
		useFormatter(this::formatDecimal)				.on(BIGDECIMAL).on(DOUBLE).on(FLOAT);
		useSimpleFormatter(Object::toString)			.on(STRING).on(BOOLEAN);
		useFormatter(this::formatDate)					.on(Type.Category.DATE);
		useFormatter(this::formatDateTime)				.on(Type.Category.DATETIME);
		useFormatter(this::formatTime)					.on(Type.Category.TIME);
		useFormatter(this::formatDiscrete)				.on(Type.Category.DISCRETE);
		useFormatter(this::formatAuto)					.on(Type.Category.AUTO);
		
		// init parser
		useSimpleParser(numberFormat_::parseBigDecimal)	.on(BIGDECIMAL);	
		useSimpleParser(numberFormat_::parseBigInteger)	.on(BIGINTEGER);	
		useSimpleParser(Boolean::valueOf)				.on(BOOLEAN);	
		useSimpleParser(Byte::valueOf)					.on(BYTE);	
		useParser(this::parseCharacter)					.on(CHARACTER);
		useSimpleParser(numberFormat_::parseFloat)		.on(FLOAT);	
		useSimpleParser(numberFormat_::parseDouble)		.on(DOUBLE);	
		useSimpleParser(numberFormat_::parseInteger)	.on(INTEGER);	
		useSimpleParser(numberFormat_::parseLong)		.on(LONG);	
		useSimpleParser(numberFormat_::parseShort)		.on(SHORT);	
		useParser(PARSE_STRING)							.on(STRING);
		useParser(this::parseDate)						.on(Type.Category.DATE);
		useParser(this::parseDateTime)					.on(Type.Category.DATETIME);
		useParser(this::parseTime)						.on(Type.Category.TIME);
		useParser(this::parseDiscrete)					.on(Type.Category.DISCRETE);
		useParser(this::parseAuto)						.on(Type.Category.AUTO);
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

	
	private String formatDecimal(Type<?> type, Number value, Style style)
	{
		return numberFormat_.formatDecimal(value, numberStyle(style), null).toString();
	}

	
	private String formatNatural(Type<?> type, Number value, Style style)
	{
		return numberFormat_.formatNatural(value, numberStyle(style), null).toString();
	}

	
	private <T> String formatDate(Type<?> type, T value, Style style)
	{
		@SuppressWarnings("unchecked")
		DateType<T> dateType = (DateType<T>)type;
		int year	= dateType.getYear(value);
		int month	= dateType.getMonth(value);
		int day		= dateType.getDay(value);
		return dateFormat_.format(year, month, day);
	}

	
	private <T> String formatDateTime(Type<?> type, T value, Style style)
	{
		@SuppressWarnings("unchecked")
		DateTimeType<T> dateType = (DateTimeType<T>)type;
		int year	= dateType.getYear(value);
		int month	= dateType.getMonth(value);
		int day		= dateType.getDay(value);
		int hour	= dateType.getHour(value);
		int minute	= dateType.getMinute(value);
		int second	= dateType.getSecond(value);
		
		return dateFormat_.format(year, month, day) + ' ' + formatTime(hour, minute, second,style);
	}

	
	private <T> String formatTime(Type<?> type, T value, Style style)
	{
		@SuppressWarnings("unchecked")
		TimeType<T> dateType = (TimeType<T>)type;
		int hour	= dateType.getHour(value);
		int minute	= dateType.getMinute(value);
		int second	= dateType.getSecond(value);
		
		return formatTime(hour, minute, second, style);
	}

	
	private <T> String formatTime(int hour, int minute, int second, Style style)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.fillLeft(hour, 2));
		sb.append(':');
		sb.append(StringUtil.fillLeft(minute, 2));
		if (timeStyle(style).showSeconds())
			sb.append(':').append(StringUtil.fillLeft(second, 2));
		return sb.toString();
	}
	
	
	private NumberStyle numberStyle(Style style)
	{
		return style instanceof NumberStyle ? (NumberStyle)style : NumberStyle.DEFAULT;
	}
	
	
	private TimeStyle timeStyle(Style style)
	{
		return style instanceof TimeStyle ? (TimeStyle)style : TimeStyle.HM;
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

	
	private final Locale locale_;
	private final DateFormat dateFormat_;
	private final NumberFormat numberFormat_;
}

