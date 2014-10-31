package org.civilian.text;


import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
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
	 * Returns a NumberFormat for the default locale.
	 */
	public static NumberFormat getDefaultInstance()
	{
		return getInstance(Locale.getDefault());
	}


	/**
	 * Returns a NumberFormat for a locale.
	 */
	public static NumberFormat getInstance(Locale locale)
	{
		return getInstance(locale, true);
	}
	
	
	/**
	 * Returns a NumberFormat for a locale.
	 * @param locale the locale
	 * @param cache determines if new created NumberFormat instances will
	 * 		be put in a global cache.
	 */
	public static NumberFormat getInstance(Locale locale, boolean cache)
	{
		Check.notNull(locale, "locale");
		
		NumberFormat instance = instances_.get(locale);
		if (instance == null)
		{
			instance = new NumberFormat(locale);
			if (cache)
				instances_.put(locale, instance);
		}
		
		return instance;
	}


	private NumberFormat(Locale locale)
	{
		locale_	= locale;
		impl_ 	= java.text.NumberFormat.getNumberInstance(locale);

		if (impl_ instanceof DecimalFormat)
		{
			DecimalFormatSymbols symbols = ((DecimalFormat)impl_).getDecimalFormatSymbols(); 
			groupingSeparator_ 			= symbols.getGroupingSeparator();
			groupingSeparatorString_ 	= groupingSeparator_ > 0 ? String.valueOf(groupingSeparator_) : null;
			decimalSeparator_ 			= symbols.getDecimalSeparator();
		}
	}
	
	
	/**
	 * Returns the locale of the NumberFormat.
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
	public String getGroupingSeparatorString()
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
	// format
	//----------------------------

	
	/**
	 * Formats a long value.
	 */
	public String formatNatural(long value)
	{
		return NumberStyle.DEFAULT.formatNatural(this, value);
	}

	
	/**
	 * Formats a double value.
	 */
	public String formatDecimal(double value)
	{
		return NumberStyle.DEFAULT.formatDecimal(this, value);
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
			return (n instanceof Double) ? (Double)n : new Double(n.doubleValue());
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
		return StringUtil.isBlank(s) ? null : new Short((short)parseLongValue(s));
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
	 * Returns true iif the character is 0x20 or 0xA0 (nbsp).
	 */
	private static boolean isSpace(char c)
	{
		return (c == ' ') || (c == 0xa0);
	}

	
	private final Locale locale_;
	private final java.text.NumberFormat impl_;
	private char decimalSeparator_ = 0;
	private char groupingSeparator_ = 0;
	private String groupingSeparatorString_;
	private static final HashMap<Locale,NumberFormat> instances_ = new HashMap<>();
}
