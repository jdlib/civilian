package org.civilian.util;


@FunctionalInterface
public interface CheckedConsumer<T,E extends Exception> 
{
	public void accept(T value) throws E;
}
