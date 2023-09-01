package org.civilian.response;


import java.io.InputStream;


public interface AsyncInput 
{
	public void setListener(AsyncInputListener listener);
	
	
	public boolean isFinished();
	
	
	public boolean isReady();

	
	public InputStream getStream();
}
