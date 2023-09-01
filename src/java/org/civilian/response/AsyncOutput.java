package org.civilian.response;


import java.io.OutputStream;


public interface AsyncOutput 
{
	public void setListener(AsyncOutputListener listener);
	
	
	public boolean isReady();
	
	
	public OutputStream getStream();
}
