package org.civilian.server.test;


import java.util.ArrayList;
import java.util.List;
import org.civilian.request.Request;
import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEvent;
import org.civilian.response.AsyncEventListener;
import org.civilian.response.AsyncWriteListener;
import org.civilian.response.Response;


public class TestAsyncContext extends AsyncContext
{
	public TestAsyncContext(Request request, Response response)
	{
		super(request, response);
	}


	@Override public void addEventListener(AsyncEventListener listener)
	{
		eventListeners_.add(listener);
	}
	
	
	@Override public void addWriteListener(AsyncWriteListener listener)
	{
		writeListeners_.add(listener);
	}

	
	private void fire(AsyncEvent.Type type)
	{
		if (eventListeners_ != null)
		{
			AsyncEvent event = new AsyncEvent(type, this);
			for (AsyncEventListener listener : eventListeners_)
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

	
	private List<AsyncEventListener> eventListeners_ = new ArrayList<>();
	private List<AsyncWriteListener> writeListeners_ = new ArrayList<>();
	private long timeOut_ = 30000;
}
