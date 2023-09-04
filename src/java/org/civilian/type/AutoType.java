package org.civilian.type;


/**
 * AutoType is a Type which knows how to parse and format values.
 * It uses {@link Category#AUTO}.
 */
public abstract class AutoType<VALUE> extends Type<VALUE> 
{
	public AutoType() 
	{
		super(Category.AUTO);
	}


	/**
	 * Formats a value.
	 * @param value a value
	 * @return the formatted value
	 */
	public abstract String format(VALUE value);
	

	/**
	 * Parses a value from a string.
	 * @param s a string
	 * @return the parsed value
	 * @throws Exception if parsing fails
	 */
	public abstract VALUE parse(String s) throws Exception;
}
