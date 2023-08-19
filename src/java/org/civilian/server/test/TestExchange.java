package org.civilian.server.test;


import org.civilian.exchange.Exchange;
import org.civilian.request.AsyncContext;
import org.civilian.request.Request;
import org.civilian.response.Response;


public class TestExchange implements Exchange
{
	public TestExchange(Request request, TestResponse response)
	{
		this.request = request;
		this.response = response;
	}
	
	
	@Override public Request getRequest() 
	{
		return request;
	}
	

	@Override public Response getResponse() 
	{
		return response;
	}
	

	@Override public AsyncContext getAsyncContext() 
	{
		throw new IllegalStateException();
	}

	@Override public boolean isAsyncStarted() 
	{
		throw new IllegalStateException();
	}


	@Override public boolean isAsyncSupported() 
	{
		throw new IllegalStateException();
	}


	@Override public AsyncContext startAsync() 
	{
		throw new IllegalStateException();
	}

	
	public final Request request;
	public final TestResponse response;
}
