package org.civilian.server.servlet;


import org.civilian.exchange.Exchange;
import org.civilian.request.AsyncContext;
import org.civilian.request.Request;
import org.civilian.response.Response;


public class ServletExchange implements Exchange 
{
	public ServletExchange(ServletRequestAdapter request, ServletResponseAdapter response)
	{
		request_ 	= request;
		response_	= response;
	}
	
	
	@Override public Request getRequest() 
	{
		return request_;
	}
	
	
	@Override public Response getResponse() 
	{
		return response_;
	}
	
	
	@Override public AsyncContext getAsyncContext() 
	{
		return request_.getAsyncContext();
	}
	
	
	@Override public boolean isAsyncStarted() 
	{
		return request_.isAsyncStarted();
	}
	
	
	@Override public boolean isAsyncSupported() 
	{
		return request_.isAsyncSupported();
	}
	
	
	@Override public AsyncContext startAsync() 
	{
		return request_.startAsync();
	}
	

	private final ServletRequestAdapter request_;
	private final ServletResponseAdapter response_;
}
