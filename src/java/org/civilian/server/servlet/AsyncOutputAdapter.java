package org.civilian.server.servlet;


import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import org.civilian.response.AsyncOutput;
import org.civilian.response.AsyncOutputListener;
import org.civilian.util.Check;


public class AsyncOutputAdapter implements AsyncOutput 
{
	public AsyncOutputAdapter(ServletOutputStream out)
	{
		out_ = out;
	}
	
	
	@Override public void setListener(AsyncOutputListener listener) 
	{
		Check.notNull(listener, "listener");
		out_.setWriteListener(new WriteListener() 
		{
			@Override public void onWritePossible() throws IOException 
			{
				listener.onOutputPossible();
			}

			@Override public void onError(Throwable t) 
			{
				listener.onOutputError(t);
			}
			
		});
	}

	
	@Override public boolean isReady() 
	{
		return out_.isReady();
	}
	

	@Override public OutputStream getStream() 
	{
		return out_;
	}
	
	
	private final ServletOutputStream out_;
}
