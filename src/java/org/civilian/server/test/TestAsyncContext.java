package org.civilian.server.test;


import java.util.ArrayList;
import java.util.List;
import org.civilian.request.Request;
import org.civilian.response.AsyncContext;
import org.civilian.response.AsyncEvent;
import org.civilian.response.AsyncEventListener;
import org.civilian.response.AsyncInput;
import org.civilian.response.AsyncOutput;
import org.civilian.response.Response;
import org.civilian.util.Check;
import org.civilian.util.CheckedRunnable;


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
	
	
	@Override public AsyncInput getAsyncInput()
	{
		throw new UnsupportedOperationException();
	}

	
	@Override public AsyncOutput getAsyncOutput()
	{
		throw new UnsupportedOperationException();
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
	

	@Override protected void completeImpl()
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
	
	
	@Override public void start(CheckedRunnable<? extends Exception> runnable) 
	{
		Check.notNull(runnable, "runnable");
		try 
		{
			runnable.run();
		} 
		catch (Throwable e) 
		{
			throw new IllegalStateException("error when running " + runnable, e);
		}
	}

	
	private List<AsyncEventListener> eventListeners_ = new ArrayList<>();
	private long timeOut_ = 30000;
}
