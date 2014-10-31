package org.civilian.text;


import java.math.BigDecimal;
import org.civilian.util.Check;


/**
 * NumberStyle allows for fine grained, locale dependent formatting of natural and decimal numbers.
 * It uses the formatting symbols of a {@link NumberFormat}.
 */
public class NumberStyle
{
	/**
	 * A NumberStyle which uses grouping and minDecimals = maxDecimals = 2.
	 */
	public static final NumberStyle DEFAULT = new NumberStyle(true, 2, 2); 

	
	/**
	 * A NumberStyle which uses grouping, minDecimals = 0 and maxDecimals = Integer.MAX_VALUE.
	 */
	public static final NumberStyle UNLIMITED = new NumberStyle(true, 0, Integer.MAX_VALUE);
	

	/**
	 * A NumberStyle which does not use grouping, minDecimals = 0 and maxDecimals = Integer.MAX_VALUE.
	 */
	public static final NumberStyle RAW = new NumberStyle(false, 0, Integer.MAX_VALUE);
	
	
			
	public NumberStyle(boolean useGrouping, int minDecimals, int maxDecimals)
	{
		Check.greaterEquals(minDecimals, 0, "minDecimals");
		Check.greaterEquals(minDecimals, minDecimals, "maxDecimals");
		
		useGrouping_ 	= useGrouping;
		minDecimals_	= minDecimals;
		maxDecimals_	= maxDecimals;
	}

	
	/**
	 * Returns if grouping of the integer part of a number should be applied.
	 */
	public boolean useGrouping()
	{
		return useGrouping_;
	}
	

	/**
	 * Returns a new NumberStyle whose grouping flag has the given value.
	 */
	public NumberStyle useGrouping(boolean value)
	{
		return value != useGrouping_ ? new NumberStyle(value, minDecimals_, maxDecimals_) : this;
	}

	
	/**
	 * Returns how many decimal digits should at least be printed when formatting a decimal.
	 */
	public int minDecimals()
	{
		return minDecimals_;
	}
	

