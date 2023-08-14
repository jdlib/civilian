package org.civilian.server.test;


import java.util.ArrayList;

import org.civilian.request.AsyncContext;
import org.civilian.request.AsyncEvent;
import org.civilian.request.AsyncListener;
import org.civilian.request.Request;


public class TestAsyncContext extends AsyncContext
{
	public TestAsyncContext(Request request)
	{
		super(request);
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
		getRequest().getApplication().process(getRequest());
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
	
	
	private ArrayList<AsyncListener> listeners_;
	private long timeOut_ = 30000;
}
