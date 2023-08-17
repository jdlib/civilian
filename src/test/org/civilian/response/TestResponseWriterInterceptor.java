package org.civilian.internal;


import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

import org.civilian.response.Response;
import org.civilian.response.ResponseWriterInterceptor;


public class TestResponseWriterInterceptor implements ResponseWriterInterceptor
{
	public TestResponseWriterInterceptor(String header)
	{
		header_ = header;
	}
	
	
	@Override public ResponseWriterInterceptor prepareWriterIntercept(Response response)
	{
		return intercept ? this : null;
	}


	@Override public Writer intercept(Writer out) throws IOException
	{
		if (!lazy)
		{
			out.write(header_);
			return out;
		}
		else
			return new LazyWriter(out);
	}
	
	
	private class LazyWriter extends FilterWriter
	{
		public LazyWriter(Writer out)
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
	private String header_;
}
