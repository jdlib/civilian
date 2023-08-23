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
	 * Formats a key value.
	 */
	public abstract String format(VALUE value);
	

	/**
	 * Parses a key value.
	 */
	public abstract VALUE parse(String s) throws Exception;
}
