package org.civilian.internal;


import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.civilian.Response;
import org.civilian.response.ResponseStreamInterceptor;


public class TestResponseStreamInterceptor implements ResponseStreamInterceptor
{
	public TestResponseStreamInterceptor(String header)
	{
		header_ = header.getBytes();
	}
	
	
	@Override public ResponseStreamInterceptor prepareStreamIntercept(Response response)
	{
		return intercept ? this : null;
	}


	@Override public OutputStream intercept(OutputStream out) throws IOException
	{
		if (!lazy)
		{
			out.write(header_);
			return out;
		}
		else
			return new LazyStream(out);
	}
	
	
	private class LazyStream extends FilterOutputStream
	{
		public LazyStream(OutputStream out)
		{
			super(out);
		}
		
		
		@Override public void flush() throws IOException
		{
			out.write(header_);
			super.flush();
		}
	}
	
	
	public boolean intercept = true;
	public boolean lazy = false;
	private byte[] header_;
}
