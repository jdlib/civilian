package org.civilian.server.test;


import java.util.ArrayList;

import org.civilian.request.Request;
import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEvent;
import org.civilian.response.AsyncListener;
import org.civilian.response.Response;


public class TestAsyncContext extends AsyncContext
{
	public TestAsyncContext(Request request, Response response)
	{
		super(request, response);
	}


	@Override public void addListener(AsyncListener listener)
	{
		if (listeners_ == null)
			listeners_ = new ArrayList<>();
		listeners_.add(listener);
	}
	
	
	private void fire(AsyncEvent.Type type)
	{
		if (listeners_ != null)
		{
			AsyncEvent event = new AsyncEvent(type, this);
			for (AsyncListener listener : listeners_)
				listener.onEvent(event);
		}
	}
	

	@Override public void complete()
	{
		getResponse().closeContent();
		fire(AsyncEvent.Type.COMPLETE);
	}
	

	@Override public void dispatch()
	{
		throw new UnsupportedOperationException();
	}
	

	@Override public void dispatch(String path)
	{
		throw new UnsupportedOperationException();
	}
	

	@Override public long getTimeout()
	{
		return timeOut_;
	}
	

	@Override public void setTimeout(long milliSeconds)
	{
		timeOut_ = milliSeconds;
	}
	
	
	@Override public void start(Runnable runnable) 
	{
		runnable.run();
	}

	
	private ArrayList<AsyncListener> listeners_;
	private long timeOut_ = 30000;
}
