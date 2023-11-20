package org.civilian.text;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import org.civilian.util.Check;
import org.civilian.util.StringUtil;



/**
 * NumberFormat provides locale dependent formatting of numbers.
 */
public class NumberFormat implements Serializable
{
	private static final long serialVersionUID = 1L;
	

	/**
	 * Creates a new NumberFormat.
	 * @param locale the locale used by the format
	 */
	public NumberFormat(Locale locale)
	{
		locale_	= Check.notNull(locale, "locale");
		impl_ 	= java.text.NumberFormat.getNumberInstance(locale);

		if (impl_ instanceof DecimalFormat)
		{
			DecimalFormatSymbols symbols 	= ((DecimalFormat)impl_).getDecimalFormatSymbols(); 
			decimalSeparator_ 				= symbols.getDecimalSeparator();
			groupingSeparator_ 				= symbols.getGroupingSeparator();
			groupingSeparatorString_ 		= groupingSeparator_ > 0 ? String.valueOf(groupingSeparator_) : null;
		}
		else
		{
			decimalSeparator_ 				= 0;
			groupingSeparator_ 				= 0;
			groupingSeparatorString_ 		= null;
		}
	}
	
	
	/**
	 * @return the locale of the NumberFormat.
	 */
	public Locale getLocale()
	{
		return locale_;
	}
	
	
	/**
	 * Returns the grouping separator char of the NumberFormat.
	 * @return the separator or 0 if not defined
	 */
	public char getGroupingSeparator()
	{
		return groupingSeparator_;
	}
	

	/**
	 * Returns the grouping separator as String.
	 * @return the separator or null if not defined
	 */
	public String getGroupingSepString()
	{
		return groupingSeparatorString_;
	}
	

