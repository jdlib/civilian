package org.civilian.server.servlet;


import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import org.civilian.response.AsyncInput;
import org.civilian.response.AsyncInputListener;
import org.civilian.util.Check;


public class AsyncInputAdapter implements AsyncInput
{
	public AsyncInputAdapter(ServletInputStream in)
	{
		in_ = in;
	}
	
	
	@Override public void setListener(AsyncInputListener listener) 
	{
		Check.notNull(listener, "listener");
		in_.setReadListener(new ReadListener() {
			@Override public void onError(Throwable t) 
			{
				listener.onInputError(t);
			}
			
			@Override public void onDataAvailable() throws IOException 
			{
				listener.onInputAvailable();
			}
			
			@Override public void onAllDataRead() throws IOException 
			{
				listener.onInputEnd();
			}
		});
	}

	
	@Override public boolean isReady() 
	{
		return in_.isReady();
	}


	@Override public boolean isFinished() 
	{
		return in_.isFinished();
	}
	

	@Override public InputStream getStream() 
	{
		return in_;
	}
	
	
	private final ServletInputStream in_;
}
