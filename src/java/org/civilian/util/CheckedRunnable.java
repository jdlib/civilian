package org.civilian.util;


@FunctionalInterface
public interface CheckedRunnable<E extends Exception> 
{
	public void run() throws E;
	
	public default Runnable unchecked() 
	{
		return () -> {
			try
			{
				run();
			}
			catch (RuntimeException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new IllegalStateException("error when running " + this, e);
			}
		};
	}
}
