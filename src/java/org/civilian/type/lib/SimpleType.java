package org.civilian.type.lib;


import org.civilian.type.Type;


public abstract class SimpleType<T> extends Type<T>
{	
	public SimpleType()
	{
		super(Category.SIMPLE);
	}
}
