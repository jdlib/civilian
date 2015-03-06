package org.civilian.text;

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
	public static final NumberStyle DEFAULT = new NumberStyle(true, 2); 

	
	/**
	 * A NumberStyle which uses grouping, minDecimals = 0 and maxDecimals = Integer.MAX_VALUE.
	 */
	public static final NumberStyle UNLIMITED = new NumberStyle(true, 0, Integer.MAX_VALUE);
	

	/**
	 * A NumberStyle which does not use grouping, minDecimals = 0 and maxDecimals = Integer.MAX_VALUE.
	 */
	public static final NumberStyle RAW = new NumberStyle(false, 0, Integer.MAX_VALUE);
	
	
			
	public NumberStyle(boolean useGrouping, int decimals)
	{
		this(useGrouping, decimals, decimals);
	}
	
	
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
	
	
	private boolean useGrouping_;
	private int minDecimals_;
	private int maxDecimals_;
}
