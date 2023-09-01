package org.civilian.response;


public interface AsyncInputListener 
{
	public void onInputAvailable();
	
	
	public void onInputEnd();
	
	
	public void onInputError(Throwable t);
}