	/**
	 * Returns the decimal separator char of the NumberFormat.
	 * @return the separator or 0 if not known
	 */
	public char getDecimalSeparator()
	{
		return decimalSeparator_;
	}

	
	//----------------------------
	// format natural
	//----------------------------

	
	/**
	 * @param value the value.
	 * @return formats a long value.
	 */
	public String formatNatural(long value)
	{
		return formatNatural(value, null).toString();
	}

	
	/**
	 * @param value the value.
	 * @param style a NumberStyle to determine grouping. If null the default
	 * 		NumberStyle is used.
	 * @return a long value.
	 */
	public String formatNatural(long value, NumberStyle style)
	{
		return formatNatural(value, style, null).toString();
	}

	
	/**
	 * Formats a long value.
	 * @param value the value.
	 * @param style a style
	 * @param builder a StringBuilder
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatNatural(long value, NumberStyle style, StringBuilder builder)
	{
		return formatNatural(String.valueOf(value), style, builder);
	}
	
	
	/**
	 * Formats a natural number.
	 * @param value the value.
	 * @param style a NumberStyle to determine grouping. If null the default
	 * 		NumberStyle is used.
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatNatural(Number value, NumberStyle style, StringBuilder builder)
	{
		return formatNatural(value != null ? value.toString() : "", style, builder);
	}

	
	private StringBuilder formatNatural(String raw, NumberStyle style, StringBuilder builder)
	{
		builder = setup(builder);
		format(raw, norm(style).useGrouping(), 0, 0, builder);
		return builder;
	}

	
	//----------------------------
	// format decimal
	//----------------------------

	
	/**
	 * Formats a double value.
	 * @param value the value.
	 * @return the formatted value
	 */
	public String formatDecimal(double value)
	{
		return formatDecimal(value, null);
	}

	
	/**
	 * Formats a double value.
	 * @param value the value.
	 * @param style a style
	 * @return the formatted value
	 */
	public String formatDecimal(double value, NumberStyle style)
	{
		return formatDecimal(value, style, null).toString();
	}

	
	/**
	 * Formats a long value.
	 * @param value the value
	 * @param style the style
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatDecimal(double value, NumberStyle style, StringBuilder builder)
	{
		return formatDecimal(String.valueOf(value), style, builder);
	}
	
	
	/**
	 * Formats a number.
	 * @param value the value
	 * @param style the style
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatDecimal(Number value, NumberStyle style, StringBuilder builder)
	{
		return formatDecimal(value != null ? value.toString() : "", style, builder);
	}

	
	/**
	 * Formats a decimal number.
	 * @param raw the raw value.
	 * @param style a NumberStyle to determine grouping and number of decimals. If null the default
	 * 		NumberStyle is used.
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	private StringBuilder formatDecimal(String raw, NumberStyle style, StringBuilder builder)
	{
		builder = setup(builder);
		
		if (raw.indexOf('E') >= 0)
			builder.append(raw); // scientific notation: give up
		else
		{
			style = norm(style);
			format(raw, style.useGrouping(), style.minDecimals(), style.maxDecimals(), builder);
		}
		
		return builder;
	}

	
	//----------------------------
	// format helpers
	//----------------------------

	
	private void format(String raw, boolean grouping, int minDecimals, int maxDecimals, StringBuilder builder)
	{
		int length 	= raw.length();
		int dot 	= raw.indexOf('.');
		
		formatNaturalPart(raw, grouping, dot < 0 ? length : dot, builder);
		if (maxDecimals > 0)
			formatFractionPart(raw, dot, minDecimals, maxDecimals, builder);
	}
	
	
	private void formatNaturalPart(String raw, boolean grouping, int end, StringBuilder builder)
	{
		int length = end;
		int start  = 0;
		if (raw.startsWith("-"))
		{
			start = 1;
			length--;
			builder.append('-');
		}
		
		if ((length > 3) && grouping)
		{
			String sep = getGroupingSepString();
			if (sep != null)
			{
				int next = length % 3;
				if (next == 0)
					next = 3;
				next += start;
				builder.append(raw, start, next);
				
				while(next < end)
				{
					builder.append(sep);
					builder.append(raw, next, next + 3);
					next += 3;
				}
				return;
			}
		}
		
		// fallback
		if (length == 0)
			builder.append('0');
		else
			builder.append(raw, start, end);
	}
	

	private void formatFractionPart(String raw, int dot, int minDecimals, int maxDecimals, StringBuilder builder)
	{
		builder.append(getDecimalSeparator());
		
		int length = raw.length();
		int added  = 0;
		
		if (dot >= 0)
		{
			int start    = dot + 1;
			int decimals = length - start;
			added 	 	 = Math.min(maxDecimals, decimals);
			builder.append(raw, start, start + added);
		}
		
		for (int i=added; i<minDecimals; i++)
			builder.append('0');
	}
	
	
	private static NumberStyle norm(NumberStyle style)
	{
		return style != null ? style : NumberStyle.DEFAULT;
	}
	
	
	private StringBuilder setup(StringBuilder builder)
	{
		return builder != null ? builder : new StringBuilder();
	}
	
	
	//----------------------------
	// parse
	//----------------------------
	
	
	public BigDecimal parseBigDecimal(String s) throws ParseException
	{
		Number n = parseNumber(s);
		if (n == null)
			return null;
		else if (n instanceof BigDecimal)
			return (BigDecimal)n;
		else if (groupingSeparatorString_ != null)
		{
			s = s.replace(groupingSeparatorString_, "");
			if (decimalSeparator_ != '.')
				s = s.replace(decimalSeparator_, '.');
			return new BigDecimal(s);
		}
		else
			return new BigDecimal(n.doubleValue());
	}
	
	
	public BigInteger parseBigInteger(String s) throws ParseException
	{
		Number n = parseNumber(s);
		if (n == null)
			return null;
		else if (n instanceof BigInteger)
			return (BigInteger)n;
		else if (groupingSeparatorString_ != null)
		{
			s = s.replace(groupingSeparatorString_, "");
			return new BigInteger(s);
		}
		else
			return new BigInteger(String.valueOf(n.longValue()));
	}
	
	
	public Double parseDouble(String s) throws ParseException
	{
		Number n = parseNumber(s);
		if (n == null)
			return null;
		else
			return (n instanceof Double) ? (Double)n : Double.valueOf(n.doubleValue());
	}

	
	public Float parseFloat(String s) throws ParseException
	{
		Number n = parseNumber(s);
		if (n == null)
			return null;
		else
			return (n instanceof Float) ? (Float)n : Float.valueOf(n.floatValue());
	}
	

	public Integer parseInteger(String s)
	{
		return StringUtil.isBlank(s) ? null : Integer.valueOf((int)parseLongValue(s));
	}

	
	public Long parseLong(String s)
	{
		return StringUtil.isBlank(s) ? null : Long.valueOf(parseLongValue(s));
	}
	

	private long parseLongValue(String s)
	{
		int length	= s.length();
		long result = 0;
		boolean negative = false;
		
		int i = skipSpace(s, 0, true);
		
		char c = s.charAt(i);
		if ((c == '-') || (c == '+'))
		{
			negative = (c == '-');
			i = skipSpace(s, i+1, true);
		}
		
		while(i < length)
		{
			c = s.charAt(i);
			if (('0' <= c) && (c <= '9'))
			{
				result *= 10;
				result -= (c - '0');
			}
			else if (c != groupingSeparator_)
			{
				if (isSpace(c))
				{
					if (!isSpace(groupingSeparator_))
					{
						skipSpace(s, i, false);
					}
				}
				else
					throw new NumberFormatException(s);
			}
			i++;
		}

		return negative ? result : -result;
	}
	
	
	public Short parseShort(String s)
	{
		return StringUtil.isBlank(s) ? null : Short.valueOf((short)parseLongValue(s));
	}

	
	private Number parseNumber(String s) throws ParseException
	{
		return StringUtil.isBlank(s) ? null : impl_.parse(normNumberString(s));
	}
	

	/**
	 * Motivation: french numbers use \160 as grouping separator
	 * if such a number is entered with a space, only the part until the
	 * first separator is recognized as number
	 */
	private String normNumberString(String s)
	{
		if (isSpace(groupingSeparator_))
		{
			if (s.indexOf(' ') != -1)
				s = s.replace(" ", "");
			if (s.indexOf((char)0xa0) != -1)
				s = s.replace("\u00a0", "");
		}
		return s;
	}
	
	
	private int skipSpace(String s, int start, boolean expectMore)
	{
 		int length = s.length();
		int i = start;
		while ((i < length) && isSpace(s.charAt(i)))
			i++;
		if (expectMore && (i == length))
			throw new NumberFormatException("unexpected end of number: " + s);
		else if (!expectMore && (i < length))
			throw new NumberFormatException("unexpected characters of number: " + s);
		return i;
	}
	
	
	/**
	 * @return true iif the character is 0x20 or 0xA0 (nbsp).
	 */
	private static boolean isSpace(char c)
	{
		return (c == ' ') || (c == 0xa0);
	}

	
	private final Locale locale_;
	private final java.text.NumberFormat impl_;
	private final char decimalSeparator_;
	private final char groupingSeparator_;
	private final String groupingSeparatorString_;
}
