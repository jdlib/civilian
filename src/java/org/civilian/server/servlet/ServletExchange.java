package org.civilian.server.servlet;


import org.civilian.exchange.Exchange;
import org.civilian.request.Request;
import org.civilian.response.AsyncContext;
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
		return response_.getAsyncContext();
	}
	
	
	@Override public boolean isAsyncStarted() 
	{
		return response_.isAsyncStarted();
	}
	
	
	@Override public boolean isAsyncSupported() 
	{
		return response_.isAsyncSupported();
	}
	
	
	@Override public AsyncContext startAsync() 
	{
		return response_.startAsync();
	}
	

	private final ServletRequestAdapter request_;
	private final ServletResponseAdapter response_;
}
