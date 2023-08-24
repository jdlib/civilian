package org.civilian.exchange;


import org.civilian.response.AsyncContext;


/**
 * Exchange is a pair of request and response.
 * Additionally it allows for asynchronous request+response processing.
 */
public interface Exchange extends RequestResponseProvider
{
	/**
	 * Returns the AsyncContext that was created by the most recent call to {@link #startAsync()}
	 * @return the AsnycContext
	 * @throws IllegalStateException if startAsync() has not been called.
	 */
	public AsyncContext getAsyncContext();
	
	
	/**
	 * Returns if this request has been put into asynchronous mode by a call to {@link #startAsync()}.
	 * @return the flag 
	 */
	public boolean isAsyncStarted();
	
	
	/**
	 * Returns if this request supports asynchronous mode. 
	 * @return the flag 
	 */
	public boolean isAsyncSupported();
	
	
	/**
	 * Puts this request into asynchronous mode, and initializes its AsyncContext.
	 * @return the AsyncContext 
	 * @throws IllegalStateException if this request does not support asynchronous operations or if called again
	 * 		in a state where the AsyncContext intervenes, or when the response has been closed.
	 */
	public AsyncContext startAsync();
}
