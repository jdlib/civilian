package org.civilian.response;


/**
 * ResponseHandler is a service which can configure a response.
 */
public interface ResponseHandler 
{
	public Exception send(Response response);
	
	
	@Override public String toString();
}
