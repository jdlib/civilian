package org.civilian.util;


import java.util.function.Function;


/**
 * Represents a function that accepts one argument and produces a result.
 * Similar to {@link Function} but allows to throw checked exceptions.
 * @param <T> the type of the input to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of the Exception
 */
@FunctionalInterface
public interface CheckedFunction<T, R, E extends Exception> 
{
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t) throws E;
}