	/**
	 * Sets the number of minimal digits which should be printed when
	 * formatting a decimal. Must be 0 <= min <= maxDecimals(). 
	 */
	public NumberStyle minDecimals(int min)
	{
		return min != minDecimals_ ? new NumberStyle(useGrouping_, min, maxDecimals_) : this;
	}
	
	
	/**
	 * Returns how many decimal digits should at most be printed when formatting a decimal.
	 */
	public int maxDecimals()
	{
		return maxDecimals_;
	}

	
	/**
	 * Sets the number of maximal digits which should be printed when
	 * formatting a decimal. 
	 */
	public NumberStyle maxDecimals(int max)
	{
		return max != maxDecimals_ ? new NumberStyle(useGrouping_, minDecimals_, max) : this;
	}
	
	
	/**
	 * Sets the number of minimal = maximal digits which should be printed when
	 * formatting a decimal. 
	 */
	public NumberStyle decimals(int value)
	{
		return decimals(value, value);
	}

	
	/**
	 * Sets the number of minimal and maximal digits which should be printed when
	 * formatting a decimal. 
	 */
	public NumberStyle decimals(int min, int max)
	{
		return (min != minDecimals_) || (max != maxDecimals_) ? new NumberStyle(useGrouping_, min, max) : this;
	}
	
	
	/**
	 * Formats a natural number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value.
	 * @return the formatted number
	 */
	public String formatNatural(NumberFormat format, long value)
	{
		return formatNatural(format, value, null).toString();
	}

	
	/**
	 * Formats a natural number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value.
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatNatural(NumberFormat format, long value, StringBuilder builder)
	{
		return formatNatural(format, String.valueOf(value), builder);
	}

	
	/**
	 * Formats a natural number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value. Must not be null.
	 * @return the formatted number
	 */
	public String formatNatural(NumberFormat format, Number value)
	{
		return formatNatural(format, value, null).toString();
	}

	
	/**
	 * Formats a natural number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value. Must not be null.
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatNatural(NumberFormat format, Number value, StringBuilder builder)
	{
		Check.notNull(value, "value");
		return formatNatural(format, value.toString(), builder);
	}

	
	private StringBuilder formatNatural(NumberFormat format, String raw, StringBuilder builder)
	{
		builder = setup(builder);
		format(format, raw, 0, 0, builder);
		return builder;
	}

	
	/**
	 * Formats a decimal number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value.
	 * @return the formatted number
	 */
	public String formatDecimal(NumberFormat format, double value)
	{
		return formatDecimal(format, value, null).toString();
	}

	
	/**
	 * Formats a decimal number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value.
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatDecimal(NumberFormat format, double value, StringBuilder builder)
	{
		return formatDecimal(format, String.valueOf(value), builder);
	}

	
	/**
	 * Formats a decimal number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value. Must not be null.
	 * @return the formatted number
	 */
	public String formatDecimal(NumberFormat format, BigDecimal value)
	{
		return formatDecimal(format, value, null).toString();
	}

	
	/**
	 * Formats a decimal number.
	 * @param format a NumberFormat which provides locale dependent symbols
	 * @param value the value. Must not be null.
	 * @param builder a StringBuilder to which the formatted number is appended. 
	 * 		If the builder is null, then a new StringBuilder will be created. 
	 * @return the StringBuilder containing the number
	 */
	public StringBuilder formatDecimal(NumberFormat format, BigDecimal value, StringBuilder builder)
	{
		Check.notNull(value, "value");
		return formatDecimal(format, String.valueOf(value), builder);
	}

	
	private StringBuilder formatDecimal(NumberFormat format, String raw, StringBuilder builder)
	{
		builder = setup(builder);

		if (raw.indexOf('E') >= 0)
			builder.append(raw); // scientific notation: give up
		else
			format(format, raw, minDecimals_, maxDecimals_, builder);
		
		return builder;
	}

	
	private StringBuilder setup(StringBuilder builder)
	{
		return builder != null ? builder : new StringBuilder();
	}
	
	
	private void format(NumberFormat format, String s, int minDecimals, int maxDecimals, StringBuilder builder)
	{
		int length 	= s.length();
		int dot 	= s.indexOf('.');
		
		formatNaturalPart(format, s, dot < 0 ? length : dot, builder);
		if (maxDecimals > 0)
			formatFractionPart(format, s, dot, minDecimals, maxDecimals, builder);
	}
	
	
	private void formatFractionPart(NumberFormat format, String s, int dot, int minDecimals, int maxDecimals, StringBuilder builder)
	{
		builder.append(format.getDecimalSeparator());
		
		int length = s.length();
		int added  = 0;
		
		if (dot >= 0)
		{
			int start    = dot + 1;
			int decimals = length - start;
			added 	 	 = Math.min(maxDecimals, decimals);
			builder.append(s, start, start + added);
		}
		
		for (int i=added; i<minDecimals; i++)
			builder.append('0');
	}
	
	
	private void formatNaturalPart(NumberFormat format, String s, int end, StringBuilder builder)
	{
		int length = end;
		int start  = 0;
		if (s.startsWith("-"))
		{
			start = 1;
			length--;
			builder.append("-");
		}
		
		if ((length > 3) && useGrouping_)
		{
			String sep = format.getGroupingSeparatorString();
			if (sep != null)
			{
				int next = length % 3;
				if (next == 0)
					next = 3;
				next += start;
				builder.append(s, start, next);
				
				while(next < end)
				{
					builder.append(sep);
					builder.append(s, next, next + 3);
					next += 3;
				}
				return;
			}
		}
		
		// fallback
		if (length == 0)
			builder.append("0");
		else
			builder.append(s, start, end);
	}
	

	private boolean useGrouping_;
	private int minDecimals_;
	private int maxDecimals_;
}
